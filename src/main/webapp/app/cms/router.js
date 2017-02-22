define(['cms/module',
        'cms/resource/ctrl',
        'cms/category/ctrl',
        'cms/article/ctrl',
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
		.state('cms.category', {
			url : '/category',
			templateUrl : 'app/cms/category/template.html',
			controller : 'CategoryCtrl as ctrl'
		})
		.state('cms.article', {
			url : '/article',
			templateUrl : 'app/cms/article/template.html',
			controller : 'ArticleCtrl as ctrl'
		})
		;
	});
});