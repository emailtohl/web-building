/**
 * 为使用zTree的辅助工具，可将字符串的文件名转换为ztree识别的对象
 * @author HeLei
 * @date 2017.02.04
 */
define([ 'common/module', 'ztree' ], function(commonModule) {
	return commonModule.factory('ztreeutil', [ function() {
		/**
		 * 判断该层级上是否已经创建了此节点，如果已创建，则返回数组序号，否则返回-1
		 */
		function getExistIndex(name, nodes) {
			var i, flag = false;
			for (i = 0; i < nodes.length; i++) {
				if (nodes[i].name == name) {
					flag = true;
					break;
				}
			}
			if (flag) {
				return i;
			} else {
				return -1;
			}
		}
		
		function setNodesOpen(nodes, names) {
			var i, node, name = names.shift();
			if (!name)
				return;
			name = decodeURIComponent(name);
			for (i = 0; i < nodes.length; i++) {
				node = nodes[i];
				if (node.isParent && node.name == name) {
					node.open = true;
					// 既然isParent是true，则children属性一定存在
					arguments.callee(node.children, names);
					break;
				}
			}
		}
		return {
			  /**
			   * 将后台获取的数据（类似于'abc/bcd/eee.exe'，'abc/edf/bbb.exe'）转换成zTree识别的数据结构
			   */
			setNodes : function(path, nodes, isOpen, color) {
				var partition, before, i, after, node;
				if (!path) {
					nodes = [];
					return;
				}
				partition = path.indexOf('/');
				if (partition > 0) {// 如果包含目录
					// 对第一个“/”分割
					before = path.slice(0, partition);
					after = path.slice(partition + 1);
					// 从数组中找到已有的目录
					i = getExistIndex(before, nodes);
					if (i == -1) {
						node = {
							name : before,
							open : isOpen ? isOpen : false,
						};
						node.children = [];
						arguments.callee(after, node.children, isOpen, color);
						nodes.push(node);
					} else {
						node = nodes[i];
						if (!node.open) {// 如果之前已经设置此目录为open状态，则不再覆盖之前的设置
							node.open = isOpen ? isOpen : false;
						}
						arguments.callee(after, node.children, isOpen, color);
					}
				} else {// 否则只是文件
					i = getExistIndex(path, nodes);
					if (i == -1) {
						node = {
							name : path,
							font : color ? {
								color : color
							} : null
						};
						nodes.push(node);
					} else {// 覆盖原先的文件
						node = nodes[i];
						node.name = path;
					}
				}
			},

			/**
			 * 将树中的节点还原为：目录+文件名
			 */
			getFilePath : function(treeNode) {
				var filePath = '';
				while (treeNode) {
					filePath = '/' + treeNode.name + filePath;
					treeNode = treeNode.getParentNode();
				}
				if (filePath && filePath.indexOf('/') == 0) {
					filePath = filePath.slice(1);
				}
				return filePath;
			},
			
			/**
			 * 根据目录设置打开状态
			 */
			setOpen : function(treeNode, path) {
				var names = path.split('/');
				setNodesOpen([treeNode], names);
			},
			/**
			 * 将中文目录转码
			 */
			encodePath : function(path) {
				var temp = path.split('/');
				for (var i = 0; i < temp.length; i++) {
					temp[i] = encodeURIComponent(temp[i]);
				}
				return temp.join('/');
			},
		};
	}]);
});