package com.github.emailtohl.building.site.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 申请表实体
 * @author HeLei
 *
 */
@Entity
@Table(name = "t_application_form")
public class ApplicationForm extends BaseEntity {
	private static final long serialVersionUID = -7992635477168684242L;
	public static enum Status {
		REQUEST("申请中"), REJECT("拒绝"), PROCESSING("处理中"), COMPLETION("完成");
		private String name;
		private Status(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	@NotNull
	private User applicant;
	private String name;
	private String description;
	private String cause;
	private User handler;
	private Status status;
	private List<ApplicationHandleHistory> applicationHandleHistory = new ArrayList<>();
	
	public ApplicationForm() {
		super();
	}
	
	public ApplicationForm(User applicant, String name, String description) {
		this(applicant, name, description, Status.REQUEST);
	}

	public ApplicationForm(User applicant, String name, String description, Status status) {
		super();
		this.applicant = applicant;
		this.name = name;
		this.description = description;
		this.status = status;
	}

	@ManyToOne
	@JoinColumn(name = "user_applicant_id")
	public User getApplicant() {
		return applicant;
	}
	public void setApplicant(User applicant) {
		this.applicant = applicant;
	}
	
	@Column(nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(nullable = false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	
	@ManyToOne
	@JoinColumn(name = "user_handler_id")
	public User getHandler() {
		return handler;
	}
	public void setHandler(User handler) {
		this.handler = handler;
	}
	
	@Column(nullable = false)
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	@OneToMany(mappedBy = "applicationForm", orphanRemoval = false)
	public List<ApplicationHandleHistory> getApplicationHandleHistory() {
		return applicationHandleHistory;
	}
	public void setApplicationHandleHistory(List<ApplicationHandleHistory> applicationHandleHistory) {
		this.applicationHandleHistory = applicationHandleHistory;
	}

	@Override
	public String toString() {
		return "ApplicationForm [applicant email = " + applicant.getEmail() + ", name=" + name + ", description=" + description
				+ ", cause=" + cause + ", handler=" + handler + ", status=" + status + "]";
	}
}
