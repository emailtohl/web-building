package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.exception.ResourceNotFoundException;
import com.github.emailtohl.building.site.controller.form.UserForm;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.mail.EmailService;
import com.github.emailtohl.building.site.service.AuthenticationService;
import com.github.emailtohl.building.site.service.UserService;
/**
 * 认证控制器，管理用户注册，更改密码，授权等功能
 * @author Helei
 */
@Controller
public class AuthenticationCtrl {
	private static final Logger logger = LogManager.getLogger();
	@Inject AuthenticationService authenticationService;
	@Inject UserService userService;
	@Inject EmailService emailService;
	@Inject ThreadPoolTaskScheduler taskScheduler;
	/**
	 * 忘记密码时，当发送邮件时，会记录一个token，该token有时效，过期会被清除
	 */
	Map<String, String> tokenMap = new ConcurrentHashMap<String, String>();
	
	
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
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "register", method = RequestMethod.POST, produces = {"text/html;charset=UTF-8"})
	public String register(HttpServletRequest requet, @Valid UserForm u, org.springframework.validation.Errors e) {
		// 第一步，判断提交表单是否有效
		if (e.hasErrors()) {
			StringBuilder s = new StringBuilder();
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
				s.append(oe.getDefaultMessage());
			}
//			throw new VerifyFailure(e.toString());
			return "redirect:register?error=" + encode(s.toString());
		}
		try {
			// 第二步，添加该用户，若报运行时异常，则抛出，告诉用户该账号不能注册
			long id = userService.addUser(u);
			// 第三步，邮件通知用户，让其激活该账号
			String url = requet.getScheme() + "://" + requet.getServerName() + ":" + requet.getServerPort() + requet.getContextPath() + "/enable?id=" + id;
			emailService.enableUser(url, u.getEmail());
			return "login";
		} catch (RuntimeException e1) {
			return "redirect:register?error=" + encode("邮箱重复");
		}
	}
	
	/**
	 * 另立一个私有方法处理URLEncoder.encode的检查型异常
	 * @param s
	 * @return
	 */
	private String encode(String s) {
		String res = null;
		try {
			res = URLEncoder.encode(s.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * 通过电子邮箱发送忘记密码的页面
	 * @return
	 */
	@RequestMapping(value = "forgetPassword", method = RequestMethod.POST)
	public void forgetPassword(HttpServletRequest requet, String email, String _csrf) {
		if (!authenticationService.isExist(email)) {
			throw new ResourceNotFoundException();
		}
		String token = UUID.randomUUID().toString();
		tokenMap.put(token, email);
		scheduleCleanToken(token);
		String url = requet.getScheme() + "://" + requet.getServerName() + ":" + requet.getServerPort() + requet.getContextPath() + "/getUpdatePasswordPage";
		emailService.updatePassword(url, email, token, _csrf);
	}
	
	/**
	 * 定时清理token
	 * @param token
	 */
	private void scheduleCleanToken(String token) {
		long l = System.currentTimeMillis() + 10 * 60 * 1000;
		Date startTime = new Date(l);
		taskScheduler.schedule(() -> {tokenMap.remove(token);}, startTime);
	}
	
	/**
	 * 忘记密码后，在邮箱中的链接中打开修改密码页面
	 * @param email
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "getUpdatePasswordPage", method = RequestMethod.GET)
	public String getUpdatePasswordPage(String email, String token, Map<String, Object> model) {
		if (!email.equals(tokenMap.get(token))) {
			return "redirect:login?error=expire";
		}
		model.put("email", email);
		model.put("token", token);
		return "updatePassword";
	}
	
	/**
	 * 重置密码，用于忘记密码处
	 * @param email
	 * @param password 修改的密码
	 * @return
	 */
	@RequestMapping(value = "updatePassword", method = RequestMethod.POST)
	public String updatePassword(String email, String password, String token) {
		if (!email.equals(tokenMap.get(token))) {
			return "redirect:login?error=expire";
		}
		authenticationService.changePassword(email, password);
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
	public Pager<User> getPageByAuthorities(@ModelAttribute UserForm u, @PageableDefault(sort = "id=desc") Pageable pageable) {
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

	public void setTaskScheduler(ThreadPoolTaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}
	
}
