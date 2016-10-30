/**
 * 审核申请单
 */
define(['applicationForm/module', 'applicationForm/service'], function(applicationFormModule) {
	return applicationFormModule
	.controller('ApplicationFormAuditCtrl', [ '$scope', '$http', '$state', 'applicationFormService', 'util', 'bootstrap-wysihtml5'
	                         , function($scope, $http, $state, applicationFormService, util) {
		var self = this;
		util.loadasync('lib/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css');
		$scope.getAuthentication();
		
		
	}]);
});