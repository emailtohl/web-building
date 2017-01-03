define(['user/module', 'user/manager/service'], function(userModule) {
	return userModule
	.controller('UserListCtrl', [ '$scope', '$http', '$state', 'userService'
	                         , function($scope, $http, $state, userService) {
		var self = this;
		$scope.getAuthentication();
		self.params = {
			page : 1,
			size : 10,
		};
		self.query = function() {
			userService.getUserPager(self.params).success(function(data) {
				console.log(data);
				self.pager = data;
			});
		};
		self.reset = function() {
			self.params = {
				page : 1,
				size : 10,
				enabled : '',
			};
		};
		self.btnClick = function(pageNumber) {
			self.params.page = pageNumber;
			self.query();
		};
		
		self['delete'] = function(id) {
			if (confirm('确认删除吗？')) {
				userService['delete'](id).success(function(data) {
					self.query();
				});
			}
		};
		// 查询
		self.query();
		
	}]);
});