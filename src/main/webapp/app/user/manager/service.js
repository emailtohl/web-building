define([ 'user/module', 'mine' ], function(userModule) {
	return userModule.factory('userService', [ '$http', function($http) {
		return {
			getUserById : function(id) {
				return $http.get('user/id/' + id);
			},	
			getUserEmail : function(email) {
				return $http.get('user/email/' + email);
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
			/**
			 * 将实体对象中的数据复制到表单对象中
			 */
			entity2form : function(e) {
				var f = {};
				f.id = e.id;
				f.email = e.email;
				f.name = e.name;
				f.post = e.post;
				f.department = {name : e.department.name};
				f.description = e.description;
				return f;
			},
			
		};
	}]);
});