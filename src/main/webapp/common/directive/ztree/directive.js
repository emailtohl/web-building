/**
 * 封装ztree
 * @author HeLei
 * @date 2017.02.04
 */
define([ 'common/module', 'jquery', 'ztree', 'common/service/util' ], function(commonModule, $) {
	commonModule.directive('ztree', ['util', function(util) {
		util.loadasync('lib/ztree/zTreeStyle.css');
		return {
			restrict : 'AE',
			scope : {
				setting : '=',
				click : '&'
			},
			template : '<ul class="ztree"></ul>',
			require : 'ngModel',
			link : function($scope, $element, $attrs, ngModelCtrl) {
				function callback(event, treeId, treeNode) {
					$scope.click({
						event : event,
						treeId : treeId,
						treeNode : treeNode
					});
				}
				function getFont(treeId, node) {
					return node.font ? node.font : {};
				}
				var setting = {
					view : {
						fontCss : getFont,
						nameIsHTML : true
					},
					callback : {
						onClick : callback
					}
				};
				$.extend(setting, $scope.setting);
				$.fn.zTree.init($element.find('ul'), setting, $scope.nodes);
				if (ngModelCtrl) {
					// When data changes inside AngularJS
					// Notify the third party directive of the change
					ngModelCtrl.$render = function() {
						$.fn.zTree.init($element.find('ul'), setting, ngModelCtrl.$viewValue);
					};
				}
			}
		};
	}]);
});