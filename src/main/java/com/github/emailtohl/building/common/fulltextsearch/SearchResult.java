package com.github.emailtohl.building.common.fulltextsearch;

/**
 * 存储全文搜索的结果
 * @author HeLei
 *
 * @param <T>
 */
public class SearchResult<T> {
	/**
	 * 结果存储的实体
	 */
	private final T entity;
	
	/**
	 * 与查询匹配的关联度
	 */
	private final double relevance;

	public SearchResult(T entity, double relevance) {
		this.entity = entity;
		this.relevance = relevance;
	}

	public double getRelevance() {
		return relevance;
	}

	public T getEntity() {
		return entity;
	}
}
