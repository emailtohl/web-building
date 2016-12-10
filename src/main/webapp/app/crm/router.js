define(['crm/module',
        'crm/ctrl',
        ], function(crmModule) {
	return crmModule.config(function($stateProvider) {
		$stateProvider
		.state('crm', {
			url : '/crm',
			templateUrl : 'app/crm/crm.html',
			controller : 'CrmCtrl as ctrl'
		})
		;
	});
});