define(['user/module', 'user/manager/service'], function(userModule) {
	return userModule
	.controller('UserDetailCtrl', ['$scope', '$http', '$state', 'userService'
	                         , function($scope, $http, $state, userService) {
		var self = this;
		self.getDetail = function(id) {
			userService.getUserById(id).success(function(data, status, fun, obj) {
				console.log(data);
				self.detail = data;
			})
			.error(function(data, status, fun, obj) {
				console.log(data);
//				location.replace('login');
			});
		};
		self.getDetail($state.params.id);
	}]);
});