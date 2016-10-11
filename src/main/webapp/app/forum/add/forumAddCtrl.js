define(['forum/module', 'forum/add/service', 'ckeditor'], function(forumModule) {
	return forumModule
	.controller('ForumAddCtrl', [ '$scope', '$http', '$state', 'forumAddService', 'util'
	                         , function($scope, $http, $state, forumAddService, util) {
		var self = this;
		$scope.getAuthentication();
		util.loadasync('lib/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css');

		// Replace the <textarea id="editor1"> with a CKEditor
		// instance, using default configuration.
		CKEDITOR.replace('editor1');
		// bootstrap WYSIHTML5 - text editor
//		$(".textarea").wysihtml5();
		
	}]);
});