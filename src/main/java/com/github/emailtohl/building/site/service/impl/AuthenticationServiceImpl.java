package com.github.emailtohl.building.site.service.impl;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.AuthenticationService;

/**
 * 认证服务的实现类
 * 
 * @author Helei
 */
@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
	private static final Logger log = LogManager.getLogger();
	@Inject UserRepository userRepository;

	@Override
	public User authenticate(String email, String password) {
		User u = userRepository.findByEmail(email);
		if (u == null) {
			log.warn("Authentication failed for non-existent user {}.", email);
			return null;
		}
		if (!BCrypt.checkpw(password, u.getPassword())) {
			log.warn("Authentication failed for user {}.", email);
			return null;
		}
		log.debug("User {} successfully authenticated.", email);
		return u;
	}
}
