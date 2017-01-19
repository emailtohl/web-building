define(['jquery', 'cms/module', 'cms/service', 'ztree'], function($, cmsModule) {
	return cmsModule
	.controller('ResourceCtrl', ['$scope', '$http', '$state', 'cmsService', 'ztreeutil',
	                                function($scope, $http, $state, service, ztreeutil) {
		var self = this, rootName = '', zTreeObj;
		var setting = {
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
		$scope.getAuthentication();
		getFileRoot();
		
		
		function getFileRoot() {
			service.getFileRoot().success(function(data) {
				var zNodes = data.children;
				rootName = data.name;
				zTreeObj = $.fn.zTree.init($("#resource-tree"), setting, zNodes);
				console.log(data);
			});
		}
		
		function zTreeBeforeRemove(treeId, treeNode) {
			var filename;
			if (confirm('确认删除吗？')) {
				filename = ztreeutil.getFilePath(treeNode);
				service['delete'](rootName + '/' + filename).success(function(data) {
					getFileRoot();
				});
			}
			return false;// 在前端不体现删除的效果，而是由后台刷新实现
		}
		
		function zTreeBeforeRename(treeId, treeNode, newName, isCancel) {
			var srcName, pre, destName;
			if (!isCancel && newName.length > 0) {
				srcName = rootName + '/' + ztreeutil.getFilePath(treeNode);
				pre = srcName.substring(0, srcName.lastIndexOf('/'));
				destName = pre  + '/' + newName;
				console.log(srcName)
				console.log(destName)
//				service.reName(srcDir, newName);
			}
			return false;
		}
	}])
	;
});