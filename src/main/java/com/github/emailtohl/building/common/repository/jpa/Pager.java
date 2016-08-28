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
	private Integer totalPage;// 总页面数
	private Integer pageNum;// 当前页码
	private Integer pageSize;// 每页最大行数
	private Integer startRecordNumber;// 返回结果从此行开始
	private List<T> dataList;// 存储查询结果
	
	public Long getTotalRow() {
		return totalRow;
	}
	public void setTotalRow(Long totalRow) {
		this.totalRow = totalRow;
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getStartRecordNumber() {
		return startRecordNumber;
	}
	public void setStartRecordNumber(Integer startRecordNumber) {
		this.startRecordNumber = startRecordNumber;
	}
	public List<T> getDataList() {
		return dataList;
	}
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

}
