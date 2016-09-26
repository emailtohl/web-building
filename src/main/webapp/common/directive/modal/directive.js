/**
 * 模态框指令
 * author helei
 */
define([ 'common/module' ], function(common) {
	common.directive('modal', [ '$document', function($document) {
		return {
			restrict : 'A',// 只在div中使用
			templateUrl : 'common/directive/modal/template.html',
			transclude : true,// 保留原元素中的内容，需要在template中相应的地方使用ng-transclude说明嵌入在哪
			scope : {
				open : '=',
				title : '@',
				whenConfirm : '&'
			},
			link : function($scope, $element, $attrs) {
				function openModal() {
					var startX = 0, startY = 0, x = 0, y = 0, modal, dialog;
					modal = $element.find('div.modal');
					
					modal.modal();                      // initialized with defaults
					modal.modal({ keyboard: false });   // initialized with no keyboard
					modal.modal('show');                // initializes and invokes show immediately
					
					$scope.confirm = function() {
						$scope.whenConfirm();
					};
					
					dialog = angular.element(document.getElementsByClassName('modal-dialog'));
					dialog.css({
						position : 'relative',
						cursor : 'move'
					});
					
					dialog.on('mousedown', function(event) {
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
						dialog.css({
							top : y + 'px',
							left : x + 'px'
						});
					}
					
					function mouseup() {
						$document.off('mousemove', mousemove);
						$document.off('mouseup', mouseup);
					}
				};

				$scope.$watch('open', function(newVal, oldVal) {
					if (newVal)
						openModal();
					else
						$('button[data-dismiss="modal"]').click();
				});
			}
		};
	} ]);
});