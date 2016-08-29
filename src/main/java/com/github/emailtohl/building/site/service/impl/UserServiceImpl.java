package com.github.emailtohl.building.site.service.impl;

import javax.inject.Inject;
import javax.persistence.AccessType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.common.repository.jpa.Pager;
import com.github.emailtohl.building.common.utils.BCryptUtil;
import com.github.emailtohl.building.common.utils.JavaBeanTools;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;

/**
 * 管理用户的相关服务，实现类中只提供功能
 * 安全，校验等功能在切面中完成
 * @author Helei
 */
@Service
public class UserServiceImpl implements UserService {
	private static final Logger logger = LogManager.getLogger();
	
	@Inject
	UserRepository userRepository;

	@Override
	public Long addUser(User u) {
		String hashPw = BCryptUtil.hash(u.getPassword());
		u.setPassword(hashPw);
		userRepository.save(u);
		return u.getId();
	}
	
	@Override
	public void enableUser(Long id) {
		userRepository.findOne(id).setEnabled(true);
	}

	@Override
	public void disableUser(Long id) {
		userRepository.findOne(id).setEnabled(false);
	}

	@Override
	public void changePassword(String email, String newPassword) {
		String hashPw = BCryptUtil.hash(newPassword);
		User u = userRepository.findByEmail(email);
		u.setPassword(hashPw);
	}

	@Override
	public void updateUser(Long id, User u) {
		User persistStatus = userRepository.findOne(id);
		JavaBeanTools.merge(persistStatus, u);
		userRepository.save(persistStatus);
	}

	@Override
	public void deleteUser(Long id) {
		User persistStatus = userRepository.findOne(id);
		persistStatus.setAuthorities(null);
		userRepository.delete(persistStatus);
	}

	@Override
	public User getUser(Long id) {
		return userRepository.findOne(id);
	}

	@Override
	public Pager<User> getUserPager(User u, Pageable pageable) {
		return userRepository.getPager(u, pageable.getPageNumber(), pageable.getPageSize(), AccessType.PROPERTY);
	}

	@Override
	public User authenticate(String email, String password) {
		User u = userRepository.findByEmail(email);
		if (u == null) {
			logger.warn("Authentication failed for non-existent user {}.", email);
			return null;
		}
		if (!BCrypt.checkpw(password, u.getPassword())) {
			logger.warn("Authentication failed for user {}.", email);
			return null;
		}
		logger.debug("User {} successfully authenticated.", email);
		return u;
	}
}
