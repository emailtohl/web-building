/**
 * 自动补全指令
 * author HeLei
 */
define([ 'common/module', 'jqueryui' ], function(common) {
	common.directive('autocomplete', [ function() {
		return {
			restrict : 'A',
			scope : {
				list : '='
			},
			require : 'ngModel',
			link : function($scope, $element, $attrs, ngModelCtrl) {
				if (ngModelCtrl) {
					// When data changes inside AngularJS
					// Notify the third party directive of the change
					ngModelCtrl.$render = function() {
						$element.autocomplete({
						    source: $scope.list
						});
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
				$element.autocomplete({
				    source: $scope.list
				});
			}
		};
	} ]);
});