define([ 'user/module', 'common/context' ], function(userModule) {
	return userModule.factory('userService', [ '$http', 'util', function($http, util) {
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
				return $http.get('user/pager?' + util.encodeUrlParams(params));
			},
			addUser : function(user) {
				return $http.post('user', user);
			},
			enableUser : function(id) {
				return $http.put('user/enableUser/' + id);
			},
			disableUser : function(id) {
				return $http.put('user/disableUser/' + id);
			},
			update : function(user) {
				if (user.post) {
					return $http.put('user/employee/' + user.id, user);
				} else {
					return $http.put('user/customer/' + user.id, user);
				}
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
				f.enabled = e.enabled;
				f.post = e.post;
				f.department = {name : e.department ? e.department.name : ''};
				f.description = e.description;
				return f;
			},
			
		};
	}]);
});