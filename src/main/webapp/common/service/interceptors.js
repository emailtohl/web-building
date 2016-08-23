/**
 * ajax调用前后的拦截器，实现功能有：
 * 1. 日志功能
 * 2. 调用错误的统一处理
 * 3. 在ajax请求的头信息中添加CSRF的token
 * author helei
 */
define([ 'common/module' ], function(commonModule) {
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
	.factory('ErrorInterceptor', [ '$q', function($q) {
		return {
			response : function(response) {
				if (typeof response.data == 'string' && response.data.indexOf('<!DOCTYPE html>') > -1) {
					location.replace('login');
				}
				return response || $q.when(response);
			},
			responseError : function(rejection) {
				if (rejection.status === 403) {
					location.replace('login');
				} else {
					alert('status: ' + rejection.status + ' , statusText: ' + rejection.statusText);
				}
				return $q.reject(rejection);
			}
		};
	} ])
	.factory('csrfTokenInterceptor', [ '$q', function($q) {
		return {
			request : function(config) {
				if (!config.headers['X-XSRF-TOKEN']) {
					var token = mine.getCookie('XSRF-TOKEN');
					if (token) {
						config.headers['X-XSRF-TOKEN'] = token;
					}
				}
				return config;
			}
		};
	}])
	.config([ '$httpProvider', function($httpProvider) {
		$httpProvider.interceptors.push('LoggingInterceptor');
		$httpProvider.interceptors.push('ErrorInterceptor');
		$httpProvider.interceptors.push('csrfTokenInterceptor');
	} ]);
});