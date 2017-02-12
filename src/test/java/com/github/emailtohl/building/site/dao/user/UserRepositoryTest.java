package com.github.emailtohl.building.site.dao.user;

import static com.github.emailtohl.building.initdb.PersistenceData.foo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.utils.BeanUtil;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.initdb.PersistenceData;
import com.github.emailtohl.building.site.dao.user.UserRepository;
import com.github.emailtohl.building.site.entities.user.Subsidiary;
import com.github.emailtohl.building.site.entities.user.User;
/**
 * 业务类测试
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
@Transactional
public class UserRepositoryTest {
	static final Logger logger = LogManager.getLogger();
	@Inject UserRepository userRepository;
	SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
	User u;
	PageRequest pageable = new PageRequest(0, 20);
	
	@Before
	public void setUp() {
		u = new User();
		u.setEmail(foo.getEmail());
		Subsidiary s = BeanUtil.deepCopy(foo.getSubsidiary());
		u.setSubsidiary(s);
		u.setRoles(BeanUtil.deepCopy(foo.getRoles()));
	}
	
	/**
	 * 测试spring data中的命名方法
	 */
	@Test
	public void testFindByEmail() {
		User user = userRepository.findByEmail(u.getEmail());
		assertEquals(u.getEmail(), user.getEmail());
	}
	/**
	 * 测试spring data中的命名方法
	 */
	@Test
	public void testFindByBirthdayBetween() throws ParseException {
		Date begin = format.parse("1982-01-01");
		Date end = format.parse("1983-01-01");
		List<User> ls = userRepository.findByBirthdayBetween(begin, end);
		assertFalse(ls.isEmpty());
	}

	/**
	 * 测试动态查询
	 */
	@Test
	public void testDynamicQuery() {
		Pager<User> p = userRepository.dynamicQuery(PersistenceData.foo, pageable);
		logger.debug(p.getContent());
		assertNotNull(p.getContent());
	}
	
	/**
	 * 将自定义的Pager转换成Spring data的Page
	 * 不过Page的有些方法返回数据不正确，例如getNumberOfElements、getTotalElements、hasPrevious等
	 * 不过若只需确定的getContent和当前页getNumber没有问题
	 */
	@Test
	public void testGetPage() {
		Page<User> p = userRepository.getPage(u, pageable);
		logger.debug("getNumber:" + p.getNumber());
		logger.debug("getNumberOfElements:" + p.getNumberOfElements());
		logger.debug("getSize:" + p.getSize());
		logger.debug("getTotalElements:" + p.getTotalElements());
		logger.debug("getTotalPages:" + p.getTotalPages());
		logger.debug("hasContent:" + p.hasContent());
		logger.debug("hasPrevious:" + p.hasPrevious());
		logger.debug("isFirst:" + p.isFirst());
		logger.debug("isLast:" + p.isLast());
		logger.debug("getContent:" + p.getContent());
		logger.debug("getSort:" + p.getSort());
		logger.debug("nextPageable:" + p.nextPageable());
		logger.debug("previousPageable:" + p.previousPageable());
		logger.debug("spliterator:" + p.spliterator());
	}
	
	@Test
	public void testGetMaxEmpNo() {
		logger.debug(userRepository.getMaxEmpNo());
		assertNotNull(userRepository.getMaxEmpNo());
	}
	
	@Test
	public void testGetPagerByRoles() {
		Pager<User> p = userRepository.getPagerByRoles(u.getEmail(), u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()), pageable);
		logger.debug("getNumber:" + p.getPageNumber());
		logger.debug("getNumberOfElements:" + p.getTotalElements());
		logger.debug("getSize:" + p.getPageSize());
		logger.debug("getTotalElements:" + p.getTotalElements());
		logger.debug("getTotalPages:" + p.getTotalPages());
		logger.debug("hasContent:" + p.getContent());
		assertEquals(0, p.getPageNumber());
		assertEquals(1, p.getTotalElements());
		assertEquals(1, p.getTotalPages());
	}
	
	@Test
	public void testGetPagerByCriteria() {
		Pager<User> p = userRepository.getPagerByCriteria(u.getEmail(), u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()), pageable);
		logger.debug("getNumber:" + p.getPageNumber());
		logger.debug("getNumberOfElements:" + p.getTotalElements());
		logger.debug("getSize:" + p.getPageSize());
		logger.debug("getTotalElements:" + p.getTotalElements());
		logger.debug("getTotalPages:" + p.getTotalPages());
		logger.debug("hasContent:" + p.getContent());
		assertEquals(1, p.getTotalElements());
		
		u.setEmail(null);
		u.getRoles().clear();
		p = userRepository.getPagerByCriteria(u.getEmail(), u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()), pageable);
		logger.debug("getNumber:" + p.getPageNumber());
		logger.debug("getNumberOfElements:" + p.getTotalElements());
		logger.debug("getSize:" + p.getPageSize());
		logger.debug("getTotalElements:" + p.getTotalElements());
		logger.debug("getTotalPages:" + p.getTotalPages());
		logger.debug("hasContent:" + p.getContent());
		assertTrue(p.getTotalElements() > 0);
	}
	
}
