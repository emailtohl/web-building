package com.github.emailtohl.building.site.dto;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.RevisionType;

import com.github.emailtohl.building.site.entities.Role;

/**
 * 角色的数据传输对象，主要传输权限名
 * @author HeLei
 */
public class RoleDto extends Role {
	private static final long serialVersionUID = -5833156704375583102L;
	
	private Set<String> authorityNames = new HashSet<>();
	
	/**
	 * 用于审计数据的修订版本号
	 */
	private Integer revision;
	/**
	 * 用于审计数据的修订时间戳
	 */
	private String revisionDate;
	/**
	 * 用于审计数据的的操作类型
	 */
	private RevisionType revisionType;

	public Set<String> getAuthorityNames() {
		return authorityNames;
	}

	public void setAuthorityNames(Set<String> authorityNames) {
		this.authorityNames = authorityNames;
	}

	public Integer getRevision() {
		return revision;
	}

	public void setRevision(Integer revision) {
		this.revision = revision;
	}

	public String getRevisionDate() {
		return revisionDate;
	}

	public void setRevisionDate(String revisionDate) {
		this.revisionDate = revisionDate;
	}

	public RevisionType getRevisionType() {
		return revisionType;
	}

	public void setRevisionType(RevisionType revisionType) {
		this.revisionType = revisionType;
	}

	@Override
	public String toString() {
		return "RoleDto [authorityNames=" + authorityNames + ", id=" + id + ", createDate=" + createDate
				+ ", modifyDate=" + modifyDate + "]";
	}
}
