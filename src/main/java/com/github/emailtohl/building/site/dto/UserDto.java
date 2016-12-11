package com.github.emailtohl.building.site.dto;

import org.springframework.beans.BeanUtils;

import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.entities.Department;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.User;

/**
 * User的数据传输对象，与User实体对象不同，数据传输对象专门传递信息，也可以接收前端的表单信息
 * 
 * @author HeLei
 */
public class UserDto extends User {
	private static final long serialVersionUID = -889260420992096961L;
	
	/**
	 * 创建用户，更改密码时，需要传输明文密码
	 */
	private String plainPassword;
	
	private Integer empNum;
	private String post;
	private Double salary;
	private Department department;
	private String title;
	private String affiliation;
	
	public String getPlainPassword() {
		return plainPassword;
	}

	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}
	
	
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

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	/**
	 * 将数据传输对象中的数据传入实体对象中
	 * @param clz
	 * @return
	 */
	public User convertUser() {
		User u = new User();
		BeanUtils.copyProperties(this, u, "iconSrc", "icon", "password");
		u.setPassword(plainPassword);
		return u;
	}
	
	public Employee convertEmployee() {
		Employee e = new Employee();
		BeanUtils.copyProperties(this, e, "iconSrc", "icon", "password");
		e.setPassword(plainPassword);
		return e;
	}
	
	public Customer convertCustomer() {
		Customer c = new Customer();
		BeanUtils.copyProperties(this, c, "iconSrc", "icon", "password");
		c.setPassword(plainPassword);
		return c;
	}
}
