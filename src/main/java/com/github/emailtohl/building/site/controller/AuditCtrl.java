package com.github.emailtohl.building.site.controller;

import static com.github.emailtohl.building.common.jpa.entity.BaseEntity.ID_PROPERTY_NAME;

import javax.inject.Inject;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.dto.RoleDto;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.entities.user.User;
import com.github.emailtohl.building.site.service.audited.AuditedService;
/**
 * 查阅Hibernate Envers产生的审计记录
 * 
 * @author HeLei
 * @date 2017.02.04
 */
@RestController
@RequestMapping("audit")
public class AuditCtrl {
	private AuditedService auditedService;
	
	@Inject
	public void setAuditedService(AuditedService auditedService) {
		this.auditedService = auditedService;
	}

	/**
	 * 根据User的email查询某实体所有历史记录
	 * 
	 * @param email
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "userRevision", method = RequestMethod.GET)
	public Pager<UserDto> getUserRevision(@RequestParam(required = false) String email,
			@PageableDefault(page = 0, size = 10, sort = {ID_PROPERTY_NAME}, direction = Direction.DESC) Pageable pageable) {
		return auditedService.getUserRevision(email, pageable);
	}

	/**
	 * 查询User某个修订版下所有的历史记录
	 * 
	 * @param revision
	 * @param email
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "usersAtRevision", method = RequestMethod.GET)
	public Pager<UserDto> getUsersAtRevision(@RequestParam(required = true) Integer revision, @RequestParam(required = false) String email,
			@PageableDefault(page = 0, size = 10, sort = {ID_PROPERTY_NAME}, direction = Direction.DESC) Pageable pageable) {
		return auditedService.getUsersAtRevision(revision, email, pageable);
	}

	/**
	 * 查询User在某个修订版时的历史记录
	 * 
	 * @param userId
	 * @param revision
	 * @return
	 */
	@RequestMapping(value = "userAtRevision", method = RequestMethod.GET)
	public User getUserAtRevision(@RequestParam(required = true) Long userId, @RequestParam(required = true) Integer revision) {
		return auditedService.getUserAtRevision(userId, revision);
	}

	/**
	 * 根据Role的名字查询某实体所有历史记录
	 * 
	 * @param name 实体属性名和属性值
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "roleRevision", method = RequestMethod.GET)
	public Pager<RoleDto> getRoleRevision(@RequestParam(required = false) String name,
			@PageableDefault(page = 0, size = 5, sort = {ID_PROPERTY_NAME}, direction = Direction.DESC) Pageable pageable) {
		return auditedService.getRoleRevision(name, pageable);
	}

	/**
	 * 查询Role在某个修订版时的历史记录
	 * 
	 * @param revision
	 * @param name
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "rolesAtRevision", method = RequestMethod.GET)
	public Pager<RoleDto> getRolesAtRevision(@RequestParam(required = true) Integer revision, @RequestParam(required = false) String name,
			@PageableDefault(page = 0, size = 5, sort = {ID_PROPERTY_NAME}, direction = Direction.DESC) Pageable pageable) {
		return auditedService.getRolesAtRevision(revision, name, pageable);
	}

	/**
	 * 查询Role在某个修订版时的历史记录
	 * 
	 * @param id
	 * @param revision
	 * @return
	 */
	@RequestMapping(value = "roleAtRevision", method = RequestMethod.GET)
	public RoleDto getRoleAtRevision(@RequestParam(required = true) Long roleId, @RequestParam(required = true) Integer revision) {
		return auditedService.getRoleAtRevision(roleId, revision);
	}
	
}
