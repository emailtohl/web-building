package com.github.emailtohl.building.site.entities;
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
}
