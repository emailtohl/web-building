define(['user/module', 'user/manager/service'], function(userModule) {
	return userModule
	.controller('UserDetailCtrl', ['$scope', '$http', '$state', 'userService'
	                         , function($scope, $http, $state, userService) {
		var self = this;
		self.getDetail = function(id) {
			$http.get('user/detail/' + id).success(function(data, status, fun, obj) {
				console.log(data);
/*				if (typeof data == 'string' && data.startsWith('<!DOCTYPE html>')) {
					location.replace('login');
				}*/
			})
			.error(function(data, status, fun, obj) {
				console.log(data);
//				location.replace('login');
			});
		};
	}]);
});