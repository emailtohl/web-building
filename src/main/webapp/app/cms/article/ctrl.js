define(['cms/module', 'cms/article/service', 'cms/category/service', 'ckeditor', 'ckeditorConfig'], function(cmsModule) {
	return cmsModule
	.controller('ArticleCtrl', ['$scope', '$http', '$state', 'articleService', 'categoryService', 'util',
	                                function($scope, $http, $state, service, categoryService, util) {
		const editorID = "article-editor";
		var self = this, promise;
		promise = $scope.getAuthentication();
		categoryService.getTypes().success(function(data) {
			self.types = data;
		});
		
		// 用于切换界面，详情状态就是新增和编辑，反之则是列表页面
		self.isDetail = false;
		self.article = {};
		self.queryParam = {
			page : 1,
			query : '',
		};
		
		self.query = function() {
			service.search(self.queryParam.query, self.queryParam.page).success(function(data) {
				self.pager = data;
				console.log(data);
				self.isDetail = false;
			});
		};
		
		self.query();
		
		self.btnClick = function(pageNumber) {
			self.query.page = pageNumber;
			self.query();
		};
		
		self.add = function() {
			self.article = {};
			self.isDetail = true;
			refresh();
		};
		
		self.edit = function(id) {
			service.findArticle(id).success(function(data) {
				console.log(data);
				self.article = data;
				self.isDetail = true;
				refresh();
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
		
		self.submit = function() {
			if (self.article.id) {
				service.updateArticle(self.article.id, self.article).success(function(data) {
					self.query();
					self.isDetail = false;
				});
			} else {
				service.saveArticle(self.article).success(function(data) {
					self.query();
					self.isDetail = false;
				});
			}
		};

		self['delete'] = function() {
			if (!self.article.id)
				return;
			if (confirm('确定删除《' + self.article.title + '》吗？')) {
				service.deleteArticle(self.article.id).success(function(data) {
					self.query();
				});
			}
		};
		
		self.approveArticle = function() {
			if (!self.article.id)
				return;
			service.approveArticle(self.article.id).success(function(data) {
				self.article.isApproved = true;
			});
		};
		
		self.rejectArticle = function() {
			if (!self.article.id)
				return;
			service.rejectArticle(self.article.id).success(function(data) {
				self.article.isApproved = false;
			});
		};
		
		self.openComment = function() {
			if (!self.article.id)
				return;
			service.openComment(self.article.id).success(function(data) {
				self.article.isComment = true;
			});
		};
		
		self.closeComment = function() {
			if (!self.article.id)
				return;
			service.closeComment(self.article.id).success(function(data) {
				self.article.isComment = false;
			});
		};
		
		/**
		 * 刷新编辑器区的内容
		 */
		function refresh() {
			var editor = CKEDITOR.instances[editorID]; // 编辑器的"name"属性的值
			if (editor) {
				editor.destroy(true);// 销毁编辑器
			}
			editor = CKEDITOR.replace(editorID, {
				filebrowserImageUploadUrl : getUrlWithCsrfParam(),
			}); // 替换编辑器，editorID为ckeditor的"id"属性的值
			editor.on('change', function(event) {
				self.article.body = this.getData();// 内容
				$scope.$apply();
			});
		}
		
		function getUrlWithCsrfParam() {
			var url = null;
			// 只有登录了，才有CSRF TOKEN，不然上传文件会报错，如果没登录则屏蔽上传文件功能
			if ($scope.isAuthenticated()) {
				url = 'forum/image?type=image';
				var token = util.getCookie('XSRF-TOKEN');
				if (token) {
					url += '&_csrf=' + token;
				}
			}
			return url;
		}
		
	}])
	;
});