package com.github.emailtohl.building.site.service.impl;

import static com.github.emailtohl.building.site.entities.Authority.USER_ROLE_AUTHORITY_ALLOCATION;
import static com.github.emailtohl.building.site.entities.BaseEntity.CREATE_DATE_PROPERTY_NAME;
import static com.github.emailtohl.building.site.entities.BaseEntity.ID_PROPERTY_NAME;
import static com.github.emailtohl.building.site.entities.BaseEntity.MODIFY_DATE_PROPERTY_NAME;
import static com.github.emailtohl.building.site.entities.BaseEntity.VERSION_PROPERTY_NAME;
import static com.github.emailtohl.building.site.entities.Role.ADMIN;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.common.Constant;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.utils.BeanUtil;
import com.github.emailtohl.building.common.utils.SecurityContextUtil;
import com.github.emailtohl.building.site.dao.DepartmentRepository;
import com.github.emailtohl.building.site.dao.RoleRepository;
import com.github.emailtohl.building.site.dao.UserRepository;
import com.github.emailtohl.building.site.entities.BaseEntity;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.entities.Department;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.entities.User.AuthenticationImpl;
import com.github.emailtohl.building.site.service.UserService;

/**
 * 管理用户的相关服务，实现类中只提供功能
 * 安全，校验等功能在切面中完成
 * @author HeLei
 */
@Service
public class UserServiceImpl implements UserService, Serializable {
	private static final long serialVersionUID = 6570774356558489623L;
	private static final transient Logger logger = LogManager.getLogger();
	private static final transient SecureRandom RANDOM = new SecureRandom();
	private static final transient int HASHING_ROUNDS = 10;
	@Inject transient UserRepository userRepository;
	@Inject transient RoleRepository roleRepository;
	@Inject transient DepartmentRepository departmentRepository;
	private transient Role admin;
	
	@PostConstruct
	public void setRoles() {
		admin = roleRepository.findByName(ADMIN);
	}
	
	@Override
	public Long addEmployee(Employee u) {
		Employee e = new Employee();
		BeanUtils.copyProperties(u, e, ID_PROPERTY_NAME, CREATE_DATE_PROPERTY_NAME, MODIFY_DATE_PROPERTY_NAME, VERSION_PROPERTY_NAME, "roles", "enabled", "department");
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
		String pw = u.getPassword();
		if (pw == null || pw.isEmpty()) {
			pw = "123456";// 设置默认密码
		}
		pw = BCrypt.hashpw(pw, BCrypt.gensalt(HASHING_ROUNDS, RANDOM));
		e.setPassword(pw);
		userRepository.save(e);
		return e.getId();
	}

	@Override
	public Long addCustomer(Customer u) {	
		Customer e = new Customer();
		BeanUtils.copyProperties(u, e, ID_PROPERTY_NAME, CREATE_DATE_PROPERTY_NAME, MODIFY_DATE_PROPERTY_NAME, VERSION_PROPERTY_NAME, "roles", "enabled", "password", "department");
		Role r = roleRepository.findByName(Role.USER);
		e.getRoles().add(r);
		r.getUsers().add(e);
		// 用户注册时，还未激活
		e.setEnabled(false);
		String pw = u.getPassword();
		if (pw == null || pw.isEmpty()) {
			pw = "123456";// 设置默认密码
		}
		pw = BCrypt.hashpw(pw, BCrypt.gensalt(HASHING_ROUNDS, RANDOM));
		e.setPassword(pw);
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
		// 能进此接口的拥有USER_ROLE_AUTHORITY_ALLOCATION权限，现在认为含有该权限的人就拥有ADMIN角色
		boolean isAdmin = SecurityContextUtil.hasAnyAuthority(USER_ROLE_AUTHORITY_ALLOCATION);
		User u = userRepository.findOne(id);
		// 先删除原有的
		/* 虽然在User实体上已定义了级联关系，但从业务逻辑理解和保险的角度上来看，还是手工操作关系的删除
		u.getRoles().clear();
		*/
		for (Iterator<Role> i = u.getRoles().iterator(); i.hasNext();) {
			Role r = i.next();
			r.getUsers().remove(u);
			i.remove();
		}
		// 再添加新增的
		for (String name : roleNames) {
			Role r = roleRepository.findByName(name);
			if (!isAdmin && r.equals(admin)) {
				throw new IllegalArgumentException("你没有权限分配ADMIN角色或改动ADMIN角色的账户");
			}
			if (r == null) {
				// 抛出异常后，事务会回滚
				throw new IllegalArgumentException("没有这个角色名： " + name);
			}
			u.getRoles().add(r);
			r.getUsers().add(u);
		}
	}
	
	@Override
	public void grantUserRole(long id) {
		User u = userRepository.findOne(id);
		Role r = roleRepository.findByName(Role.USER);
		u.getRoles().add(r);
		r.getUsers().add(u);
	}
	
