define(['jquery', 'cms/module', 'cms/resource/service', 'ztree'], function($, cmsModule) {
	return cmsModule
	.controller('ResourceCtrl', ['$scope', '$http', '$state', 'resourceService', 'util', 'ztreeutil',
	                                function($scope, $http, $state, service, util, ztreeutil) {
		var self = this, hideRoot = 'resource'/*这个路径在rootName上一级，用于前端访问所用*/, rootName, style, zTreeObj, cm;
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
				fontCss: function(treeId, treeNode) {
					// 被搜索到的节点设置为红色
					return treeNode.selected ? {color:"red"} : {};
				}
			},
			/*
			check : {
				enable: true,
				chkStyle: 'checkbox',
				chkboxType:  { 'Y' : 'ps', 'N' : 'ps' },
			},
			*/
		};
		
		self.currentNodeIsFile = false;// 若点击是文件夹，则展示文件上传的表单，若点击的是文件，则展示文件的内容
		self.charset = 'UTF-8';// 文本文件默认字符集
		self.contentType = ''; // 文件的类型、txt/image/pdf等等
		self.content = '';// 文本文件的内容
		self.path = '';// 维护当前被点击节点的路径的信息
		self.dirty = false;// 判断文本是否被修改过，由CodeMirror触发的修改事件修改该状态
		
		util.loadasync('lib/ztree/zTreeStyle.css');
		util.loadasync('lib/ztree/diy.css');
		$scope.getAuthentication();
		
		// 加载根目录
		getFileRoot();
		// 获取所有的字符集
		service.getAvailableCharsets().success(function(data) {
			self.availableCharsets = data;
		});
		// 全文搜索整个文件系统，找出跟查询条件匹配的文件
		self.query = function() {
			service.query(self.queryParam).success(function(data) {
				var zNodes = data;
				rootName = zNodes.name;
				zTreeObj = $.fn.zTree.init($("#resource-tree"), setting, zNodes);
			});
		};
		// 修改文件文本
		self.updateText = function() {
			if (!self.dirty)
				return;
			// 后台识别的是相对路径
			var i = self.path.indexOf(rootName + '/');
			var path = self.path.substring(i);
			service.writeText(path, self.content, self.charset).success(function(data) {
				getFileRoot(self.path);
			});
		};
		// 上传结束后的逻辑
		self.postUpload = function(msg) {
			console.log(msg);
			getFileRoot(self.path);
		};
		// 用于提交表达的校验，若上传文件为空，则提交按钮不被开放
		self.invalidFile = function() {
			return $('input[name="file"]').val() ? false : true;
		};
		// 获取下载图片、视频、pdf等文件的路径，这是因为下载路径还需要加上隐藏的根路径
		self.getDownloadPath = function() {
			return hideRoot + '/' + self.path;
		};
		
		/**
		 * 获取根目录
		 */
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
		
		/**
		 * 删除节点前的逻辑：
		 * 先去后台删除对应的节点，如果后台删除成功，前端页面就重载根目录
		 */
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
		
		/**
		 * 重命名前的逻辑：
		 * 先去后台重命名对应的节点，如果后台修改成功，前端页面就重载根目录，并指定被修改的节点为打开状态
		 */
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
		
		/**
		 * 当鼠标hover到节点时，会添加一个按钮，下面是添加按钮的逻辑：
		 * 1.若是文件节点或者已经添加了按钮，则忽略
		 * 2.通过创建dom元素，添加一个按钮
		 * 3.为该按钮绑定一个点击监听器：首先向后台提交一个创建文件夹的请求，待后台响应时，重新加载整个目录
		 */
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
		/**
		 * 处理节点被点击时的逻辑
		 */
		function zTreeOnClick(event, treeId, treeNode) {
			if (treeNode.isParent) {
				self.currentNodeIsFile = false;
				$scope.$apply(function() {
					self.path = ztreeutil.getFilePath(treeNode);
					self.path = ztreeutil.encodePath(self.path);
				});
			} else {
				self.currentNodeIsFile = true;
				onFileClick(event, treeId, treeNode);
			}
		}
		
		/**
		 * 当文件被点击时，展示文件的逻辑：
		 * 文本文件就直接打开，pdf另起一页观看，视频和音频则下载……
		 */
		function onFileClick(event, treeId, treeNode) {
			var path, suffixIndex, suffix, mode;
			if (treeNode.isParent) {// 只在文件上才有效
				return;
			}
			path = ztreeutil.getFilePath(treeNode);
			self.path = path;// 相对路径
			// 根据文件后缀做判断
			suffixIndex = path.lastIndexOf('.');
			if (suffixIndex > -1) {
				suffix = path.substring(suffixIndex + 1, path.length);
				switch (suffix) {
				case 'jpg':
					$scope.$apply(function() {
						self.contentType = 'image';
					});
					break;
				case 'bmp':
					$scope.$apply(function() {
						self.contentType = 'image';
					});
					break;
				case 'png':
					$scope.$apply(function() {
						self.contentType = 'image';
					});
					break;
				case 'gif':
					$scope.$apply(function() {
						self.contentType = 'image';
					});
					break;
				case 'mp4':
					$scope.$apply(function() {
						self.contentType = 'video';
					});
					break;
				case 'ogv':
					$scope.$apply(function() {
						self.contentType = 'video';
					});
					break;
				case 'webm':
					$scope.$apply(function() {
						self.contentType = 'video';
					});
					break;
				case 'mp3':
					$scope.$apply(function() {
						self.contentType = 'audio';
					});
					break;
				case 'pdf':
					$scope.$apply(function() {
						self.contentType = 'pdf';
					});
					if (window.confirm('是否新起一页打开该PDF文档？'))
						window.open(self.getDownloadPath());
					break;
				default:
					self.contentType = 'text';
					switch (suffix) {
					case 'js':
						mode = 'javascript';
						break;
					case 'css':
						mode = 'css';
						break;
					case 'xml':
						mode = 'xml';
						break;
					default:
						mode = 'htmlmixed';
						break;
					}
					loadText(path, mode);
					break;
				}
			}
		}
		
		/**
		 * 加载文本内容
		 */
		function loadText(path, mode) {
			util.loadasync('lib/codemirror/lib/codemirror.css');
			/**
			 * It will automatically load the modes that the mixed HTML mode depends on (XML, JavaScript, and CSS)
			 */
			require([ 'lib/codemirror/lib/codemirror', 'lib/codemirror/mode/htmlmixed/htmlmixed'
				, 'lib/codemirror/mode/javascript/javascript', 'lib/codemirror/mode/xml/xml'
				, 'lib/codemirror/mode/diff/diff', 'lib/codemirror/mode/css/css'],
				function(CodeMirror) {
					service.loadText(path, self.charset).success(function(data) {
						var textarea = document.getElementById('cms-content-text');
						textarea.innerHTML = '';
						$('div.CodeMirror').remove();
						self.content = data;
						textarea.value = data;
						cm = CodeMirror.fromTextArea(textarea, {
							lineNumbers: true,
						    mode : mode ? mode : 'htmlmixed',
						});
						cm.setSize('auto','450px');
						cm.on('change', function(_codeMirror, changeObj) {
							self.content = _codeMirror.doc.getValue();
							if (!self.dirty) {
								$scope.$apply(function() {
									self.dirty = true;
								});
							}
						});  
					});
			});
		}
		
	}])
	;
});