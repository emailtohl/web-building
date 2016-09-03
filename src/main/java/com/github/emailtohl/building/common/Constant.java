package com.github.emailtohl.building.common;
/**
 * 常量定义
 * @author HeLei
 */
public interface Constant {
	/**
	 * 邮箱的正则匹配式
	 */
	String PATTERN_EMAIL = "^[a-z0-9`!#$%^&*'{}?/+=|_~-]+(\\.[a-z0-9`!#$%^&*'{}?/+=" +
			"|_~-]+)*@([a-z0-9]([a-z0-9-]*[a-z0-9])?)+(\\.[a-z0-9]" +
			"([a-z0-9-]*[a-z0-9])?)*$";
}
