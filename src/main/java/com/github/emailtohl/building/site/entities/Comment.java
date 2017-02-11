package com.github.emailtohl.building.site.entities;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * 评论嵌入类
 * @author HeLei
 * @date 2017.02.11
 */
@Embeddable
public class Comment implements Comparable<Comment> {
	@NotNull
	private String content;
	@NotNull
	private Date date;
	private String critics = "匿名";
	private String icon = "";
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCritics() {
		return critics;
	}

	public void setCritics(String critics) {
		this.critics = critics;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public int compareTo(Comment o) {
		return this.getDate().compareTo(o.getDate());
	}
	
}
