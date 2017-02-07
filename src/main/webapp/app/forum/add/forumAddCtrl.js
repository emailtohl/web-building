define(['forum/module', 'forum/add/service', 'ckeditor'], function(forumModule) {
	return forumModule
	.controller('ForumAddCtrl', [ '$scope', '$http', '$state', 'forumAddService', 'util'
	                         , function($scope, $http, $state, forumAddService, util) {
		var self = this;
		$scope.getAuthentication();
//		util.loadasync('lib/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css');
		
		self.forumPost = {};
		
		// Replace the <textarea id="editor1"> with a CKEditor
		// instance, using default configuration.
		CKEDITOR.editorConfig = function( config ) {
		    config.language = 'zh';
		    config.uiColor = '#AADC6E';
		};
		var editor = CKEDITOR.replace('editor1', {
			filebrowserImageUploadUrl : 'forum/image'
		});
		editor.on('change', function(event) {
			self.forumPost.body = this.getData();// 内容
			$scope.$apply();
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