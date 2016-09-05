package com.github.emailtohl.building.site.service.impl;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.common.repository.jpa.Pager;
import com.github.emailtohl.building.common.utils.BCryptUtil;
import com.github.emailtohl.building.common.utils.BeanTools;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.Manager;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;

/**
 * 管理用户的相关服务，实现类中只提供功能
 * 安全，校验等功能在切面中完成
 * @author Helei
 */
@Service
public class UserServiceImpl implements UserService {
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger();

	@Inject
	UserRepository userRepository;
	
	@Override
	public Long addUser(User u) {
		// 确保添加新用户时，没有授予任何权限，没有启动等
		if (u.getAuthorities() != null) {
			u.getAuthorities().clear();
		}
		u.setEnabled(false);
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
	public void deleteUser(Long id) {
		User persistStatus = userRepository.findOne(id);
		// 先删除外联关系
		persistStatus.setAuthorities(null);
		userRepository.delete(persistStatus);
	}

	@Override
	public Pager<User> getUserPager(User u, Pageable pageable) {
		Pager<User> p = userRepository.dynamicQuery(u, pageable.getPageNumber());
		List<User> ls = filter(p.getDataList());
		p.setDataList(ls);
		return p;
	}

	@Override
	public Page<User> getUserPage(User u, Pageable pageable) {
		Pager<User> p = this.getUserPager(u, pageable);
		return new PageImpl<User>(p.getDataList(), pageable, p.getTotalRow());
	}
	
	@Override
	public User getUser(Long id) {
		return filter(userRepository.findOne(id));
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void mergeUser(Long id, User u) {
		User persistStatus = userRepository.findOne(id);
		// 是否启动，授权，不走此接口，所以在调用merge方法前，先将其设置为null
		u.setEnabled(null);
		u.setAuthorities(null);
		BeanTools.merge(persistStatus, u);
		userRepository.save(persistStatus);
	}
	
	/**
	 * JPA提供者能根据用户的类型确定到底是User、Employ还是Manager
	 * @param users
	 * @return
	 */
	private List<User> filter(List<? extends User> users) {
		List<User> ls = new ArrayList<User>();
		for (User u : users) {
			// Manager对象能获取最完整的信息
			Manager nu = new Manager();
			BeanUtils.copyProperties(u, nu, "password", "authorities");
			ls.add(nu);
		}
		return ls;
	}

	/**
	 * JPA提供者能根据用户的类型确定到底是User、Employ还是Manager
	 * @param users
	 * @return
	 */
	private User filter(User user) {
		// Manager对象能获取最完整的信息
		Manager nu = new Manager();
		BeanUtils.copyProperties(user, nu, "password");
		return nu;
	}

}
