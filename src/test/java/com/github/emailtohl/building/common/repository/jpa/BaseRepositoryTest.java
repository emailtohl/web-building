/*package com.github.emailtohl.building.common.repository.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.management.relation.Role;
import javax.persistence.AccessType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.core.userdetails.User;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.github.emailtohl.building.common.repository.jpa.BaseRepository.JpqlAndArgs;
import com.github.emailtohl.building.common.repository.jpa.BaseRepository.PredicateAndArgs;
import com.github.emailtohl.building.common.repository.jpa.relationEntities.Relation1;
import com.github.emailtohl.building.common.repository.jpa.relationEntities.Relation2;
import com.github.emailtohl.building.common.repository.jpa.relationEntities.TestRelationRepository;

public class BaseRepositoryTest {
	AnnotationConfigApplicationContext ctx = SpringUtils.ctx;
	private static final Logger logger = LogManager.getLogger();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	class TestBaseRepository extends BaseRepository<User> {
	}

	class TestEmployeeRepository extends BaseRepository<Employee> {
	}

	class TestUser extends User {
		private static final long serialVersionUID = 7043142959549948986L;
		String extendsPropery = "extendsPropery";

		public String getExtendsPropery() {
			return extendsPropery;
		}

		public void setExtendsPropery(String extendsPropery) {
			this.extendsPropery = extendsPropery;
		}
	}

	class TestContact extends Contact {
		private static final long serialVersionUID = -4087260701365727981L;
		String extendsPropery = "extendsPropery";

		public String getExtendsPropery() {
			return extendsPropery;
		}

		public void setExtendsPropery(String extendsPropery) {
			this.extendsPropery = extendsPropery;
		}
	}

	UserRepositoryCustomization userRepository;
	EmployeeRepositoryCustomization employeeRepository;
	TestBaseRepository baseRepository;

	@Before
	public void setUp() throws Exception {
		userRepository = ctx.getBean(UserRepositoryImpl.class);
		employeeRepository = ctx.getBean(EmployeeRepositoryImpl.class);
		baseRepository = new TestBaseRepository();
	}

	@Test
	public void testBaseRepository() {
		BaseRepository<User> bru = new BaseRepository<User>() {
		};
		TestBaseRepository tbr = new TestBaseRepository();
		logger.trace(bru);
		logger.trace(tbr);
	}

	@Test
	public void testBaseRepositoryClassOfLongClassOfE() {
		BaseRepository<User> br = new BaseRepository<User>(User.class) {
		};
		logger.trace(br);
	}

	@Test
	public void testGetPagerStringObjectArrayLongInteger() throws ParseException {
		Date d = sdf.parse("1982-02-12");
		
		 * 序列可以倒着写
		 
		String jpql = "select u from User u where u.enabled = ?2 and u.birthday = ?1";
		Pager<User> pager = userRepository.getPager(jpql, new Object[] { d, true }, 1L, 10);
		List<User> ls = pager.getDataList();
		for (User u : ls) {
			logger.debug(u);
		}
		logger.debug(pager.getPageNum());
		logger.debug(pager.getPageSize());
		logger.debug(pager.getStartRecordNumber());
		logger.debug(pager.getTotalPage());
		logger.debug(pager.getTotalRow());
		
	}

	@Test
	public void testGetPagerStringMapOfStringObjectLongInteger() {
		
		 * 使用命名方式传入参数
		 * Collection集合作为一个参数
		 * 对嵌入集合，一对多，多对多情况的JPQL写法
		 
		String jpql = "SELECT DISTINCT u FROM User u JOIN u.authority a WHERE u.nickname LIKE :nickname AND a IN :authority";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("nickname", "foo");
		args.put("authority", Arrays.asList("ADMIN", "USER"));
		Pager<User> pager = userRepository.getPager(jpql, args, 1L, 10);
		List<User> ls = pager.getDataList();
		for (User u : ls) {
			logger.debug(u);
		}
		logger.debug(pager.getPageNum());
		logger.debug(pager.getPageSize());
		logger.debug(pager.getStartRecordNumber());
		logger.debug(pager.getTotalPage());
		logger.debug(pager.getTotalRow());
	}

	@Test
	public void testGetPagerELongIntegerAccessType() {
		TestUser u = new TestUser();
		TestContact c = new TestContact();
		c.setEmail("foo@test.com");
		c.setAddress("重庆");
		c.setTelephone("18702392680");

		u.setName("foo");
		u.setGender(Gender.MALE);
		u.setContact(c);

		Role r = new Role();
		r.setName("ADMIN");

		r.setUsers(new HashSet<User>(Arrays.asList(u)));
		u.setRoles(new HashSet<Role>(Arrays.asList(r)));
		
		 * 将实体作为参数，查询出Pager
		 * 此处实体类是基类，而派生类中的属性不会被分析出来，所以派生类可以放心地继承实体并作为DTO传输数据
		 
		Pager<User> pu = userRepository.getPager(u, 1L, 20, AccessType.PROPERTY);
		List<User> ls = pu.getDataList();
		for (User user : ls) {
			logger.debug(user);
		}

		
		 * 下面是一对多对一关系，嵌入集合属性的查询
		 
		Employee emp = new Employee();
		emp.setEmpNum(1);
		emp.setContact(new Contact("重庆", "company@test.com", "18712345678"));
		Company com = new Company();
		com.setName("XXX注册公司");
		Department d = new Department();
		d.setCompany(com);
		emp.setDepartment(d);
		emp.setAuthority(new HashSet<String>(Arrays.asList("ADMIN", "USER")));
		Pager<Employee> pe = employeeRepository.getPager(emp, 1L, 20, null);
		List<Employee> le = pe.getDataList();
		for (Employee e : le) {
			logger.debug(e);
		}
	}

	@Test
	public void testJpqlAndArgsByPropety() {
		TestUser u = new TestUser();
		TestContact c = new TestContact();
		c.setEmail("foo@test.com");
		c.setAddress("重庆");
		c.setTelephone("18702392680");

		u.setName("foo");
		u.setGender(Gender.MALE);
		u.setContact(c);

		Role r = new Role();
		r.setName("ADMIN");

		r.setUsers(new HashSet<User>(Arrays.asList(u)));
		u.setRoles(new HashSet<Role>(Arrays.asList(r)));

		
		 * 通过JavaBean属性的方式访问实体对象
		 
		JpqlAndArgs jaa = baseRepository.jpqlAndArgsByPropety(u);
		logger.debug(jaa.jpql);
		logger.debug(jaa.args);
	}

	@Test
	public void testJpqlAndArgsByField() {
		TestUser u = new TestUser();
		TestContact c = new TestContact();
		c.setEmail("foo@test.com");
		c.setAddress("重庆");
		c.setTelephone("18702392680");

		u.setName("foo");
		u.setGender(Gender.MALE);
		u.setContact(c);

		Role r = new Role();
		r.setName("ADMIN");

		r.setUsers(new HashSet<User>(Arrays.asList(u)));
		u.setRoles(new HashSet<Role>(Arrays.asList(r)));
		
		
		 * 访问实体对象的Field字段
		 
		JpqlAndArgs jaa = baseRepository.jpqlAndArgsByField(u);
		logger.debug(jaa.jpql);
		logger.debug(jaa.args);
	}

	@Test
	public void testPredicateAndArgsByProperty() {
		TestUser u = new TestUser();
		TestContact c = new TestContact();
		c.setEmail("foo@test.com");
		c.setAddress("重庆");
		c.setTelephone("18702392680");

		u.setName("foo");
		u.setGender(Gender.MALE);
		u.setContact(c);

		Role r = new Role();
		r.setName("ADMIN");

		r.setUsers(new HashSet<User>(Arrays.asList(u)));
		u.setRoles(new HashSet<Role>(Arrays.asList(r)));
		
		 * 访问实体对象，并返回WHERE子句和对应参数
		 
		PredicateAndArgs paa = baseRepository.predicateAndArgs(u, null);
		logger.debug(paa.predicate);
		logger.debug(paa.args);
		logger.debug(paa.alias);
		logger.debug(paa.entityName);
	}

	*//**
	 * 下面是测试正则表达式是否匹配各种可能的JPQL形式
	 *//*
	@Test
	public void testJpqlPattern() {
		String jpql;
		Matcher m;
		// 最基本的查询
		jpql = "SELECT p FROM Player p";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Player p", m.group(baseRepository.fromIndex).trim());
		assertEquals("p", m.group(baseRepository.selectIndex).trim());
		assertNull(m.group(baseRepository.predicateIndex));
		assertEquals("Player", m.group(baseRepository.entityNameIndex).trim());

		// 查询出所有的player,包括其子类，也可以写成这样
		jpql = "From Player as p";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		logger.debug(m.group(baseRepository.aliasIndex));
		assertNull(m.group(baseRepository.selectIndex));
		assertEquals("From Player as p", m.group(baseRepository.fromIndex).trim());
		assertEquals("p", m.group(baseRepository.aliasIndex).trim());
		assertNull(m.group(baseRepository.predicateIndex));
		assertEquals("Player", m.group(baseRepository.entityNameIndex).trim());

		// 去除重复的元素
		jpql = "SELECT DISTINCT p  FROM Player p  WHERE p.position = ?1";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Player p  WHERE p.position = ?1", m.group(baseRepository.fromIndex).trim());
		assertEquals("p", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE p.position = ?1", m.group(baseRepository.predicateIndex).trim());
		assertEquals("Player", m.group(baseRepository.entityNameIndex).trim());

		// 结合查询关联
		jpql = "SELECT DISTINCT p FROM Player p, IN(p.teams) t";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Player p, IN(p.teams) t", m.group(baseRepository.fromIndex).trim());
		assertEquals("p", m.group(baseRepository.selectIndex).trim());
		assertNull(m.group(baseRepository.predicateIndex));
		assertEquals("Player", m.group(baseRepository.entityNameIndex).trim());

		// 查询所有有team的player 也可以写成如下
		jpql = "SELECT DISTINCT p FROM Player p JOIN p.teams t";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Player p JOIN p.teams t", m.group(baseRepository.fromIndex).trim());
		assertEquals("p", m.group(baseRepository.selectIndex).trim());
		assertNull(m.group(baseRepository.predicateIndex));
		assertEquals("Player", m.group(baseRepository.entityNameIndex).trim());

		// 或者
		jpql = "SELECT DISTINCT p FROM Player p WHERE p.team IS NOT EMPTY";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Player p WHERE p.team IS NOT EMPTY", m.group(baseRepository.fromIndex).trim());
		assertEquals("p", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE p.team IS NOT EMPTY", m.group(baseRepository.predicateIndex).trim());
		assertEquals("Player", m.group(baseRepository.entityNameIndex).trim());

		// 关联关系的查询过滤
		jpql = "SELECT t FROM Team t JOIN t.league l WHERE l.sport = 'soccer' OR l.sport ='football'";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Team t JOIN t.league l WHERE l.sport = 'soccer' OR l.sport ='football'",
				m.group(baseRepository.fromIndex).trim());
		assertEquals("t", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE l.sport = 'soccer' OR l.sport ='football'", m.group(baseRepository.predicateIndex).trim());
		assertEquals("Team", m.group(baseRepository.entityNameIndex).trim());

		// 查询所有league sports属性的team对象
		jpql = "SELECT DISTINCT p FROM Player p, IN (p.teams) t WHERE t.league.sport = :sport";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Player p, IN (p.teams) t WHERE t.league.sport = :sport",
				m.group(baseRepository.fromIndex).trim());
		assertEquals("p", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE t.league.sport = :sport", m.group(baseRepository.predicateIndex).trim());
		assertEquals("Player", m.group(baseRepository.entityNameIndex).trim());

		// LIKE：
		jpql = "SELECT p FROM Player p WHERE p.name LIKE 'Mich%'";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Player p WHERE p.name LIKE 'Mich%'", m.group(baseRepository.fromIndex).trim());
		assertEquals("p", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE p.name LIKE 'Mich%'", m.group(baseRepository.predicateIndex).trim());
		assertEquals("Player", m.group(baseRepository.entityNameIndex).trim());

		// IS NULL：
		jpql = "SELECT t FROM Team t WHERE t.league IS NULL";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Team t WHERE t.league IS NULL", m.group(baseRepository.fromIndex).trim());
		assertEquals("t", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE t.league IS NULL", m.group(baseRepository.predicateIndex).trim());
		assertEquals("Team", m.group(baseRepository.entityNameIndex).trim());

		// IS EMPTY：
		jpql = "SELECT p FROM Player p WHERE p.teams IS EMPTY";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Player p WHERE p.teams IS EMPTY", m.group(baseRepository.fromIndex).trim());
		assertEquals("p", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE p.teams IS EMPTY", m.group(baseRepository.predicateIndex).trim());
		assertEquals("Player", m.group(baseRepository.entityNameIndex).trim());

		// BETWEEN：
		jpql = "SELECT DISTINCT p FROM Player p WHERE p.salary BETWEEN :lowerSalary AND :higherSalary";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Player p WHERE p.salary BETWEEN :lowerSalary AND :higherSalary",
				m.group(baseRepository.fromIndex).trim());
		assertEquals("p", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE p.salary BETWEEN :lowerSalary AND :higherSalary",
				m.group(baseRepository.predicateIndex).trim());
		assertEquals("Player", m.group(baseRepository.entityNameIndex).trim());

		// 复合条件：
		jpql = "SELECT DISTINCT p1 FROM Player p1, Player p2 WHERE p1.salary > p2.salary AND p2.name = :name";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Player p1, Player p2 WHERE p1.salary > p2.salary AND p2.name = :name",
				m.group(baseRepository.fromIndex).trim());
		assertEquals("p1", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE p1.salary > p2.salary AND p2.name = :name", m.group(baseRepository.predicateIndex).trim());
		assertEquals("Player", m.group(baseRepository.entityNameIndex).trim());

		// 子查询：
		jpql = "SELECT c FROM Customer c WHERE (SELECT COUNT(o) FROM c.orders o) > 10";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals("FROM Customer c WHERE (SELECT COUNT(o) FROM c.orders o) > 10",
				m.group(baseRepository.fromIndex).trim());
		assertEquals("c", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE (SELECT COUNT(o) FROM c.orders o) > 10", m.group(baseRepository.predicateIndex).trim());
		assertEquals("Customer", m.group(baseRepository.entityNameIndex).trim());

		// EXISTS子查询：
		jpql = "SELECT DISTINCT emp FROM Employee emp WHERE EXISTS ( SELECT spouseEmp FROM Employee spouseEmp WHERE spouseEmp = emp.spouse)";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals(
				"FROM Employee emp WHERE EXISTS ( SELECT spouseEmp FROM Employee spouseEmp WHERE spouseEmp = emp.spouse)",
				m.group(baseRepository.fromIndex).trim());
		assertEquals("emp", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE EXISTS ( SELECT spouseEmp FROM Employee spouseEmp WHERE spouseEmp = emp.spouse)",
				m.group(baseRepository.predicateIndex).trim());
		assertEquals("Employee", m.group(baseRepository.entityNameIndex).trim());

		// ALL和ANY配合=<>=>使用：
		jpql = "SELECT emp FROM Employee emp WHERE emp.salary > ALL ( SELECT m.salary FROM Manager m WHERE m.department = emp.department)";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.selectIndex));
		assertEquals(
				"FROM Employee emp WHERE emp.salary > ALL ( SELECT m.salary FROM Manager m WHERE m.department = emp.department)",
				m.group(baseRepository.fromIndex).trim());
		assertEquals("emp", m.group(baseRepository.selectIndex).trim());
		assertEquals("WHERE emp.salary > ALL ( SELECT m.salary FROM Manager m WHERE m.department = emp.department)",
				m.group(baseRepository.predicateIndex).trim());
		assertEquals("Employee", m.group(baseRepository.entityNameIndex).trim());

		// 构造语句：
		jpql = "SELECT NEW com.xyz.CustomerDetail(c.name, c.country.name) FROM customer c WHERE c.lastname = 'Coss' AND c.firstname = 'Roxane'";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.aliasIndex));
		assertEquals("FROM customer c WHERE c.lastname = 'Coss' AND c.firstname = 'Roxane'",
				m.group(baseRepository.fromIndex).trim());
		assertEquals("c", m.group(baseRepository.aliasIndex).trim());
		assertEquals("WHERE c.lastname = 'Coss' AND c.firstname = 'Roxane'",
				m.group(baseRepository.predicateIndex).trim());
		assertEquals("customer", m.group(baseRepository.entityNameIndex).trim());

		// Order By：
		jpql = "SELECT p.product_name FROM Order o, IN(o.lineItems) l JOIN o.customer c WHERE c.lastname = 'Faehmel' AND c.firstname = 'Robert' ORDER BY o.quantity";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.aliasIndex));
		assertEquals(
				"FROM Order o, IN(o.lineItems) l JOIN o.customer c WHERE c.lastname = 'Faehmel' AND c.firstname = 'Robert' ORDER BY o.quantity",
				m.group(baseRepository.fromIndex).trim());
		assertEquals("o", m.group(baseRepository.aliasIndex).trim());
		assertEquals("WHERE c.lastname = 'Faehmel' AND c.firstname = 'Robert' ORDER BY o.quantity",
				m.group(baseRepository.predicateIndex).trim());
		assertEquals("Order", m.group(baseRepository.entityNameIndex).trim());

		// GROUP BY：
		jpql = "SELECT c.country, COUNT(c)  FROM Customer c GROUP BY c.country";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.aliasIndex));
		assertEquals("FROM Customer c GROUP BY c.country", m.group(baseRepository.fromIndex).trim());
		assertEquals("c", m.group(baseRepository.aliasIndex).trim());
		assertNull(m.group(baseRepository.predicateIndex));
		assertEquals("Customer", m.group(baseRepository.entityNameIndex).trim());

		// Having：
		jpql = "SELECT c.status, AVG(o.totalPrice) FROM Order o JOIN o.customer c GROUP BY c.status HAVING c.status IN (1, 2, 3)";
		m = baseRepository.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(baseRepository.fromIndex));
		logger.debug(m.group(baseRepository.aliasIndex));
		assertEquals("FROM Order o JOIN o.customer c GROUP BY c.status HAVING c.status IN (1, 2, 3)",
				m.group(baseRepository.fromIndex).trim());
		assertEquals("o", m.group(baseRepository.aliasIndex).trim());
		assertNull(m.group(baseRepository.predicateIndex));
		assertEquals("Order", m.group(baseRepository.entityNameIndex).trim());
	}
	
	@Test
	public void testRelation() {
		Relation1 r1 = new Relation1();
		Relation2 r2 = new Relation2();
		r1.setId((short) 1);
		r2.setId((short) 2);
		r1.setRelation2(r2);
		r2.setRelation1(r1);
		BaseRepository<Relation1> relationRepository = new TestRelationRepository();
		JpqlAndArgs jaa = relationRepository.jpqlAndArgsByPropety(r1);
		logger.info(jaa.jpql);
		assertFalse(jaa.jpql.contains("relation2"));
	}
	
}*/