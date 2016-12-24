/**
 * 这是路由模块，运行前，app各组件已经组装好，现在进行各模块的粘合
 * 其他路由配置已经在各自模块中定义
 */
define([ 'app' ], function(webBuilding) {
	return webBuilding
	.config(function($stateProvider, $urlRouterProvider) {
		$urlRouterProvider.otherwise('/dashboard');
	})
	// withCredentials是一个设置在底层 XMLHttpRequest(AJAX)对象的标记，可以跨站访问时携带cookie
	.config(function ($httpProvider) {
		$httpProvider.defaults.withCredentials = true;
	})
	;
});