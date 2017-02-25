define(['forum/module', 'forum/add/service', 'ckeditor', 'ckeditorConfig'], function(forumModule) {
	return forumModule
	.controller('ForumAddCtrl', [ '$scope', '$http', '$state', 'forumAddService', 'util'
	                         , function($scope, $http, $state, forumAddService, util) {
		var self = this, promise;
		promise = $scope.getAuthentication();
//		util.loadasync('lib/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css');
		
		self.forumPost = {};
		
		// 需要先获取登录信息才能上传图片
		promise.then(function(data) {
			/**
			 * CKEDITOR API手册参考：
			 * http://docs.ckeditor.com/#!/guide
			 */
			// Replace the <textarea id="editor1"> with a CKEditor
			// instance, using default configuration.
			CKEDITOR.editorConfig = function( config ) {
			    config.language = 'zh';
			    config.uiColor = '#AADC6E';
			};
			
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
			var editor = CKEDITOR.replace('editor1', {
				filebrowserImageUploadUrl : getUrlWithCsrfParam(),
			});
			editor.on('change', function(event) {
				self.forumPost.body = this.getData();// 内容
				$scope.$apply();
			});
			/*
			editor.on('fileUploadRequest', function(evt) {
				var xhr = evt.data.fileLoader.xhr;
				var token = util.getCookie('XSRF-TOKEN');
				if (token) {
					xhr.setRequestHeader('X-XSRF-TOKEN', token);
				}
			});
			*/
			
		});

		self.submit = function() {
			self.forumPost.email = $scope.authentication.username;
			forumAddService.add(self.forumPost).success(function(data) {
				self.forumPost = {};
				$state.go('forum.search', {}, { reload : true });
			});
		}
	}]);
});