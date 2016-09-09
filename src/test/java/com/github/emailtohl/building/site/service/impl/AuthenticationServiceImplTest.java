package com.github.emailtohl.building.site.service.impl;

import static com.github.emailtohl.building.site.entities.Authority.ADMIN;
import static com.github.emailtohl.building.site.entities.Authority.EMPLOYEE;
import static com.github.emailtohl.building.site.entities.Authority.MANAGER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.utils.BeanTools;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.AuthenticationService;
import com.github.emailtohl.building.site.service.UserService;
/**
 * 对实现的测试，接口上对安全注解的测试在不含在内
 * @author Helei
 */
public class AuthenticationServiceImplTest {
	AuthenticationService authenticationService;
	UserService userService;
	AuthenticationManager authenticationManager;
	
	@Before
	public void setUp() {
		authenticationService = SpringUtils.context.getBean(AuthenticationService.class);
		userService = SpringUtils.context.getBean(UserService.class);
	}
	
	@Test
	public void testAuthenticate() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Authentication auth = authenticationService.authenticate("emailtohl@163.com", "123456");
		Object o = auth.getPrincipal();
		String actual = BeanTools.getPropertyNameValueMap(o).get("username").toString();
		assertEquals("emailtohl@163.com", actual);
	}
	
//	需要在安全上下文中验证，这里不容易单元测试
//	@Test
	public void testGetPageByAuthorities() {
		User u = new User();
		u.setEmail("foo@test.com");
		// 查询页从第0页开始
		Pager<User> p = authenticationService.getPageByAuthorities(u, new PageRequest(0, 20));
		assertTrue(p.getContent().size() > 0);
	}
	
//	需要在安全上下文中验证，这里不容易单元测试
//	@Test
	public void testGrantedAuthority() {
		Long id = userService.getUserByEmail("foo@test.com").getId();
			authenticationService.grantedAuthority(id,
					new HashSet<Authority>(Arrays.asList(ADMIN, MANAGER)));
		authenticationService.grantedAuthority(id,
				new HashSet<Authority>(Arrays.asList(EMPLOYEE)));
//		User u = userService.getUser(id);
//		assertTrue(u.getAuthorities().containsAll(Arrays.asList(ADMIN, USER)));
	}

}
