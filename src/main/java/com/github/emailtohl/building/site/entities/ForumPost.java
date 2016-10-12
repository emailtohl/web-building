package com.github.emailtohl.building.site.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

/**
 * 论坛帖子对象
 * 
 * @author HeLei
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "t_Post")
@Indexed
//@Analyzer(impl = IKAnalyzer.class)
public class ForumPost extends BaseEntity {
	private static final long serialVersionUID = 5500398003740322853L;
	private User user;
	private String title;
	private String body;
	private String keywords;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "user_id")
	@IndexedEmbedded
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Basic
	@Field
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Lob
	@Field
	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Basic
	@Field(boost = @Boost(2.0F))// 关键字加权因子
	public String getKeywords() {
		return this.keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

}
