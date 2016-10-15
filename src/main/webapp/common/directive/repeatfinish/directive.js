/**
 * ng-repeat结束后执行
 * author HeLei
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