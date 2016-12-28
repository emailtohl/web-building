define(['roleAuthCfg/module',
        'roleAuthCfg/ctrl',
        'roleAuthCfg/audit/ctrl',
        ], function(roleAuthCfgModule) {
	return roleAuthCfgModule.config(function($stateProvider) {
		$stateProvider
		.state('roleAuthCfg', {
			url : '/roleAuthCfg',
			templateUrl : 'app/roleAuthCfg/roleAuthCfg.html',
			controller : 'RoleAuthCfgCtrl as ctrl'
		})
		.state('roleAuthCfgAudit', {
			'abstract' : 'true',
			url : '/audit',
			template : '<div ui-view></div>',
		})
		.state('roleAuthCfgAudit.list', {
			url : '/list',
			templateUrl : 'app/roleAuthCfg/audit/list.html',
			controller : 'RoleAuthAuditListCtrl as ctrl'
		})
		.state('roleAuthCfgAudit.detail', {
			url : '/detail/id/{id}/revision/{revision}',
			templateUrl : 'app/roleAuthCfg/audit/detail.html',
			controller : 'RoleAuthAuditDetailCtrl as ctrl'
		})
		;
	});
});