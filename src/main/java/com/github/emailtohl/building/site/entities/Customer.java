package com.github.emailtohl.building.site.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统的用户一般分为外部客户和内部职员
 * 这是为客户建立的实体类
 * 
 * @author HeLei
 */
@Entity
@Table(name = "t_customer")
public class Customer extends User {
	private static final long serialVersionUID = -1136305533524407299L;
	
}
