define(['cms/module', 'common/context' ], function(cmsModule) {
	return cmsModule.factory('categoryService', [ '$http', 'util', function($http, util) {
		return {
			getTypes : function(name, page) {
				var param = {
					page : page,
					name : name,
				};
				param = util.encodeUrlParams(param);
				return $http.get('cms/types' + (param ? '?' + param : ''));
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