define([ 'user/module', 'common/context' ], function(userModule) {
	return userModule.factory('roleAllocationService', [ '$http', 'util', function($http, util) {
		return {
			getPageByRoles : function(params) {
				return $http.get('user/pageByRoles?' + util.encodeUrlParams(params));
			},
			grantRoles : function(id, roles) {
				return $http.put('user/grantRoles/' + id, roles);
			},
			getRoles : function() {
				return $http.get('user/role');
			},
		};
	}]);
});