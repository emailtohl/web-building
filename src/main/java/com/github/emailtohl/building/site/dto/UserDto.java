package com.github.emailtohl.building.site.dto;

import com.github.emailtohl.building.site.entities.Employee;

/**
 * User的数据传输对象，与User实体对象不同，数据传输对象专门传递信息，也可以接收前端的表单信息
 * 
 * @author HeLei
 */
public class UserDto extends Employee {
	private static final long serialVersionUID = -889260420992096961L;
	/**
	 * 在继承结构上区分是哪种实体
	 */
	private UserType userType;
	
	/**
	 * 创建用户，更改密码时，需要传输明文密码
	 */
	private String plainPassword;
	
	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getPlainPassword() {
		return plainPassword;
	}

	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}

	public static enum UserType {
		USER, EMPLOYEE, MANAGER;
	}
}
