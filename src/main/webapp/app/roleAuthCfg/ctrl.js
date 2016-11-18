define(['roleAuthCfg/module', 'roleAuthCfg/service'], function(roleAuthCfgModule) {
	return roleAuthCfgModule
	.controller('RoleAuthCfgCtrl', [ '$scope', '$http', '$state', 'roleAuthCfgService'
	                         , function($scope, $http, $state, roleAuthCfgService) {
		var self = this;
		$scope.getAuthentication();
		self.form = {};// 要提交的表单数据
		function getRoles() {
			roleAuthCfgService.getRoles().success(function(data) {
				self.roles = data;
			});
		}
		function getAuthorities() {
			roleAuthCfgService.getAuthorities().success(function(data) {
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
					roleAuthCfgService.updateRole(self.form.id, self.form).success(function(data) {
						getRoles();
					});
				} else {// 没有id的是新增
					roleAuthCfgService.createRole(self.form).success(function(data) {
						getRoles();
					});
				}
			},
		};
		self.openModal = function(id) {
			if (id) {// 如果是编辑
				roleAuthCfgService.getRole(id).success(function(data) {
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
		
		self['delete'] = function(id, name) {
			if (confirm('确定删除“' + name + '”角色吗？')) {
				roleAuthCfgService.deleteRole(id).success(function() {
					getRoles();
				});
			}
		};
	}]);
});