define([ 'user/module', 'mine' ], function(userModule) {
	return userModule.factory('authorityService', [ '$http', function($http) {
		return {
			getPagerByAuthories : function(user) {
				return $http.get('user/authoritiesPage?' + mine.encodeUrlParams(user));
			},
			authorize : function(user) {
				return $http.put('user/authorize', user);
			},
		};
	}]);
});