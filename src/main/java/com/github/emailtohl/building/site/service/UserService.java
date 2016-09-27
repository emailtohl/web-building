package com.github.emailtohl.building.site.service;

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

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.Manager;
import com.github.emailtohl.building.site.entities.User;

/**
 * 用户管理的服务
 * 
 * User类中，有的属性，如authorities是需要有权限才能调用的。
 * 所以新增User时，不会添加该属性，更新User时，也不会更新这些属性
 * 关于授权，需要在涉及权限的接口中定义
 * 
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
	 * 修改密码，限制只能本人才能修改
	 * 登录页面中通过邮箱方式修改密码在AuthenticationService接口中
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
	 * 通过邮箱名查询用户，通过认证的均可调用
	 * 
	 * @param email
	 * @return
	 */
	@PostAuthorize("hasAnyAuthority('ADMIN', 'MANAGER') || #email == principal.username")
	User getUserByEmail(@NotNull String email);
	
	/**
	 * 修改用户
	 * 这里的方法名使用的是merge，传入的User参数只存储需要更新的属性，不更新的属性值为null
	 * 
	 * 修改用户时，不设置authorities和enable属性
	 * 
	 * @param u中的id不能为null， u中属性不为null的值为修改项
	 */
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER') || #u.email == authentication.principal.username")
	void mergeUser(@Min(value = 1L) Long id, Manager u/* 用最大范围接收数据 */);
	
	/**
	 * 获取用户Pager
	 * 
	 * 实现类中要对Pager中返回的List中敏感信息进行过滤
	 * 
	 * @param u
	 * @param pageable
	 * @return
	 */
	@NotNull
	@PreAuthorize("isAuthenticated()")
	Pager<User> getUserPager(User u, Pageable pageable);
	
	/**
	 * 获取用户Page,这里的Page是Spring Data提供的数据结构
	 * 
	 * 实现类中要对Pager中返回的List中敏感信息进行过滤
	 * 
	 * @param u
	 * @param pageable
	 * @return
	 */
	@NotNull
	@PreAuthorize("isAuthenticated()")
	Page<User> getUserPage(User u, Pageable pageable);
	
}
