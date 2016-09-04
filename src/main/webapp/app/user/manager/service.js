define([ 'user/module', 'mine' ], function(userModule) {
	return userModule.factory('userService', [ '$http', function($http) {
		return {
			getUserById : function(id) {
				return $http.get('user/' + id);
			},	
			getUserEmail : function(email) {
				return $http.get('user/' + email);
			},
			/**
			 * query对象中含有查询页码：pageNumber，pageSize
			 */
			getUserPager : function(params) {
				return $http.get('user/pager?' + mine.encodeUrlParams(params));
			},
			addUser : function(user) {
				return $http.post('user', user);
			},
			update : function(user) {
				return $http.put('user/' + user.id, user);
			},
			'delete' : function(id) {
				return $http['delete']('user/' + id);
			},
		};
	}]);
});