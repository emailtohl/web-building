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
		
		function userRevision() {
			auditService.userRevision(self.params).success(function(data) {
				self.tuple = data;
				console.log(data);
			});
		}
		function userAtRevision(userId, revision) {
			auditService.userAtRevision(userId, revision).success(function(data) {
				self.user = data;
				console.log(data);
			});
		}
		userRevision();
		self.userAtRevision = function(userId, revision) {
			userAtRevision(userId, revision);
		};
		
	}]);
});