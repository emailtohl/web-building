package com.github.emailtohl.building.site.service.cms;

import java.io.Serializable;
import java.util.List;

import com.github.emailtohl.building.site.entities.cms.Article;
import com.github.emailtohl.building.site.entities.cms.Comment;
import com.github.emailtohl.building.site.entities.cms.Type;

/**
 * web页面所需要的数据结构
 * @author HeLei
 * @date 2017.02.18
 */
public class WebPage implements Serializable {
	private static final long serialVersionUID = 2672917432577749422L;
	
	/**
	 * 页面中展现的文章
	 */
	private List<Article> articles;
	
	/**
	 * 总文章数
	 */
	private int total;
	
	/**
	 * 边栏中最新评论
	 */
	private List<Comment> comments;
	
	/**
	 * 边栏中的文章类型
	 */
	private List<Type> types;

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Type> getTypes() {
		return types;
	}

	public void setTypes(List<Type> types) {
		this.types = types;
	}
	
	
}
