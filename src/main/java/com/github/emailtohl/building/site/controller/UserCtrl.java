package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import javax.inject.Inject;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.emailtohl.building.common.repository.jpa.Pager;
import com.github.emailtohl.building.exception.ResourceNotFoundException;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;

/**
 * 用户管理的控制器
 * @author Helei
 */
@Controller
@RequestMapping("user")
public class UserCtrl {
	@Inject
	UserService userService;
	
	public String getCurrentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	@RequestMapping(value = "", method = OPTIONS)
	public ResponseEntity<Void> discover() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Allow", "OPTIONS,HEAD,GET,POST");
		return new ResponseEntity<>(null, headers, HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value = "{id}", method = OPTIONS)
	public ResponseEntity<Void> discover(@PathVariable("id") long id) {
		if (userService.getUser(id) == null)
			throw new ResourceNotFoundException();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Allow", "OPTIONS,HEAD,GET,PUT,DELETE");
		return new ResponseEntity<>(null, headers, HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value = "{id}", method = GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public User getUser(@PathVariable("id") Long id) {
		return userService.getUser(id);
	}
	
	@RequestMapping(value = "pager", method = GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Pager<User> getUserPager(@RequestBody User u, Pageable pageable) {
		return userService.getUserPager(u, pageable);
	}
	
	@RequestMapping(value = "", method = POST)
	@ResponseBody
	public ResponseEntity<User> addUser(@RequestBody User u) {
		Long id = userService.addUser(u);
		String uri = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/user/{id}")
				.buildAndExpand(id).toString();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", uri);
		return new ResponseEntity<>(u, headers, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "{id}", method = PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@PathVariable("id") long id, @RequestBody User user) {
		userService.updateUser(id, user);
	}
	
	@RequestMapping(value = "{id}", method = DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") long id) {
		userService.deleteUser(id);
	}
}
