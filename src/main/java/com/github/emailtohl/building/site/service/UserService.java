package com.github.emailtohl.building.site.service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.github.emailtohl.building.common.repository.jpa.Pager;
import com.github.emailtohl.building.site.entities.User;

/**
 * 用户管理的服务
 * @author Helei
 */
@Transactional
public interface UserService {
	@NotNull Long addUser(@Valid User u);
	void updateUser(User u);
	void changePassword(@Min(value = 1L) Long id, @Pattern(regexp = "^[^\\s&\"<>]+$") String newPassword);
	void deleteUser(@Min(value = 1L) Long id);
	User getUser(@Min(value = 1L) Long id);
	@NotNull Pager<User> getUserPager(@NotNull User u);
}
