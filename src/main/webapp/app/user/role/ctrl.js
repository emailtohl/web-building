define(['user/module', 'user/role/service'], function(userModule) {
	return userModule
	.controller('RoleAllocationCtrl', ['$scope', '$http', '$state', 'roleAllocationService',
	                                function($scope, $http, $state, roleAllocationService) {
		var self = this;
		$scope.getAuthentication();
		self.params = {
			page : 1,
			size : 10,
			email : '',
			roles : []
		};
		roleAllocationService.getRoles().success(function(data) {
			self.roles = data;
		});
		self.query = function() {
			roleAllocationService.getPageByRoles(self.params).success(function(data, status, fun, obj) {
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
						user.roleNames.push(user.roles[i].name);
					}
				}
			});
		}
		self.query();
		self.btnClick = function(pageNumber) {
			self.params.page = pageNumber;
			self.query();
		};
		self.reset = function() {
			self.params.email = '';
			self.params.roles.length = 0;
		};
		
		self.modal = {
			success : {open : false},
			fail : {open : false},
		};
		self.onChange = function(id, value) {
			roleAllocationService.grantRoles(id, value).then(function(respose) {
				self.modal.success.open = true;
			}, function(respose) {
				self.query();
				self.modal.fail.open = true;
			});
		};
	}]);
});