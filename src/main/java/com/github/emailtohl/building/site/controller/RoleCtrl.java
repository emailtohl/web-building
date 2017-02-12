package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.emailtohl.building.site.dto.RoleDto;
import com.github.emailtohl.building.site.entities.role.Authority;
import com.github.emailtohl.building.site.entities.role.Role;
import com.github.emailtohl.building.site.service.role.RoleService;
/**
 * 角色管理的控制器
 * @author HeLei
 * @date 2017.02.04
 */
@RestController
public class RoleCtrl {
	private static final Logger logger = LogManager.getLogger();
	@Inject RoleService roleService;
	
	/**
	 * 获取角色
	 */
	@RequestMapping(value = "role/{id}", method = GET)
	public Role getRole(@PathVariable("id") Long id) {
		return roleService.getRole(id);
	}
	
	/**
	 * 获取所有角色
	 * @return
	 */
	@RequestMapping(value = "role", method = GET)
	public List<Role> getRoles() {
		return roleService.getRoles();
	}
	
	/**
	 * 获取所有权限
	 * @return
	 */
	@RequestMapping(value = "authority", method = GET)
	public List<Authority> getAuthorities() {
		return roleService.getAuthorities();
	}
	
	/**
	 * 对long createRole(@Valid Role role);的封装，便于控制器调用
	 * @param role 包含角色的基本信息
	 * @param authorityNames 角色含有的权限名字
	 * @return
	 */
	@RequestMapping(value = "role", method = POST)
	public ResponseEntity<?> createRole(@RequestBody @Valid RoleDto role, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		long id = roleService.createRole(role, role.getAuthorityNames());
		String uri = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/role/{id}")
				.buildAndExpand(id).toString();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", uri);
		return new ResponseEntity<>(role, headers, HttpStatus.CREATED);
	}
	
	/**
	 * 修改角色
	 * 角色和用户的关系有程序内部控制，修改时不做关联，也就是说不会修改用户和角色的关系
	 * @param id
	 * @param role
	 */
	@RequestMapping(value = "role/{id}", method = PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateRole(@PathVariable long id, @RequestBody @Valid RoleDto role) {
		roleService.updateRole(id, role);
		// 该方法会替换以前的权限
		roleService.grantAuthorities(id, role.getAuthorityNames());
	}
	
	/**
	 * 为角色分配权限
	 * @param roleId 角色ID
	 * @param authorityNames 权限名
	 */
	@RequestMapping(value = "role/{id}/authorityNames/{authorityNames}", method = PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void grantAuthorities(@PathVariable("id") long roleId,
			@PathVariable("authorityNames") String authorityNames) {
		Set<String> set = new HashSet<>();
		for (String name : authorityNames.split(",")) {
			set.add(name);
		}
		roleService.grantAuthorities(roleId, set);
	}
	
	/**
	 * 删除角色
	 * @param id
	 */
	@RequestMapping(value = "role/{id}", method = DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRole(@PathVariable long id) {
		roleService.deleteRole(id);
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

}
