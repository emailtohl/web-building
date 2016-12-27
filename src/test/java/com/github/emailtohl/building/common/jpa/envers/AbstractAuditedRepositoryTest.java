package com.github.emailtohl.building.common.jpa.envers;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.envers.RevisionType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.dao.audit.CleanAuditData;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class AbstractAuditedRepositoryTest {
	private static final Logger logger = LogManager.getLogger();
	@Inject ApplicationContext context;
	@Inject SecurityContextManager securityContextManager;
	@Inject @Named("userServiceImpl") UserService userService;
	@Inject CleanAuditData cleanAuditTestData;
	class AuditedRepositoryForTest extends AbstractAuditedRepository<User> {}
	AuditedRepositoryForTest audRepos;
	private Long id;
	private Sort sort = new Sort(Sort.Direction.DESC, "name");
	private Pageable pageable = new PageRequest(0, 20, sort);
	

	@Before
	public void setUp() throws Exception {
		securityContextManager.setEmailtohl();
		audRepos = new AuditedRepositoryForTest();
		AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		factory.autowireBeanProperties(audRepos, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		factory.initializeBean(audRepos, "auditedRepositoryForTest");
		
		Customer u = new Customer();
		u.setEmail("forAuditTest@test.com");
		u.setTitle("ceo");
		u.setName("forAuditTest");
		u.setUsername("forAuditTest");
		u.setPassword("123456");
		id = userService.addCustomer(u);
		
		Customer uu = (Customer) userService.getUser(id);
		uu.setName("forAuditTestForUpdate");
		uu.setTitle("cto");
		userService.mergeCustomer(uu.getEmail(), uu);
		userService.grantRoles(id, "employee");
	}

	@After
	public void tearDown() throws Exception {
		// 删除后还有一次审计记录
		userService.deleteUser(id);
		cleanAuditTestData.cleanUserAudit(id);
	}

	@Test
	public void test() {
		Number origin = null;
		
		Map<String, String> propertyNameValueMap = new HashMap<>();
		propertyNameValueMap.put("name", "forAuditTest");
		// test getEntityRevision
		Page<Tuple<User>> page = audRepos.getEntityRevision(propertyNameValueMap, pageable);
		for (Tuple<User> tuple : page.getContent()) {
			logger.debug(tuple.getEntity());
			logger.debug(tuple.getDefaultRevisionEntity());
			logger.debug(tuple.getRevisionType());
			
			Number rev = tuple.getDefaultRevisionEntity().getId();
			// test getEntityAtRevision
			User ru = audRepos.getEntityAtRevision(id, rev);
			logger.debug(ru);
			if (tuple.getRevisionType() == RevisionType.ADD) {
				origin = rev;
			}
			
			Page<User> pu = audRepos.getEntitiesAtRevision(rev, propertyNameValueMap, pageable);
			assertFalse(pu.getContent().isEmpty());
			pu.getContent().forEach(u -> {
				logger.debug(u);
			});
		
		}
		assertFalse(page.getContent().isEmpty());
		
		if (origin != null) {
			// 由于实体基类BaseEntity的createDate为不可变，所以回滚时遭遇数据库约束异常，暂时不能使用此接口
			/*
			audRepos.rollback(id, origin);
			User bygone = userService.getUser(id);
			assertEquals("forAuditTestForUpdate", bygone.getName());
			logger.debug(bygone.getRoles());
			*/
		}
	}

}
