define(['role/module', 'role/service'], function(roleModule) {
	return roleModule
	.controller('RoleCtrl', [ '$scope', '$http', '$state', 'roleService'
	                         , function($scope, $http, $state, roleService) {
		var self = this;
		$scope.getAuthentication();
		self.form = {};// 要提交的表单数据
		function getRoles() {
			roleService.getRoles().success(function(data) {
				self.roles = data;
			});
		}
		function getAuthorities() {
			roleService.getAuthorities().success(function(data) {
				self.authorities = data;
			});
		}
		getRoles();
		getAuthorities();
		
		self.modal = {
			open : false,
			title : '角色属性',
			type : '',
			whenConfirm : function() {
				if (self.form.id) {// 有id的是编辑
					roleService.updateRole(self.form.id, self.form).success(function(data) {
						getRoles();
					});
				} else {// 没有id的是新增
					roleService.createRole(self.form).success(function(data) {
						getRoles();
					});
				}
			},
		};
		self.openModal = function(id) {
			if (id) {// 如果是编辑
				roleService.getRole(id).success(function(data) {
					self.form = data;
					var authorityNames = [];
					for (var i = 0; i < data.authorities.length; i++) {
						authorityNames.push(data.authorities[i].name);
					}
					self.form.authorityNames = authorityNames;
				});
			} else {// 否则是新增
				self.form = {
					name : null,
					description : null,
					authorityNames : []
				};
			}
			self.modal.open = true;
		}
		
	}]);
});