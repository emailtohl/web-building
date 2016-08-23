define(['dashboard/module', 'uirouter', 'dashboard/dashboardCtrl'], function(dashboardModule) {
	return dashboardModule.config(function($stateProvider) {
		$stateProvider
		.state('dashboard', {
			url : '/dashboard',
			templateUrl : 'app/dashboard/dashboard.html',
			controller : 'DashboardCtrl as ctrl'
		});
	});
});