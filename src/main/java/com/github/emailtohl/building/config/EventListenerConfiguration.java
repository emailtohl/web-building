package com.github.emailtohl.building.config;

import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_PRODUCTION;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_QA;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.emailtohl.building.message.cluster.ClusterEventMulticaster;
import com.github.emailtohl.building.message.cluster.ClusterManager;
import com.github.emailtohl.building.message.listener.AuthenticationInterestedParty;
import com.github.emailtohl.building.message.listener.ClusterInterestedParty;
import com.github.emailtohl.building.message.listener.LoginInterestedParty;
import com.github.emailtohl.building.message.listener.LogoutInterestedParty;

/**
 * Listener需要注册ServletContext，不在测试环境中加载
 * 
 * @author HeLei
 */
@Configuration
@Profile({ PROFILE_PRODUCTION, PROFILE_QA })
public class EventListenerConfiguration {
	/**
	 * spring感知到事件时，要使用消息广播器，提供applicationEventMulticaster名字供spring识别
	 * @return
	 */
	@Bean
	public ClusterEventMulticaster applicationEventMulticaster() {
		return new ClusterEventMulticaster();
	}
	
	@Bean
	public ClusterManager clusterManager() {
		return new ClusterManager();
	}
	
	@Bean
	public ClusterInterestedParty clusterInterestedParty() {
		return new ClusterInterestedParty();
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
