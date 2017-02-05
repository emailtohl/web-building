package com.github.emailtohl.building.site.entities;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.initdb.PersistenceData;
import com.github.emailtohl.building.site.dao.UserRepository;
/**
 * 业务类测试
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class UserTest {
	@Inject UserRepository userRepository;
	
	@Test
	public void testGetAge() throws ParseException {
		SimpleDateFormat s = new SimpleDateFormat("YYYY-MM-DD");
		User u = new User();
		u.setBirthday(s.parse("1982-02-12"));
		System.out.println(u.getAge());
	}

	@Test
	public void testGetIcon() throws FileNotFoundException, IOException {
		ClassLoader cl = UserTest.class.getClassLoader();
		User u = userRepository.findByEmail("emailtohl@163.com");
		try (InputStream is = cl.getResourceAsStream("img/icon-head-emailtohl.png")) {
			byte[] expecteds = new byte[is.available()];
			is.read(expecteds);
			byte[] actuals = u.getIcon();
			Assert.assertArrayEquals(expecteds, actuals);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAuthorities() {
		assertTrue(PersistenceData.emailtohl.authorities().contains(Authority.USER_DELETE));
	}
}
