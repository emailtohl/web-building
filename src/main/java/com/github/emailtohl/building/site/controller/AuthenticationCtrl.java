package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;

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
import com.github.emailtohl.building.exception.VerifyFailure;
import com.github.emailtohl.building.mail.EmailService;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.AuthenticationService;
import com.github.emailtohl.building.site.service.UserService;
/**
 * 认证控制器
 * @author Helei
 */
@Controller
public class AuthenticationCtrl {
	@Inject
	AuthenticationService authenticationService;
	@Inject
	UserService userService;
	@Inject
	EmailService emailService;
	
	/**
	 * GET方法获取登录页面
	 * POST方法配置在Spring security中对用户进行认证
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	/**
	 * GET方法获取注册页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String register() {
		return "register";
	}
	
	/**
	 * POST方法注册一个账号，如果成功，则返回到登录页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "register", method = RequestMethod.POST, produces = {"text/html;charset=UTF-8"})
	public String register(HttpServletRequest requet, @Valid User u, org.springframework.validation.Errors e) {
		if (e.hasErrors()) {
			throw new VerifyFailure();
		}
		long id = userService.addUser(u);
		String htmlText = "<html><head><meta charset=\"utf-8\"></head><body><a href=\"" + requet.getScheme() + "://" + requet.getServerName() + ":" + requet.getServerPort() + requet.getContextPath() + "/enable?id=" + id + "\">点击此链接激活账号</a></body></html>";
		emailService.sendMail(u.getEmail(), "激活账号", htmlText);
		return "login";
	}
	
	/**
	 * 激活账号
	 * @param id
	 */
	@RequestMapping(value = "enable", method = RequestMethod.GET)
	public String enable(long id) {
		userService.enableUser(id);
		// 注意，若未给用户授权，则spring security自带的认证器会认为认证失败，所以初始化时必须给予一定权限
		authenticationService.grantedAuthority(id, new HashSet<Authority>(Arrays.asList(Authority.USER)));
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
			Authentication authentication = context.getAuthentication();
			if (authentication != null) {
				map = new HashMap<String, Object>();
				map.put("username", authentication.getName());
				map.put("details", authentication.getDetails());
				map.put("principal", authentication.getPrincipal());
			}
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
	public void authorize(@PathVariable @Min(1L) Long id, @RequestBody Set<Authority> authorities) {
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

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
}
