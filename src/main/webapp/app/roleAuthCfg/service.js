define([ 'roleAuthCfg/module', 'common/context' ], function(roleAuthCfgModule) {
	return roleAuthCfgModule.factory('roleAuthCfgService', [ '$http', 'util', function($http, util) {
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
			/**
			 * 获取Pager<Tuple<Role>>：根据Role的名字查询某实体所有历史记录
			 */
			roleRevision : function(params) {
				var args = util.encodeUrlParams(params);
				return $http.get('audited/roleRevision' + (args ? '?' + args : ''));
			},
			/**
			 * 获取Pager<Role>：查询Role在某个修订版时的历史记录
			 */
			rolesAtRevision : function(params) {
				if (!params.revision) {
					throw new ReferenceError('修订版本号revision为空');
				}
				return $http.get('audited/rolesAtRevision?' + util.encodeUrlParams(params));
			},
			/**
			 * 查询Role在某个修订版时的历史记录
			 */
			roleAtRevision : function(roleId, revision) {
				if (!roleId || !revision) {
					throw new ReferenceError('角色Id和修订版本号revision都不能为空');
				}
				var params = {
					roleId : roleId,
					revision : revision
				};
				return $http.get('audited/roleAtRevision?' + util.encodeUrlParams(params));
			},
		};
	}]);
});