define(['cms/module', 'cms/category/service'], function(cmsModule) {
	return cmsModule
	.controller('CategoryCtrl', ['$scope', '$http', '$state', 'resourceService',
	                                function($scope, $http, $state, service) {
		var self = this;
		$scope.getAuthentication();
		
	}])
	;
});