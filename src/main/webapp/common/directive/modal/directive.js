/**
 * 模态框指令
 * author helei
 */
define([ 'common/module' ], function(common) {
	common.directive('modal', [ '$document', function($document) {
		return {
			restrict : 'A',// 只在div中使用
			templateUrl : 'common/directive/modal/template.html',
			scope : {
				title : '@',
				confirm : '&'
			},
			link : function($scope, $element, $attrs) {
				var startX = 0, startY = 0, x = 0, y = 0;
//				element = angular.element(document.getElementsByClassName("modal-dialog"));
				$element.css({
					position : 'relative',
					cursor : 'move'
				});

				$element.on('mousedown', function(event) {
					// Prevent default dragging of selected content
					event.preventDefault();
					startX = event.pageX - x;
					startY = event.pageY - y;
					$document.on('mousemove', mousemove);
					$document.on('mouseup', mouseup);
				});

				function mousemove(event) {
					y = event.pageY - startY;
					x = event.pageX - startX;
					$element.css({
						top : y + 'px',
						left : x + 'px'
					});
				}

				function mouseup() {
					$document.off('mousemove', mousemove);
					$document.off('mouseup', mouseup);
				}
				
				$element.modal();                      // initialized with defaults
				$element.modal({ keyboard: false });   // initialized with no keyboard
				$element.modal('show');                // initializes and invokes show immediately
				
				$scope.confirm({});
			}
		};
	} ]);
});