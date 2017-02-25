define(['cms/module', 'cms/article/service', 'cms/category/service'], function(cmsModule) {
	return cmsModule
	.controller('ArticleCtrl', ['$scope', '$http', '$state', 'articleService', 'categoryService',
	                                function($scope, $http, $state, service, categoryService) {
		var self = this;
		$scope.getAuthentication();
		categoryService.getTypes().success(function(data) {
			self.types = data;
		});
		
		// 用于切换界面，详情状态就是新增和编辑，反之则是列表页面
		self.isDetail = false;
		self.article = {};
		self.queryParam = {
			page : 1,
			query : '',
		};
		
		self.query = function() {
			service.search(self.queryParam.query, self.queryParam.page).success(function(data) {
				self.pager = data;
				console.log(data);
				self.isDetail = false;
			});
		};
		
		self.query();
		
		self.btnClick = function(pageNumber) {
			self.query.page = pageNumber;
			self.query();
		};
		
		self.add = function() {
			self.article = {};
			self.isDetail = true;
		};
		
		self.edit = function(id) {
			service.findArticle(id).success(function(data) {
				console.log(data);
				self.article = data;
				self.isDetail = true;
			});
		};
		
		self.back = function() {
			self.isDetail = false;
		};
		
		self.submit = function() {
			if (self.article.id) {
				service.updateArticle(self.article.id, self.article).success(function(data) {
					self.query();
					self.isDetail = false;
				});
			} else {
				service.saveArticle(self.article).success(function(data) {
					self.query();
					self.isDetail = false;
				});
			}
		};

		self['delete'] = function() {
			if (!self.article.id)
				return;
			if (confirm('确定删除' + self.article.name + '吗？')) {
				service.deleteArticle(self.article.id).success(function(data) {
					self.query();
				});
			}
		};
		
	}])
	;
});