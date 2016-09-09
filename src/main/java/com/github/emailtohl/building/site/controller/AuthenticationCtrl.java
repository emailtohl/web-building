package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.AuthenticationService;
/**
 * 认证控制器
 * @author Helei
 */
@Controller
public class AuthenticationCtrl {
	@Inject
	AuthenticationService authenticationService;
	
	/**
	 * GET方法获取登录页面
	 * POST方法配置在Spring security中对用户进行认证
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login(Map<String, Object> model) {
		return "login";
	}

	/**
	 * 获取用户的认证信息
	 * @return
	 */
	@RequestMapping(value = "authentication", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> authentication() {
		Map<String, Object> map = null;
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			map = new HashMap<String, Object>();
			Authentication authentication = context.getAuthentication();
			map.put("username", authentication.getName());
			map.put("details", authentication.getDetails());
			map.put("principal", authentication.getPrincipal());
		}
		return map;
	}
	
	/**
	 * 获取用户权限列表
	 * @param u
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "authentication/page", method = GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Pager<User> getPageByAuthorities(@ModelAttribute User u, @PageableDefault(sort = "id=desc") Pageable pageable) {
		return authenticationService.getPageByAuthorities(u, pageable);
	}
	
	/**
	 * 对用户授权，权限识别由service控制
	 * @param id
	 * @param authorities
	 */
	@RequestMapping(value = "authentication/authorize/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public void authorize(@PathVariable Long id, @RequestBody Set<Authority> authorities) {
		authenticationService.grantedAuthority(id, authorities);
	}
	
	/**
	 * 测试接口
	 * @return
	 */
	@RequestMapping({ "secure" })
	public String securePage() {
		return "secure";
	}
}
