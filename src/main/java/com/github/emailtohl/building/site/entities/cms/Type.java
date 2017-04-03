package com.github.emailtohl.building.site.entities.cms;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.github.emailtohl.building.common.jpa.entity.BaseEntity;

/**
 * 文章的分类
 * @author HeLei
 * @date 2017.02.17
 */
@Entity
@Table(name = "t_article_type")
public class Type extends BaseEntity {
	private static final long serialVersionUID = -1103006931831197370L;
	/**
	 * 分类的名字
	 */
	@NotNull
	private String name;
	
	/**
	 * 分类的描述
	 */
	private String description;
	
	/**
	 * 上一级分类
	 */
	private Type parent;
	
	/**
	 * 分类下的文章
	 */
	private Set<Article> articles = new LinkedHashSet<Article>();

	@Column(unique = true, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne
	@JoinColumn(name = "parent_type")
	public Type getParent() {
		return parent;
	}

	public void setParent(Type parent) {
		this.parent = parent;
	}

	// 使用LazyCollectionOption.EXTRA，集合在调用size(),isEmpty(),contains()等操作时不会加载实例
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	/*
	 * 孤儿删除和级联删除的区别：
	 * orphanRemoval is an entirely ORM-specific thing. It marks "child" entity
	 * to be removed when it's no longer referenced from the "parent" entity,
	 * e.g. when you remove the child entity from the corresponding collection
	 * of the parent entity.
	 * 
	 * ON DELETE CASCADE is a database-specific thing, it deletes the "child"
	 * row in the database when the "parent" row is deleted.
	 */
	@OneToMany(mappedBy = "type", orphanRemoval = true)
	@OrderBy(BaseEntity.CREATE_DATE_PROPERTY_NAME)
	public Set<Article> getArticles() {
		return articles;
	}

	public void setArticles(Set<Article> articles) {
		this.articles = articles;
	}

	@Override
	public String toString() {
		return "Type [name=" + name + ", parent=" + parent + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		Type other = (Type) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}
	

}
