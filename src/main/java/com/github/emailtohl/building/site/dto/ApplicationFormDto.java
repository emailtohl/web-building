package com.github.emailtohl.building.site.dto;

import java.util.ArrayList;
import java.util.List;

import com.github.emailtohl.building.site.entities.flow.ApplicationForm;
import com.github.emailtohl.building.site.entities.flow.ApplicationHandleHistory;
/**
 * 申请单数据存放对象，主要存放申请单历史记录
 * 在实体中交叉引用会导致gson解析失败
 * 所以原ApplicationForm实体对象中的applicationHandleHistory属性被设置为了transient
 * @author HeLei
 * @date 2017.02.04
 */
public class ApplicationFormDto extends ApplicationForm {
	private static final long serialVersionUID = 3705282461930275407L;
	List<ApplicationHandleHistory> historyList = new ArrayList<>();
	
	public List<ApplicationHandleHistory> getHistoryList() {
		return historyList;
	}
	public void setHistoryList(List<ApplicationHandleHistory> historyList) {
		this.historyList = historyList;
	}
	
}
