package com.github.emailtohl.building.site.controller.form;

import com.github.emailtohl.building.site.entities.Manager;

/**
 * User的表单对象，与User实体对象不同，表单对象专门接收前端简单的表单信息
 * 
 * 不过前端若通过json提交复杂的数据模型，可表示继承、关联等关系时，本表单对象就可以直接继承User实体了
 * 
 * 为获取最广泛的数据，UserForm继承Manager实体
 * 
 * @author HeLei
 */
public class UserForm extends Manager {
	private static final long serialVersionUID = -889260420992096961L;

}
