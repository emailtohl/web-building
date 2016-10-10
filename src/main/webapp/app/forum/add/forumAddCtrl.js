define(['forum/module', 'forum/add/service'], function(forumModule) {
	return forumModule
	.controller('ForumAddCtrl', [ '$scope', '$http', '$state', 'forumAddService'
	                         , function($scope, $http, $state, forumAddService) {
		var self = this;
		$scope.getAuthentication();
	}]);
});