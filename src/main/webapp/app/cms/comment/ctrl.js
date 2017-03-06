define(['cms/module', 'cms/article/service', 'cms/category/service'], function(cmsModule) {
	return cmsModule
	.filter('omit', function() {
		return function(content) {
			if (typeof content != 'string')
				return content;
			var i = content.length;
			if (i > 20)
				i = 20;
			return content.substring(0, i) + '……';
		}
	})
	.controller('CommentCtrl', ['$scope', '$http', '$state', 'commentService', 'util',
	                                function($scope, $http, $state, service, util) {
		const editorID = "comment-article-body";
		var self = this, promise = $scope.getAuthentication();
		// 用于切换界面，详情状态就是新增和编辑，反之则是列表页面
		self.isDetail = false;
		self.comment = {};
		self.queryParam = {
			page : 1,
			query : '',
		};
		
		self.query = function() {
			service.queryComments(self.queryParam.query, self.queryParam.page).success(function(data) {
				self.pager = data;
				console.log(data);
				self.isDetail = false;
			});
		};
		
		self.query();
		
		self.edit = function(id) {
			service.findComment(id).success(function(data) {
				console.log(data);
				self.comment = data;
				self.isDetail = true;
				
				$('#' + editorID).val(self.comment.article.body);
				var editor = CKEDITOR.instances[editorID]; // 编辑器的"name"属性的值
				if (editor) {
					editor.destroy(true);// 销毁编辑器
				}
				editor = CKEDITOR.replace(editorID); // 替换编辑器，editorID为ckeditor的"id"属性的值
				CKEDITOR.on('instanceReady', function(ev) {
					editor.setReadOnly(true);
				});
			});
		};
		
		self.btnClick = function(pageNumber) {
			self.queryParam.page = pageNumber;
			self.query();
		};

		self.approvedComment = function() {
			if (!self.comment.id)
				return;
			service.approvedComment(self.comment.id).success(function(data) {
				self.comment.isApproved = true;
			});
		};
		
		self.rejectComment = function() {
			if (!self.comment.id)
				return;
			service.rejectComment(self.comment.id).success(function(data) {
				self.comment.isApproved = false;
			});
		};
		
		self.back = function() {
			// 如果有管理权限，则可能做了启动评论、关闭评论，允许发布、禁止发布等功能，所以需要刷新列表。反之则可以直接返回列表页面
			if ($scope.hasAuthority('content_manager')) {
				self.query();
			} else {
				self.isDetail = false;
			}
		};
		
		self['delete'] = function() {
			if (!self.comment.id)
				return;
			if (confirm('确定删除该评论吗？')) {
				service.deleteComment(self.comment.id).success(function(data) {
					self.query();
				});
			}
		};
		
	}])
	;
});