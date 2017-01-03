define(['user/module', 'user/audit/service'], function(userModule) {
	return userModule
	.controller('UserAuditListCtrl', ['$scope', '$http', '$state', 'userAuditService',
	                                function($scope, $http, $state, userAuditService) {
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
			size : 10,
			email : '',
		};
		function userRevision() {
			userAuditService.userRevision(self.params).success(function(data) {
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
			});
		}
		userRevision();
		self.query = function() {
			userRevision();
		};
		self.btnClick = function(pageNumber) {
			self.params.page = pageNumber;
			userRevision();
		};
		self.reset = function() {
			self.params = {
				page : 1,
				size : 10,
				email : '',
			};
		};
		self.detail = function(id, revision) {
			$state.go('user.audit.detail', {id : id, revision : revision}, {reload : true});
		};
		
	}])
	.controller('UserAuditDetailCtrl', ['$scope', '$http', '$state', 'userAuditService',
	                                function($scope, $http, $state, userAuditService) {
		var self = this;
		self.id = $state.params.id;
		self.revision = $state.params.revision;
		self.dictionary = {
//			'ADMIN' : '系统管理员',
//			'EMPLOYEE' : '职员',
//			'MANAGER' : '经理',
			'USER' : '普通用户',
			'MALE' : '男',
			'FEMALE' : '女',
			'UNSPECIFIED' : '未知',
		};
		function userAtRevision(userId, revision) {
			userAuditService.userAtRevision(userId, revision).success(function(data) {
				self.detail = data;
			});
		}
		userAtRevision(self.id, self.revision);
		
		/**
		 * 在详情中展示字符串，有的值是对象，所以需要处理
		 */
		self.getValue = function(k, v) {
			var result, i, j, temp/*, auth*/;
			switch (k) {
				case 'department':
					result = v.name;
					break;
				case 'subsidiary':
					result = v.country + ' ' + v.province + ' ' + v.city;
					break;
				case 'gender':
					result = self.dictionary[v];
					break;
				case 'roles':
					temp = [];
					for (i = 0; i < v.length; i++) {
						for (j = 0; j < v[i].authorities.length; j++) {
							auth = v[i].authorities[j];
							if (auth instanceof String) {
								temp.push(auth);
							} else {
								temp.push(auth.name);
							}
							result = temp.join('，');
						}
					}
					break;
				default:
					result = v;
				break;
			}
			return result;
		};
	}])
	;
});