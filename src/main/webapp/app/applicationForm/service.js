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
			transit : function(form) {
				return $http.put('applicationForm/' + form.id, form);
			},
			history : function(applicant, handler, status, start, end) {
				var param = util.encodeUrlParams({
					applicant : applicant,
					handler : handler,
					status : status,
					start : start,
					end : end
				});
				return $http.get('applicationForm/history' + (param ? '?' + param : ''));
			},
		};
	}]);
});