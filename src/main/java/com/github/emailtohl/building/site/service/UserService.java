package com.github.emailtohl.building.site.service;

import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.User;

/**
 * 用户管理的服务
 * 
 * User类中，有的属性，如authorities是需要有权限才能调用的。
 * 所以新增User时，不会添加该属性，更新User时，也不会更新这些属性
 * @author Helei
 */
@Transactional
@Validated
public interface UserService {
	/**
	 * 添加用户
	 * 新增用户时，不设置authorities属性，且enable属性为false
	 * @param u
	 * @return 新增User的id
	 */
	@Min(value = 1L)
	Long addUser(@Valid User u);
	
	/**
	 * 启用用户
	 * @param id
	 */
	void enableUser(@Min(value = 1L) Long id);
	
	/**
	 * 禁用用户
	 * @param id
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	void disableUser(@Min(value = 1L) Long id);
	
	/**
	 * 为用户授权
	 * 如果是ADMIN则都允许，如果是MANAGER则只能授权EMPLOYEE和USER
	 * hassPermission()是spring security的PermissionEvaluator的接口，可在其中定义计算逻辑
	 * targetObject是spring security提供的另外一个值，它代表了要进行计算的当前列表元素
	 * @param id
	 * @param authorities
	 */
	@PreAuthorize("hassPermission(targetObject, 'grantedAuthority')")
	void grantedAuthority(@Min(value = 1L) Long id, @NotNull Set<Authority> authorities);
	
	/**
	 * 修改用户
	 * 这里的方法名使用的是merge，传入的User参数只存储需要更新的属性，不更新的属性值为null
	 * 
	 * 修改用户时，不设置authorities和enable属性
	 * 
	 * @param u中的id不能为null， u中属性不为null的值为修改项
	 */
	@PreAuthorize("hassPermission(targetId, targetType, 'mergeUser')")
	void mergeUser(@Min(value = 1L) Long id, User u);
	
	/**
	 * 修改密码，限制只能本人才能修改
	 * authentication是直接从SecurityContextHolder中获取的对象
	 * @param id
	 * @param newPassword
	 */
	@PreAuthorize("#email == authentication.principal.username")
	void changePassword(String email, @Pattern(regexp = "^[^\\s&\"<>]+$") String newPassword);
	
	/**
	 * 删除用户，只有ADMIN才能执行，MANAGER可以禁用用户
	 * @param id
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	void deleteUser(@Min(value = 1L) Long id);
	
	/**
	 * 查询用户，通过认证的均可调用
	 * returnObject和principal是spring security内置对象
	 * @param id
	 * @return
	 */
	@PostAuthorize("hasAnyAuthority('ADMIN', 'MANAGER') || returnObject.email == principal.username")
	User getUser(@Min(value = 1L) Long id);
	
	/**
	 * 获取用户Page
	 * 
	 * 实现类中要对Pager中返回的List中敏感信息进行过滤
	 * 
	 * @param u
	 * @param pageable
	 * @return
	 */
	@NotNull
	Page<User> getUserPager(User u, Pageable pageable);
	
	/**
	 * 认证用户，由于要返回用户信息，所以只有本人才能调用。
	 * 事实上认证功能已经交给spring security框架完成
	 * @param email
	 * @param password
	 * @return
	 */
	User authenticate(String email, String password);
}
