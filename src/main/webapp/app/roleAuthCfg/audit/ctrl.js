define(['roleAuthCfg/module', 'roleAuthCfg/service'], function(roleAuthCfgModule) {
	return roleAuthCfgModule
	.controller('RoleAuthAuditListCtrl', ['$scope', '$http', '$state', 'roleAuthCfgService',
	                                function($scope, $http, $state, service) {
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
			size : 5,
			name : '',
		};
		function roleRevision() {
			service.roleRevision(self.params).success(function(data) {
				self.pager = data;
			});
		}
		roleRevision();
		self.query = function() {
			roleRevision();
		};
		self.btnClick = function(pageNumber) {
			self.params.page = pageNumber;
			roleRevision();
		};
		self.reset = function() {
			self.params = {
				page : 1,
				size : 10,
				name : '',
			};
		};
		self.detail = function(id, revision) {
			$state.go('roleAuthCfgAudit.detail', {id : id, revision : revision}, {reload : true});
		};
		
	}])
	.controller('RoleAuthAuditDetailCtrl', ['$scope', '$http', '$state', 'roleAuthCfgService',
	                                function($scope, $http, $state, service) {
		var self = this;
		self.id = $state.params.id;
		self.revision = $state.params.revision;
		function roleAtRevision(roleId, revision) {
			service.roleAtRevision(roleId, revision).success(function(data) {
				self.detail = data;
			});
		}
		roleAtRevision(self.id, self.revision);
		
		/**
		 * 在详情中展示字符串，有的值是对象，所以需要处理
		 */
		self.getValue = function(k, v) {
			var result, i, j, temp/*, auth*/;
			return result;
		};
	}])
	;
});