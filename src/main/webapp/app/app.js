/**
 * 这是主要的app模块，运行前Angular的各个module已组装好，本模块仅仅将各module组合起来
 */
define([ 'angular', 'uirouter', 'common/context', 'test/context', 'user/context', 'dashboard/context' ],
	function(angular) {
		return angular.module('appdemo', [ 'ui.router', 'commonModule', 'testModule', 'userModule', 'dashboardModule' ])
		.run([ '$rootScope', '$state', '$stateParams', '$http', function($rootScope, $state, $stateParams, $http) {
			// 让页面能同步状态，显示出该状态应有的效果，例如某菜单被激活的样式
			$rootScope.$state = $state;
			$rootScope.$stateParams = $stateParams;
			// 初始化模态框，遮罩
			$rootScope.modal = {
				open : false,
				type : 'Default',
				msg : 'hello world',
				close : function() {
					this.open = false;
				},
				save : function(params) {
					console.log(params);
					this.open = false;
				}
			};
			// 获取当前用户的认证信息，页面可以直接通过{{authentication.username}}获取用户名
			$rootScope.getAuthentication = function(callback) {
				$http.get('authentication').success(function(data) {
					console.log('authentication:')
					console.log(data);
					$rootScope.authentication = data;
					if (callback) {
						callback(data);
					}
				});
			};
			// 判断是否有此权限
			$rootScope.hasAuthority = function(authority) {
				var flag = false, authorities, i;
				authorities = $rootScope.authentication
						&& $rootScope.authentication.principal
						&& $rootScope.authentication.principal.authorities;
				if (authorities) {
					for (i = 0; i < authorities.length; i++) {
						if (authorities[i].role === authority) {
							flag = true;
							break;
						}
					}
				}
				return flag;
			};
		} ]);
	}
);