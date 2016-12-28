package com.github.emailtohl.building.site.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 角色关联的授权
 * @author HeLei
 */
@org.hibernate.envers.Audited
@Entity
@Table(name = "t_authority")
public class Authority extends BaseEntity {
	private static final long serialVersionUID = 2353467451352218773L;
	/**
	 * 权限配置的权限
	 */
	public static final String USER_ROLE_AUTHORITY_ALLOCATION = "user_role_authority_allocation";
	/**
	 * 创建普通账号，用于用户自行注册时
	 */
	public static final String USER_CREATE_ORDINARY = "user_create_ordinary";
	/**
	 * 创建有一定权限的账号，用于管理员创建时
	 */
	public static final String USER_CREATE_SPECIAL = "user_create_special";
	/**
	 * 激活账号
	 */
	public static final String USER_ENABLE = "user_enable";
	/**
	 * 禁用账号
	 */
	public static final String USER_DISABLE = "user_disable";
	/**
	 * 授予用户角色
	 */
	public static final String USER_GRANT_ROLES = "user_grant_roles";
	/**
	 * 读取所有用户的权限
	 */
	public static final String USER_READ_ALL = "user_read_all";
	/**
	 * 读取自己账号信息
	 */
	public static final String USER_READ_SELF = "user_read_self";
	/**
	 * 修改所有用户的权限，用于管理员
	 */
	public static final String USER_UPDATE_ALL = "user_update_all";
	/**
	 * 修改自己账号的权限，用于普通用户
	 */
	public static final String USER_UPDATE_SELF = "user_update_self";
	/**
	 * 删除用户的权限
	 */
	public static final String USER_DELETE = "user_delete";
	/**
	 * 客户管理的权限
	 */
	public static final String USER_CUSTOMER = "user_customer";
	/**
	 * 处理申请单状态的权限
	 */
	public static final String APPLICATION_FORM_TRANSIT = "application_form_transit";
	/**
	 * 删除申请单
	 */
	public static final String APPLICATION_FORM_DELETE = "application_form_delete";
	/**
	 * 查询申请单的处理历史
	 */
	public static final String APPLICATION_FORM_READ_HISTORY = "application_form_read_history";
	/**
	 * 删除论坛帖子
	 */
	public static final String FORUM_DELETE = "forum_delete";
	/**
	 * 审计修改用户信息
	 */
	public static final String AUDIT_USER = "audit_user";
	/**
	 * 审计修改角色信息
	 */
	public static final String AUDIT_ROLE = "audit_role";
	
	public Authority() {
		super();
	}
	public Authority(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}
	
	@NotNull
	private String name;
	private String description;
	private transient Set<Role> roles = new HashSet<Role>();
	
	@Column(nullable = false, unique = true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@org.hibernate.envers.NotAudited
	@org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	@ManyToMany(targetEntity = Role.class, fetch = FetchType.LAZY, mappedBy = "authorities")
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	@Override
	public String toString() {
		return "Authority [name=" + name + "]";
	}
	
	/**
	 * 基于唯一标识name的equals和hashCode方法
	 */
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Authority))
			return false;
		final Authority that = (Authority) other;
		if (this.name == null || that.getName() == null)
			return false;
		else
			return this.name.equals(that.getName());
	}
}
