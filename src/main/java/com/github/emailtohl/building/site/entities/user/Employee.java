package com.github.emailtohl.building.site.entities.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;

import com.github.emailtohl.building.site.entities.organization.Department;
/**
 * 系统的用户一般分为外部客户和内部职员
 * 这是为内部职员建立的实体类
 * @author HeLei
 * @date 2017.02.04
 */
@org.hibernate.envers.Audited
@Entity
//@Table(name = "t_employee")
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
	
	@org.hibernate.envers.NotAudited
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "department_id")
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	@Override
	public String toString() {
		return "Employee [empNum=" + empNum + ", post=" + post + ", salary=" + salary + ", department=" + department
				+ ", email=" + email + ", address=" + address + ", telephone=" + telephone + ", birthday=" + birthday
				+ ", description=" + description + ", roles=" + roles + "]";
	}
	
}
