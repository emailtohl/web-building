define([ 'applicationForm/module' ], function(applicationFormModule) {
	return applicationFormModule.factory('applicationFormService', [ '$http', 'util', function($http, util) {
		return {
			mine : function() {
				return $http.get('applicationForm/mine');
			},
			query : function(name, status) {
				var param = {
					name : name,
					status : status
				};
				param = util.encodeUrlParams(param);
				return $http.get('applicationForm/query' + (param ? '?' + param : ''));
			},
			get : function(id) {
				return $http.get('applicationForm/' + id);
			},
			add : function(applicationForm) {
				return $http.post('applicationForm', applicationForm);
			},
			transit : function(id, status, cause) {
				return $http.put('applicationForm/' + id, {
					status : status,
					cause : causep
				});
			},
		};
	}]);
});