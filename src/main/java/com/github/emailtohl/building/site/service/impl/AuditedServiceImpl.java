package com.github.emailtohl.building.site.service.impl;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.entity.BaseEntity;
import com.github.emailtohl.building.common.jpa.envers.Tuple;
import com.github.emailtohl.building.site.dao.audit.RoleAudit;
import com.github.emailtohl.building.site.dao.audit.UserAudit;
import com.github.emailtohl.building.site.dto.RoleDto;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.entities.role.Role;
import com.github.emailtohl.building.site.entities.user.User;
import com.github.emailtohl.building.site.service.AuditedService;
/**
 * 查询被审计的实体的历史记录
 * @author HeLei
 * @date 2017.02.04
 */
@Service
public class AuditedServiceImpl implements AuditedService {
	@Inject UserAudit userAudit;
	@Inject RoleAudit roleAudit;

	@Override
	public Pager<UserDto> getUserRevision(String email, Pageable pageable) {
		Map<String, String> propertyNameValueMap = new HashMap<>();
		propertyNameValueMap.put("email", email);
		Page<Tuple<User>> page = userAudit.getEntityRevision(propertyNameValueMap, pageable);
		List<UserDto> ls = page.getContent().stream().map(this::convert).collect(toList());
		return new Pager<>(ls, page.getTotalElements(), page.getNumber(), page.getSize());
	}

	@Override
	public Pager<UserDto> getUsersAtRevision(int revision, String email, Pageable pageable) {
		Map<String, String> propertyNameValueMap = new HashMap<>();
		propertyNameValueMap.put("email", email);
		Page<User> page = userAudit.getEntitiesAtRevision(revision, propertyNameValueMap, pageable);
		List<UserDto> ls = page.getContent().stream().map(this::convert).collect(toList());
		return new Pager<>(ls, page.getTotalElements(), page.getNumber(), page.getSize());
	}

	@Override
	public UserDto getUserAtRevision(long userId, int revision) {
		return convert(userAudit.getEntityAtRevision(userId, revision));
	}

	@Override
	public Pager<RoleDto> getRoleRevision(String name, Pageable pageable) {
		Map<String, String> propertyNameValueMap = new HashMap<>();
		propertyNameValueMap.put("name", name);
		Page<Tuple<Role>> page = roleAudit.getEntityRevision(propertyNameValueMap, pageable);
		List<RoleDto> ls = page.getContent().stream().map(this::convertRole).collect(toList());
		return new Pager<>(ls, page.getTotalElements(), page.getNumber(), page.getSize());
	}

	@Override
	public Pager<RoleDto> getRolesAtRevision(int revision, String name, Pageable pageable) {
		Map<String, String> propertyNameValueMap = new HashMap<>();
		propertyNameValueMap.put("name", name);
		Page<Role> page = roleAudit.getEntitiesAtRevision(revision, propertyNameValueMap, pageable);
		List<RoleDto> ls = page.getContent().stream().map(this::convert).collect(toList());
		return new Pager<>(ls, page.getTotalElements(), page.getNumber(), page.getSize());
	}

	@Override
	public RoleDto getRoleAtRevision(long roleId, int revision) {
		return convert(roleAudit.getEntityAtRevision(roleId, revision));
	}
	
	private UserDto convert(Tuple<User> t) {
		UserDto dto = new UserDto();
		User user = t.getEntity();
		if (user != null) {
			BeanUtils.copyProperties(user, dto, "password", "icon", BaseEntity.VERSION_PROPERTY_NAME);
		}
		DefaultRevisionEntity dre = t.getDefaultRevisionEntity();
		if (dre != null) {
			dto.setRevision(dre.getId());
			LocalDateTime dt = LocalDateTime.ofInstant(dre.getRevisionDate().toInstant(), ZoneId.systemDefault());
			dto.setRevisionDate(dt.toString());
		}
		dto.setRevisionType(t.getRevisionType());
		return dto;
	}
	
	private UserDto convert(User user) {
		user.getRoles().size();// 让代理初始化
		UserDto dto = new UserDto();
		BeanUtils.copyProperties(user, dto, "password", "icon", BaseEntity.VERSION_PROPERTY_NAME);
		return dto;
	}
	
	private RoleDto convertRole(Tuple<Role> t) {
		RoleDto dto = new RoleDto();
		Role role = t.getEntity();
		if (role != null) {
			BeanUtils.copyProperties(role, dto, BaseEntity.VERSION_PROPERTY_NAME);
			role.getAuthorities().forEach(a -> dto.getAuthorityNames().add(a.getName()));
		}
		DefaultRevisionEntity dre = t.getDefaultRevisionEntity();
		if (dre != null) {
			dto.setRevision(dre.getId());
			LocalDateTime dt = LocalDateTime.ofInstant(dre.getRevisionDate().toInstant(), ZoneId.systemDefault());
			dto.setRevisionDate(dt.toString());
		}
		dto.setRevisionType(t.getRevisionType());
		return dto;
	}
	
	private RoleDto convert(Role role) {
		RoleDto dto = new RoleDto();
		BeanUtils.copyProperties(role, dto);
		role.getAuthorities().forEach(a -> dto.getAuthorityNames().add(a.getName()));
		return dto;
	}
	
	public AuditedServiceImpl() {
		super();
	}

	public AuditedServiceImpl(UserAudit userAudit, RoleAudit roleAudit) {
		super();
		this.userAudit = userAudit;
		this.roleAudit = roleAudit;
	}

	public void setUserAudit(UserAudit userAudit) {
		this.userAudit = userAudit;
	}

	public void setRoleAudit(RoleAudit roleAudit) {
		this.roleAudit = roleAudit;
	}

}
