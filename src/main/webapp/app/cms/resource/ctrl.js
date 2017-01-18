define(['jquery', 'cms/module', 'cms/service', 'ztree'], function($, cmsModule) {
	return cmsModule
	.controller('ResourceCtrl', ['$scope', '$http', '$state', 'cmsService', 'ztreeutil',
	                                function($scope, $http, $state, service, ztreeutil) {
		var self = this;
		$scope.getAuthentication();
		self.setting = {};
		function getFileRoot() {
			service.getFileRoot().success(function(data) {
				self.zNodes = data.children;
				console.log(self.zNodes);
				self.zTreeObj = $.fn.zTree.init($("#resource-tree"), self.setting, self.zNodes);
			});
		}
		getFileRoot();
		
	}])
	;
});