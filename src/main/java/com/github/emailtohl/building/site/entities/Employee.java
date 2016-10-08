package com.github.emailtohl.building.site.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

@Entity
@Table(name = "t_employee")
public class Employee extends User {
	private static final long serialVersionUID = 3500096827826710751L;
	private Integer empNum;
	private String post;
	private Double salary;
	private Department department;
	
	@Column(name = "emp_num", unique = true/*, nullable = false*/) // 这里不能声明不可为空，否则会影响其他继承层次上的关系，这就是弊端
	@Min(value = 1)
	public Integer getEmpNum() {
		return empNum;
	}
	public void setEmpNum(Integer empNum) {
		this.empNum = empNum;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public Double getSalary() {
		return salary;
	}
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "department_id")
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
}
