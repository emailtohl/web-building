define([ 'user/module', 'common/context' ], function(userModule) {
	return userModule.factory('authorityService', [ '$http', 'util', function($http, util) {
		return {
			getPageByRoles : function(params) {
				return $http.get('user/pageByRoles?' + util.encodeUrlParams(params));
			},
			grantRoles : function(id, roles) {
				return $http.put('user/grantRoles/' + id, roles);
			},
		};
	}]);
});