define(['angular', 'goods/module'], function(angular) {
	return angular.module('goodsModule')
	.controller('GoodsListCtrl', ['$scope', '$http', '$state', function($scope, $http, $state) {
		var self = this;
		self.date = '999-999';
		self.test = {test1:1};
		self.testList = [{a:1,b:2}, {a:1,b:2}, {a:1,b:2}];
		
		self.to = '2016-02-05';
		self.from = '2016-01-16';
		
	}]);
});