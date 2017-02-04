/**
 * 分页组件：生成页码按钮
 * 实际上生成页码按钮，必要信息只有当前页和总页数，可以通过代码一次性生成
 * 不过这是静态的，但是当点击页码按钮后，查询结果会更新，这就需要重新生成页码按钮，这不符合angular的编程风格
 * 定义angular的指令后，只需监控当前页pageNumber和总页数totalPages，指令会自动更新页码按钮
 * 
 * @author HeLei
 * @date 2017.02.04
 */
define([ 'common/module' ], function(common) {
	common
	.directive('pager', function() {
		function init($scope) {
			// 查询页码，默认查询第一页
			if (!$scope.pageNumber || $scope.pageNumber < 1) {
				$scope.pageNumber = 1;
			}
			// 查询之后，如果没有查询到结果，总页数可能为0
			if ($scope.totalPages == null || $scope.totalPages < 0) {
				$scope.totalPages = 0;
			}
			// 显示的item数，默认显示5个
			if (!$scope.buttonCount || $scope.buttonCount < 1) {
				$scope.buttonCount = 5;
			}
		}
		return {
			restrict : 'EA',
			templateUrl : 'common/directive/pager/template.html',
			scope : {
				pageNumber : '=',
				totalPages : '=',
				buttonCount : '@',// 显示多少个按钮，默认5个
				onClick : '&'
			},
			/**
			 * 链接时，只定义初始化数据和处理方法，数据变化的维护在controller中
			 */
			link : function($scope, $element, $attrs) {
				init($scope);
				$scope.click = function(selectPageNum) {
					if (selectPageNum < 1 || selectPageNum > $scope.totalPages
							|| selectPageNum == $scope.pageNumber) {
						return;
					}
					$scope.pageNumber = selectPageNum;
					$scope.onClick({pageNumber : selectPageNum});
				};
				$scope.previous = function() {
					if ($scope.pageNumber > 1) {
						$scope.click($scope.pageNumber - 1);
					}
				};
				$scope.next = function() {
					if ($scope.pageNumber < $scope.totalPages) {
						$scope.click($scope.pageNumber + 1);
					}
				};
				$scope.clickMore = function() {
					// $scope.pageNumArr被初始化为空数组，其值是在controller而非link处维护
					var arr = $scope.pageNumArr || [];
					if (arr.length > 0) {
						$scope.click(arr[arr.length - 1] + 1);
					}
				};
				$scope.getClass = function(pageNumber) {
					return $scope.pageNumber === pageNumber ? 'active' : '';
				};
			},
			/**
			 * 数据变化的维护在controller中
			 */
			controller : function($scope) {
				$scope.$watch('pageNumber', function(newVal, oldVal) {
					refresh();
				});
				$scope.$watch('totalPages', function(newVal, oldVal) {
					refresh();
				});
				function refresh() {
					var i, j, startItem, totalPages, pageNumber, buttonCount;
					init($scope);
					totalPages = $scope.totalPages;
					pageNumber = $scope.pageNumber;
					buttonCount = $scope.buttonCount;
					if (totalPages) {// 总页数为空或为0，都会计算为false
						$scope.isShow = true;
					} else {
						$scope.isShow = false;
						return;
					}
					if (pageNumber > 1) {
						$scope.previousBtn = true;
					} else {
						$scope.previousBtn = false;
					}
					if (pageNumber < totalPages) {
						$scope.nextBtn = true;
					} else {
						$scope.nextBtn = false;
					}
					$scope.pageNumArr = [];
					if (totalPages - pageNumber + 1 > buttonCount) {
						$scope.more = true;
						for (i = pageNumber, j = 0; j < buttonCount; i++, j++) {
							$scope.pageNumArr.push(i);
						}
					} else {
						$scope.more = false;
						startItem = totalPages - buttonCount + 1;
						if (startItem < 1) {
							startItem = 1;
						}
						for (i = startItem; i <= totalPages; i++) {
							$scope.pageNumArr.push(i);
						}
					}
				}
			}
		};
	} );
});