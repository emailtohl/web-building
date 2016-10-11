define([ 'forum/module' ], function(forumModule) {
	return forumModule.factory('forumAddService', [ '$http', function($http) {
		return {
			add : function(forumPost) {
				return $http.post('forum', forumPost);
			},
			search : function(query) {
				return $http.get('forum/search?query=' + query);
			},
		};
	}]);
});