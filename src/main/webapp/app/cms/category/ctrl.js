define(['cms/module', 'cms/category/service'], function(cmsModule) {
	return cmsModule
	.controller('CategoryCtrl', ['$scope', '$http', '$state', 'categoryService',
	                                function($scope, $http, $state, service) {
		var self = this;
		$scope.getAuthentication();
		// 用于切换界面，详情状态就是新增和编辑，反之则是列表页面
		self.isDetail = false;
		self.form = {};
		
		getTypes();
		
		self.add = function() {
			self.form = {};
			self.isDetail = true;
		};
		
		self.edit = function(id) {
			service.findTypeById(id).success(function(data) {
				console.log(data);
				self.form = data;
				if (self.form.parent) {
					self.form.parent = self.form.parent.name;
				}
				self.isDetail = true;
			});
		};
		
		self.back = function() {
			self.isDetail = false;
		};
		
		self.submit = function() {
			if (self.form.id) {
				service.updateType(self.form.id, self.form).success(function(data) {
					getTypes();
					self.isDetail = false;
				});
			} else {
				service.saveType(self.form).success(function(data) {
					getTypes();
					self.isDetail = false;
				});
			}
		};
		
		function getTypes() {
			service.getTypes().success(function(data) {
				self.typeList = data;
				console.log(data);
			});
		}
		
	}])
	;
});