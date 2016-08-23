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
	private Long totalRow;// 总行数
	private Long totalPage;// 总页面数
	private Long pageNum;// 当前页码
	private Integer pageSize;// 每页最大行数
	private Long startRecordNumber;// 返回结果从此行开始
	private List<T> dataList;// 存储查询结果
	
	public Long getTotalRow() {
		return totalRow;
	}
	public void setTotalRow(Long totalRow) {
		this.totalRow = totalRow;
	}
	public Long getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Long totalPage) {
		this.totalPage = totalPage;
	}
	public Long getPageNum() {
		return pageNum;
	}
	public void setPageNum(Long pageNum) {
		this.pageNum = pageNum;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Long getStartRecordNumber() {
		return startRecordNumber;
	}
	public void setStartRecordNumber(Long startRecordNumber) {
		this.startRecordNumber = startRecordNumber;
	}
	public List<T> getDataList() {
		return dataList;
	}
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

}
