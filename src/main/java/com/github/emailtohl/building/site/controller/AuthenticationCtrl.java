package com.github.emailtohl.building.site.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
			map.put("credentials", authentication.getCredentials());
			map.put("details", authentication.getDetails());
			map.put("principal", authentication.getPrincipal());
		}
		return map;
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
