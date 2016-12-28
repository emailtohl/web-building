define([ 'user/module', 'common/context' ], function(userModule) {
	return userModule.factory('auditService', [ '$http', 'util', function($http, util) {
		return {
			/**
			 * 获取Pager<Tuple<User>>：根据User的email查询某实体所有历史记录
			 */
			userRevision : function(email) {
				return $http.get('audited/userRevision' + (email ? '?email=' + email : ''));
			},
			/**
			 * 获取Pager<User>：查询User某个修订版下所有的历史记录
			 */
			usersAtRevision : function(revision, email) {
				if (!revision) {
					throw new ReferenceError('修订版本号revision为空');
				}
				var params = {
					revision : revision,
					email : email
				};
				return $http.get('audited/usersAtRevision?' + util.encodeUrlParams(params));
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
					revision : revision,
				};
				return $http.get('audited/userAtRevision?' + util.encodeUrlParams(params));
			},
		};
	}]);
});