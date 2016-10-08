package com.github.emailtohl.building.site.dto;

import com.github.emailtohl.building.site.entities.Manager;

/**
 * User的数据传输对象，与User实体对象不同，数据传输对象专门传递信息，也可以接收前端的表单信息
 * 
 * @author HeLei
 */
public class UserDto extends Manager {
	private static final long serialVersionUID = -889260420992096961L;
	/**
	 * 在继承结构上区分是哪种实体
	 */
	private UserType userType;
	
	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public static enum UserType {
		USER, EMPLOYEE, MANAGER;
	}
}
