package com.github.emailtohl.building.site.entities.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.github.emailtohl.building.common.jpa.entity.BaseEntity;

/**
 * 评论嵌入类
 * @author HeLei
 * @date 2017.02.11
 */
@org.hibernate.annotations.BatchSize(size = 10)// 因n+1查询问题，盲猜优化，一次性加载size个代理
@Entity
@Table(name = "t_article_comment")
public class Comment extends BaseEntity implements Comparable<Comment> {
	private static final long serialVersionUID = 2074688008515735092L;
	
	@NotNull
	private String content;
	private String critics = "匿名";
	private String icon = "";
	@NotNull
	private Article article;
	private boolean isApproved = true;
	
	@org.hibernate.search.annotations.Field
	@Lob
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@org.hibernate.search.annotations.Field
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

	@ManyToOne
	@JoinColumn(name = "article_id", nullable = false)
	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	@Column(name = "is_approved")
	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	@Override
	public int compareTo(Comment o) {
		return getCreateDate().compareTo(o.getCreateDate());
	}
	
}
