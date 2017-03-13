package com.github.emailtohl.building.site.dao.audit;

import javax.inject.Inject;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.github.emailtohl.building.common.utils.SecurityContextUtil;
import com.github.emailtohl.building.site.dao.user.UserRepository;
import com.github.emailtohl.building.site.entities.user.User;

/**
 * Spring data的审计接口
 * @author HeLei
 * @date 2017.03.13
 */
@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<User> {
	@Inject
	UserRepository userRepository;
	
	@Override
	public User getCurrentAuditor() {
		return userRepository.findByEmail(SecurityContextUtil.getCurrentUsername());
	}

}
