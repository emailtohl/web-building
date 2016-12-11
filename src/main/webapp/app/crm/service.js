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
				return $http.post('user/customer', customer);
			},
		};
	}]);
});