package com.github.emailtohl.building.site.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 角色类
 * @author HeLei
 */
@Entity
@Table(name = "t_role")
public class Role extends BaseEntity {
	private static final long serialVersionUID = 5715974372158270885L;
	/**
	 * 管理员
	 */
	public static final String ADMIN = "admin";
	/**
	 * 经理
	 */
	public static final String MANAGER = "manager";
	/**
	 * 雇员
	 */
	public static final String EMPLOYEE = "employee";
	/**
	 * 普通用户
	 */
	public static final String USER = "user";
	
	public Role() {
		super();
	}
	
	public Role(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	@NotNull
	private String name;
	private String description;
	private Set<User> users = new HashSet<User>();
	private Set<Authority> authorities = new HashSet<Authority>();
	
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
	
	@ManyToMany(targetEntity = User.class, fetch = FetchType.LAZY, mappedBy = "roles")
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	@ManyToMany(targetEntity = Authority.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "t_role_authority"
	, joinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "id") }
	, inverseJoinColumns = { @JoinColumn(name = "authority_id", referencedColumnName = "id") })
	public Set<Authority> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public String toString() {
		return "Role [name=" + name + ", authorities=" + authorities + "]";
	}
	
}
