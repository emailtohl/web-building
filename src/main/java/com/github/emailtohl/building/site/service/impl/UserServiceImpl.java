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
import com.github.emailtohl.building.site.dao.RoleRepository;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.entities.Department;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.Role;
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
	@Inject UserRepository userRepository;
	@Inject RoleRepository roleRepository;
	@Inject DepartmentRepository departmentRepository;
	
	@Override
	public Long addEmployee(Employee u) {
		Employee e = new Employee();
		BeanUtils.copyProperties(u, e, "roles", "enabled", "password", "department");
		// 关于工号
		synchronized (this) {
			Integer max = userRepository.getMaxEmpNo();
			if (max == null) {
				max = 0;
			}
			e.setEmpNum(++max);
		}
		// 关于初始授权
		Role r = roleRepository.findByName(Role.EMPLOYEE);
		e.getRoles().add(r);
		r.getUsers().add(e);
		// 关于部门
		Department d = u.getDepartment();
		if (d != null && d.getName() != null) {
			d = departmentRepository.findByName(d.getName());
			e.setDepartment(d);
		}
		// 创建雇员时，可以直接激活可用
		e.setEnabled(true);
		String hashPw;
		if (u.getPassword() == null) {
			hashPw = BCryptUtil.hash("123456");// 设置默认密码
		} else {
			hashPw = BCryptUtil.hash(u.getPassword());
		}
		e.setPassword(hashPw);
		userRepository.save(e);
		return e.getId();
	}

	@Override
	public Long addCustomer(Customer u) {	
		Customer e = new Customer();
		BeanUtils.copyProperties(u, e, "roles", "enabled", "password", "department");
		Role r = roleRepository.findByName(Role.USER);
		e.getRoles().add(r);
		r.getUsers().add(e);
		// 用户注册时，还未激活
		e.setEnabled(false);
		String hashPw;
		if (u.getPassword() == null) {
			hashPw = BCryptUtil.hash("123456");// 设置默认密码
		} else {
			hashPw = BCryptUtil.hash(u.getPassword());
		}
		e.setPassword(hashPw);
		userRepository.save(e);
		return e.getId();
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
	public void grantRoles(long id, String... roleNames) {
		User u = userRepository.findOne(id);
		for (String name : roleNames) {
			Role r = roleRepository.findByName(name);
			if (r == null) {
				throw new IllegalArgumentException("没有这个角色名： " + name);
			}
			u.getRoles().add(r);
			r.getUsers().add(u);
		}
	}

	@Override
	public void changePassword(String email, String newPassword) {
		String hashPw = BCryptUtil.hash(newPassword);
		User u = userRepository.findByEmail(email);
		u.setPassword(hashPw);
	}

	@Override
	public void changePasswordByEmail(String email, String newPassword) {
		changePassword(email, newPassword);
	}

	@Override
	public void deleteUser(Long id) {
		User entity = userRepository.findOne(id);
		// 先删除外联关系
		entity.setRoles(null);
		userRepository.delete(entity);
	}

	@Override
	public User getUser(Long id) {
		return convert(userRepository.findOne(id));
	}

	@Override
	public User getUserByEmail(String email) {
		return convert(userRepository.findByEmail(email));
	}
	
	@Override
	public void mergeEmployee(String email, Employee emp) {
		User u = userRepository.findByEmail(email);
		if (!(u instanceof Employee)) {
			throw new IllegalArgumentException("未找到该职员");
		}
		Employee entity = (Employee) u;
		// 修改密码，启用/禁用账户，授权功能，不走此接口，所以在调用merge方法前，先将其设置为null
		emp.setRoles(null);
		emp.setPassword(null);
		emp.setEnabled(null);
		emp.setDepartment(null);
		BeanTools.merge(entity, emp);
		Department d = emp.getDepartment();
		if (d != null && d.getName() != null) {
			entity.setDepartment(departmentRepository.findByName(d.getName()));
		}
		userRepository.save(entity);
	}
	
	public void mergeCustomer(String email, Customer cus) {
		User u = userRepository.findByEmail(email);
		if (!(u instanceof Customer)) {
			throw new IllegalArgumentException("未找到该客户");
		}
		Customer entity = (Customer) u;
		// 修改密码，启用/禁用账户，授权功能，不走此接口，所以在调用merge方法前，先将其设置为null
		cus.setRoles(null);
		cus.setPassword(null);
		cus.setEnabled(null);
		BeanTools.merge(entity, cus);
		userRepository.save(entity);
	}

	@Override
	public Pager<User> getUserPager(User u, Pageable pageable) {
		Pager<User> pe = userRepository.dynamicQuery(u, pageable.getPageNumber());
		List<User> ls = convert(pe.getContent());
		Pager<User> pd = new Pager<User>(ls, pe.getTotalElements(), pageable.getPageNumber(), pe.getPageSize());
		return pd;
	}

	@Override
	public Page<User> getUserPage(User u, Pageable pageable) {
		Pager<User> p = this.getUserPager(u, pageable);
		return new PageImpl<User>(p.getContent(), pageable, p.getTotalElements());
	}
	
	/**
	 * JPA提供者能根据用户的类型确定到底是User、Employ还是Manager
	 * @param users
	 * @return
	 */
	private List<User> convert(List<? extends User> users) {
		List<User> ls = new ArrayList<User>();
		users.forEach(u -> {
			User result;
			if (u instanceof Employee) {
				result = new Employee();
			} else if (u instanceof Customer) {
				result = new Customer();
			} else {
				result = new User();
			}
			BeanUtils.copyProperties(u, result, "password", "authorities");
			ls.add(result);
		});
		return ls;
	}

	/**
	 * JPA提供者能根据用户的类型确定到底是User、Employ还是Manager
	 * @param users
	 * @return
	 */
	private User convert(User user) {
		User result;
		if (user instanceof Employee) {
			result = new Employee();
		} else if (user instanceof Customer) {
			result = new Customer();
		} else {
			result = new User();
		}
		BeanUtils.copyProperties(user, result, "password");
		return result;
	}
}
