package com.github.emailtohl.building.site.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统的用户一般分为外部客户和内部职员
 * 这是为客户建立的实体类
 * 
 * @author HeLei
 */
@org.hibernate.envers.Audited
@Entity
@Table(name = "t_customer")
public class Customer extends User {
	private static final long serialVersionUID = -1136305533524407299L;
	/**
	 * 客户职位
	 */
	private String title;
	/**
	 * 所属机构
	 */
	private String affiliation;
	
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
	@Override
	public String toString() {
		return "Customer [name=" + name + ", title=" + title + ", affiliation=" + affiliation + ", email=" + email + ", address=" + address
				+ ", telephone=" + telephone + ", birthday=" + birthday + ", id=" + id + "]";
	}
	
}
