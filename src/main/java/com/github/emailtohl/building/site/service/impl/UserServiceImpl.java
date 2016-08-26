package com.github.emailtohl.building.site.service.impl;

import javax.inject.Inject;
import javax.persistence.AccessType;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.common.repository.jpa.Pager;
import com.github.emailtohl.building.common.utils.BCryptUtil;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;

/**
 * 管理用户的相关服务
 * @author Helei
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Inject
	UserRepository userRepository;
	@Inject
	BCryptUtil BCryptUtil;

	@Override
	public Long addUser(User u) {
		String hashPw = BCryptUtil.hash(u.getPassword());
		u.setPassword(hashPw);
		userRepository.add(u);
		return u.getId();
	}

	@Override
	public void updateUser(Long id, User u) {
		User persistStatus = userRepository.get(id);
		updateUser(u, persistStatus);
	}

	@Override
	public void changePassword(Long id, String newPassword) {
		User u = userRepository.get(id);
		String hashPw = BCryptUtil.hash(u.getPassword());
		u.setPassword(hashPw);
	}

	@Override
	public void deleteUser(Long id) {
		userRepository.delete(id);
	}

	@Override
	public User getUser(Long id) {
		return userRepository.get(id);
	}

	@Override
	public Pager<User> getUserPager(User u, Pageable pageable) {
		return userRepository.getPager(u, (long)pageable.getPageNumber(), pageable.getPageSize(), AccessType.PROPERTY);
	}

	private void updateUser(User updateObj, User persistStatus) {
		
	}
}
