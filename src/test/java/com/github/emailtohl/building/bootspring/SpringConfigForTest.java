package com.github.emailtohl.building.bootspring;

import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_DEVELPMENT;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;

import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.service.user.UserService;
import com.github.emailtohl.building.stub.SecurityContextManager;
import com.github.emailtohl.building.stub.ServiceStub;
/**
 * 建立此配置类的原因是为了不让RootContextConfiguration引用src/test/java目录下的类
 * 否则在生产环境中启动时会发生编译异常
 * @author HeLei
 * @date 2017.02.04
 */
@Profile(PROFILE_DEVELPMENT)
@Configuration
@Import({ RootContextConfiguration.class })
public class SpringConfigForTest {
	@Inject AuthenticationManager authenticationManager;
	
	@Bean
	public SecurityContextManager securityContextManager() throws Exception {
		return new SecurityContextManager(authenticationManager);
	}
	
	@Bean
	public ServiceStub serviceStub() {
		return new ServiceStub();
	}
	
	@Bean
	public UserService userServiceMock() {
		return serviceStub().getUserService();
	}
}
