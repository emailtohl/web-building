package com.github.emailtohl.building.initdb;

import static com.github.emailtohl.building.site.entities.Authority.*;
import static com.github.emailtohl.building.site.entities.Authority.USER_CREATE_SPECIAL;
import static com.github.emailtohl.building.site.entities.Authority.USER_DELETE;
import static com.github.emailtohl.building.site.entities.Authority.USER_DISABLE;
import static com.github.emailtohl.building.site.entities.Authority.USER_ENABLE;
import static com.github.emailtohl.building.site.entities.Authority.USER_READ_ALL;
import static com.github.emailtohl.building.site.entities.Authority.USER_READ_SELF;
import static com.github.emailtohl.building.site.entities.Authority.USER_UPDATE_ALL;
import static com.github.emailtohl.building.site.entities.Authority.USER_UPDATE_SELF;
import static com.github.emailtohl.building.site.entities.Role.ADMIN;
import static com.github.emailtohl.building.site.entities.Role.EMPLOYEE;
import static com.github.emailtohl.building.site.entities.Role.MANAGER;
import static com.github.emailtohl.building.site.entities.Role.USER;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.Company;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.entities.Department;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.Subsidiary;
import com.github.emailtohl.building.site.entities.User.Gender;
/**
 * 初始化数据库使用的测试数据
 * @author HeLei
 */
public class PersistenceData {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static SecureRandom r = new SecureRandom();
	static final int HASHING_ROUNDS = 10;
	
	public final static Authority
			user_create_ordinary = new Authority(USER_CREATE_ORDINARY, "创建普通账号，用于用户自行注册时"),
			user_create_special = new Authority(USER_CREATE_SPECIAL, "创建有一定权限的账号，用于管理员创建时"),
			user_enable = new Authority(USER_ENABLE, "激活账号"),
			user_disable = new Authority(USER_DISABLE, "禁用账号"),
			user_grant_roles = new Authority(USER_GRANT_ROLES, "授予角色"),
			user_read_all = new Authority(USER_READ_ALL, "读取所有用户的权限"),
			user_read_self = new Authority(USER_READ_SELF, "读取自己账号信息"),
			user_update_all = new Authority(USER_UPDATE_ALL, "修改所有用户的权限，用于管理员"),
			user_update_self = new Authority(USER_UPDATE_SELF, "修改自己账号的权限，用于普通用户"),
			user_delete = new Authority(USER_DELETE, "删除用户的权限");

	public final static Role admin = new Role(ADMIN, "管理员"), manager = new Role(MANAGER, "经理"),
			employee = new Role(EMPLOYEE, "雇员"), user = new Role(USER, "普通用户");
	
	public final static Customer emailtohl = new Customer();
	public final static Employee foo = new Employee();
	public final static Employee bar = new Employee();
	public final static Customer baz = new Customer();
	
	public final static Company company = new Company();
	public final static Department product = new Department(), qa = new Department();

