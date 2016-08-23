package com.github.emailtohl.building.initdb;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.github.emailtohl.building.site.entities.Authority;
import com.github.emailtohl.building.site.entities.Company;
import com.github.emailtohl.building.site.entities.Department;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.Manager;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.Subsidiary;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.entities.User.Gender;

public class PersistenceData {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static SecureRandom r = new SecureRandom();
	static final int HASHING_ROUNDS = 10;
	
	public static final Authority add = new Authority(), delete = new Authority(), update = new Authority(),
			query = new Authority();
	public static final Role admin = new Role(), employee = new Role(), manager = new Role(), user = new Role();
	public static final User emailtohl = new User();
	public static final Manager foo = new Manager();
	public static final Employee bar = new Employee();
	public static final Company company = new Company();
	public static final Department product = new Department(), qa = new Department();

	static {
		String salt = BCrypt.gensalt(HASHING_ROUNDS, r);
		add.setName("增加");
		delete.setName("删除");
		update.setName("修改");
		query.setName("查询");
		
		admin.setName("ADMIN");
		manager.setName("MANAGER");
		employee.setName("EMPLOYEE");
		user.setName("USER");

		admin.getAuthorities().addAll(Arrays.asList(add, delete, update, query));
		manager.getAuthorities().addAll(Arrays.asList(add, delete, update, query));
		employee.getAuthorities().addAll(Arrays.asList(add, delete, query));
		user.getAuthorities().addAll(Arrays.asList(query));

		add.getRoles().addAll(Arrays.asList(admin, manager, employee));
		delete.getRoles().addAll(Arrays.asList(admin, manager));
		update.getRoles().addAll(Arrays.asList(admin, manager, employee));
		query.getRoles().addAll(Arrays.asList(admin, manager, employee, user));
		// 附属属性
		Subsidiary s = new Subsidiary();
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
		s.setCity("重庆");
		s.setCountry("中国");
		s.setProvince("重庆");
		s.setLanguage("zh");
		s.setMobile("187******82");
		emailtohl.setSubsidiary(s);
		emailtohl.setGender(Gender.MALE);
		emailtohl.getRoles().addAll(Arrays.asList(admin, user));
		try {
			emailtohl.setBirthday(sdf.parse("1982-02-12"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		foo.setName("foo");
		foo.setUsername("foo@test.com");
		foo.setEmail("foo@test.com");
		foo.setEnabled(true);
		foo.setIcon(null);
		foo.setPassword(BCrypt.hashpw("123456", salt));
		foo.setDescription("test");
		s.setCity("西安");
		s.setCountry("中国");
		s.setProvince("陕西");
		s.setLanguage("zh");
		s.setMobile("139******11");
		foo.setSubsidiary(s);
		foo.setGender(Gender.MALE);
		foo.getRoles().addAll(Arrays.asList(manager));
		try {
			foo.setBirthday(sdf.parse("1990-12-13"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		foo.setDescription("foo的工作岗位");
		foo.setPost("系统分析师");
		foo.setSalary(10000.00);
		foo.setDepartment(product);
		
		bar.setName("bar");
		bar.setUsername("bar@test.com");
		bar.setEmail("bar@test.com");
		bar.setEnabled(true);
		bar.setIcon(null);
		bar.setPassword(BCrypt.hashpw("123456", salt));
		bar.setDescription("test");
		s.setCity("昆明");
		s.setCountry("中国");
		s.setProvince("云南");
		s.setLanguage("zh");
		s.setMobile("130******77");
		bar.setSubsidiary(s);
		bar.setGender(Gender.FEMALE);
		bar.getRoles().addAll(Arrays.asList(user));
		try {
			bar.setBirthday(sdf.parse("1991-10-24"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		bar.setDescription("bar的工作岗位");
		bar.setPost("QA人员");
		bar.setSalary(6000.00);
		bar.setDepartment(qa);

		admin.getUsers().addAll(Arrays.asList(emailtohl));
		admin.getUsers().addAll(Arrays.asList(emailtohl));
		user.getUsers().addAll(Arrays.asList(emailtohl, foo, bar));
		
		product.setEmployees(new HashSet<Employee>(Arrays.asList(foo)));
		qa.setEmployees(new HashSet<Employee>(Arrays.asList(bar)));
	}
}
