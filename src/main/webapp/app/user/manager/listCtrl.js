define(['user/module', 'user/manager/service'], function(userModule) {
	return userModule
	.controller('UserListCtrl', [ '$scope', '$http', '$state', 'userService'
	                         , function($scope, $http, $state, userService) {
		var self = this;
		$scope.getAuthentication();
		self.params = {
			page : 1,
			size : 20,
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
				size : 20,
				enabled : '',
			};
		};
		self.btnClick = function(pageNumber) {
			self.params.page = pageNumber;
			self.query();
		};
		
		self['delete'] = function(id) {
			if (prompt('确认删除吗？') != null) {
				userService['delete'](id).success(function(data) {
					self.query();
				});
			}
		};
		// 查询
		self.query();
		
		self.form = {};
		// 下面是新增职员功能
		self.modal = {
			open : false,
			title : '新增职员',
			whenConfirm : function() {
				userService.addUser(self.form).success(function(data) {
					console.log('确认')
					
				});
			},
			type : '',
		};
		self.openModal = function() {
			self.modal.open = true;
		};
		
	}]);
});