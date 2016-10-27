package com.github.emailtohl.building.site.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.github.emailtohl.building.site.entities.ApplicationForm.Status;

/**
 * 申请表的历史处理记录
 * @author HeLei
 */
@Entity
@Table(name = "t_application_handle_history")
public class ApplicationHandleHistory extends BaseEntity {
	private static final long serialVersionUID = -352345718622378836L;
	private ApplicationForm applicationForm;
	private User handler;
	private String cause;
	private Status status;
	
	@ManyToOne
	@JoinColumn(name = "application_form_id", nullable = false)
	public ApplicationForm getApplicationForm() {
		return applicationForm;
	}
	public void setApplicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
	}
	
	@ManyToOne
	@JoinColumn(name = "handler_id")
	public User getHandler() {
		return handler;
	}
	public void setHandler(User handler) {
		this.handler = handler;
	}
	
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	
	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "ApplicationHandleHistory [applicationForm name=" + applicationForm.getName() + ", handler=" + handler.getEmail() + ", cause="
				+ cause + ", status=" + status + "]";
	}

}
