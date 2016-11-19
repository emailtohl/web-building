define([ 'applicationForm/module' ], function(applicationFormModule) {
	return applicationFormModule.factory('applicationFormService', [ '$http', 'util', function($http, util) {
		return {
			mine : function(page) {
				return $http.get('applicationForm/mine?page=' + page);
			},
			query : function(page, name, status) {
				var param = {
					page : page,
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
			getHistoryById : function(id) {
				return $http.get('applicationForm/history/' + id);
			},
			history : function(page, applicant, handler, name, status, start, end) {
				var param = util.encodeUrlParams({
					page : page,
					applicant : applicant,
					handler : handler,
					name : name,
					status : status,
					start : start,
					end : end
				});
				return $http.get('applicationForm/history' + (param ? '?' + param : ''));
			},
			'delete' : function(id) {
				return $http['delete']('applicationForm/' + id);
			},
		};
	}]);
});