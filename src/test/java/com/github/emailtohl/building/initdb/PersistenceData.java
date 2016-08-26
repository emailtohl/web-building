package com.github.emailtohl.building.initdb;

import static com.github.emailtohl.building.site.entities.Authority.ADMIN;
import static com.github.emailtohl.building.site.entities.Authority.MANAGER;
import static com.github.emailtohl.building.site.entities.Authority.USER;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.github.emailtohl.building.site.entities.Company;
import com.github.emailtohl.building.site.entities.Department;
import com.github.emailtohl.building.site.entities.Employee;
import com.github.emailtohl.building.site.entities.Manager;
import com.github.emailtohl.building.site.entities.Subsidiary;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.entities.User.Gender;
public class PersistenceData {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static SecureRandom r = new SecureRandom();
	static final int HASHING_ROUNDS = 10;
	
	public static final User emailtohl = new User();
	public static final Manager foo = new Manager();
	public static final Employee bar = new Employee();
	public static final Company company = new Company();
	public static final Department product = new Department(), qa = new Department();

	static {
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
		emailtohl.getAuthorities().addAll(Arrays.asList(ADMIN, USER));
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
		foo.setDescription("test");
		s = new Subsidiary();
		s.setCity("西安");
		s.setCountry("中国");
		s.setProvince("陕西");
		s.setLanguage("zh");
		s.setMobile("139******11");
		foo.setSubsidiary(s);
		foo.setGender(Gender.MALE);
		foo.getAuthorities().addAll(Arrays.asList(MANAGER));
		try (InputStream is = cl.getResourceAsStream("img/icon-head-foo.jpg")) {
			foo.setBirthday(sdf.parse("1990-12-13"));
			icon = new byte[is.available()];
			is.read(icon);
			foo.setIcon(icon);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		foo.setDescription("foo的工作岗位");
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
		bar.setDescription("test");
		s = new Subsidiary();
		s.setCity("昆明");
		s.setCountry("中国");
		s.setProvince("云南");
		s.setLanguage("zh");
		s.setMobile("130******77");
		bar.setSubsidiary(s);
		bar.setGender(Gender.FEMALE);
		bar.getAuthorities().addAll(Arrays.asList(USER));
		try (InputStream is = cl.getResourceAsStream("img/icon-head-bar.jpg")) {
			bar.setBirthday(sdf.parse("1991-10-24"));
			icon = new byte[is.available()];
			is.read(icon);
			bar.setIcon(icon);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		bar.setDescription("bar的工作岗位");
		bar.setPost("QA人员");
		bar.setSalary(6000.00);
		bar.setDepartment(qa);
		bar.setIconSrc("download/img/icon-head-bar.jpg");
		
		product.setEmployees(new HashSet<Employee>(Arrays.asList(foo)));
		qa.setEmployees(new HashSet<Employee>(Arrays.asList(bar)));
	}
}
