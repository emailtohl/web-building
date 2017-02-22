define(['cms/module', 'cms/article/service'], function(cmsModule) {
	return cmsModule
	.controller('ArticleCtrl', ['$scope', '$http', '$state', 'articleService',
	                                function($scope, $http, $state, service) {
		var self = this;
		$scope.getAuthentication();
		
	}])
	;
});