define(['cms/module', 'common/context' ], function(cmsModule) {
	return cmsModule.factory('categoryService', [ '$http', 'util', function($http, util) {
		return {
			getTypes : function() {
				return $http.get('cms/types');
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