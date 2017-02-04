/**
 * ng-repeat结束后执行
 * @author HeLei
 * @date 2017.02.04
 */
define([ 'common/module' ], function(common) {
	common.directive('repeatFinish', [ function() {
		return {
			link : function($scope, $element, $attrs) {
				if ($scope.$last) {
					$scope.$eval($attrs.repeatFinish);
				}
			}
		};
	} ]);
});