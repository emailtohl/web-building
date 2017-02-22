define(['cms/module', 'cms/category/service'], function(cmsModule) {
	return cmsModule
	.controller('CategoryCtrl', ['$scope', '$http', '$state', 'categoryService',
	                                function($scope, $http, $state, service) {
		var self = this;
		$scope.getAuthentication();
		
		getTypes();
		
		function getTypes() {
			service.getTypes().success(function(data) {
				self.typeList = data;
				console.log(data);
			});
		}
		
	}])
	;
});