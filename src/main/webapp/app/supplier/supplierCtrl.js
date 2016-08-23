define(['supplier/module'], function(supplierModule) {
	return supplierModule
	.controller('SupplierCtrl', ['$scope', '$http', function($scope, $http) {
		var self = this;
		self.request = function() {
			$http.get('chat/node').success(function(data) {
				console.log(data);
			});
		};
	}]);
});