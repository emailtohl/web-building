define(['jquery', 'cms/module', 'cms/service', 'ztree'], function($, cmsModule) {
	return cmsModule
	.controller('ContentCtrl', ['$scope', '$http', '$state', 'cmsService', 'util', 'ztreeutil',
	                                function($scope, $http, $state, service, util, ztreeutil) {
		var self = this, hideRoot = 'resource'/*这个路径在rootName上一级，用于前端访问所用*/, rootName, style, zTreeObj, cm;
		self.charset = 'UTF-8';
		self.contentType = '';
		self.content = '';
		self.path = '';
		self.dirty = false;
		var setting = {
			callback : {
				onClick : zTreeOnClick,
			},
			view: {
				fontCss: function(treeId, treeNode) {
					// 被搜索到的节点设置为红色
					return treeNode.selected ? {color:"red"} : {};
				}
			}
		};
		util.loadasync('lib/ztree/zTreeStyle.css');
		$scope.getAuthentication();
		getFileRoot();
		service.getAvailableCharsets().success(function(data) {
			self.availableCharsets = data;
		});
		
		self.query = function() {
			service.query(self.queryParam).success(function(data) {
				var zNodes = data;
				rootName = zNodes.name;
				zTreeObj = $.fn.zTree.init($("#content-tree"), setting, zNodes);
			});
		}
		
		self.updateText = function() {
			if (!self.dirty)
				return;
			service.writeText(self.path, self.content, self.charset).success(function(data) {
				getFileRoot(self.path);
			});
		}
		
		function getFileRoot(openPath) {
			service.getFileRoot().success(function(data) {
				var zNodes = data;
				rootName = zNodes.name;
				zNodes.open = true;
				if (openPath) {
					ztreeutil.setOpen(zNodes, openPath);
				}
				zTreeObj = $.fn.zTree.init($("#content-tree"), setting, zNodes);
			});
		}
		
		function zTreeOnClick(event, treeId, treeNode) {
			var path, suffixIndex, suffix, mode;
			if (treeNode.isParent) {// 只在文件上才有效
				return;
			}
			path = ztreeutil.getFilePath(treeNode);
			self.path = hideRoot + '/' + path;
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
						window.open(self.path);
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