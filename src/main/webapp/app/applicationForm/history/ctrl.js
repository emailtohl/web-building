/**
 * 查询审核记录
 */
define(['applicationForm/module', 'applicationForm/service', 'moment'], function(applicationFormModule, service, moment) {
	return applicationFormModule
	.controller('ApplicationFormHistoryCtrl', [ '$scope', '$http', '$state', 'applicationFormService', 'util'
	                         , function($scope, $http, $state, applicationFormService, util) {
		const initForm = JSON.stringify({
			page : 1,
			name : null,
			applicant : null,
			handler : null,
			status : null,
			start : moment().startOf('year').format('YYYY-MM-DD HH:mm:ss'),
			end : moment().format('YYYY-MM-DD HH:mm:ss'),
		});
		var self = this;
		util.loadasync('lib/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css');
		$scope.getAuthentication();
		
		self.statusMap = {
			REQUEST : '申请中',
			REJECT : '拒绝',
			PROCESSING : '处理中',
			COMPLETION : '完成'
		}
		
		self.form = JSON.parse(initForm);
		self.reset = function() {
			self.form = JSON.parse(initForm);
		};
		
		self.getPager = function() {
			applicationFormService.history(
				self.form.page,
				self.form.applicant,
				self.form.handler,
				self.form.name,
				self.form.status,
				self.form.start,
				self.form.end).success(
				function(data) {
					self.pager = data;
					console.log(self.pager);
				});
		};
		self.getPager();
		
		self.modal = {
			open : false,
			title : '申请单处理记录',
			type : '',
			whenConfirm : function() {
				self.modal.open = false
			},
		};
		self.openModal = function(id) {
			// 如果是普通申请人，没有审批权限，则不打开审批模态框
			if (!$scope.hasAuthority('application_form_transit')) {
				return;
			}
			applicationFormService.getHistoryById(id).success(function(data) {
				self.detail = data;
				self.modal.open = true;
			});
		};
		self.page = function(pageNumber) {
			self.form.page = pageNumber;
			self.getPager();
		};
	}]);
});