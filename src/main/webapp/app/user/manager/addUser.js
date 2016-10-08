define(['user/module', 'user/manager/service'], function(userModule) {
	return userModule
	.controller('AddUserCtrl', [ '$scope', '$http', '$state', 'userService'
	                         , function($scope, $http, $state, userService) {
		var self = this;
		$scope.getAuthentication();
		
		self.form = {};
		
		self.submit = function() {
			userService.addUser(self.form).success(function(data) {
				console.log('确认')
				$state.go('user.list', {}, { reload : true });
			});
		};
		
	}]);
});