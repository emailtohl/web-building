/**
 * 该日期插件具体使用方法，配置，请参看：
 * http://eonasdan.github.io/bootstrap-datetimepicker/Options/
 * format配置的key如下：
            LT : 'h:m',
            LTS : 'h:m:s',
            L : 'YYYY-MM-DD',
            LL : 'YYYY年MMMD日',
            LLL : 'YYYY-MM-DD HH:mm',
            LLLL : 'YYYY年MMMD日ddddAh点mm分',
            l : 'YYYY-MM-DD',
            ll : 'YYYY年MMMD日',
            lll : 'YYYY年MMMD日Ah点mm分',
            llll : 'YYYY年MMMD日ddddAh点mm分'
 * author HeLei
 */

define([ 'common/module', 'common/service/util', 'datetimepicker-zh' ], function(commonModule) {
	commonModule.directive('datetimepicker', [ 'util', function(util) {
		util.loadasync('lib/bootstrap-datetimepicker/bootstrap-datetimepicker.min.css');
		return {
			restrict : 'A',
			scope : {
				config : '='
			},
			controller : function($scope) {
				/*$scope.$watch('config', function(newVal, oldVal) {
					 console.log("config: " + newVal);
				});*/
			},
			require : 'ngModel',
			link : function($scope, $element, $attrs, ngModelCtrl) {
				var ele = $element.get(0);
				var options = {
					locale : 'zh-CN',
					format: 'LLL',
				};
				
				$.extend(options, $scope.config);
				// Angular将元素封装成了jqLite，可直接使用jQuery的接口
				$($element).datetimepicker(options);
				
				if (ngModelCtrl) {
					// When data changes inside AngularJS
					// Notify the third party directive of the change
					ngModelCtrl.$render = function() {
						$element.val(ngModelCtrl.$viewValue);
					};
					// When data changes outside of AngularJS
					$element.on('blur', function(args) {
						// Also tell AngularJS that it needs to update the UI
						$scope.$apply(function() {
							// Set the data within AngularJS
							ngModelCtrl.$setViewValue($element.val());
						});
					});
				}
			}
		};
	} ]);
});