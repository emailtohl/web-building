package com.github.emailtohl.building.config;

import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_PRODUCTION;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_QA;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.emailtohl.building.message.listener.AuthenticationInterestedParty;
import com.github.emailtohl.building.message.listener.ClusterEventMulticaster;
import com.github.emailtohl.building.message.listener.ClusterInterestedParty;
import com.github.emailtohl.building.message.listener.ClusterManager;
import com.github.emailtohl.building.message.listener.LoginInterestedParty;
import com.github.emailtohl.building.message.listener.LogoutInterestedParty;

/**
 * 在此注册那些不在测试环境中加载的Bean
 * 
 * @author HeLei
 */
@Configuration
@Profile({ PROFILE_PRODUCTION, PROFILE_QA })
public class BeanRegisterConfiguration {
	
	@Bean
	public ClusterManager clusterManager() {
		return new ClusterManager();
	}
	
	@Bean
	public ClusterInterestedParty clusterInterestedParty() {
		return new ClusterInterestedParty();
	}
	/**
	 * applicationEventMulticaster这个名字是有意义的，spring会识别它并将其用作消息广播的Bean
	 * @return
	 */
	@Bean
	public ClusterEventMulticaster applicationEventMulticaster() {
		return new ClusterEventMulticaster();
	}
	
	@Bean
	public AuthenticationInterestedParty authenticationInterestedParty() {
		return new AuthenticationInterestedParty();
	}
	
	@Bean
	public LoginInterestedParty loginInterestedParty() {
		return new LoginInterestedParty();
	}
	
	@Bean
	public LogoutInterestedParty logoutInterestedParty() {
		return new LogoutInterestedParty();
	}
}
