/**
 * 这是路由模块，运行前，app各组件已经组装好，现在进行各模块的粘合
 * 其他路由配置已经在各自模块中定义
 */
define([ 'app', 'uirouter' ], function(app) {
	return app
	.config(function($stateProvider, $urlRouterProvider) {
		$urlRouterProvider.otherwise('/dashboard');
	});
});