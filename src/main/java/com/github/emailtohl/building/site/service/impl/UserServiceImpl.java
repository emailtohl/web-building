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

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.utils.BCryptUtil;
import com.github.emailtohl.building.common.utils.BeanTools;
import com.github.emailtohl.building.site.dao.DepartmentRepository;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.dto.UserDto;
import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.Department;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.Manager;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;

/**
 * 管理用户的相关服务，实现类中只提供功能
 * 安全，校验等功能在切面中完成
 * @author HeLei
 */
@Service
public class UserServiceImpl implements UserService {
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger();

	@Inject
	UserRepository userRepository;
	@Inject
	DepartmentRepository departmentRepository;
	
	@Override
	public Long addUser(UserDto u) {
		User entity;
		if (u.getUserType() != null) {
			switch (u.getUserType()) {
			case EMPLOYEE :
				entity = newEmployee(u);
				break;
			case MANAGER :
				entity = newManager(u);
				break;
			default :
				entity = newUser(u);
			}
		} else {
			entity = newUser(u);
		}
		// 新建时，账户处于未激活状态，通过授权接口进行激活
		entity.setEnabled(false);
		String hashPw;
		if (u.getPlainPassword() == null) {
			hashPw = BCryptUtil.hash("123456");// 设置默认密码
		} else {
			hashPw = BCryptUtil.hash(u.getPlainPassword());
		}
		entity.setPassword(hashPw);
		userRepository.save(entity);
		return entity.getId();
	}
	/**
	 * 获取普通的User实体，初始化默认授权
	 * @param u
	 * @return
	 */
	private synchronized User newUser(UserDto u) {
		User entity = new User();
		copyProperties(u, entity);
		entity.getAuthorities().add(Authority.USER);
		return entity;
	}
	/**
	 * 获取的Employee含emp_no，初始化默认授权
	 * @param u
	 * @return
	 */
	private synchronized Employee newEmployee(UserDto u) {
		Employee e = new Employee();
		copyProperties(u, e);
		Integer max = userRepository.getMaxEmpNo();
		if (max == null) {
			max = 0;
		}
		e.setEmpNum(++max);
		e.getAuthorities().add(Authority.EMPLOYEE);
		Department d = u.getDepartment();
		if (d != null && d.getName() != null) {
			d = departmentRepository.findByName(d.getName());
			e.setDepartment(d);
		}
		return e;
	}
	/**
	 * 获取的Manager含emp_no，初始化默认授权
	 * @param u
	 * @return
	 */
	private synchronized Manager newManager(UserDto u) {
		Manager m = new Manager();
		copyProperties(u, m);
		Integer max = userRepository.getMaxEmpNo();
		if (max == null) {
			max = 0;
		}
		m.setEmpNum(++max);
		m.getAuthorities().add(Authority.MANAGER);
		Department d = u.getDepartment();
		if (d != null && d.getName() != null) {
			d = departmentRepository.findByName(d.getName());
			m.setDepartment(d);
		}
		return m;
	}
	
	/**
	 * 统一定义复制属性
	 */
	private void copyProperties(UserDto src, User dest) {
		BeanUtils.copyProperties(src, dest, "authorities", "enabled", "password", "department");
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
		User entity = userRepository.findOne(id);
		// 先删除外联关系
		entity.setAuthorities(null);
		userRepository.delete(entity);
	}

	@Override
	public Pager<UserDto> getUserPager(UserDto u, Pageable pageable) {
		Pager<User> pe = userRepository.dynamicQuery(u, pageable.getPageNumber());
		List<UserDto> ls = convert(pe.getContent());
		Pager<UserDto> pd = new Pager<UserDto>(ls, pe.getTotalElements(), pe.getPageSize());
		return pd;
	}

	@Override
	public Page<UserDto> getUserPage(UserDto u, Pageable pageable) {
		Pager<UserDto> p = this.getUserPager(u, pageable);
		return new PageImpl<UserDto>(p.getContent(), pageable, p.getTotalElements());
	}
	
	@Override
	public UserDto getUser(Long id) {
		return convert(userRepository.findOne(id));
	}

	@Override
	public UserDto getUserByEmail(String email) {
		return convert(userRepository.findByEmail(email));
	}

	@Override
	public void mergeUser(Long id, UserDto u) {
		User entity = userRepository.findOne(id);
		// 修改密码，授权功能，不走此接口，所以在调用merge方法前，先将其设置为null
		u.setAuthorities(null);
		u.setPassword(null);
		Department d = u.getDepartment();
		if (d != null && d.getName() != null) {
			u.setDepartment(departmentRepository.findByName(d.getName()));
		}
		BeanTools.merge(entity, u);
		userRepository.save(entity);
	}
	
	/**
	 * JPA提供者能根据用户的类型确定到底是User、Employ还是Manager
	 * @param users
	 * @return
	 */
	private List<UserDto> convert(List<? extends User> users) {
		List<UserDto> ls = new ArrayList<UserDto>();
		users.forEach(u -> {
			UserDto dto = new UserDto();
			BeanUtils.copyProperties(u, dto, "password", "authorities");
			ls.add(dto);
		});
		return ls;
	}

	/**
	 * JPA提供者能根据用户的类型确定到底是User、Employ还是Manager
	 * @param users
	 * @return
	 */
	private UserDto convert(User user) {
		// Manager对象能获取最完整的信息
		UserDto dto = new UserDto();
		BeanUtils.copyProperties(user, dto, "password");
		return dto;
	}

}
