define(['jquery', 'cms/module', 'cms/service', 'ztree'], function($, cmsModule) {
	return cmsModule
	.controller('ContentCtrl', ['$scope', '$http', '$state', 'cmsService', 'util', 'ztreeutil',
	                                function($scope, $http, $state, service, util, ztreeutil) {
		var self = this, rootName, style, zTreeObj;
		self.charset = 'UTF-8';
		self.contentType = 'text';
		var setting = {
			callback : {
				onClick : zTreeOnClick,
			},
		};
		util.loadasync('lib/ztree/zTreeStyle.css');
		$scope.getAuthentication();
		getFileRoot();
		
		function getFileRoot() {
			service.getFileRoot().success(function(data) {
				var zNodes = data;
				rootName = zNodes.name;
				zNodes.open = true;
				zTreeObj = $.fn.zTree.init($("#content-tree"), setting, zNodes);
			});
		}
		
		function zTreeOnClick(event, treeId, treeNode) {
			var path, suffixIndex, suffix;
			if (treeNode.isParent) {// 只在文件上才有效
				return;
			}
			path = ztreeutil.getFilePath(treeNode);
			// 根据文件后缀做判断
			suffixIndex = path.lastIndexOf('.');
			if (suffixIndex > -1) {
				suffix = path.substring(suffixIndex + 1, path.length);
				switch (suffix) {
				case 'jpg':
					self.contentType = 'jpg';
					break;
				case 'png':
					self.contentType = 'png';
					break;
				case 'mp4':
					self.contentType = 'mp4';
					break;
				case 'mp3':
					self.contentType = 'mp3';
					break;
				default:
					self.contentType = 'jpg';
					loadText(path);
					break;
				}
			}
		}
		
		function loadText(path) {
			service.loadText(path, self.charset).success(function(data) {
				
			});
		}
	}])
	;
});