	@Override
	public void changePassword(String email, String newPassword) {
		String hashPw = BCrypt.hashpw(newPassword, BCrypt.gensalt(HASHING_ROUNDS, RANDOM));
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
		Department d = emp.getDepartment();
		if (d != null && d.getName() != null) {
			entity.setDepartment(departmentRepository.findByName(d.getName()));
		}
		// 修改密码，启用/禁用账户，授权功能，不走此接口，所以在调用merge方法前，先将其设置为null
		emp.setRoles(null);
		emp.setPassword(null);
		emp.setEnabled(null);
		emp.setDepartment(null);
		BeanUtil.merge(entity, emp);
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
		BeanUtil.merge(entity, cus);
		userRepository.save(entity);
	}

	@Override
	public void updateIconSrc(long id, String iconSrc) {
		userRepository.getOne(id).setIconSrc(iconSrc);
	}

	@Override
	public void updateIcon(long id, byte[] icon) {
		userRepository.getOne(id).setIcon(icon);
	}

	@Override
	public Pager<User> getUserPager(User u, Pageable pageable) {
		String fuzzy = u.getEmail();
		if (fuzzy != null && !fuzzy.isEmpty()) {
			fuzzy = '%' + fuzzy + '%';
			u.setEmail(fuzzy);
		}
		Pager<User> pe = userRepository.dynamicQuery(u, pageable);
		List<User> ls = convert(pe.getContent());
		Pager<User> pd = new Pager<User>(ls, pe.getTotalElements(), pageable.getPageNumber(), pe.getPageSize());
		return pd;
	}

	@Override
	public Page<User> getUserPage(User u, Pageable pageable) {
		Pager<User> p = this.getUserPager(u, pageable);
		return new PageImpl<User>(p.getContent(), pageable, p.getTotalElements());
	}

	@Override
	public boolean isExist(String email) {
		if (userRepository.findByEmail(email) == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Pager<User> getPageByRoles(String email, Set<String> roleNames, Pageable pageable) {
		String fuzzy = email;
		if (email != null && !email.isEmpty()) {
			fuzzy = '%' + email + '%';
		}
		return userRepository.getPagerByCriteria(fuzzy, roleNames, pageable);
	}
	
	@Override
	public List<Role> getRoles() {
		return roleRepository.findAll();
	}

	@Override
	public Authentication authenticate(String email, String password) {
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
		AuthenticationImpl a = u.getAuthentication();
		a.setAuthenticated(true);
		a.eraseCredentials();
		Details d = new Details();
		d.setSessionId(ThreadContext.get("sessionId"));
		d.setRemoteAddress(ThreadContext.get("remoteAddress"));
		a.setDetails(d);
		return a;
	}
	
	@Override
	public void setPublicKey(BigInteger publicKey, BigInteger module) {
		String email = SecurityContextUtil.getCurrentUsername();
		User u = userRepository.findByEmail(email);
		if (u != null) {
			u.setPublicKey(publicKey.toString());
			u.setModule(module.toString());
		}
	}
	
	@Override
	public void clearPublicKey() {
		String email = SecurityContextUtil.getCurrentUsername();
		User u = userRepository.findByEmail(email);
		if (u != null) {
			u.setPublicKey(null);
			u.setModule(null);
		}
	}
	
	public class Details implements Serializable {
		private static final long serialVersionUID = -7461854984848054398L;
		String remoteAddress;
		String sessionId;
		String certificateSerialNumber;
		public String getRemoteAddress() {
			return remoteAddress;
		}
		public void setRemoteAddress(String remoteAddress) {
			this.remoteAddress = remoteAddress;
		}
		public String getSessionId() {
			return sessionId;
		}
		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}
		public String getCertificateSerialNumber() {
			return certificateSerialNumber;
		}
		public void setCertificateSerialNumber(String certificateSerialNumber) {
			this.certificateSerialNumber = certificateSerialNumber;
		}
	}
	
	/**
	 * 下面是实现AuthenticationProvider，可以供Spring Security框架使用
	 */
	@Transactional
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken credentials = (UsernamePasswordAuthenticationToken) authentication;
		String email = credentials.getPrincipal().toString();
		String password = credentials.getCredentials().toString();
		// 用户名和密码用完后，记得擦除
		credentials.eraseCredentials();
		return authenticate(email, password);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication == UsernamePasswordAuthenticationToken.class;
	}

	
	/**
	 * JPA提供者能根据用户的类型确定到底是User、Employ还是Manager
	 * @param users
	 * @return
	 */
	private List<User> convert(List<? extends User> users) {
		List<User> ls = new ArrayList<User>();
		users.forEach(u -> {
			User target;
			if (u instanceof Employee) {
				target = new Employee();
			} else if (u instanceof Customer) {
				target = new Customer();
			} else {
				target = new User();
			}
			BeanUtils.copyProperties(u, target, "password", "authorities", BaseEntity.VERSION_PROPERTY_NAME);
			ls.add(target);
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
		BeanUtils.copyProperties(user, result, "password", "icon", BaseEntity.VERSION_PROPERTY_NAME);
		return result;
	}

	/**
	 * 实现UserDetailsService
	 */
	private transient Pattern p = Pattern.compile("username=(" + Constant.PATTERN_EMAIL.substring(1, Constant.PATTERN_EMAIL.length() - 1) + ')');
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Matcher m = p.matcher(username);
		if (!m.find()) {
			return null;
		}
		String email = m.group(1);
		User u = userRepository.findByEmail(email);
		return u.getUserDetails();
	}

}
