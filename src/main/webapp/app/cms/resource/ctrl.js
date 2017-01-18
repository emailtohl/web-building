define(['cms/module', 'cms/service'], function(cmsModule) {
	return cmsModule
	.controller('ResourceCtrl', ['$scope', '$http', '$state', 'cmsService',
	                                function($scope, $http, $state, service) {
		var self = this;
		$scope.getAuthentication();
		
		
	}])
	;
});