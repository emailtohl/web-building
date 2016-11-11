define(['roleAuthCfg/module',
        'roleAuthCfg/ctrl',
        ], function(roleAuthCfgModule) {
	return roleAuthCfgModule.config(function($stateProvider) {
		$stateProvider
		.state('roleAuthCfg', {
			url : '/roleAuthCfg',
			templateUrl : 'app/roleAuthCfg/roleAuthCfg.html',
			controller : 'RoleAuthCfgCtrl as ctrl'
		})
		;
	});
});