define(['auth/module', 'uirouter', 'auth/user/userCtrl', 'auth/authorities/authoritiesCtrl'], function(authModule) {
	return authModule.config(function($stateProvider) {
		$stateProvider
		.state('auth', {
			'abstract' : 'true',
			url : '/auth',
			template : '<div ui-view></div>'
		})
		.state('auth.user', {
			url : '/user',
			templateUrl : 'app/auth/user/user.html',
			controller : 'UserCtrl as ctrl'
		})
		.state('auth.authorities', {
			url : '/authorities',
			templateUrl : 'app/auth/authorities/authorities.html',
			controller : 'AuthoritiesCtrl as ctrl'
		});
	});
});