package com.github.emailtohl.building.common.jpa;

import static com.github.emailtohl.building.site.entities.Role.ADMIN;
import static com.github.emailtohl.building.site.entities.Role.EMPLOYEE;
import static com.github.emailtohl.building.site.entities.Role.USER;
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

import javax.inject.Inject;
import javax.persistence.AccessType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.common.jpa.AbstractDynamicQueryRepository.JpqlAndArgs;
import com.github.emailtohl.building.common.jpa.AbstractDynamicQueryRepository.PredicateAndArgs;
import com.github.emailtohl.building.common.jpa.relationEntities.Relation1;
import com.github.emailtohl.building.common.jpa.relationEntities.Relation2;
import com.github.emailtohl.building.common.jpa.relationEntities.TestRelationRepository;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.dao.RoleRepository;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.Subsidiary;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.entities.User.Gender;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class AbstractDynamicQueryRepositoryTest {
	private static final Logger logger = LogManager.getLogger();
	@Inject ApplicationContext context;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	class Concrete extends AbstractDynamicQueryRepository<User> {}
	Concrete concrete;
	TestUser u;
	Role role_admin, role_employee, role_user;
	
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

	class TestSubsidiary extends Subsidiary {
		private static final long serialVersionUID = -4087260701365727981L;
		String extendsPropery = "extendsPropery";

		public String getExtendsPropery() {
			return extendsPropery;
		}

		public void setExtendsPropery(String extendsPropery) {
			this.extendsPropery = extendsPropery;
		}
	}
	
	@Before
	public void setUp() throws Exception {
		concrete = new Concrete();
		AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		factory.autowireBeanProperties(concrete, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		factory.initializeBean(concrete, "concreteDynamicQueryRepository");
		// 将对象注册到Spring中，即可获得该对象所需的依赖
		logger.debug(concrete.entityManager);
		RoleRepository roleRepository = context.getBean(RoleRepository.class);
		role_admin = roleRepository.findByName(ADMIN);
		role_employee = roleRepository.findByName(EMPLOYEE);
		role_user = roleRepository.findByName(USER);
		
		u = new TestUser();
		u.setRoles(new HashSet<Role>(Arrays.asList(role_admin, role_employee)));
		u.setEnabled(true);
		u.setEmail("emailtohl@163.com");
		u.setGender(Gender.MALE);
		// 附加属性，查询时不会将其分析出来
		u.setExtendsPropery("hello world");
		TestSubsidiary c = new TestSubsidiary();
		c.setCity("重庆");
		c.setCountry("中国");
		// 附加属性，查询时不会将其分析出来
		c.setExtendsPropery("hello world");
		c.setLanguage("zh");
		
	}
	
	@Test
	public void testGetPagerStringObjectArrayIntegerInteger() throws ParseException {
		Date d = sdf.parse("1982-01-01");
		//序列可以倒着写
		String jpql = "select u from User u join u.roles r where u.enabled = ?2 and u.birthday > ?1 and r.name = ?3";
		Pager<User> pager = concrete.getPager(jpql, new Object[] { d, true, Role.USER }, 0, 10);
		List<User> ls = pager.getContent();
		assertFalse(ls.isEmpty());
		for (User u : ls) {
			logger.debug(u);
		}
		logger.debug(pager.getPageNumber());
		logger.debug(pager.getPageSize());
		logger.debug(pager.getOffset());
		logger.debug(pager.getTotalPages());
		logger.debug(pager.getTotalElements());
	}

	@Test
	public void testGetPagerStringMapOfStringObjectIntegerInteger() {
		//序列可以倒着写
		String jpql = "SELECT DISTINCT u FROM User u JOIN u.roles r WHERE u.email LIKE :email AND r.name IN :roleNames";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("email", "emailtohl@163.com");
		args.put("roleNames", Arrays.asList(Role.ADMIN, Role.USER));
		Pager<User> pager = concrete.getPager(jpql, args, 0, 10);
		List<User> ls = pager.getContent();
		assertFalse(ls.isEmpty());
	}

	@Test
	public void testGetPagerEIntegerIntegerAccessType() {
		//将实体作为参数，查询出Pager
		//此处实体类是基类，而派生类中的属性不会被分析出来，所以派生类可以放心地继承实体并作为DTO传输数据
		Pager<User> pu = concrete.getPager(u, 0, 5, AccessType.PROPERTY);
		List<User> ls = pu.getContent();
		assertFalse(ls.isEmpty());
		for (User user : ls) {
			logger.debug(user);
		}
	}

	@Test
	public void testJpqlAndArgsByPropety() {
		JpqlAndArgs jaa = concrete.jpqlAndArgsByPropety(u);
		logger.debug(jaa);
		assertFalse(Arrays.asList(jaa.args).isEmpty());
	}

	@Test
	public void testJpqlAndArgsByField() {
		JpqlAndArgs jaa = concrete.jpqlAndArgsByField(u);
		logger.debug(jaa);
		assertFalse(Arrays.asList(jaa.args).isEmpty());
	}

	@Test
	public void testPredicateAndArgs() {
		PredicateAndArgs pa = concrete.predicateAndArgs(u, AccessType.PROPERTY);
		logger.debug(pa);
		assertEquals("User", pa.entityName);
		assertEquals("_U", pa.alias);
		assertFalse(Arrays.asList(pa.args).isEmpty());
	}

	/**
	 * 下面是测试正则表达式是否匹配各种可能的JPQL形式
	 */
	@Test
	public void testJpqlPattern() {
		String jpql;
		Matcher m;
		// 最基本的查询
		jpql = "SELECT p FROM Player p";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Player p", m.group(concrete.fromIndex).trim());
		assertEquals("p", m.group(concrete.selectIndex).trim());
		assertNull(m.group(concrete.predicateIndex));
		assertEquals("Player", m.group(concrete.entityNameIndex).trim());

		// 查询出所有的player,包括其子类，也可以写成这样
		jpql = "From Player as p";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		logger.debug(m.group(concrete.aliasIndex));
		assertNull(m.group(concrete.selectIndex));
		assertEquals("From Player as p", m.group(concrete.fromIndex).trim());
		assertEquals("p", m.group(concrete.aliasIndex).trim());
		assertNull(m.group(concrete.predicateIndex));
		assertEquals("Player", m.group(concrete.entityNameIndex).trim());

		// 去除重复的元素
		jpql = "SELECT DISTINCT p  FROM Player p  WHERE p.position = ?1";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Player p  WHERE p.position = ?1", m.group(concrete.fromIndex).trim());
		assertEquals("p", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE p.position = ?1", m.group(concrete.predicateIndex).trim());
		assertEquals("Player", m.group(concrete.entityNameIndex).trim());

		// 结合查询关联
		jpql = "SELECT DISTINCT p FROM Player p, IN(p.teams) t";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Player p, IN(p.teams) t", m.group(concrete.fromIndex).trim());
		assertEquals("p", m.group(concrete.selectIndex).trim());
		assertNull(m.group(concrete.predicateIndex));
		assertEquals("Player", m.group(concrete.entityNameIndex).trim());

		// 查询所有有team的player 也可以写成如下
		jpql = "SELECT DISTINCT p FROM Player p JOIN p.teams t";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Player p JOIN p.teams t", m.group(concrete.fromIndex).trim());
		assertEquals("p", m.group(concrete.selectIndex).trim());
		assertNull(m.group(concrete.predicateIndex));
		assertEquals("Player", m.group(concrete.entityNameIndex).trim());

		// 或者
		jpql = "SELECT DISTINCT p FROM Player p WHERE p.team IS NOT EMPTY";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Player p WHERE p.team IS NOT EMPTY", m.group(concrete.fromIndex).trim());
		assertEquals("p", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE p.team IS NOT EMPTY", m.group(concrete.predicateIndex).trim());
		assertEquals("Player", m.group(concrete.entityNameIndex).trim());

		// 关联关系的查询过滤
		jpql = "SELECT t FROM Team t JOIN t.league l WHERE l.sport = 'soccer' OR l.sport ='football'";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Team t JOIN t.league l WHERE l.sport = 'soccer' OR l.sport ='football'",
				m.group(concrete.fromIndex).trim());
		assertEquals("t", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE l.sport = 'soccer' OR l.sport ='football'", m.group(concrete.predicateIndex).trim());
		assertEquals("Team", m.group(concrete.entityNameIndex).trim());

		// 查询所有league sports属性的team对象
		jpql = "SELECT DISTINCT p FROM Player p, IN (p.teams) t WHERE t.league.sport = :sport";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Player p, IN (p.teams) t WHERE t.league.sport = :sport",
				m.group(concrete.fromIndex).trim());
		assertEquals("p", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE t.league.sport = :sport", m.group(concrete.predicateIndex).trim());
		assertEquals("Player", m.group(concrete.entityNameIndex).trim());

		// LIKE：
		jpql = "SELECT p FROM Player p WHERE p.name LIKE 'Mich%'";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Player p WHERE p.name LIKE 'Mich%'", m.group(concrete.fromIndex).trim());
		assertEquals("p", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE p.name LIKE 'Mich%'", m.group(concrete.predicateIndex).trim());
		assertEquals("Player", m.group(concrete.entityNameIndex).trim());

		// IS NULL：
		jpql = "SELECT t FROM Team t WHERE t.league IS NULL";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Team t WHERE t.league IS NULL", m.group(concrete.fromIndex).trim());
		assertEquals("t", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE t.league IS NULL", m.group(concrete.predicateIndex).trim());
		assertEquals("Team", m.group(concrete.entityNameIndex).trim());

		// IS EMPTY：
		jpql = "SELECT p FROM Player p WHERE p.teams IS EMPTY";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Player p WHERE p.teams IS EMPTY", m.group(concrete.fromIndex).trim());
		assertEquals("p", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE p.teams IS EMPTY", m.group(concrete.predicateIndex).trim());
		assertEquals("Player", m.group(concrete.entityNameIndex).trim());

		// BETWEEN：
		jpql = "SELECT DISTINCT p FROM Player p WHERE p.salary BETWEEN :lowerSalary AND :higherSalary";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Player p WHERE p.salary BETWEEN :lowerSalary AND :higherSalary",
				m.group(concrete.fromIndex).trim());
		assertEquals("p", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE p.salary BETWEEN :lowerSalary AND :higherSalary",
				m.group(concrete.predicateIndex).trim());
		assertEquals("Player", m.group(concrete.entityNameIndex).trim());

		// 复合条件：
		jpql = "SELECT DISTINCT p1 FROM Player p1, Player p2 WHERE p1.salary > p2.salary AND p2.name = :name";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Player p1, Player p2 WHERE p1.salary > p2.salary AND p2.name = :name",
				m.group(concrete.fromIndex).trim());
		assertEquals("p1", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE p1.salary > p2.salary AND p2.name = :name", m.group(concrete.predicateIndex).trim());
		assertEquals("Player", m.group(concrete.entityNameIndex).trim());

		// 子查询：
		jpql = "SELECT c FROM Customer c WHERE (SELECT COUNT(o) FROM c.orders o) > 10";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals("FROM Customer c WHERE (SELECT COUNT(o) FROM c.orders o) > 10",
				m.group(concrete.fromIndex).trim());
		assertEquals("c", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE (SELECT COUNT(o) FROM c.orders o) > 10", m.group(concrete.predicateIndex).trim());
		assertEquals("Customer", m.group(concrete.entityNameIndex).trim());

		// EXISTS子查询：
		jpql = "SELECT DISTINCT emp FROM Employee emp WHERE EXISTS ( SELECT spouseEmp FROM Employee spouseEmp WHERE spouseEmp = emp.spouse)";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals(
				"FROM Employee emp WHERE EXISTS ( SELECT spouseEmp FROM Employee spouseEmp WHERE spouseEmp = emp.spouse)",
				m.group(concrete.fromIndex).trim());
		assertEquals("emp", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE EXISTS ( SELECT spouseEmp FROM Employee spouseEmp WHERE spouseEmp = emp.spouse)",
				m.group(concrete.predicateIndex).trim());
		assertEquals("Employee", m.group(concrete.entityNameIndex).trim());

		// ALL和ANY配合=<>=>使用：
		jpql = "SELECT emp FROM Employee emp WHERE emp.salary > ALL ( SELECT m.salary FROM Manager m WHERE m.department = emp.department)";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.selectIndex));
		assertEquals(
				"FROM Employee emp WHERE emp.salary > ALL ( SELECT m.salary FROM Manager m WHERE m.department = emp.department)",
				m.group(concrete.fromIndex).trim());
		assertEquals("emp", m.group(concrete.selectIndex).trim());
		assertEquals("WHERE emp.salary > ALL ( SELECT m.salary FROM Manager m WHERE m.department = emp.department)",
				m.group(concrete.predicateIndex).trim());
		assertEquals("Employee", m.group(concrete.entityNameIndex).trim());

		// 构造语句：
		jpql = "SELECT NEW com.xyz.CustomerDetail(c.name, c.country.name) FROM customer c WHERE c.lastname = 'Coss' AND c.firstname = 'Roxane'";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.aliasIndex));
		assertEquals("FROM customer c WHERE c.lastname = 'Coss' AND c.firstname = 'Roxane'",
				m.group(concrete.fromIndex).trim());
		assertEquals("c", m.group(concrete.aliasIndex).trim());
		assertEquals("WHERE c.lastname = 'Coss' AND c.firstname = 'Roxane'",
				m.group(concrete.predicateIndex).trim());
		assertEquals("customer", m.group(concrete.entityNameIndex).trim());

		// Order By：
		jpql = "SELECT p.product_name FROM Order o, IN(o.lineItems) l JOIN o.customer c WHERE c.lastname = 'Faehmel' AND c.firstname = 'Robert' ORDER BY o.quantity";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.aliasIndex));
		assertEquals(
				"FROM Order o, IN(o.lineItems) l JOIN o.customer c WHERE c.lastname = 'Faehmel' AND c.firstname = 'Robert' ORDER BY o.quantity",
				m.group(concrete.fromIndex).trim());
		assertEquals("o", m.group(concrete.aliasIndex).trim());
		assertEquals("WHERE c.lastname = 'Faehmel' AND c.firstname = 'Robert' ORDER BY o.quantity",
				m.group(concrete.predicateIndex).trim());
		assertEquals("Order", m.group(concrete.entityNameIndex).trim());

		// GROUP BY：
		jpql = "SELECT c.country, COUNT(c)  FROM Customer c GROUP BY c.country";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.aliasIndex));
		assertEquals("FROM Customer c GROUP BY c.country", m.group(concrete.fromIndex).trim());
		assertEquals("c", m.group(concrete.aliasIndex).trim());
		assertNull(m.group(concrete.predicateIndex));
		assertEquals("Customer", m.group(concrete.entityNameIndex).trim());

		// Having：
		jpql = "SELECT c.status, AVG(o.totalPrice) FROM Order o JOIN o.customer c GROUP BY c.status HAVING c.status IN (1, 2, 3)";
		m = concrete.jpqlPattern.matcher(jpql);
		assertTrue(m.find());
		logger.debug(m.group(0));
		logger.debug(m.group(concrete.fromIndex));
		logger.debug(m.group(concrete.aliasIndex));
		assertEquals("FROM Order o JOIN o.customer c GROUP BY c.status HAVING c.status IN (1, 2, 3)",
				m.group(concrete.fromIndex).trim());
		assertEquals("o", m.group(concrete.aliasIndex).trim());
		assertNull(m.group(concrete.predicateIndex));
		assertEquals("Order", m.group(concrete.entityNameIndex).trim());
	}
	
	/**
	 * 测试类关联关系中的相互引用，在分析对象时是否会引起无限递归
	 */
	@Test
	public void testRelation() {
		Relation1 r1 = new Relation1();
		Relation2 r2 = new Relation2();
		r1.setId((short) 1);
		r2.setId((short) 2);
		r1.setRelation2(r2);
		r2.setRelation1(r1);
		AbstractDynamicQueryRepository<Relation1> relationRepository = new TestRelationRepository();
		JpqlAndArgs jaa = relationRepository.jpqlAndArgsByPropety(r1);
		logger.info(jaa.jpql);
		assertFalse(jaa.jpql.contains("relation2"));
	}
}
