define(['forum/module', 'forum/add/service', 'ckeditor'], function(forumModule) {
	return forumModule
	.controller('ForumAddCtrl', [ '$scope', '$http', '$state', 'forumAddService', 'util'
	                         , function($scope, $http, $state, forumAddService, util) {
		var self = this;
		$scope.getAuthentication();
		util.loadasync('lib/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css');
		
		self.forumPost = {};
		
		// Replace the <textarea id="editor1"> with a CKEditor
		// instance, using default configuration.
		var editor = CKEDITOR.replace('editor1');
		editor.on('change', function(event) {
			self.forumPost.body = this.getData();// 内容
			$scope.$apply();
		});
		
		self.submit = function() {
			forumAddService.add(self.forumPost).success(function(data) {
				self.forumPost = {};
			});
		}
	}]);
});