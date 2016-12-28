define(['user/module', 'user/manager/service'], function(userModule) {
	return userModule
	.controller('UserDetailCtrl', [ '$scope', '$http', '$state', 'userService'
	                         , function($scope, $http, $state, userService) {
		var self = this;
		self.form = {};// 要提交的表单数据
		$scope.getAuthentication();
		self.getDetail = function(id) {
			userService.getUserById(id).success(function(data, status, fun, obj) {
				self.detail = data;
			})
			.error(function(data, status, fun, obj) {
				console.log(data);
//				location.replace('login');
			});
		};
		self.getDetail($state.params.id);
		self.whenDone = function() {
			setTimeout(function() {
				self.getDetail($state.params.id);
				$scope.getAuthentication();
			}, 1000);
		};
		self.dictionary = {
//			'ADMIN' : '系统管理员',
//			'EMPLOYEE' : '职员',
//			'MANAGER' : '经理',
			'USER' : '普通用户',
			'MALE' : '男',
			'FEMALE' : '女',
			'UNSPECIFIED' : '未知',
		};
		/**
		 * 在详情中展示字符串，有的值是对象，所以需要处理
		 */
		self.getValue = function(k, v) {
			var result, i, j, temp, auth;
			switch (k) {
				case 'department':
					result = v.name;
					break;
				case 'subsidiary':
					result = v.country + ' ' + v.province + ' ' + v.city;
					break;
				case 'gender':
					result = self.dictionary[v];
					break;
				case 'roles':
					temp = [];
					for (i = 0; i < v.length; i++) {
						for (j = 0; j < v[i].authorities.length; j++) {
							auth = v[i].authorities[j];
							if (auth instanceof String) {
								temp.push(auth);
							} else {
								temp.push(auth.name);
							}
							result = temp.join('，');
						}
					}
					break;
				default:
					result = v;
				break;
			}
			return result;
		};
		
		self.modal = {
			open : false,
			title : '编辑用户信息',
			type : '',
			whenConfirm : function() {
				if (self.detail.empNum && self.detail.empNum > 0) {
					userService.updateEmployee(self.form).success(function(data) {
//						$state.go('user.detail', { id : self.form.id }, { reload : true });
						self.getDetail(self.form.id);
					});
				} else {
					userService.updateCustomer(self.form).success(function(data) {
//						$state.go('user.detail', { id : self.form.id }, { reload : true });
						self.getDetail(self.form.id);
					});
				}
			},
		};
		self.edit = function() {
			self.form = userService.entity2form(self.detail);
			self.modal.open = true;
		};
		
		self.enableUser = function() {
			var id = self.detail.id;
			if (!id) {
				return;
			}
			userService.enableUser(id).success(function(data) {
				self.getDetail(id);
			});
		};
		self.disableUser = function() {
			var id = self.detail.id;
			if (!id) {
				return;
			}
			userService.disableUser(id).success(function(data) {
				self.getDetail(id);
			});
		};
		$('input[name="icon"]').on('change', function(e) {
			$('#submit-file').attr('disabled', null);
		});
	}]);
});