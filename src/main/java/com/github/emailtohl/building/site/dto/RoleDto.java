package com.github.emailtohl.building.site.dto;

import java.util.HashSet;
import java.util.Set;

import com.github.emailtohl.building.site.entities.Role;

/**
 * 角色的数据传输对象，主要传输权限名
 * @author HeLei
 */
public class RoleDto extends Role {
	private static final long serialVersionUID = -5833156704375583102L;
	
	private Set<String> authorityNames = new HashSet<>();

	public Set<String> getAuthorityNames() {
		return authorityNames;
	}

	public void setAuthorityNames(Set<String> authorityNames) {
		this.authorityNames = authorityNames;
	}

	@Override
	public String toString() {
		return "RoleDto [authorityNames=" + authorityNames + ", id=" + id + ", createDate=" + createDate
				+ ", modifyDate=" + modifyDate + "]";
	}
}
