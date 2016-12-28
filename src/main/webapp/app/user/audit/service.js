define([ 'user/module', 'common/context' ], function(userModule) {
	return userModule.factory('userAuditService', [ '$http', 'util', function($http, util) {
		return {
			/**
			 * 根据User的email查询某实体所有历史记录
			 */
			userRevision : function(params) {
				var args = util.encodeUrlParams(params);
				return $http.get('audit/userRevision' + (args ? '?' + args : ''));
			},
			/**
			 * 查询User某个修订版下所有的历史记录
			 */
			usersAtRevision : function(params) {
				if (!params.revision) {
					throw new ReferenceError('修订版本号revision为空');
				}
				return $http.get('audit/usersAtRevision?' + util.encodeUrlParams(params));
			},
			/**
			 * 查询User在某个修订版时的历史记录
			 */
			userAtRevision : function(userId, revision) {
				if (!userId || !revision) {
					throw new ReferenceError('用户Id和修订版本号revision都不能为空');
				}
				var params = {
					userId : userId,
					revision : revision
				};
				return $http.get('audit/userAtRevision?' + util.encodeUrlParams(params));
			},
		};
	}]);
});