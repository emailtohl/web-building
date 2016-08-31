package com.github.emailtohl.building.site.entities;

import java.util.Collection;
import java.util.Iterator;

/**
 * 用户的权限，符合spring security的授权方式
 * @author Helei
 */
public enum Authority {
	ADMIN("系统管理员"), EMPLOYEE("职员"), MANAGER("经理"), USER("普通用户");
	
	private String zhName;
	Authority(String zhName) {
		this.zhName = zhName;
	}
	
	@Override
	public String toString() {
		return this.zhName;
	}
	
	/**
	 * 将枚举集合转换为字符串数组
	 * @param collection
	 * @return
	 */
	public static String[] toStringArray(Collection<Authority> collection) {
		Iterator<Authority> i = collection.iterator();
		String[] arr = new String[collection.size()];
		int j = 0;
		while (i.hasNext()) {
			arr[j] = i.next().name();
			j++;
		}
		return arr;
	}
}
