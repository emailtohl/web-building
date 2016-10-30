define(['applicationForm/module',
        'applicationForm/application/ctrl',
        'applicationForm/audit/ctrl',
        'applicationForm/history/ctrl'
        ], function(applicationFormModule) {
	return applicationFormModule.config(function($stateProvider) {
		$stateProvider
		.state('applicationForm', {
			'abstract' : 'true',
			url : '/applicationForm',
			template : '<div ui-view></div>'
		})
		.state('applicationForm.submit', {
			url : '/submit',
			templateUrl : 'app/applicationForm/application/application.html',
			controller : 'ApplicationCtrl as ctrl'
		})
		.state('applicationForm.audit', {
			url : '/audit',
			templateUrl : 'app/applicationForm/audit/audit.html',
			controller : 'ApplicationFormAuditCtrl as ctrl'
		})
		.state('applicationForm.history', {
			url : '/history',
			templateUrl : 'app/applicationForm/history/history.html',
			controller : 'ApplicationFormHistoryCtrl as ctrl'
		})
		;
	});
});