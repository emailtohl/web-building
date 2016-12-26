package com.github.emailtohl.building.site.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.common.jpa.envers.Tuple;
import com.github.emailtohl.building.site.dao.audit.ApplicationFormAudit;
import com.github.emailtohl.building.site.dao.audit.RoleAudit;
import com.github.emailtohl.building.site.dao.audit.UserAudit;
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
	public Page<User> getUsersAtRevision(Number revision, Map<String, String> propertyNameValueMap, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUserAtRevision(Long userId, Number revision) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Tuple<Role>> getRoleRevision(Map<String, String> propertyNameValueMap, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Role> getEntitiesAtRevision(Number revision, Map<String, String> propertyNameValueMap,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role getEntityAtRevision(Long roleId, Number revision) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Tuple<ApplicationForm>> getApplicationFormRevision(Map<String, String> propertyNameValueMap,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<ApplicationForm> getApplicationFormsAtRevision(Number revision,
			Map<String, String> propertyNameValueMap, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationForm getApplicationFormAtRevision(Long applicationFormid, Number revision) {
		// TODO Auto-generated method stub
		return null;
	}

}
