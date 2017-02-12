package com.github.emailtohl.building.site.entities.organization;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.github.emailtohl.building.common.jpa.entity.BaseEntity;
/**
 * 公司实体
 * @author HeLei
 * @date 2017.02.04
 */
@Entity
@Table(name = "t_company")
public class Company extends BaseEntity {
	private static final long serialVersionUID = 2560110793039918070L;
	private String name;
	private String description;
	private transient Set<Department> departments;
	
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
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	public Set<Department> getDepartments() {
		return departments;
	}
	public void setDepartments(Set<Department> departments) {
		this.departments = departments;
	}
	
	@Override
	public String toString() {
		return "Company [name=" + name + ", description=" + description + "]";
	}
	
}
