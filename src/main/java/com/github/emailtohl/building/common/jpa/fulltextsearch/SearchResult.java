package com.github.emailtohl.building.common.jpa.fulltextsearch;

import java.io.Serializable;

/**
 * 存储全文搜索的结果
 * @author HeLei
 *
 * @param <E extends Serializable>
 */
public class SearchResult<E extends Serializable> {
	/**
	 * 结果存储的实体
	 */
	private final E entity;
	
	/**
	 * 与查询匹配的关联度
	 */
	private final double relevance;

	public SearchResult(E entity, double relevance) {
		this.entity = entity;
		this.relevance = relevance;
	}

	public double getRelevance() {
		return relevance;
	}

	public E getEntity() {
		return entity;
	}
}
