/**
 * 模态框指令
 * @author HeLei
 * @date 2017.02.04
 */
define([ 'common/module', 'bootstrap' ], function(common) {
	common.directive('modal', [ '$document', function($document) {
		return {
			restrict : 'A',// 只在div中使用
			templateUrl : 'common/directive/modal/template.html',
			transclude : true,// 保留原元素中的内容，需要在template中相应的地方使用ng-transclude说明嵌入在哪
			scope : {
				open : '=',// 是否打开模态框，双向绑定，使用它时，不能用{{}}，如<div modal open="ctrl.open"></div>
				title : '@',// 模态框标题，单向取值，使用它时，使用{{}}取值，如<div modal title="{{ctrl.title}}"></div>
				whenConfirm : '&',// 确认按钮的回调函数
				type : '@',// class样式，有primary、info、warning、success、danger，默认无
				disabled : '='
			},
			link : function($scope, $element, $attrs) {
				function openModal() {
					var modal = $element.find('div.modal');
					// 打开模态框
//					modal.modal();                      // initialized with defaults
//					modal.modal({ keyboard: false });   // initialized with no keyboard
					modal.modal('show');                // initializes and invokes show immediately
					
					/*
					 * 由于点击confirm按钮后，调用$scope.$apply()会报错
					 * 虽然对指令功能没有影响，但是会给人困扰，所以，使用一个特别的标记位
					 * 若是点击confirm按钮关闭的模态框，则不调用$scope.$apply()
					 */
					var confirmBtnHasBeenClicked = false;
					
					// 绑定“确定”按钮的处理程序
					$scope.confirm = function() {
						confirmBtnHasBeenClicked = true;
						$scope.whenConfirm();
						modal.modal('hide');
					};
					
					// 如果模态框被其他方式关闭，则修改open的状态
					modal.on('hidden.bs.modal', function(e) {
						$scope.open = false;
						// Also tell AngularJS that it needs to update the UI
						if (!confirmBtnHasBeenClicked)
							$scope.$apply();
					});
					
					// 添加class样式
					$scope.typeModel = {};
					switch ($scope.type) {
						case 'primary':
							$scope.typeModel.primary = true;
							break;
						case 'info':
							$scope.typeModel.info = true;
							break;
						case 'warning':
							$scope.typeModel.warning = true;
							break;
						case 'success':
							$scope.typeModel.success = true;
							break;
						case 'danger':
							$scope.typeModel.danger = true;
							break;
						default :
							for (p in $scope.typeModel) {
								if ($scope.typeModel.hasOwnProperty(p))
									$scope.typeModel[p] = false;
							}
					}
					
					// 下面是让模态框可以拖动
					var startX = 0, startY = 0, x = 0, y = 0, dialog;
					dialog = angular.element(document.getElementsByClassName('modal-dialog'));
					dialog.css({
						position : 'relative',
						cursor : 'move'
					});
					
					dialog.on('mousedown', function(event) {
						var tagName = event.target.tagName.toUpperCase();
						// 如果点击模态框中的输入框，则不作拖动
						if ('INPUT' == tagName || 'TEXTAREA' == tagName) {
							return;
						}
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

				// 监控open的值，一旦被外部设置为true则打开模态框
				// 关闭模态框的功能bootstrap插件已经提供，只需将open同步为false即可
				$scope.$watch('open', function(newVal, oldVal) {
					if (newVal)
						openModal();
				});
	
			}
		};
	} ]);
});