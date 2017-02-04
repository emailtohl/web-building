/**
 * ajax调用前后的拦截器，实现功能有：
 * 1. 日志功能
 * 2. 打开和关闭加载时的屏幕遮罩
 * 3. 出现错误时的统一处理
 * 4. 在ajax请求的头信息中添加CSRF的token
 * 5. 前端的页码是从第1页开始，而后端为了跟Spring data统一，页码从第0页开始，发送请求前和发送请求后进行统一转换
 * 
 * @author HeLei
 * @date 2017.02.04
 */
define([ 'common/module', 'common/service/util' ], function(commonModule) {
	return commonModule
	.factory('LoggingInterceptor', [ '$q', function($q) {
		return {
			request : function(config) {
//				console.log('Request made with ', config);
				return config;
				// If an error, not allowed, or my custom condition,
				// return $q.reject('Not allowed');
			},
			requestError : function(rejection) {
				console.log('Request error due to ', rejection);
				// Continue to ensure that the next promise chain
				// sees an error
				return $q.reject(rejection);
				// Or handled successfully?
				// return someValue
			},
			response : function(response) {
//				console.log('Response from server', response);
				// Return a promise
				return response || $q.when(response);
			},
			responseError : function(rejection) {
				console.log('Error in response ', rejection);
				// Continue to ensure that the next promise chain
				// sees an error
				// Can check auth status code here if need to
				// if (rejection.status === 403) {
				// Show a login dialog
				// return a value to tell controllers it has
				// been handled
				// }
				// Or return a rejection to continue the
				// promise failure chain
				return $q.reject(rejection);
			}
		};
	} ])
	.factory('OverlayInterceptor', [ '$rootScope', '$q', function($rootScope, $q) {
		return {
			request : function(config) {
				$rootScope.overlay = true;
				return config;
			},
			requestError : function(rejection) {
				return $q.reject(rejection);
			},
			response : function(response) {
				$rootScope.overlay = false;
				return response || $q.when(response);
			},
			responseError : function(rejection) {
				$rootScope.overlay = false;
				return $q.reject(rejection);
			}
		};
	} ])
	.factory('ErrorInterceptor', [ '$rootScope', '$q', function($rootScope, $q) {
		function isLoginPage(response) {
			if (!(typeof response.data == 'string'))
				return false;
			// 如果是下载的页面
			if (response.config.url == 'fileUploadServer/loadText')
				return false;
			// 当spring security拦截后，会将默认页面返回，这个页面带有<!DOCTYPE html>，所以根据这个标记进行识别
			if (response.data.indexOf('<!DOCTYPE html>') > -1)
				return true;
			else
				return false;
		}
		function isExpired(response) {
			return (typeof response.data == 'string') && response.data.match(/session.+expired/i);
		}
		return {
			response : function(response) {
				if (isLoginPage(response) || isExpired(response)) {
					location.replace('login');
				}
				return response || $q.when(response);
			},
			responseError : function(rejection) {
				if (rejection.status === 403) {
					location.replace('login');
				} else {
					var i, p, error, data = [];
					if (rejection.data && rejection.data.errors instanceof Array) {
						for (i = 0; i < rejection.data.errors.length; i++) {
							error =  rejection.data.errors[i];
							for (p in error) {
								if (error.hasOwnProperty(p)) {
									data.push(p + ' : ' + error[p]);
								}
							}
						}
					}
					$rootScope.errorModal.open = true;
					$rootScope.errorModal.status = rejection.status;
					$rootScope.errorModal.statusText = rejection.statusText;
					$rootScope.errorModal.data = data.join(', ');
					$rootScope.$apply();
					//alert('status: ' + rejection.status + ' , statusText: ' + rejection.statusText);
				}
				return $q.reject(rejection);
			}
		};
	} ])
	.factory('csrfTokenInterceptor', [ '$q', 'util', function($q, util) {
		return {
			request : function(config) {
				if (!config.headers['X-XSRF-TOKEN']) {
					var token = util.getCookie('XSRF-TOKEN');
					if (token) {
						config.headers['X-XSRF-TOKEN'] = token;
					}
				}
				return config;
			}
		};
	}])
	.factory('pagerInterceptor', [ '$q', function($q) {
		var p = /page=(\d+)/;
		return {
			request : function(config) {
				var url, matches, page;
				url = config.url;
				matches = url.match(p);
				if (matches && matches.length === 2) {
					page = parseInt(matches[1]);
					if (page > 0) {
						page--;
						config.url = url.replace(p, 'page=' + page);
						console.log('request page由 ' + matches[1] + ' 改为 ', page);
					}
				}
//				console.log('Request made with ', config);
				return config;
			},
			response : function(response) {
				if (response.data.pageNumber != null) {
					response.data.pageNumber++;
					console.log('response pageNumber由 ' + (response.data.pageNumber - 1) + ' 改为 ' + response.data.pageNumber);
				}
//				console.log('Response from server', response);
				// Return a promise
				return response || $q.when(response);
			},
		};
	} ])
	.config([ '$httpProvider', function($httpProvider) {
		$httpProvider.interceptors.push('LoggingInterceptor');
		$httpProvider.interceptors.push('OverlayInterceptor');
		$httpProvider.interceptors.push('ErrorInterceptor');
		$httpProvider.interceptors.push('csrfTokenInterceptor');
		$httpProvider.interceptors.push('pagerInterceptor');
	} ]);
});