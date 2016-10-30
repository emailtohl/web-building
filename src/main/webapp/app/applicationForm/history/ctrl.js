/**
 * 查询审核
 */
define(['applicationForm/module', 'applicationForm/service'], function(applicationFormModule) {
	return applicationFormModule
	.controller('ApplicationFormHistoryCtrl', [ '$scope', '$http', '$state', 'applicationFormService', 'util'
	                         , function($scope, $http, $state, applicationFormService, util) {
		var self = this;
		util.loadasync('lib/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css');
		$scope.getAuthentication();
		
		
	}]);
});