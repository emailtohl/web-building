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
		service.getAvailableCharsets().success(function(data) {
			self.availableCharsets = data;
		});
		
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
					self.contentType = 'image';
					$scope.$apply(function() {
						self.content = path;
					});
					break;
				case 'png':
					self.contentType = 'image';
					break;
				case 'mp4':
					self.contentType = 'video';
					break;
				case 'mp3':
					self.contentType = 'audio';
					break;
				case 'pdf':
					self.contentType = 'pdf';
					break;
				default:
					self.contentType = 'text';
					loadText(path);
					break;
				}
			}
		}
		
		/**
		 * 加载文本内容
		 */
		function loadText(path) {
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
						var cm = CodeMirror.fromTextArea(textarea, {
							lineNumbers: true,
						    mode: "htmlmixed",
						});
						cm.on('change', function(_codeMirror, changeObj) {
							self.content = _codeMirror.doc.getValue();
						});  
					});
			});
		}
		
	}])
	;
});