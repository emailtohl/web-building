define(['user/module', 'user/audit/service'], function(userModule) {
	return userModule
	.controller('UserAuditCtrl', ['$scope', '$http', '$state', 'auditService',
	                                function($scope, $http, $state, auditService) {
		var self = this;
		$scope.getAuthentication();
		self.params = {
			page : 1,
			pageSize : 20,
			email : '',
		};
	}])
	;
});