	static {
		user_create_ordinary.getRoles().addAll(Arrays.asList(admin, manager, employee, user));
		user_create_special.getRoles().addAll(Arrays.asList(admin, manager));
		user_enable.getRoles().addAll(Arrays.asList(admin, manager, employee, user));
		user_disable.getRoles().addAll(Arrays.asList(admin, manager));
		user_grant_roles.getRoles().addAll(Arrays.asList(admin, manager));
		user_read_all.getRoles().addAll(Arrays.asList(admin, manager, employee));
		user_read_self.getRoles().addAll(Arrays.asList(admin, manager, employee, user));
		user_update_all.getRoles().addAll(Arrays.asList(admin, manager));
		user_update_self.getRoles().addAll(Arrays.asList(admin, manager, employee, user));
		user_delete.getRoles().add(admin);
		
		admin.getAuthorities().addAll(Arrays.asList(user_create_ordinary, user_create_special, user_enable, user_disable, user_grant_roles, user_read_all, user_read_self, user_update_all, user_update_self, user_delete));
		manager.getAuthorities().addAll(Arrays.asList(user_create_ordinary, user_create_special, user_enable, user_disable, user_grant_roles, user_read_all, user_read_self, user_update_all, user_update_self));
		employee.getAuthorities().addAll(Arrays.asList(user_create_ordinary, user_enable, user_read_all, user_read_self, user_update_self));
		user.getAuthorities().addAll(Arrays.asList(user_create_ordinary, user_enable, user_read_self, user_update_self));
		
		admin.getUsers().add(emailtohl);
		manager.getUsers().add(foo);
		employee.getUsers().add(bar);
		user.getUsers().addAll(Arrays.asList(emailtohl, baz));
		
		String salt = BCrypt.gensalt(HASHING_ROUNDS, r);
//		URL url = PersistenceData.class.getProtectionDomain().getCodeSource().getLocation();
		ClassLoader cl = PersistenceData.class.getClassLoader();
		byte[] icon;

		// 附属属性
		Subsidiary s;
		/*
		 * 下面是创建一对多对一数据模型
		 */
		company.setName("XXX注册公司");
		company.setDescription("公司上面还有集团公司");

		product.setName("生产部");
		product.setDescription("研发生产部门");
		product.setCompany(company);
		qa.setName("质量部");
		qa.setDescription("质量与测试部门");
		qa.setCompany(company);

		company.setDepartments(new HashSet<Department>(Arrays.asList(product, qa)));
		
		emailtohl.setName("hl");
		emailtohl.setUsername("emailtohl@163.com");
		emailtohl.setEmail("emailtohl@163.com");
		emailtohl.setEnabled(true);
		emailtohl.setIcon(null);
		emailtohl.setPassword(BCrypt.hashpw("123456", salt));
		emailtohl.setDescription("developer");
		s = new Subsidiary();
		s.setCity("重庆");
		s.setCountry("中国");
		s.setProvince("重庆");
		s.setLanguage("zh");
		s.setMobile("187******82");
		emailtohl.setSubsidiary(s);
		emailtohl.setGender(Gender.MALE);
		emailtohl.getRoles().addAll(Arrays.asList(admin, user));
		// cl.getResourceAsStream方法返回的输入流已经是BufferedInputStream对象，无需再装饰
		try (InputStream is = cl.getResourceAsStream("img/icon-head-emailtohl.png")) {
			emailtohl.setBirthday(sdf.parse("1982-02-12"));
			icon = new byte[is.available()];
			is.read(icon);
			emailtohl.setIcon(icon);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		emailtohl.setIconSrc("download/img/icon-head-emailtohl.png");

		foo.setName("foo");
		foo.setUsername("foo@test.com");
		foo.setEmail("foo@test.com");
		foo.setEnabled(true);
		foo.setIcon(null);
		foo.setPassword(BCrypt.hashpw("123456", salt));
		foo.setDescription("业务管理人员");
		s = new Subsidiary();
		s.setCity("西安");
		s.setCountry("中国");
		s.setProvince("陕西");
		s.setLanguage("zh");
		s.setMobile("139******11");
		foo.setSubsidiary(s);
		foo.setGender(Gender.MALE);
		foo.getRoles().add(manager);
		try (InputStream is = cl.getResourceAsStream("img/icon-head-foo.jpg")) {
			foo.setBirthday(sdf.parse("1990-12-13"));
			icon = new byte[is.available()];
			is.read(icon);
			foo.setIcon(icon);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		foo.setEmpNum(1);
		foo.setPost("系统分析师");
		foo.setSalary(10000.00);
		foo.setDepartment(product);
		foo.setIconSrc("download/img/icon-head-foo.jpg");
		
		bar.setName("bar");
		bar.setUsername("bar@test.com");
		bar.setEmail("bar@test.com");
		bar.setEnabled(true);
		bar.setIcon(null);
		bar.setPassword(BCrypt.hashpw("123456", salt));
		bar.setDescription("普通职员");
		s = new Subsidiary();
		s.setCity("昆明");
		s.setCountry("中国");
		s.setProvince("云南");
		s.setLanguage("zh");
		s.setMobile("130******77");
		bar.setSubsidiary(s);
		bar.setGender(Gender.FEMALE);
		bar.getRoles().add(employee);
		try (InputStream is = cl.getResourceAsStream("img/icon-head-bar.jpg")) {
			bar.setBirthday(sdf.parse("1991-10-24"));
			icon = new byte[is.available()];
			is.read(icon);
			bar.setIcon(icon);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		bar.setEmpNum(2);
		bar.setPost("QA人员");
		bar.setSalary(6000.00);
		bar.setDepartment(qa);
		bar.setIconSrc("download/img/icon-head-bar.jpg");
		
		baz.setName("baz");
		baz.setUsername("baz@test.com");
		baz.setEmail("baz@test.com");
		baz.setEnabled(true);
		baz.setIcon(null);
		baz.setPassword(BCrypt.hashpw("123456", salt));
		baz.setDescription("普通客户");
		s = new Subsidiary();
		s.setCity("成都");
		s.setCountry("中国");
		s.setProvince("四川");
		s.setLanguage("zh");
		s.setMobile("136******87");
		baz.setSubsidiary(s);
		baz.setGender(Gender.FEMALE);
		baz.getRoles().add(user);
		try (InputStream is = cl.getResourceAsStream("img/icon-head-baz.jpg")) {
			baz.setBirthday(sdf.parse("1995-11-20"));
			icon = new byte[is.available()];
			is.read(icon);
			baz.setIcon(icon);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		baz.setIconSrc("download/img/icon-head-baz.jpg");
		
		product.setEmployees(new HashSet<Employee>(Arrays.asList(foo)));
		qa.setEmployees(new HashSet<Employee>(Arrays.asList(bar)));
	}
}
