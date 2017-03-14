package com.github.emailtohl.building.site.service.role;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.exception.NotFoundException;
import com.github.emailtohl.building.site.dao.audit.CleanAuditData;
import com.github.emailtohl.building.site.dao.role.AuthorityRepository;
import com.github.emailtohl.building.site.entities.role.Authority;
import com.github.emailtohl.building.site.entities.role.Role;
import com.github.emailtohl.building.site.entities.user.Employee;
import com.github.emailtohl.building.site.entities.user.User;
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
	String roleName = "roleTest";
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
		
		r.setName(roleName);
		r.setDescription("test");
		
		r.getAuthorities().add(auth1);
		auth1.getRoles().add(r);
		
		authorityRepository.save(auth1);
		authorityRepository.save(auth2);
		auth1Id = auth1.getId();
		auth2Id = auth2.getId();
		
		// 注意：在新建用户时，接口只授予Employee角色，所以需要创建好了User后再授予角色
		roleId = roleService.createRole(r);
		userId = userService.addEmployee(u).getId();
	}

	@After
	public void tearDown() throws Exception {
		roleService.deleteRole(roleId);
		cleanAuditData.cleanRoleAudit(roleId);

		userService.deleteUser(userId);
		cleanAuditData.cleanUserAudit(userId);

		authorityRepository.delete(auth1Id);
		cleanAuditData.cleanAuthorityAudit(auth1Id);
		authorityRepository.delete(auth2Id);
		cleanAuditData.cleanAuthorityAudit(auth2Id);
		
//		Cache c = cacheManager.getCache(AuthorityRepository.CACHE_NAME);
//		c.clear();
	}

	@Test
	public void testGet() {
		assertNotNull(roleService.getRole(roleId));
		assertFalse(roleService.getRoles().isEmpty());
		assertTrue(roleService.getAuthorities().contains(auth1));
		assertTrue(roleService.getAuthorities().contains(auth2));
	}

	@Test
	public void testUpdateAndCache() throws NotFoundException {
		// 先执行带缓存的方法，使其缓存上
		User tu = userService.getUserByEmail(u.getEmail());
		// 初创用户时，只有Employee权限
		boolean flag = tu.getRoles().parallelStream().anyMatch(tr -> Role.EMPLOYEE.equals(tr.getName()));
		assertTrue(flag);
		
		// 为其授予新的角色
		userService.grantRoles(tu.getId(), roleName);
		tu = userService.getUserByEmail(u.getEmail());
		// 用户的角色变了，查看是否清空缓存重新加载最新状态
		flag = tu.getRoles().parallelStream().anyMatch(tr -> roleName.equals(tr.getName()));
		assertTrue(flag);
		
		String newRoleName = "updateRole";
		Role updateRole = new Role(newRoleName, "for test");
		// 更新角色名字
		roleService.updateRole(roleId, updateRole);
		// 再次加载user
		tu = userService.getUserByEmail(u.getEmail());
		flag = tu.getRoles().parallelStream().anyMatch(tr -> roleName.equals(tr.getName()));
		// 不再包含原先的"roleTest"的角色名
		assertFalse(flag);
		flag = tu.getRoles().parallelStream().anyMatch(tr -> newRoleName.equals(tr.getName()));
		// 包含新的角色名，证明缓存被清除
		assertTrue(flag);
	}
}
