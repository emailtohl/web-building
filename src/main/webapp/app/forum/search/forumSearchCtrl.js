define(['forum/module', 'forum/search/service'], function(forumModule) {
	return forumModule
	.controller('ForumSearchCtrl', ['$scope', '$http', '$state', 'forumSearchService',
	                                function($scope, $http, $state, forumSearchService) {
		var self = this;
		$scope.getAuthentication();
	}]);
});