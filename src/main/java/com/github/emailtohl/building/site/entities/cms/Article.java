package com.github.emailtohl.building.site.entities.cms;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.github.emailtohl.building.common.jpa.entity.BaseEntity;
import com.github.emailtohl.building.site.entities.user.User;

/**
 * 文章实体
 * @author HeLei
 * @date 2017.02.11
 */
@org.hibernate.envers.Audited
@org.hibernate.search.annotations.Indexed
@org.hibernate.search.annotations.Analyzer(impl = org.apache.lucene.analysis.standard.StandardAnalyzer.class)
@Entity
@Table(name = "t_article")
public class Article extends BaseEntity {
	private static final long serialVersionUID = -5430993268188939177L;
	@NotNull
	private String title;
	private String keywords;
	@NotNull
	private String body;
	@NotNull
	private User author;
	private String type;
	private boolean isComment = true;
	private List<Comment> comments = new ArrayList<>();
	
	@org.hibernate.search.annotations.Field(store = org.hibernate.search.annotations.Store.YES)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@org.hibernate.envers.NotAudited
	@org.hibernate.search.annotations.Field(boost = @org.hibernate.search.annotations.Boost(1.5F), store = org.hibernate.search.annotations.Store.YES)// 关键字加权因子
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	@org.hibernate.search.annotations.Field(store = org.hibernate.search.annotations.Store.NO)
	@Lob
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	@org.hibernate.envers.NotAudited
	@org.hibernate.search.annotations.IndexedEmbedded
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "author_id")
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	
	@org.hibernate.envers.NotAudited
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@org.hibernate.envers.NotAudited
	@Column(name = "is_comment")
	public boolean isComment() {
		return isComment;
	}
	public void setComment(boolean isComment) {
		this.isComment = isComment;
	}
	
	@org.hibernate.envers.NotAudited
	@ElementCollection(targetClass = Comment.class, fetch = FetchType.LAZY)
	@CollectionTable(name = "t_comment")
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
}
