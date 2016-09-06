package com.github.emailtohl.building.common.repository.jpa;

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
	 * 总记录数
	 * 设置为基本类型，表示该值一定不为null
	 */
	private long totalElements;
	/**
	 * 总页面数
	 */
	private Integer totalPages;
	/**
	 * 当前页码，默认第0页开始
	 */
	private Integer pageNumber;
	/**
	 * 每页最大行数，默认20条
	 */
	private int pageSize;
	/**
	 * 偏移量，返回的结果从此行开始
	 */
	private Integer offset;
	/**
	 * 存储查询结果
	 */
	private List<T> content;

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
	}

	public Long getTotalElements() {
		return totalElements;
	}
	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}
	public Integer getTotalPages() {
		if (totalPages == null) {// 如果没有手动设置总页数，则根据总元素和每页大小进行推算
			return (int) ((totalElements + pageSize - 1) / pageSize);
		} else {
			return totalPages;
		}
	}
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getOffset() {
		if (pageNumber != null) {// 如果存在当前页，则可以推算出偏移量
//			这是页码从第1页开始的计算方式
//			return (pageNumber - 1) * pageSize;
//			这是页码从第0页开始的计算方式
			return pageNumber * pageSize;
		} else {
			return offset;
		}
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public List<T> getContent() {
		return content;
	}
	public void setContent(List<T> content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "Pager [totalElements=" + totalElements + ", totalPages=" + totalPages + ", pageNumber=" + pageNumber
				+ ", pageSize=" + pageSize + ", offset=" + offset + ", content=" + content + "]";
	}

}
