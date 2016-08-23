/**
 * pagerModel的属性：
 * totalRow;// 总行数
 * totalPage;// 总页面数
 * pageNum;// 当前页码
 * pageSize;// 每页最大行数
 * startRecordNumber;// 返回结果从此行开始
 * dataList;// 存储查询结果
 * author helei
 */
define([ 'common/module' ], function(common) {
	common
	.constant('PAGE_BTN_NUM', 5)// 显示多少个item供点击
	.constant('PAGE_SIZE', 20)// 每页显示的行数
	.directive('pager', [ 'PAGE_BTN_NUM', 'PAGE_SIZE', function(pageBtnNum, pageSize) {
		return {
			restrict : 'EA',
			templateUrl : 'common/directive/pager/template.html',
			scope : {
				pagerModel : '=',
				condition : '=',
				get : '&'
			},
			link : function($scope, $element, $attrs) {
				if (!$scope.condition) {
					$scope.condition = {};
				}
				$scope.click = function(selectPageNum) {
					$scope.condition.pageNum = selectPageNum;
					$scope.condition.pageSize = pageSize;
					$scope.get();
				};
				$scope.previous = function() {
					if ($scope.condition.pageNum > 1) {
						$scope.click($scope.condition.pageNum - 1);
					}
				};
				$scope.next = function() {
					if ($scope.condition.pageNum < $scope.pagerModel.totalPage) {
						$scope.click($scope.condition.pageNum + 1);
					}
				};
				$scope.clickMore = function() {
					// $scope.pageNumArr被初始化为空数组，其值是在controller而非link处维护
					var arr = $scope.pageNumArr || [];
					if (arr && arr.length > 0) {
						$scope.click(arr[arr.length - 1] + 1);
					}
				};
				$scope.getClass = function(pageNum) {
					return $scope.condition.pageNum === pageNum ? 'active' : '';
				};
				
			},
			controller : function($scope) {
				$scope.$watch('pagerModel', function(newVal, oldVal){
					var i, j, startItem,
					totalPage = $scope.pagerModel && $scope.pagerModel.totalPage,
					pageNum = $scope.pagerModel && $scope.pagerModel.pageNum;
					$scope.condition.pageNum = pageNum;
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
				});
			}
		};
	} ]);
});