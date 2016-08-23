define(['auth/module'], function(authModule) {
	return authModule
	.controller('AuthoritiesCtrl', ['$scope', '$http', '$state', 'userService', 'PAGE_BTN_NUM', 'PAGE_SIZE',
	                                function($scope, $http, $state, userService, pageBtnNum, pageSize) {
		var self = this;
		self.pager = {
			pageSize : pageSize
		};
		self.condition = {};
		self.query = function() {
			var user = {
				nickname : self.condition.nickname,
				authority : [self.condition.authority],
				page : self.condition.pageNum - 1,
				size : self.condition.pageSize
			};
			userService.nicknameAndAuthority(user).success(function(data, status, fun, obj) {
				self.pager = data;
			});
		}
		self.query();
		self.authorizeModal = function(id) {
			userService.getDetail(id).success(function(data, status, fun, obj) {
				self.user = data;
				self.user.birthday = null;
				$('#dialog_edit').dialog({
					autoOpen : true,
					modal : true,
					width : 800,
					buttons : {
						"Save" : function() {
							userService.authorize(self.user).success(function(data, status, fun, obj) {
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
		self.reset = function() {
			self.condition.nickname = '';
			self.condition.authority = [];
		};
	}]);
});