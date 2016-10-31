/**
 * 审核申请单
 */
define(['applicationForm/module', 'applicationForm/service'], function(applicationFormModule) {
	return applicationFormModule
	.controller('ApplicationFormAuditCtrl', [ '$scope', '$http', '$state', 'applicationFormService', 'util'
	                         , function($scope, $http, $state, applicationFormService, util) {
		var self = this;
		util.loadasync('lib/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css');
		$scope.getAuthentication();
		
		self.statusMap = {
			REQUEST : '申请中',
			REJECT : '拒绝',
			PROCESSING : '处理中',
			COMPLETION : '完成'
		}
		
		self.getPager = function() {
			if ($scope.hasAuthority('application_form_transit')) {// 如果有审批权限，则可查看所有申请单
				applicationFormService.query().success(function(data) {
					self.pager = data;
					console.log(data)
				});
			} else {// 否则查询与自己有关的申请单
				applicationFormService.mine().success(function(data) {
					self.pager = data;
					console.log(data)
				});
			}
		};
		self.getPager();
		
		self.modal = {
			open : false,
			title : '审核申请单',
			type : '',
			whenConfirm : function() {
				self.form.id = self.detail.id;
				self.form.name = self.detail.name;
				self.form.description = self.detail.description;
				applicationFormService.transit(self.form).success(function(data) {
					self.openModal.open = false;
					self.getPager();
				});
			},
		};
		self.openModal = function(id) {
			// 如果是普通申请人，没有审批权限，则不打开审批模态框
			if (!$scope.hasAuthority('application_form_transit')) {
				return;
			}
			applicationFormService.get(id).success(function(data) {
				console.log(data);
				self.detail = data;
				self.modal.open = true;
			});
		};
		
	}]);
});