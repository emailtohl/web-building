define(['user/module',
        'user/manager/listCtrl',
        'user/manager/detailCtrl',
        'user/authority/authorityCtrl'
        ], function(userModule) {
	return userModule.config(function($stateProvider) {
		$stateProvider
		.state('user', {
			'abstract' : 'true',
			url : '/user',
			template : '<div ui-view></div>'
		})
		.state('user.list', {
			url : '/list',
			templateUrl : 'app/user/manager/list.html',
			controller : 'UserListCtrl as ctrl'
		})
		.state('user.detail', {
			url : '/detail',
			templateUrl : 'app/user/manager/detail.html',
			controller : 'UserDetailCtrl as ctrl'
		})
		.state('user.authority', {
			url : '/authority',
			templateUrl : 'app/user/authority/authority.html',
			controller : 'AuthorityCtrl as ctrl'
		});
	});
});