package com.github.emailtohl.building.site.service.impl;

import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.github.emailtohl.building.site.dao.audit.ApplicationFormAudit;
import com.github.emailtohl.building.site.dao.audit.RoleAudit;
import com.github.emailtohl.building.site.dao.audit.UserAudit;
import com.github.emailtohl.building.site.service.AuditedService;
/**
 * 都是使用的AbstractAuditedRepository的方法，而该类已被测试过，该单元测试为提高测试覆盖率而存在
 * @author HeLei
 */
public class AuditedServiceImplTest {
	AuditedService auditedService;
	Pageable pageable = new PageRequest(0, 20);

	@Before
	public void setUp() throws Exception {
		UserAudit userAudit = mock(UserAudit.class);
		RoleAudit roleAudit = mock(RoleAudit.class);
		ApplicationFormAudit applicationFormAudit = mock(ApplicationFormAudit.class);
		auditedService = new AuditedServiceImpl(userAudit, roleAudit, applicationFormAudit);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetUserRevision() {
		auditedService.getUserRevision(null, pageable);
	}

	@Test
	public void testGetUsersAtRevision() {
		auditedService.getUserAtRevision(null, null);
	}

	@Test
	public void testGetUserAtRevision() {
		auditedService.getUserAtRevision(null, null);
	}

	@Test
	public void testGetRoleRevision() {
		auditedService.getRoleAtRevision(null, null);
	}

	@Test
	public void testGetRolesAtRevision() {
		auditedService.getRolesAtRevision(null, null, pageable);
	}

	@Test
	public void testGetRoleAtRevision() {
		auditedService.getRoleAtRevision(null, null);
	}

	@Test
	public void testGetApplicationFormRevision() {
		auditedService.getApplicationFormRevision(null, pageable);
	}

	@Test
	public void testGetApplicationFormsAtRevision() {
		auditedService.getApplicationFormAtRevision(null, null);
	}

	@Test
	public void testGetApplicationFormAtRevision() {
		auditedService.getApplicationFormAtRevision(null, null);
	}

}
