define([ 'crm/module', 'common/context' ], function(crmModule) {
	return crmModule.factory('crmService', [ '$http', 'util', function($http, util) {
		return {
			getRole : function(id) {
				return $http.get('role/' + id);
			},
			getRoles : function() {
				return $http.get('role');
			},
			getAuthorities : function() {
				return $http.get('authority');
			},
			createRole : function(role) {
				return $http.post('role', role);
			},
			updateRole : function(id, role) {
				return $http.put('role/' + id, role);
			},
			grantAuthorities : function(id, authorityNames) {
				return $http.put('role/' + id + '/authorityNames/' + authorityNames);
			},
			deleteRole : function(id) {
				return $http['delete']('role/' + id);
			},
		};
	}]);
});