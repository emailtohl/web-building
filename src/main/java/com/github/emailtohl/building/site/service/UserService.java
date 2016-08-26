package com.github.emailtohl.building.site.service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.data.domain.Pageable;
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
public interface UserService {
	/**
	 * 添加用户，只有管理人员才能执行
	 * @param u
	 * @return
	 */
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
	@NotNull
	Long addUser(@Valid User u);
	
	/**
	 * 修改用户，只有管理人员才能执行
	 * @param u
	 */
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
	void updateUser(@Min(value = 1L) Long id, User u);
	
	/**
	 * 修改密码，限制只能本人才能修改
	 * @param id
	 * @param newPassword
	 */
	@PreAuthorize("principal.id.equals(#id)")
	void changePassword(@Min(value = 1L) Long id, @Pattern(regexp = "^[^\\s&\"<>]+$") String newPassword);
	
	/**
	 * 删除用户，只有管理人员才能执行
	 * @param id
	 */
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
	void deleteUser(@Min(value = 1L) Long id);
	
	/**
	 * 查询用户
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
}
