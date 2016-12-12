define([ 'crm/module', 'common/context' ], function(crmModule) {
	return crmModule.factory('crmService', [ '$http', 'util', function($http, util) {
		return {
			query : function(params) {
				return $http.get('customer/pager?' + util.encodeUrlParams(params));
			},
			get : function(id) {
				return $http.get('customer/' + id);
			},
			add : function(customer) {
				// 默认密码是123456
				customer.plainPassword = '123456';
				return $http.post('user/customer', this.entity2form(customer));
			},
			update : function(customer) {
				return $http.put('customer/' + customer.id, this.entity2form(customer));
			},
			download : function() {
				window.open('customer/download');
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
				f.title = e.title;
				f.affiliation = e.affiliation;
				f.enabled = e.enabled;
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