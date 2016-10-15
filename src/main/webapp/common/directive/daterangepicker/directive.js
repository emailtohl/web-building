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
	commonModule.directive('daterangepicker', [ 'util', function(util) {
		util.loadasync('lib/bootstrap-datetimepicker/bootstrap-datetimepicker.min.css');
		return {
			restrict : 'AE',
			scope : {
				config : '=config',
				localFrom : '=from',
				localTo : '=to'
			},
			templateUrl : 'common/directive/daterangepicker/template.html',
			link : function($scope, $element, $attrs) {
				var $fromDiv, $fromInput, $toDiv, $toInput;
				if (!$scope.localFrom) {
					$scope.localFrom = '';
				}
				if (!$scope.localTo) {
					$scope.localTo = '';
				}
				$fromDiv = $element.find('#from');
				$fromInput = $fromDiv.find('input');
				$toDiv = $element.find('#to');
				$toInput = $toDiv.find('input');
				var options = {
					locale : 'zh-CN',
					format : 'LLL',
				};
				$.extend(options, $scope.config);
				$fromDiv.datetimepicker(options);
				options.useCurrent = false;
				$toDiv.datetimepicker(options);
				
				$fromDiv.on("dp.change", function(e) {
					$toDiv.data("DateTimePicker").minDate(e.date);
					$scope.localFrom = $fromInput.val();
					$element.scope().$digest();
				});
				$toDiv.on("dp.change", function(e) {
					$fromDiv.data("DateTimePicker").maxDate(e.date);
					$scope.localTo = $toInput.val();
					$element.scope().$digest();
				});
			}
		};
	} ]);
});