define(['user/module'], function(userModule) {
	return userModule
	.controller('AuthorityCtrl', ['$scope', '$http', '$state', 'authorityService',
	                                function($scope, $http, $state, authorityService) {
		var self = this;
		self.params = {
			page : 1,
			pageSize : 20,
			email : '',
			authorities : []
		};
		self.query = function() {
			authorityService.getPagerByAuthories(self.params).success(function(data, status, fun, obj) {
				self.pager = data;
				console.log(data);
			});
		}
		self.query();
		self.btnClick = function(pageNumber) {
			self.params.page = pageNumber;
			self.query();
		};
		self.reset = function() {
			self.params.email = '';
			self.params.authorities.length = 0;
		};
		
		self.onChange = function(id, value) {
			authorityService.authorize(id, value);
		};
	}]);
});