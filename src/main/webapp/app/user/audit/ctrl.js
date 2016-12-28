define(['user/module', 'user/audit/service'], function(userModule) {
	return userModule
	.controller('UserAuditListCtrl', ['$scope', '$http', '$state', 'auditService',
	                                function($scope, $http, $state, auditService) {
		var self = this;
		self.roleMap = {
			admin : '管理员',
			manager : '经理',
			employee : '雇员',
			user : '用户'
		};
		self.typeMap = {
			ADD : '新增',
			MOD : '修改',
			DEL : '删除'
		};
		$scope.getAuthentication();
		self.params = {
			page : 1,
			pageSize : 20,
			email : '',
		};
		function userRevision() {
			auditService.userRevision(self.params).success(function(data) {
				self.pager = data;
				for (var i = 0; i < self.pager.content.length; i++) {
					bindRoleNames(self.pager.content[i]);
				}
				/**
				 * 用户的角色是一个对象，为了在页面显示出来，所以再为用户模型绑上字符串的角色数组
				 */
				function bindRoleNames(user) {
					user.roleNames = [];
					for (var i = 0; i < user.roles.length; i++) {
						user.roleNames.push(self.roleMap[user.roles[i].name]);
					}
				}
				console.log(data);
			});
		}
		userRevision();
		self.query = function() {
			userRevision();
		};
		self.reset = function() {
			self.params = {
				page : 1,
				pageSize : 20,
				email : '',
			};
		};
		self.detail = function(id, revision) {
			$state.go('user.audit.detail', {id : id, revision : revision}, {reload : true})
		};
		
	}])
	.controller('UserAuditDetailCtrl', ['$scope', '$http', '$state', 'auditService',
	                                function($scope, $http, $state, auditService) {
		var self = this;
		self.id = $state.params.id;
		self.revision = $state.params.revision;
		function userAtRevision(userId, revision) {
			auditService.userAtRevision(userId, revision).success(function(data) {
				self.user = data;
				console.log(data);
			});
		}
		userAtRevision(self.id, self.revision);
	}])
	;
});