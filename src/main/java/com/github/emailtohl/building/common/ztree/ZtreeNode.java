package com.github.emailtohl.building.common.ztree;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * 前端zTree的数据模型
 * 
 * @author HeLei
 */
public class ZtreeNode implements Serializable , Comparable<ZtreeNode>{
	private static final long serialVersionUID = -1932148922352477076L;
	private transient static volatile long serial = 0;
	
	public ZtreeNode() {
		synchronized(ZtreeNode.class) {
			if (serial == Long.MAX_VALUE) {
				serial = 0;
			}
			serial++;
			id = serial;
		}
	}
	
	/**
	 * 根据文件目录创建一个ZtreeNode实例
	 * @param absolutePath 文件目录
	 * @return ZtreeNode实例
	 */
	public static ZtreeNode newInstance(String absolutePath) {
		return newInstance(new File(absolutePath));
	}
	
	/**
	 * 根据文件目录创建一个ZtreeNode实例
	 * @param f 文件目录
	 * @return ZtreeNode实例
	 */
	public static ZtreeNode newInstance(File f) {
		return newInstance(f, 0);
	}
	
	private static ZtreeNode newInstance(File f, long pid) {
		ZtreeNode n = new ZtreeNode();
		n.name = f.getName();
		n.pid = pid;
		if (f.isDirectory()) {
			Set<ZtreeNode> children = new TreeSet<>();
			for (File sf : f.listFiles()) {
				children.add(newInstance(sf, n.id));
			}
			n.children = children;
			n.isParent = true;
		} else {
			n.isParent = false;
		}
		return n;
	}

	
	/** id 自动生成 */
	private final long id;
	
	/** pid 父节点，根节点为0 */
	private long pid = 0;
	
	/** 节点名 */
	private String name;
	
	/** 记录 treeNode 节点是否为父节点 */
	@SuppressWarnings("unused")
	private boolean isParent = true;
	
	/** 判断 treeNode 节点是否被隐藏 */
	private boolean isHidden = false;
	
	/** 记录 treeNode 节点的 展开 / 折叠 状态 */
	private boolean open = false;
	
	/** 节点的 checkBox / radio 的 勾选状态 [setting.check.enable = true & treeNode.nocheck = false 时有效] */
	private boolean checked = false;
	
	/** 设置节点的 checkbox / radio 是否禁用 [setting.check.enable = true 时有效] */
	private boolean chkDisabled = false;
	
	/** 设置节点是否隐藏 checkbox / radio [setting.check.enable = true 时有效] */
	private boolean nocheck = false;
	
	/** 节点自定义图标的 URL 路径 */
	private String icon;
	
	/** 父节点自定义折叠时图标的 URL 路径 */
	private String iconClose;
	
	/** 父节点自定义展开时图标的 URL 路径 */
	private String iconOpen;
	
	/** 节点自定义图标的 className */
	private String iconSkin;
	
	/** 设置点击节点后在何处打开 url。[treeNode.url 存在时有效] 例如：{ "id":1, "name":"test1", "url":"http://myTest.com", "target":"_blank"} */
	private String target;
	
	/** 节点链接的目标 URL */
	private String url;
	
	/** 节点的子节点数据集合， 在前端，如果是文件而非目录，该字段应该为null，所以此处不初始化 */
	private Set<ZtreeNode> children;

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** 记录 treeNode 节点是否为父节点，依赖于children属性 */
	public boolean isParent() {
		return children != null;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
//	private Pattern p = Pattern.compile(File.separator);
	private Pattern p = Pattern.compile("[\\\\/]");
	/**
	 * 根据路径匹配，打开对应的目录
	 * @param path
	 */
	public void setOpen(String path) {
		LinkedList<String> queue = new LinkedList<String>();
		for (String name : path.split(p.pattern())) {
			queue.add(name);
		}
		Set<ZtreeNode> nodes = new TreeSet<ZtreeNode>();
		nodes.add(this);
		setOpen(nodes, queue);
	}
	
	private void setOpen(Set<ZtreeNode> nodes, LinkedList<String> queue) {
		String name = queue.poll();
		for (ZtreeNode node : nodes) {
			if (!node.isParent || name == null || !name.equals(node.name))
				continue;
			node.open = true;
			// 根据定义node.isParent == true，那么node.children != null，不过保险起见还是做判断
			if (node.children != null) {
				setOpen(node.children, queue);
			}
		}
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isChkDisabled() {
		return chkDisabled;
	}

	public void setChkDisabled(boolean chkDisabled) {
		this.chkDisabled = chkDisabled;
	}

	public boolean isNocheck() {
		return nocheck;
	}

	public void setNocheck(boolean nocheck) {
		this.nocheck = nocheck;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconClose() {
		return iconClose;
	}

	public void setIconClose(String iconClose) {
		this.iconClose = iconClose;
	}

	public String getIconOpen() {
		return iconOpen;
	}

	public void setIconOpen(String iconOpen) {
		this.iconOpen = iconOpen;
	}

	public String getIconSkin() {
		return iconSkin;
	}

	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Set<ZtreeNode> getChildren() {
		return children;
	}

	public void setChildren(Set<ZtreeNode> children) {
		this.children = children;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", name=" + name + ", children=" + children + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZtreeNode other = (ZtreeNode) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(ZtreeNode o) {
		return name.compareTo(o.name);
	}
	
}
