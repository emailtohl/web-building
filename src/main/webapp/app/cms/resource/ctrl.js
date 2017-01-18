define(['jquery', 'cms/module', 'cms/service', 'ztree'], function($, cmsModule) {
	return cmsModule
	.controller('ResourceCtrl', ['$scope', '$http', '$state', 'cmsService', 'ztreeutil',
	                                function($scope, $http, $state, service, ztreeutil) {
		var self = this;
		$scope.getAuthentication();
		self.setting = {
			edit : {
				enable : true,
				showRemoveBtn : true,
				removeTitle : '删除节点',
				showRenameBtn: true,
				renameTitle: '编辑节点名称',
			},
			callback : {
				beforeRemove : zTreeBeforeRemove,
				beforeRename: zTreeBeforeRename,
			},
		};
		function getFileRoot() {
			service.getFileRoot().success(function(data) {
				self.rootName = data.name;
				self.zNodes = data.children;
				console.log(self.zNodes);
				self.zTreeObj = $.fn.zTree.init($("#resource-tree"), self.setting, self.zNodes);
			});
		}
		getFileRoot();
		
		
		function zTreeBeforeRemove(treeId, treeNode) {
			var filename;
			if (!confirm('确认删除吗？'))
				return false;
			filename = ztreeutil.getFilePath(treeNode);
			service['delete'](self.rootName + '/' + filename).success(function(data) {
				getFileRoot();
			});
		}
		function zTreeBeforeRename(treeId, treeNode, newName, isCancel) {
			var srcName, pre, destName;
			if (isCancel || newName.length == 0)
				return false;
			srcName = self.rootName + '/' + ztreeutil.getFilePath(treeNode);
			pre = srcName.substring(0, srcName.lastIndexOf('/'));
			destName = pre  + '/' + newName;
			console.log(srcName)
			console.log(destName)
//			service.reName(srcDir, newName);
			return true;
		}
	}])
	;
});