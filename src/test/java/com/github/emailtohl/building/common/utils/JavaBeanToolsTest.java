package com.github.emailtohl.building.common.utils;

import static com.github.emailtohl.building.common.utils.JavaBeanTools.compareProperties;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.copyList;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.copyProperties;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.deepCopy;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.fieldMap;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.getDeclaredField;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.getFieldMap;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.getGenericClass;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.getModifiedField;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.injectField;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.injectFieldWithString;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.injectPropertyWithString;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.merge;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.propertyMap;
import static com.github.emailtohl.building.common.utils.JavaBeanTools.saveListToMap;
import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.emailtohl;
import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.relation.Role;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.emailtohl.building.common.repository.jpa.relationEntities.Relation1;
import com.github.emailtohl.building.common.repository.jpa.relationEntities.Relation2;
import com.github.emailtohl.building.site.entities.Subsidiary;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.entities.User.Gender;
public class JavaBeanToolsTest {
	private static final Logger logger = LogManager.getLogger();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Test
	public void testGetFieldMap() {
		Map<String, Object> map = getFieldMap(emailtohl);
		logger.debug(map);
		assertTrue((boolean) map.get("enabled"));
		assertEquals("emailtohl@163.com", map.get("email"));
		assertEquals(emailtohl.getSubsidiary(), map.get("subsidiary"));
		assertEquals(emailtohl.getAuthorities(), map.get("authorities"));
	}
	
	@Test
	public void testPropertyMap() {
		Map<String, PropertyDescriptor> map = propertyMap(emailtohl);
		logger.debug(map.keySet());
		assertNotNull(map.get("id"));
	}

	@Test
	public void testFieldMap() {
		Pattern p = Pattern.compile("<(\\w+(\\.\\w+)*)>");
		Map<String, Field> map = fieldMap(emailtohl);
		Field f = map.get("authorities");
		Type t = f.getGenericType();
		logger.debug(t);
		
		Matcher m = p.matcher(t.toString());
		if (m.find())
			logger.debug(m.group(1));
		assertEquals("com.github.emailtohl.building.site.entities.Authority", m.group(1));
		
		f = map.get("email");
		t = f.getGenericType();
		logger.debug(t);
		
		m = p.matcher(t.toString());
		assertFalse(m.find());
	}

	@Test
	public void testGetDeclaredField() {
		Field f = getDeclaredField(foo, "authorities");
		Class<?> c = f.getType();
		assertTrue(c.isAssignableFrom(Set.class));
	}

	@Test
	public void testCopyProperties() {
		User u = copyProperties(bar, User.class);
		logger.debug(u);
		assertEquals("bar", u.getName());
	}

	@Test
	public void testCopyList() {
		List<User> ls = Arrays.asList(emailtohl, foo, bar);
		List<User> lsp = copyList(ls, User.class);
		logger.debug(lsp);
		assertEquals(Gender.MALE, lsp.get(0).getGender());
	}

	@Test
	public void testDeepCopy() {
		User clone;
		clone = copyProperties(foo, User.class);
		// 普通复制的是对象的引用，所以内存地址相等
		assertTrue(clone.getAuthorities() == foo.getAuthorities());
		// 深度复制出来的对象，内存地址不相等
		clone = deepCopy(foo);
		logger.debug(clone);
		assertFalse(clone.getAuthorities() == foo.getAuthorities());

		Relation1 r1 = new Relation1();
		Relation2 r2 = new Relation2();
		r1.setRelation2(r2);
		r2.setRelation1(r1);
		// 测试对象间的交叉引用会不会出现无限递归的情况
		Relation1 rClone = deepCopy(r1);
		logger.debug(rClone);
		assertFalse(rClone.getRelation2() == r1.getRelation2());
	}

	@Test
	public void testGetModifiedField() {
		Map<String, Object> map = getModifiedField(foo, bar);
		logger.debug(map);
		assertTrue(map.containsKey("email"));
	}

	@Test
	public void testInjectField() {
		class TestBean extends User {
			private static final long serialVersionUID = 2602043693538454711L;
			int i = 1;
			boolean b1 = true;
			Boolean b2 = true;
			byte by1 = 1;
			Byte by2 = 1;
			char c1 = 1;
			Character c2 = 1;
			@Override
			public String toString() {
				return "TestBean [i=" + i + ", b1=" + b1 + ", b2=" + b2 + ", by1=" + by1 + ", by2=" + by2 + ", c1=" + c1
						+ ", c2=" + c2 + "]";
			}
		}
		TestBean t = new TestBean();
		Map<String, Field> map = fieldMap(t);
		injectField(map.get("i"), t, 2);
		injectField(map.get("b1"), t, false);
		injectField(map.get("b2"), t, false);
		injectField(map.get("by1"), t, 2);
		injectField(map.get("by2"), t, 2);
		injectField(map.get("c1"), t, 97);
		injectField(map.get("c2"), t, 97);
		logger.debug(t);
		assertEquals(2, t.i);
		assertFalse(t.b1);
		assertFalse(t.b2);
		assertTrue(2 == t.by1);
		assertTrue(2 == t.by2);
		assertEquals(97, t.c1);
		assertEquals(new Character('a'), t.c2);
	}

