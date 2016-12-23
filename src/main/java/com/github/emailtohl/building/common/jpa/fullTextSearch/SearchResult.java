package com.github.emailtohl.building.common.jpa.fullTextSearch;

import java.io.Serializable;

import org.apache.lucene.document.Document;

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
	private E entity;
	
	/**
	 * 与查询匹配的关联度
	 */
	private double relevance;
	
	/**
	 * lucence的Document
	 */
	private Document doc;

	public SearchResult() {
		super();
	}

	public SearchResult(E entity, double relevance, Document doc) {
		super();
		this.entity = entity;
		this.relevance = relevance;
		this.doc = doc;
	}

	public double getRelevance() {
		return relevance;
	}

	public E getEntity() {
		return entity;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
	
}
