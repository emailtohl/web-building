define(['forum/module',
        'forum/add/forumAddCtrl',
        'forum/search/forumSearchCtrl',
        ], function(forumModule) {
	return forumModule.config(function($stateProvider) {
		$stateProvider
		.state('forum', {
			'abstract' : 'true',
			url : '/forum',
			template : '<div ui-view></div>'
		})
		.state('forum.add', {
			url : '/add',
			templateUrl : 'app/forum/add/add.html',
			controller : 'ForumAddCtrl as ctrl'
		})
		.state('forum.search', {
			url : '/search/{query}',
			templateUrl : 'app/forum/search/list.html',
			controller : 'ForumSearchCtrl as ctrl'
		})
		;
	});
});