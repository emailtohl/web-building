define(['role/module', 'role/service'], function(roleModule) {
	return roleModule
	.controller('RoleCtrl', [ '$scope', '$http', '$state', 'roleService'
	                         , function($scope, $http, $state, roleService) {
		var self = this;
		self.form = {};// 要提交的表单数据
		$scope.getAuthentication();
		
		
	}]);
});