	@Test
	public void testInjectFieldWithString() {
		class TestBean extends User {
			private static final long serialVersionUID = 6465392695061494961L;
			int i = 1;
			boolean b = true;
			byte by = 1;
			char c = 1;
			Gender gender = Gender.UNSPECIFIED;

			@Override
			public String toString() {
				return "TestBean [i=" + i + ", b=" + b + ", by=" + by + ", c=" + c + ", gender=" + gender + "]";
			}
		}
		TestBean t = new TestBean();
		Map<String, Field> map = fieldMap(t);
		injectFieldWithString(map.get("i"), t, "2");
		injectFieldWithString(map.get("b"), t, "false");
		injectFieldWithString(map.get("by"), t, "2");
		injectFieldWithString(map.get("c"), t, "a");
		injectFieldWithString(map.get("gender"), t, "FEMALE");
		injectFieldWithString(map.get("birthday"), t, "1982-02-12");
		logger.debug(t);
		assertEquals(2, t.i);
		assertFalse(t.b);
		assertTrue(2 == t.by);
		assertEquals('a', t.c);
		assertEquals(Gender.FEMALE, t.gender);
		assertEquals(Date.valueOf("1982-02-12"), t.getBirthday());
	}

	@Test
	public void testInjectPropertyWithString() throws IntrospectionException {
		@SuppressWarnings("unused")
		class TestBean extends User {
			private static final long serialVersionUID = -3673169944292160591L;
			int i = 1;
			boolean b = true;
			byte by = 1;
			char c = 1;
			Gender gender = Gender.UNSPECIFIED;

			public int getI() {
				return i;
			}
			public void setI(int i) {
				this.i = i;
			}
			public boolean isB() {
				return b;
			}
			public void setB(boolean b) {
				this.b = b;
			}
			public byte getBy() {
				return by;
			}
			public void setBy(byte by) {
				this.by = by;
			}
			public char getC() {
				return c;
			}
			public void setC(char c) {
				this.c = c;
			}
			public Gender getGender() {
				return gender;
			}
			public void setGender(Gender gender) {
				this.gender = gender;
			}
			@Override
			public String toString() {
				return "TestBean [i=" + i + ", b=" + b + ", by=" + by + ", c=" + c + ", gender=" + gender + "]";
			}
		}

		TestBean t = new TestBean();
		for (PropertyDescriptor pd : Introspector.getBeanInfo(TestBean.class).getPropertyDescriptors()) {
			String name = pd.getName();
			switch (name) {
			case "i":
				injectPropertyWithString(pd, t, "2");
				break;
			case "b":
				injectPropertyWithString(pd, t, "false");
				break;
			case "by":
				injectPropertyWithString(pd, t, "2");
				break;
			case "c":
				injectPropertyWithString(pd, t, "a");
				break;
			case "gender":
				injectPropertyWithString(pd, t, "FEMALE");
				break;
			case "birthday":
				injectPropertyWithString(pd, t, "1982-02-12");
				break;
			default:

			}
		}
		logger.debug(t);
		assertEquals(2, t.i);
		assertFalse(t.b);
		assertTrue(2 == t.by);
		assertEquals('a', t.c);
		assertEquals(Gender.FEMALE, t.gender);
		assertEquals(Date.valueOf("1982-02-12"), t.getBirthday());
	}

	@Test
	public void testMerge() {
		User p = new User();
		p = merge(p, emailtohl, foo, bar);
		logger.debug(p);
		assertEquals(p.getName(), bar.getName());
	}
	
	@Test
	public void testSaveListToMap() {
		emailtohl.setId(1L);
		foo.setId(2L);
		bar.setId(3L);
		Map<Long, User> map = saveListToMap(Arrays.asList(emailtohl, foo, bar), "id");
		logger.debug(map);
		assertEquals(emailtohl, map.get(1L));
		assertEquals(foo, map.get(2L));
		assertEquals(bar, map.get(3L));
	}
	
	class TestGenericType {
		Set<Gender> set;
		List<Role> ls;
		Map<Integer, Subsidiary> map;
		public Set<Gender> getSet() {
			return set;
		}
		public void setSet(Set<Gender> set) {
			this.set = set;
		}
		public List<Role> getLs() {
			return ls;
		}
		public void setLs(List<Role> ls) {
			this.ls = ls;
		}
		public Map<Integer, Subsidiary> getMap() {
			return map;
		}
		public void setMap(Map<Integer, Subsidiary> map) {
			this.map = map;
		}
	}
	
	@Test
	public void testGetGenericClassField() throws NoSuchFieldException, SecurityException {
		Class<TestGenericType> c = TestGenericType.class;
		assertEquals(Gender.class, getGenericClass(c.getDeclaredField("set"))[0]);
		assertEquals(Role.class, getGenericClass(c.getDeclaredField("ls"))[0]);
		assertEquals(Integer.class, getGenericClass(c.getDeclaredField("map"))[0]);
		assertEquals(Subsidiary.class, getGenericClass(c.getDeclaredField("map"))[1]);
	}

	@Test
	public void testGetGenericClassPropertyDescriptor() throws IntrospectionException {
		BeanInfo bi = Introspector.getBeanInfo(TestGenericType.class, Object.class);
		for (PropertyDescriptor p : bi.getPropertyDescriptors()) {
			if ("set".equals(p.getName())) {
				assertEquals(Gender.class, getGenericClass(p)[0]);
			}
			if ("ls".equals(p.getName())) {
				assertEquals(Role.class, getGenericClass(p)[0]);
			}
			if ("map".equals(p.getName())) {
				assertEquals(Integer.class, getGenericClass(p)[0]);
			}
			if ("map".equals(p.getName())) {
				assertEquals(Subsidiary.class, getGenericClass(p)[1]);
			}
		}
	}

	@Test
	public void testCompareProperties() {
		boolean b = compareProperties(foo, new String[] {"gender", "roles"}, emailtohl);
		assertTrue(b);
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
}
