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
				return $http.post('user/employee', user);
			},
			enableUser : function(id) {
				return $http.put('user/enableUser/' + id);
			},
			disableUser : function(id) {
				return $http.put('user/disableUser/' + id);
			},
			updateEmployee : function(user) {
				return $http.put('user/employee/' + user.id, user);
			},
			updateCustomer : function(user) {
				return $http.put('user/customer/' + user.id, user);
			},
			'delete' : function(id) {
				return $http['delete']('user/' + id);
			},
			/**
			 * 将实体对象中的数据复制到表单对象中
			 */
			entity2form : function(e) {
				var f = {
					subsidiary : {}
				};
				f.id = e.id;
				f.email = e.email;
				f.telephone = e.telephone;
				f.name = e.name;
				f.enabled = e.enabled;
				f.post = e.post;
				f.department = {name : e.department ? e.department.name : ''};
				f.description = e.description;
				f.address = e.address;
				f.subsidiary.mobile = e.subsidiary && e.subsidiary.mobile;
				f.subsidiary.city = e.subsidiary && e.subsidiary.city;
				f.subsidiary.province = e.subsidiary && e.subsidiary.province;
				f.subsidiary.country = e.subsidiary && e.subsidiary.country;
				f.subsidiary.language = e.subsidiary && e.subsidiary.language;
				return f;
			},
			
		};
	}]);
});