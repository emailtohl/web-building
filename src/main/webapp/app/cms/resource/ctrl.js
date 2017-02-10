define(['jquery', 'cms/module', 'cms/service', 'ztree'], function($, cmsModule) {
	return cmsModule
	.controller('ResourceCtrl', ['$scope', '$http', '$state', 'cmsService', 'util', 'ztreeutil',
	                                function($scope, $http, $state, service, util, ztreeutil) {
		var self = this, rootName, style, zTreeObj;
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
				onClick : zTreeOnClick,
			},
			view : {
				selectedMulti: false,
				addHoverDom : addHoverDom,// 添加一个hover的按钮，处理新增逻辑
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
		util.loadasync('lib/ztree/diy.css');
		$scope.getAuthentication();
		getFileRoot();
		// 上传结束后的逻辑
		self.postUpload = function(msg) {
			console.log(msg);
			getFileRoot(self.path);
		};
		self.invalidFile = function() {
			return $('input[name="file"]').val() ? false : true;
		}
		
		function getFileRoot(openPath) {
			service.getFileRoot().success(function(data) {
				var zNodes = data;
				rootName = zNodes.name;
				zNodes.open = true;
				if (openPath) {
					ztreeutil.setOpen(zNodes, openPath);
				}
				zTreeObj = $.fn.zTree.init($("#resource-tree"), setting, zNodes);
			});
		}
		
		function zTreeBeforeRemove(treeId, treeNode) {
			var filename;
			if (treeNode.name == rootName && treeNode.getParentNode() == null) {
				alert('根目录不能删除!');
				return false;
			}
			if (confirm('确认删除吗？')) {
				filename = ztreeutil.getFilePath(treeNode);
				service['delete'](filename).success(function(data) {
					getFileRoot(filename);
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
				service.reName(srcName, destName).success(function(data) {
					getFileRoot(destName);
				});
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
				+ "' title='add node' onfocus='this.blur();' style='margin-left:5px;'></span>";
			sObj.after(addStr);
			var addBtn = $("#addBtn_" + treeNode.tId);
			if (addBtn) {
				addBtn.bind("click", function() {
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
						getFileRoot(dirName);
					});
					return false;
				});
			}
		};
		function zTreeOnClick(event, treeId, treeNode) {
			if (!treeNode.isParent) {// 只在目录上才有效
				return;
			}
			$scope.$apply(function() {
				self.path = ztreeutil.getFilePath(treeNode);
				self.path = ztreeutil.encodePath(self.path);
			});
		}
	}])
	;
});