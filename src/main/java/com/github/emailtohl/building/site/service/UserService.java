package com.github.emailtohl.building.site.service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.common.repository.jpa.Pager;
import com.github.emailtohl.building.site.entities.User;

/**
 * 用户管理的服务
 * @author Helei
 */
@Transactional
@Validated
@PreAuthorize("isFullyAuthenticated")
public interface UserService {
	/**
	 * 添加用户，只有管理人员才能执行
	 * @param u
	 * @return
	 */
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('MANAGER') and #u.enabled == false and !#u.authorities.contains('ADMIN'))")
	@NotNull
	Long addUser(@Valid User u);
	
	/**
	 * 启用用户
	 * @param id
	 */
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('MANAGER') and #u.enabled == false and !#u.authorities.contains('ADMIN'))")
	void enableUser(@Min(value = 1L) Long id);
	
	/**
	 * 禁用用户
	 * @param id
	 */
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
	void disableUser(@Min(value = 1L) Long id);
	
	/**
	 * 修改用户，只有管理人员才能执行
	 * @param u
	 */
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
	void updateUser(@Min(value = 1L) Long id, User u);
	
	/**
	 * 修改密码，限制只能本人或管理员（管理员是否能修改值得商榷）才能修改
	 * authentication是直接从SecurityContextHolder中获取的对象
	 * @param id
	 * @param newPassword
	 */
//	@PreAuthorize("#email == authentication.principal.username or hasAuthority('ADMIN')")
	void changePassword(String email, @Pattern(regexp = "^[^\\s&\"<>]+$") String newPassword);
	
	/**
	 * 删除用户，只有ADMIN才能执行，MANAGER可以禁用用户
	 * @param id
	 */
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	void deleteUser(@Min(value = 1L) Long id);
	
	/**
	 * 查询用户，通过认证的均可调用
	 * @param id
	 * @return
	 */
	User getUser(@Min(value = 1L) Long id);
	
	/**
	 * 获取用户Pager
	 * @param u
	 * @param pageable
	 * @return
	 */
	@NotNull
	Pager<User> getUserPager(User u, Pageable pageable);
	
	/**
	 * 认证用户，由于要返回用户信息，所以只有本人才能调用。
	 * 事实上认证功能已经交给spring security框架完成
	 * @param email
	 * @param password
	 * @return
	 */
	@PostFilter("filterObject.user.email == authentication.principal.username")
	User authenticate(String email, String password);
}
