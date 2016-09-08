package com.github.emailtohl.building.common.jpa;

import java.io.Serializable;
import java.util.List;

/**
 *************************************************
 * 封装分页查询功能，提供查询结果以及附属信息，如最大页数、当前页数等
 * 
 * @author HeLei
 *************************************************
 */
public class Pager<T> implements Serializable {
	private static final long serialVersionUID = -5098353318676033935L;
	/**
	 * 存储查询结果
	 */
	private List<T> content;
	
	/**
	 * 总记录数
	 */
	private long totalElements;
	
	/**
	 * 每页最大行数，默认20条
	 */
	private int pageSize;
	
	/**
	 * 总页面数
	 */
	private int totalPages;
	
	/**
	 * 当前页码，默认第0页开始
	 */
	private int pageNumber;
	
	/**
	 * 偏移量，返回的结果从此行开始
	 */
	private int offset;

	public Pager(List<T> content) {
		this(content, content.size());
	}
	
	public Pager(List<T> content, long totalElements) {
		// 默认每页20条
		this(content, totalElements, 20);
	}
	
	public Pager(List<T> content, long totalElements, int pageSize) {
		this.content = content;
		this.totalElements = totalElements;
		this.pageSize = pageSize;
		this.totalPages = (int) ((this.totalElements + this.pageSize - 1) / this.pageSize);
	}
	
	public List<T> getContent() {
		return content;
	}
	public void setContent(List<T> content) {
		this.content = content;
	}
	
	/**
	 * 总元素构造时完成，不提供写方法
	 * @return
	 */
	public long getTotalElements() {
		return totalElements;
	}
	
	/**
	 * 页面尺寸构造时完成，不提供写方法
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}
	
	/**
	 * 总页数在构造时根据总元素和页面尺寸计算获得，不提供写方法
	 * @return
	 */
	public int getTotalPages() {
		return totalPages;
	}
	
	/**
	 * 存储查询时的页码数，从第0页开始
	 * @return
	 */
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
		this.offset = pageNumber * this.pageSize;
	}
	
	/**
	 * 存储查询页码时，计算获得
	 * @return
	 */
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
		this.pageNumber = offset / this.pageSize;
	}

	@Override
	public String toString() {
		return "Pager [totalElements=" + totalElements + ", totalPages=" + totalPages + ", pageNumber=" + pageNumber
				+ ", pageSize=" + pageSize + ", offset=" + offset + ", content=" + content + "]";
	}

}
