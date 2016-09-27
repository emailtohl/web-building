package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.exception.ResourceNotFoundException;
import com.github.emailtohl.building.site.entities.Manager;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;
import com.google.gson.Gson;

/**
 * 用户管理的控制器
 * @author Helei
 */
@Controller
@RequestMapping("user")
public class UserCtrl {
	private static final Logger logger = LogManager.getLogger();
	@Inject
	UserService userService;
	@Inject
	Gson gson;
	
	public String getCurrentUsername() {
		String username = null;
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a != null) {
			username = a.getName();
		}
		return username;
	}
	
	/**
	 * 查询user资源下提供哪些方法
	 * @return
	 */
	@RequestMapping(value = "", method = OPTIONS)
	public ResponseEntity<Void> discover() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Allow", "OPTIONS,HEAD,GET");
		return new ResponseEntity<Void>(null, headers, HttpStatus.NO_CONTENT);
	}
	
	/**
	 * 查询user/id下支持哪些方法
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "{id}", method = OPTIONS)
	public ResponseEntity<Void> discover(@PathVariable("id") long id) {
		if (userService.getUser(id) == null)
			throw new ResourceNotFoundException("未找到此资源");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Allow", "OPTIONS,HEAD,GET,PUT,DELETE");
		return new ResponseEntity<Void>(null, headers, HttpStatus.NO_CONTENT);
	}
	
	/**
	 * 通过id获取User
	 * 注意，userService获取的User对象，可能是一个普通的User，也可能是继承User的Employ或Manager
	 * 如果控制器返回一个User，由于没有相关的getter方法，Spring MVC调用的序列化方法不会将Employ或Manager中的属性解析
	 * 这里的解决方案是先序列化为JSON，然后以字符串形式返回到前端
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "id/{id}", method = GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String getUserById(@PathVariable("id") Long id) {
		User u = userService.getUser(id);
		if (u == null) {
			throw new ResourceNotFoundException("未找到此资源");
		}
		return gson.toJson(u);
	}
	
	/**
	 * 通过email获取User
	 * 同getUserById中的注释一样，这里也是以字符串形式返回到前端
	 * 
	 * 这里不用@PathVariable从路径中获取ip地址，因为小数点后面的内容会被截取
	 * 
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "email", method = GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String getUserByEmail(@RequestParam String email) {
		User u = userService.getUserByEmail(email);
		if (u == null) {
			throw new ResourceNotFoundException();
		}
		return gson.toJson(u);
	}
	
	/**
	 * 获取分页对象
	 * 这里返回的对象没有使用org.springframework.data.domain.Page<E>
	 * 主要是因为该对象在Spring MVC中不能序列化并传到前端
	 * @param u
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "pager", method = GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Pager<User> getUserPager(@ModelAttribute User u, @PageableDefault(sort = "id=desc") Pageable pageable) {
		return userService.getUserPager(u, pageable);
	}
	
	/**
	 * 新增一个User
	 * 适用于管理员操作，或RestFull风格的调用，受安全策略保护
	 * 若注册页面中新增一个用户，可用/register，POST添加
	 * @param u
	 * @return
	 */
	@RequestMapping(value = "", method = POST)
	public ResponseEntity<?> addUser(@RequestBody @Valid User u, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Long id = userService.addUser(u);
		String uri = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/user/{id}")
				.buildAndExpand(id).toString();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", uri);
		return new ResponseEntity<>(u, headers, HttpStatus.CREATED);
	}
	
	/**
	 * 修改一个User
	 * @param id
	 * @param user
	 */
	@RequestMapping(value = "{id}", method = PUT)
	public ResponseEntity<Void> update(@PathVariable("id") @Min(1L) long id, @Valid @RequestBody Manager user/* 用最大范围来接收表单数据 */, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		userService.mergeUser(id, user);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * 删除一个User
	 * @param id
	 */
	@RequestMapping(value = "{id}", method = DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") @Min(1L) long id) {
		userService.deleteUser(id);
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}
	
}
