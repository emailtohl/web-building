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
		u.setEnabled(false);
		String hashPw = BCryptUtil.hash(u.getPassword());
		u.setPassword(hashPw);
		Department d = u.getDepartment();
		if (d != null && d.getName() != null) {
			d = departmentRepository.findByName(d.getName());
			u.setDepartment(d);
		}
		User entity;
		if (u.getUserType() != null) {
			switch (u.getUserType()) {
			case EMPLOYEE :
				entity = getNewEmployee();
				break;
			case MANAGER :
				entity = getNewManager();
				break;
			default :
				entity = new User();
			}
		} else {
			entity = new User();
		}
		// 确保添加新用户时，没有授予任何权限，没有启动等
		BeanUtils.copyProperties(u, entity, "authorities");
		userRepository.save(entity);
		return entity.getId();
	}
	
	// 获取的Employee含emp_no
	private synchronized Employee getNewEmployee() {
		Employee e = new Employee();
		Integer max = userRepository.getMaxEmpNo();
		if (max == null) {
			max = 1;
		}
		e.setEmpNum(max++);
		return e;
	}
	// 获取的Manager含emp_no
	private synchronized Manager getNewManager() {
		Manager m = new Manager();
		Integer max = userRepository.getMaxEmpNo();
		if (max == null) {
			max = 1;
		}
		m.setEmpNum(max++);
		return m;
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
		// 是否启动，授权，不走此接口，所以在调用merge方法前，先将其设置为null
		u.setEnabled(null);
		u.setAuthorities(null);
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
