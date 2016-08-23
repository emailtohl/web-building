package com.github.emailtohl.building.site.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
/**
 * Entity 授权
 * @author Helei
 */
@Entity
@Table(name = "t_authority")
public class Authority implements Serializable {
	private static final long serialVersionUID = -2016506896694264888L;
	private Short id;
	private String name;
	private transient Set<Role> roles = new HashSet<Role>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Short getId() {
		return id;
	}
	public void setId(Short id) {
		this.id = id;
	}
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	// 使用mappedBy将映射关系交到Role类的permissions属性上
	@ManyToMany(targetEntity = Role.class, mappedBy = "authorities", fetch = FetchType.LAZY)
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	@Override
	public String toString() {
		return "Authority [id=" + id + ", name=" + name + "]";
	}

}
