define(['supplier/module', 'uirouter', 'supplier/supplierCtrl'], function(supplierModule) {
	return supplierModule.config(function($stateProvider) {
		$stateProvider
		.state('supplier', {
			url : '/supplier',
			templateUrl : 'app/supplier/supplier.html',
			controller : 'SupplierCtrl as ctrl'
		});
	});
});