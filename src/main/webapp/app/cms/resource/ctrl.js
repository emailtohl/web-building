define(['jquery', 'cms/module', 'cms/service', 'ztree'], function($, cmsModule) {
	return cmsModule
	.controller('ResourceCtrl', ['$scope', '$http', '$state', 'cmsService', 'util', 'ztreeutil',
	                                function($scope, $http, $state, service, util, ztreeutil) {
		var self = this, style, zTreeObj;
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
			view : {
				selectedMulti: false,
				addHoverDom : addHoverDom,
			},
			/*
			check : {
				enable: true,
				chkStyle: 'checkbox',
				chkboxType:  { 'Y' : 'ps', 'N' : 'ps' },
			},
			*/
		};
		util.loadasync('lib/ztree/zTreeStyle.css');
		// 新增按钮的样式
		style = $('<style type="text/css">'
				+ '.ztree li span.button.add {margin-left:2px; margin-right: -1px; background-position:-144px 0; vertical-align:top; *vertical-align:middle}'
				+ '</style>');
		$('head').append(style);
		$scope.getAuthentication();
		getFileRoot();
		
		function getFileRoot() {
			service.getFileRoot().success(function(data) {
				var zNodes = data;
//				zNodes.name = 'root';
				zNodes.open = true;
				zTreeObj = $.fn.zTree.init($("#resource-tree"), setting, zNodes);
			});
		}
		
		function zTreeBeforeRemove(treeId, treeNode) {
			var filename;
			if (treeNode.isFirstNode) {
				alert('根目录不能删除!');
				return false;
			}
			if (confirm('确认删除吗？')) {
				filename = ztreeutil.getFilePath(treeNode);
				service['delete'](filename).success(function(data) {
					getFileRoot();
				});
			}
			return false;// 在前端不体现删除的效果，而是由后台刷新实现
		}
		
		function zTreeBeforeRename(treeId, treeNode, newName, isCancel) {
			var srcName, pre, destName;
			if (!isCancel && newName.length > 0) {
				srcName = ztreeutil.getFilePath(treeNode);
				pre = srcName.substring(0, srcName.lastIndexOf('/'));
				if (pre) {
					destName = pre  + '/' + newName;
				} else {
					destName = newName;
				}
//				service.reName(srcDir, newName);
			}
			return false;
		}
		
		var newCount = 1;
		function addHoverDom(treeId, treeNode) {
			var sObj = $("#" + treeNode.tId + "_span");
			if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0) {
				return;
			}
			if (!treeNode.isParent) {// 只在目录上新增目录节点，文件需要上传
				return;
			}
			var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
				+ "' title='add node' onfocus='this.blur();'></span>";
			sObj.after(addStr);
			var btn = $("#addBtn_" + treeNode.tId);
			if (btn) {
				btn.bind("click", function() {
					/*
					var zTree = $.fn.zTree.getZTreeObj("resource-tree");
					zTree.addNodes(treeNode, {
						id : (100 + newCount),
						pId : treeNode.id,
						name : "new node" + (newCount++)
					});
					*/
					var dirName = ztreeutil.getFilePath(treeNode);
					dirName += '/new node' + (newCount++);
					service.createDir(dirName).success(function(data) {
						getFileRoot();
					});
					return false;
				});
			}
		};
	}])
	;
});