/**
 * 查询审核记录
 */
define(['applicationForm/module', 'applicationForm/service', 'moment'], function(applicationFormModule, service, moment) {
	return applicationFormModule
	.controller('ApplicationFormHistoryCtrl', [ '$scope', '$http', '$state', 'applicationFormService', 'util'
	                         , function($scope, $http, $state, applicationFormService, util) {
		var self = this;
		util.loadasync('lib/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css');
		$scope.getAuthentication();
		
		self.form = {
			applicant : null,
			handler : null,
			status : 'REQUEST',
			start : moment().startOf('year').format('YYYY-MM-DD HH:mm:ss'),
			end : moment().format('YYYY-MM-DD HH:mm:ss'),
		};
		self.getPager = function(form) {
			if ($scope.hasAuthority('application_form_transit')) {// 如果有审批权限，则可查看所有申请单
				applicationFormService.history(form.applicant, form.handler, form.status, form.start, form.end)
				.success(function(data) {
					self.pager = data;
					console.log(data)
				});
			}
		};
		self.getPager(self.form);
		
	}]);
});