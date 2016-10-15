define([ 'user/module', 'common/context' ], function(userModule) {
	return userModule.factory('authorityService', [ '$http', 'util', function($http, util) {
		return {
			getPagerByAuthories : function(params) {
				return $http.get('authentication/page?' + util.encodeUrlParams(params));
			},
			authorize : function(id, authorities) {
				return $http.put('authentication/authorize/' + id, authorities);
			},
		};
	}]);
});