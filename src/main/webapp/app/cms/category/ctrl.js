define(['cms/module', 'cms/category/service'], function(cmsModule) {
	return cmsModule.controller('CategoryCtrl', ['$scope', '$http', '$state', 'categoryService',
	                                function($scope, $http, $state, service) {
		var self = this;
		$scope.getAuthentication();
		getTypes();
		// 用于切换界面，详情状态就是新增和编辑，反之则是列表页面
		self.isDetail = false;
		self.form = {};
		self.queryParam = {
			page : 1,
			name : '',
		};
		
		self.query = function() {
			service.getTypePager(self.queryParam.name, self.queryParam.page).success(function(data) {
				self.pager = data;
				self.isDetail = false;
			});
		};
		
		self.query();
		
		self.btnClick = function(pageNumber) {
			self.query.page = pageNumber;
			self.query();
		};
		
		self.add = function() {
			self.form = {};
			self.isDetail = true;
		};
		
		self.edit = function(id) {
			service.findTypeById(id).success(function(data) {
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
					self.query();
					getTypes();
					self.isDetail = false;
				});
			} else {
				service.saveType(self.form).success(function(data) {
					self.query();
					getTypes();
					self.isDetail = false;
				});
			}
		};

		self['delete'] = function() {
			if (!self.form.id)
				return;
			if (confirm('确定删除' + self.form.name + '吗？')) {
				service.deleteType(self.form.id).success(function(data) {
					self.query();
					getTypes();
				});
			}
		};
		
		function getTypes() {
			service.getTypes().success(function(data) {
				self.types = data;
			});
		}
	}])
	;
});