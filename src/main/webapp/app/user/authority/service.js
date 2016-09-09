define([ 'user/module', 'mine' ], function(userModule) {
	return userModule.factory('authorityService', [ '$http', function($http) {
		return {
			getPagerByAuthories : function(params) {
				return $http.get('authentication/page?' + mine.encodeUrlParams(params));
			},
			authorize : function(id, authorities) {
				return $http.put('authentication/authorize/' + id, authorities);
			},
		};
	}]);
});