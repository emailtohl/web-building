/**
 * 提交申请单
 */
define(['applicationForm/module', 'applicationForm/service'], function(applicationFormModule) {
	return applicationFormModule
	.controller('ApplicationCtrl', [ '$scope', '$http', '$state', 'applicationFormService', 'util'
	                         , function($scope, $http, $state, applicationFormService, util) {
		var self = this;
		util.loadasync('lib/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css');
		$scope.getAuthentication();
		self.form = self.form || {};
		self.submit = function() {
			applicationFormService.add(self.form).success(function(data) {
				$state.go('applicationForm.audit', {}, { reload : true });
			});
		};
		
	}]);
});