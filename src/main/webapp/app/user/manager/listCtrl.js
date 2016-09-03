define(['user/module', 'user/manager/service'], function(userModule) {
	return userModule
	.controller('UserListCtrl', ['$scope', '$http', '$state', 'userService', 'PAGE_BTN_NUM', 'PAGE_SIZE'
	                         , function($scope, $http, $state, userService, pageBtnNum, pageSize) {
		var self = this;
		self.pager = {
			pageSize : pageSize
		};
		self.condition = {};
		self.query = function() {
			var user = {
				name : self.condition.name,
				nickname : self.condition.nickname,
				page : self.condition.pageNum - 1,
				size : self.condition.pageSize
			};
			userService.getPager(user).success(function(data, status, fun, obj) {
				self.pager = data;
			});
		}
		self.query();
		self.openEditDialog = function(id) {
			userService.getDetail(id).success(function(data, status, fun, obj) {
				self.user = data;
				self.user.birthday = null;
				$('#dialog_edit').dialog({
					autoOpen : true,
					modal : true,
					width : 800,
					buttons : {
						"Save" : function() {
							userService.saveUser(self.user).success(function(data, status, fun, obj) {
								self.query();
							});
							$(this).dialog("close");
						},
						"Cancel" : function() {
							$(this).dialog("close");
						}
					}
				});
			});
		};
		self['delete'] = function(id) {
			userService['delete'](id).success(function(data, status, fun, obj) {
				self.query();
			});
		};
		self.reset = function() {
			self.condition.name = '';
			self.condition.nickname = '';
		};
	}]);
});