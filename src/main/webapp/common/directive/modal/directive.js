/**
 * type类型有：
 * Default、Primary、Info、Warning、Success、Danger
 * 默认scope，读取父控制器上的域直到$rootscope
 * author helei
 */
define([ 'common/module' ], function(common) {
	common.directive('modal', [ function() {
		return {
			restrict : 'EA',
			scope : {
				/**
				 * modalModel的结构如下：
				 * modalModel = {
				 * 		open : false,
				 * 		type : 'Success',
				 * 		close : function() {
				 * 			$scope.modal.open = false;
				 * 		},
				 * 		save : function(params) {
				 * 			console.log(params);
				 * 			$scope.modal.open = false;
				 * 		}
				 * 	};
				 */
				modalModel : '='
			},
			templateUrl : 'common/directive/modal/template.html'
		};
	} ]);
});