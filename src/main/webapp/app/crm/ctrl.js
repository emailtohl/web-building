define(['crm/module', 'crm/service'], function(crmModule) {
	return crmModule
	.controller('CrmCtrl', [ '$scope', '$http', '$state', 'crmService'
	                         , function($scope, $http, $state, crmService) {
		const init = JSON.stringify({
			page : 1,
			name : '',
			title : '',
			affiliation : ''
		});
		var self = this;
		$scope.getAuthentication();
		self.form = JSON.parse(init);
		self.customer = {};
		function query() {
			crmService.query(self.form).success(function(data) {
				console.log(data);
				self.pager = data;
			});
		}
		query();
		self.query = function() {
			query();
		};
		self.btnClick = function(pageNumber) {
			self.form.page = pageNumber;
			query();
		};
		self.reset = function() {
			self.form = JSON.parse(init);
		};
		self.openModal = function(id) {
			if (id) {// 有id表示编辑
				crmService.get(id).success(function(data) {
					console.log(data);
					self.customer = data;
				});
			} else {// 否则就是新增
				self.customer = {};
			}
			self.modal.open = true;
		};
		self.modal = {
			open : false,
			title : '编辑用户信息',
			type : '',
			whenConfirm : function() {
				if (self.detail.empNum && self.detail.empNum > 0) {
					userService.updateEmployee(self.form).success(function(data) {
//							$state.go('user.detail', { id : self.form.id }, { reload : true });
						self.getDetail(self.form.id);
					});
				} else {
					userService.updateCustomer(self.form).success(function(data) {
//							$state.go('user.detail', { id : self.form.id }, { reload : true });
						self.getDetail(self.form.id);
					});
				}
			},
		};
	}]);
});