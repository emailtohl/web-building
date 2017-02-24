define(['cms/module', 'cms/category/service'], function(cmsModule) {
	return cmsModule
	// 选择框中，过滤掉自身：ng-repeat="x in ctrl.typeList | excludeSelf:ctrl.form.id"
	.filter('excludeSelf', function() {
		return function(arr, selfId) {
			var i, newArr;
			if (!(arr instanceof Array) || !angular.isNumber(selfId))
				return arr;
			newArr = [];
			for (var i = 0; i < arr.length; i++) {
				if (arr[i].id != selfId)
					newArr.push(arr[i]);
			}
			return newArr;
		}
	})
	.controller('CategoryCtrl', ['$scope', '$http', '$state', 'categoryService',
	                                function($scope, $http, $state, service) {
		var self = this;
		$scope.getAuthentication();
		// 用于切换界面，详情状态就是新增和编辑，反之则是列表页面
		self.isDetail = false;
		self.form = {};
		self.queryParam = {
			page : 1,
			name : '',
		};
		
		self.query = function() {
			service.getTypes(self.queryParam.name, self.queryParam.page).success(function(data) {
				self.pager = data;
				console.log(data);
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
					self.query();
					self.isDetail = false;
				});
			} else {
				service.saveType(self.form).success(function(data) {
					self.query();
					self.isDetail = false;
				});
			}
		};

		
	}])
	;
});