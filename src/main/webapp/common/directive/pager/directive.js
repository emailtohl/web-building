/**
 * 分页组件：生成页码按钮
 * 实际上生成页码按钮，必要信息只有当前页和总页数，可以通过代码一次性生成，例如mine中提供的createPageItems方法
 * 不过这是静态的，但是当点击页码按钮后，查询结果会更新，这就需要重新生成页码按钮，这不符合angular的编程风格
 * 定义angular的指令后，只需监控当前页pageNum和总页数totalPage，指令会自动更新页码按钮
 * 
 * @author Helei
 */
define([ 'common/module' ], function(common) {
	common
	.directive('pager', function() {
		function init($scope) {
			if (!$scope.pageNum || $scope.pageNum < 1) {
				$scope.pageNum = 1;
			}
			if (!$scope.totalPage || $scope.totalPage < 1) {
				$scope.totalPage = 1;
			}
			if (!$scope.pageBtnNum || $scope.pageBtnNum < 1) {
				$scope.pageBtnNum = 5;
			}
		}
		return {
			restrict : 'EA',
			templateUrl : 'common/directive/pager/template.html',
			scope : {
				pageNum : '=',
				totalPage : '=',
				pageBtnNum : '@',// 显示多少个按钮，默认5个
				onClick : '&'
			},
			/**
			 * 链接时，只定义初始化数据和处理方法，数据变化的维护在controller中
			 */
			link : function($scope, $element, $attrs) {
				init($scope);
				$scope.click = function(selectPageNum) {
					$scope.pageNum = selectPageNum;
					$scope.onClick({pageNum : selectPageNum});
				};
				$scope.previous = function() {
					if ($scope.pageNum > 1) {
						$scope.click($scope.pageNum - 1);
					}
				};
				$scope.next = function() {
					if ($scope.pageNum < $scope.totalPage) {
						$scope.click($scope.pageNum + 1);
					}
				};
				$scope.clickMore = function() {
					// $scope.pageNumArr被初始化为空数组，其值是在controller而非link处维护
					var arr = $scope.pageNumArr || [];
					if (arr.length > 0) {
						$scope.click(arr[arr.length - 1] + 1);
					}
				};
				$scope.getClass = function(pageNum) {
					return $scope.pageNum === pageNum ? 'active' : '';
				};
			},
			/**
			 * 数据变化的维护在controller中
			 */
			controller : function($scope) {
				$scope.$watch('pageNum', function(newVal, oldVal){
					refresh();
				});
				$scope.$watch('totalPage', function(newVal, oldVal){
					refresh();
				});
				function refresh() {
					var i, j, startItem, totalPage, pageNum, pageBtnNum;
					init($scope);
					totalPage = $scope.totalPage;
					pageNum = $scope.pageNum;
					pageBtnNum = $scope.pageBtnNum;
					if (totalPage) {
						$scope.isShow = true;
					} else {
						$scope.isShow = false;
						return;
					}
					if (pageNum > 1) {
						$scope.previousBtn = true;
					} else {
						$scope.previousBtn = false;
					}
					if (pageNum < totalPage) {
						$scope.nextBtn = true;
					} else {
						$scope.nextBtn = false;
					}
					$scope.pageNumArr = [];
					if (totalPage - pageNum + 1 > pageBtnNum) {
						$scope.more = true;
						for (i = pageNum, j = 0; j < pageBtnNum; i++, j++) {
							$scope.pageNumArr.push(i);
						}
					} else {
						$scope.more = false;
						startItem = totalPage - pageBtnNum + 1;
						if (startItem < 1) {
							startItem = 1;
						}
						for (i = startItem; i <= totalPage; i++) {
							$scope.pageNumArr.push(i);
						}
					}
				}
			}
		};
	} );
});