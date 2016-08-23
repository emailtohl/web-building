define(['goods/module', 'uirouter', 'goods/list/goodsListCtrl', 'goods/detail/goodsDetailCtrl'], function(goodsModule) {
	return goodsModule.config(function($stateProvider) {
		$stateProvider
		.state('goods', {
			'abstract' : 'true',
			url : '/goods',
			template : '<div ui-view></div>'
		})
		.state('goods.goodsList', {
			url : '/goodsList',
			templateUrl : 'app/goods/list/goodsList.html',
			controller : 'GoodsListCtrl as ctrl'
		})
		.state('goods.goodsDetail', {
			url : '/goodsDetail',
			templateUrl : 'app/goods/detail/goodsDetail.html',
			controller : 'GoodsDetailCtrl as ctrl'
		});
	});
});