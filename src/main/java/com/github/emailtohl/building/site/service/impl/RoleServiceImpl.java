package com.github.emailtohl.building.site.service.impl;

import static com.github.emailtohl.building.common.jpa.entity.BaseEntity.CREATE_DATE_PROPERTY_NAME;
import static com.github.emailtohl.building.common.jpa.entity.BaseEntity.ID_PROPERTY_NAME;
import static com.github.emailtohl.building.common.jpa.entity.BaseEntity.MODIFY_DATE_PROPERTY_NAME;
import static com.github.emailtohl.building.common.jpa.entity.BaseEntity.VERSION_PROPERTY_NAME;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.site.dao.AuthorityRepository;
import com.github.emailtohl.building.site.dao.RoleRepository;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.service.RoleService;
/**
 * 角色管理服务的实现
 * @author HeLei
 * @date 2017.02.04
 */
@Service
public class RoleServiceImpl implements RoleService {
	@Inject RoleRepository roleRepository;
	@Inject AuthorityRepository authorityRepository;

	@Override
	public Role getRole(long id) {
		return roleRepository.findOne(id);
	}
	
	@Override
	public List<Role> getRoles() {
		return roleRepository.findAll();
	}

	@Override
	public List<Authority> getAuthorities() {
		return authorityRepository.findAll();
	}

	@Override
	public long createRole(Role role) {
		Role r = new Role();
		// 仅仅只复制基本属性，权限集合通过手工代码完成
		BeanUtils.copyProperties(role, r, ID_PROPERTY_NAME, CREATE_DATE_PROPERTY_NAME, MODIFY_DATE_PROPERTY_NAME, VERSION_PROPERTY_NAME, "users", "authorities");
		r.getAuthorities().addAll(role.getAuthorities());
		roleRepository.save(r);
		return r.getId();
	}

	@Override
	public long createRole(Role role, Set<String> authorityNames) {
		role.getAuthorities().clear();
		for (String name : authorityNames) {
			Authority a = authorityRepository.findByName(name);
			if (a != null) {
				role.getAuthorities().add(a);
			}
		}
		return createRole(role);
	}

	@Override
	public void updateRole(long id, Role role) {
		Role r = roleRepository.findOne(id);
		// 仅复制基本属性
		BeanUtils.copyProperties(role, r, ID_PROPERTY_NAME, CREATE_DATE_PROPERTY_NAME, MODIFY_DATE_PROPERTY_NAME, VERSION_PROPERTY_NAME, "users", "authorities");
		r.getAuthorities().clear();
		r.getAuthorities().addAll(role.getAuthorities());
	}

	@Override
	public void grantAuthorities(long roleId, Set<String> authorityNames) {
		Role r = roleRepository.findOne(roleId);
		r.getAuthorities().clear();
		for (String name : authorityNames) {
			Authority a = authorityRepository.findByName(name);
			if (a != null) {
				r.getAuthorities().add(a);
			}
		}
	}

	@Override
	public void deleteRole(long id) {
		Role r = roleRepository.findOne(id);
		r.getUsers().forEach(u -> u.getRoles().remove(r));
		r.getUsers().clear();
		r.getAuthorities().forEach(a -> a.getRoles().remove(r));
		r.getAuthorities().clear();
		roleRepository.delete(id);
	}

}
