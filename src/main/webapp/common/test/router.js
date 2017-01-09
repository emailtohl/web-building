define(['test/module', 'uirouter', 'test/Controller' ], function(testModule) {
	return testModule.config(function($stateProvider) {
		$stateProvider
		.state('test', {
			abstract : 'true',
			url : '/test',
			template : '<div ui-view></div>'
		})
		.state('test.datetimepicker', {
			url : '/testDatetimepicker',
			templateUrl : 'common/test/test-datetimepicker.html',
			controller : 'TestDatetimepickerCtrl as ctrl'
		})
		.state('test.select2', {
			url : '/testSelect2',
			templateUrl : 'common/test/test-select2.html',
			controller : 'TestSelect2Ctrl as ctrl'
		})
		.state('test.fileupload', {
			url : '/fileupload',
			templateUrl : 'common/test/test-fileupload.html',
			controller : 'TestFileuploadCtrl as ctrl'
		})
		.state('test.pager', {
			url : '/pager',
			templateUrl : 'common/test/test-pager.html',
			controller : 'TestPagerCtrl as ctrl'
		})
		.state('test.modal', {
			url : '/modal',
			templateUrl : 'common/test/test-modal.html',
			controller : 'TestModalCtrl as ctrl'
		})
		.state('test.ztree', {
			url : '/ztee',
			templateUrl : 'common/test/test-ztree.html',
			controller : 'TestZtreeCtrl as ctrl'
		})
		.state('test.pattern', {
			url : '/pattern',
			templateUrl : 'common/test/test-pattern.html',
			controller : 'TestPatternCtrl as ctrl'
		})
		.state('test.cluster', {
			url : '/cluster',
			templateUrl : 'common/test/test-cluster.html',
			controller : 'TestClusterCtrl as ctrl'
		})
		;
	});
});