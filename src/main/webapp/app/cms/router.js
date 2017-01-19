define(['cms/module',
        'cms/resource/ctrl',
        'cms/content/ctrl',
        ], function(cmsModule) {
	return cmsModule.config(function($stateProvider) {
		$stateProvider
		.state('cms', {
			'abstract' : 'true',
			url : '/cms',
			template : '<div ui-view></div>'
		})
		.state('cms.resource', {
			url : '/resource',
			templateUrl : 'app/cms/resource/template.html',
			controller : 'ResourceCtrl as ctrl'
		})
		.state('cms.content', {
			url : '/content',
			templateUrl : 'app/cms/content/template.html',
			controller : 'ContentCtrl as ctrl'
		})
		;
	});
});