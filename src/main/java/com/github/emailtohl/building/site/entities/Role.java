package com.github.emailtohl.building.site.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
/**
 * Entity 角色表
 * @author Helei
 */
@Entity
@Table(name = "t_role")
public class Role implements Serializable {
	private static final long serialVersionUID = -2461761212651426011L;
	private Short id;
	private String name;
	private transient Set<User> users = new HashSet<User>();
	private Set<Authority> authorities = new HashSet<Authority>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Short getId() {
		return id;
	}
	public void setId(Short id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	// 使用mappedBy将映射关系交到User类的roles属性上
	@ManyToMany(targetEntity = User.class, mappedBy = "roles", fetch = FetchType.LAZY)
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
		return "Role [id=" + id + ", name=" + name + ", authorities=" + authorities + "]";
	}
}
