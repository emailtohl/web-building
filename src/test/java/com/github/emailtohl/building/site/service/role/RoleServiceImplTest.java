package com.github.emailtohl.building.site.service.role;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.dao.audit.CleanAuditData;
import com.github.emailtohl.building.site.dao.role.AuthorityRepository;
import com.github.emailtohl.building.site.entities.role.Authority;
import com.github.emailtohl.building.site.entities.role.Role;
import com.github.emailtohl.building.site.entities.user.Employee;
import com.github.emailtohl.building.site.entities.user.User;
import com.github.emailtohl.building.site.service.role.RoleService;
import com.github.emailtohl.building.site.service.user.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
/**
 * 业务类测试
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class RoleServiceImplTest {
	static final Logger logger = LogManager.getLogger();
	@Inject @Named("userServiceImpl") UserService userService;
	@Inject SecurityContextManager securityContextManager;
	@Inject RoleService roleService;
	@Inject AuthorityRepository authorityRepository;
	@Inject CacheManager cacheManager;
	@Inject CleanAuditData cleanAuditData;
	
	Employee u;
	Role r;
	Authority auth1, auth2;
	Long userId, roleId, auth1Id, auth2Id;

	@Before
	public void setUp() throws Exception {
		securityContextManager.setEmailtohl();
		
		u = new Employee();
		r = new Role();
		
		auth1 = new Authority("authorityTest1", "测试权限1的描述");
		auth2 = new Authority("authorityTest2", "测试权限2的描述");
		
		u.setName("userEmailTest");
		u.setEmail("userEmailTest@test.com");
		u.setPassword("123456");
		
		r.setName("roleTest");
		r.setDescription("test");
		
		u.getRoles().add(r);
		r.getUsers().add(u);
		
		r.getAuthorities().add(auth1);
		auth1.getRoles().add(r);
		
		authorityRepository.save(auth1);
		authorityRepository.save(auth2);
		auth1Id = auth1.getId();
		auth2Id = auth2.getId();
		
		roleId = roleService.createRole(r);
		userId = userService.addEmployee(u).getId();
	}

	@After
	public void tearDown() throws Exception {
		if (roleId != null) {// 先删角色的，roleService会将其关联的权限和用户全部删除
			roleService.deleteRole(roleId);
			cleanAuditData.cleanRoleAudit(roleId);
			// 测试用户是否与角色切断关系
			User qu = userService.getUser(userId);
			assertFalse(qu.getRoles().contains(r));
			
			// 测试权限是否与角色切断关系
//			Authority a1 = authorityRepository.findOne(auth1Id),
//					a2 = authorityRepository.findOne(auth2Id);
//			因现在authorityRepository中的事务还未提交，无法获取到数据
//			assertFalse(a1.getRoles().contains(r));
//			assertFalse(a2.getRoles().contains(r));
		}
		
		if (userId != null) {
			userService.deleteUser(userId);
			cleanAuditData.cleanUserAudit(userId);
		}
		
		if (auth1Id != null) {
			authorityRepository.delete(auth1Id);
			cleanAuditData.cleanAuthorityAudit(auth1Id);
		}
		if (auth2Id != null) {
			authorityRepository.delete(auth2Id);
			cleanAuditData.cleanAuthorityAudit(auth2Id);
		}
		
		Cache c = cacheManager.getCache(AuthorityRepository.CACHE_NAME);
		c.clear();
	}

	@Test
	public void test() {
		assertFalse(roleService.getRoles().isEmpty());
		assertFalse(roleService.getAuthorities().isEmpty());
		
		Role r = new Role();
		r.setName("改名");
		r.setDescription("改描述");
		auth2 = authorityRepository.findOne(auth2Id);
		r.getAuthorities().add(auth2);
		roleService.updateRole(roleId, r);
		// 再次查询该角色，测试是否更新基本内容和相关权限
		Role q = roleService.getRole(roleId);
		assertFalse(q.getAuthorities().contains(auth1));
		assertTrue(q.getAuthorities().contains(auth2));
		assertEquals("改名", r.getName());
		assertEquals("改描述", r.getDescription());
	}
	
	@Test
	public void testCreateRoleRoleSetOfString() {
		Role otherRole = new Role();
		otherRole.setName("otherRole");
		otherRole.setDescription("otherRole test");
		Long id = null;
		try {
			id = roleService.createRole(otherRole, new HashSet<String>(Arrays.asList(auth1.getName(), auth2.getName())));
			assertNotNull(id);
			Role q = roleService.getRole(id);
			logger.debug(q.getAuthorities());
			assertTrue(q.getAuthorities().contains(auth1));
			assertTrue(q.getAuthorities().contains(auth2));
		} finally {
			if (id != null) {
				roleService.deleteRole(id);
				cleanAuditData.cleanRoleAudit(id);
			}
		}
	}

	@Test
	public void testGrantAuthorities() {
		roleService.grantAuthorities(roleId, new HashSet<String>(Arrays.asList(auth2.getName())));
		Role q = roleService.getRole(roleId);
		logger.debug(q.getAuthorities());
		assertFalse(q.getAuthorities().contains(auth1));
		assertTrue(q.getAuthorities().contains(auth2));
	}

}
