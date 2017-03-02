package com.github.emailtohl.building.site.service.role;

import static com.github.emailtohl.building.site.entities.role.Authority.USER_ROLE_AUTHORITY_ALLOCATION;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.site.entities.role.Authority;
import com.github.emailtohl.building.site.entities.role.Role;
/**
 * 角色管理的服务层
 * @author HeLei
 * @date 2017.02.04
 */
@PreAuthorize("hasAuthority('" + USER_ROLE_AUTHORITY_ALLOCATION + "')")
@Transactional
@Validated
public interface RoleService {
	String CACHE_NAME_ROLE = "roleCache";
	String CACHE_NAME_ROLE_LIST = "roleListCache";
	String CACHE_NAME_AUTHORITY_LIST = "authorityListCache";
	/**
	 * 获取角色
	 */
	@Cacheable(value = CACHE_NAME_ROLE, key = "#root.args[0]")
	Role getRole(long id);
	
	/**
	 * 获取所有角色
	 * @return
	 */
	@Cacheable(value = CACHE_NAME_ROLE_LIST)
	List<Role> getRoles();
	
	/**
	 * 获取所有权限
	 * @return
	 */
	@Cacheable(value = CACHE_NAME_AUTHORITY_LIST)
	List<Authority> getAuthorities();
	
	/**
	 * 新增一个角色
	 * 角色和用户的关系有程序内部控制，创建时不做关联
	 * @param role
	 * @return
	 */
	@CacheEvict(value = { CACHE_NAME_ROLE, CACHE_NAME_ROLE_LIST }, allEntries = true)
	long createRole(@Valid Role role);
	
	/**
	 * 对long createRole(@Valid Role role);的封装，便于控制器调用
	 * @param role 包含角色的基本信息
	 * @param authorityNames 角色含有的权限名字
	 * @return
	 */
	@CacheEvict(value = { CACHE_NAME_ROLE, CACHE_NAME_ROLE_LIST }, allEntries = true)
	long createRole(@Valid Role role, Set<String> authorityNames);
	
	/**
	 * 修改角色
	 * 角色和用户的关系有程序内部控制，修改时不做关联，也就是说不会修改用户和角色的关系
	 * @param id
	 * @param role
	 */
	@CacheEvict(value = { CACHE_NAME_ROLE, CACHE_NAME_ROLE_LIST }, allEntries = true)
	void updateRole(long id, @Valid Role role);
	
	/**
	 * 为角色分配权限
	 * @param roleId 角色ID
	 * @param authorityNames 权限名
	 */
	@CacheEvict(value = { CACHE_NAME_ROLE, CACHE_NAME_ROLE_LIST }, allEntries = true)
	void grantAuthorities(long roleId, Set<String> authorityNames);
	
	/**
	 * 删除角色
	 * @param id
	 */
	@CacheEvict(value = { CACHE_NAME_ROLE, CACHE_NAME_ROLE_LIST }, allEntries = true)
	void deleteRole(long id);
}
