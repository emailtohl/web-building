define(['role/module',
        'role/roleCtrl',
        ], function(roleModule) {
	return roleModule.config(function($stateProvider) {
		$stateProvider
		.state('role', {
			url : '/role',
			templateUrl : 'app/role/role.html',
			controller : 'RoleCtrl as ctrl'
		})
		;
	});
});