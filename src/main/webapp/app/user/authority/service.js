define([ 'user/module', 'mine' ], function(userModule) {
	return userModule.factory('authorityService', [ '$http', function($http) {
		userService = {
			getPager : function(user) {
				return $http.get('user/pager?' + mine.encodeFormData(user));
			},
			nicknameAndAuthority : function(user) {
				return $http.get('user/nicknameAndAuthority?' + mine.encodeFormData(user));
			},
			getDetail : function(id) {
				return $http.get('user/' + id);
			},
			saveUser : function(user) {
				return $http.put('user', user);
			},
			authorize : function(user) {
				return $http.put('user/authorize', user);
			},
			'delete' : function(id) {
				return $http['delete']('user/' + id);
			},
		};
		return userService;
	}]);
});