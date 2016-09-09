define([ 'user/module', 'mine' ], function(userModule) {
	return userModule.factory('authorityService', [ '$http', function($http) {
		return {
			getPagerByAuthories : function(params) {
				return $http.get('user/authoritiesPage?' + mine.encodeUrlParams(params));
			},
			authorize : function(user) {
				return $http.put('user/authorize', user);
			},
		};
	}]);
});