/**
 * 这是主要的app模块，运行前Angular的各个module已组装好，本模块仅仅将各module组合起来
 */
define([ 'angular', 'uirouter', 'angular-animate', 'angular-cookies', 'common/context', 'test/context', 'user/context', 'roleAuthCfg/context', 'dashboard/context', 'forum/context', 'applicationForm/context', 'crm/context', 'cms/context', 'encryption/context' ],
	function(angular) {
		var webBuilding = angular.module('webBuilding', [ 'ui.router', 'ngAnimate', 'commonModule', 'testModule', 'userModule', 'roleAuthCfgModule', 'dashboardModule', 'forumModule', 'applicationFormModule', 'crmModule', 'cmsModule', 'encryptionModule' ])
		.run([ '$rootScope', '$state', '$stateParams', '$http', function($rootScope, $state, $stateParams, $http) {
			// 让页面能同步状态，显示出该状态应有的效果，例如某菜单被激活的样式
			$rootScope.$state = $state;
			$rootScope.$stateParams = $stateParams;
			// 执行失败的提示框
			$rootScope.errorModal = {
				open : false,
				title : '失败',
				type : 'danger',
				whenConfirm : function() {
					$rootScope.errorModal = false;
				},
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
						if (authorities[i] == authority || authorities[i].role === authority) {
							flag = true;
							break;
						}
					}
				}
				return flag;
			};
			// 判断是否登录
			$rootScope.isAuthenticated = function() {
				return $rootScope.authentication
				&& $rootScope.authentication.principal
				&& $rootScope.authentication.principal.username;
			};
			
			// 注销
			$rootScope.logout = function() {
				$http.post('logout').success(function(data) {
					location.replace('login');
				});
			};
			
			// 进入全文搜索
			$('form[name="fulltextsearch"]').on('submit', function(e) {
				e.preventDefault();
				$state.go('forum.search', {query : $(this).find('input').val()}, { reload : true});
			});
			
			// 获取图片信息
			$rootScope.getIconSrc = function() {
				var iconSrc = $rootScope.authentication && $rootScope.authentication.iconSrc;
				if (!iconSrc)
					iconSrc = $rootScope.authentication && $rootScope.authentication.principal && $rootScope.authentication.principal.iconSrc;
				if (!iconSrc)
					iconSrc = 'lib/AdminLTE/img/user2-160x160.jpg';
				return iconSrc;
			}
			
			
		} ]);
		
		// 对class="pop"的元素定义动画
		webBuilding.animation(".pop", [ "$animateCss", function($animateCss) {
			return {
				enter : function(element) {
					return $animateCss(element, {
						from : {
							opacity : 0
						},
						to : {
							opacity : 1
						},
						duration : 0.8
					});
				},
				leave : function(element) {
					return $animateCss(element, {
						from : {
							opacity : 1
						},
						to : {
							opacity : 0
						},
						duration : 0.8
					});
				}
			}
		} ]);
		
		return webBuilding;
	}
);