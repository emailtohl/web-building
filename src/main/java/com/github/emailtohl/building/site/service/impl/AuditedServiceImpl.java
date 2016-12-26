package com.github.emailtohl.building.site.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.common.jpa.envers.Tuple;
import com.github.emailtohl.building.site.dao.audit.ApplicationFormAudit;
import com.github.emailtohl.building.site.dao.audit.ApplicationFormAuditImpl;
import com.github.emailtohl.building.site.dao.audit.RoleAudit;
import com.github.emailtohl.building.site.dao.audit.RoleAuditImpl;
import com.github.emailtohl.building.site.dao.audit.UserAudit;
import com.github.emailtohl.building.site.dao.audit.UserAuditImpl;
import com.github.emailtohl.building.site.entities.ApplicationForm;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.AuditedService;

/**
 * 查询被审计的实体的历史记录
 * @author HeLei
 */
@Service
public class AuditedServiceImpl implements AuditedService {
	@Inject UserAudit userAudit;
	@Inject RoleAudit roleAudit;
	@Inject ApplicationFormAudit applicationFormAudit;

	@Override
	public Page<Tuple<User>> getUserRevision(String email, Pageable pageable) {
		Map<String, String> propertyNameValueMap = new HashMap<>();
		propertyNameValueMap.put("email", email);
		return userAudit.getEntityRevision(propertyNameValueMap, pageable);
	}

	@Override
	public Page<User> getUsersAtRevision(Number revision, String email, Pageable pageable) {
		Map<String, String> propertyNameValueMap = new HashMap<>();
		propertyNameValueMap.put("email", email);
		return userAudit.getEntitiesAtRevision(revision, propertyNameValueMap, pageable);
	}

	@Override
	public User getUserAtRevision(Long userId, Number revision) {
		return userAudit.getEntityAtRevision(userId, revision);
	}

	@Override
	public Page<Tuple<Role>> getRoleRevision(String name, Pageable pageable) {
		Map<String, String> propertyNameValueMap = new HashMap<>();
		propertyNameValueMap.put("name", name);
		return roleAudit.getEntityRevision(propertyNameValueMap, pageable);
	}

	@Override
	public Page<Role> getRolesAtRevision(Number revision, String name, Pageable pageable) {
		Map<String, String> propertyNameValueMap = new HashMap<>();
		propertyNameValueMap.put("name", name);
		return roleAudit.getEntitiesAtRevision(revision, propertyNameValueMap, pageable);
	}

	@Override
	public Role getRoleAtRevision(Long roleId, Number revision) {
		return roleAudit.getEntityAtRevision(roleId, revision);
	}

	@Override
	public Page<Tuple<ApplicationForm>> getApplicationFormRevision(String name, Pageable pageable) {
		Map<String, String> propertyNameValueMap = new HashMap<>();
		propertyNameValueMap.put("name", name);
		return applicationFormAudit.getEntityRevision(propertyNameValueMap, pageable);
	}

	@Override
	public Page<ApplicationForm> getApplicationFormsAtRevision(Number revision, String name, Pageable pageable) {
		Map<String, String> propertyNameValueMap = new HashMap<>();
		propertyNameValueMap.put("name", name);
		return applicationFormAudit.getEntitiesAtRevision(revision, propertyNameValueMap, pageable);
	}

	@Override
	public ApplicationForm getApplicationFormAtRevision(Long applicationFormid, Number revision) {
		return applicationFormAudit.getEntityAtRevision(applicationFormid, revision);
	}

	public AuditedServiceImpl(UserAudit userAudit, RoleAudit roleAudit, ApplicationFormAudit applicationFormAudit) {
		super();
		this.userAudit = userAudit;
		this.roleAudit = roleAudit;
		this.applicationFormAudit = applicationFormAudit;
	}

	public AuditedServiceImpl() {
		super();
	}

	public void setUserAudit(UserAuditImpl userAudit) {
		this.userAudit = userAudit;
	}

	public void setRoleAudit(RoleAuditImpl roleAudit) {
		this.roleAudit = roleAudit;
	}

	public void setApplicationFormAudit(ApplicationFormAuditImpl applicationFormAudit) {
		this.applicationFormAudit = applicationFormAudit;
	}

}
