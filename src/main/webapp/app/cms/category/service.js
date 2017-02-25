define(['cms/module', 'common/context' ], function(cmsModule) {
	return cmsModule
	// 选择框中，过滤掉自身：ng-repeat="x in ctrl.typeList | excludeSelf:ctrl.form.id"
	.filter('excludeSelf', function() {
		return function(arr, selfId) {
			var i, newArr;
			if (!(arr instanceof Array) || !angular.isNumber(selfId))
				return arr;
			newArr = [];
			for (var i = 0; i < arr.length; i++) {
				if (arr[i].id != selfId)
					newArr.push(arr[i]);
			}
			return newArr;
		}
	})
	.factory('categoryService', [ '$http', 'util', function($http, util) {
		return {
			getTypes : function() {
				return $http.get('cms/types');
			},
			getTypePager : function (name, page) {
				var param = {
					page : page,
					name : name,
				};
				param = util.encodeUrlParams(param);
				return $http.get('cms/typePager' + (param ? '?' + param : ''));
			},
			findTypeById : function(id) {
				return $http.get('cms/type/' + id);
			},
			saveType : function(type) {
				return $http.post('cms/type', type);
			},
			updateType : function(id, type) {
				return $http.put('cms/type/' + id, type);
			},
			deleteType : function(id) {
				return $http['delete']('cms/type/' + id);
			},
		};
	}]);
});