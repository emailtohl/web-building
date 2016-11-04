package com.github.emailtohl.building.config;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.github.emailtohl.building.site.service.impl.SystemInfo;
import com.github.emailtohl.building.site.service.impl.SystemInfo.Info;
import com.github.emailtohl.building.site.service.impl.SystemInfo.Observe;

/**
 * Websocket的应用程序受容器（如Tomcat）直接管理和调用，所以难以手工将其注入到Spring容器，并接受Spring的依赖注入
 * 应用程序可以继承Spring提供的适配器（AbstractWebSocketHandler），并在配置文件中将Websocket程序纳入Spring管理
 * @author HeLei
 */
@EnableWebSocket
public class WebsocketConfiguration implements WebSocketConfigurer {
	@Bean
	public WebSocketHandler testWebSocketHandler() {
		return new TestWebSocketHandler();
	}
	@Bean
	public SystemInfoListener systemInfoListener() {
		return new SystemInfoListener();
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(testWebSocketHandler(), "helloSpringWebSocket");
		registry.addHandler(systemInfoListener(), "systemInfoListener");
	}
	
	public static class TestWebSocketHandler extends AbstractWebSocketHandler {
		@Inject
		ThreadPoolTaskScheduler taskScheduler;
		
		@PostConstruct
		public boolean isInitialize() {
			return taskScheduler == null ? false : true;
		}
		
		@Override
		public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
			System.out.println(message);
			session.sendMessage(new TextMessage("收到消息了：" + message));
		}
	}
	
	/**
	 * 将系统信息主动推送到前台
	 * @author HeLei
	 */
	public static class SystemInfoListener extends AbstractWebSocketHandler implements Observe {
		@Inject SystemInfo systemInfo;

		@PostConstruct
		public void initialize() {
			systemInfo.getObserves().add(this);
		}
		
		@Override
		public void notify(Info info) {
			
		}
		
		@Override
		public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
			session.sendMessage(new TextMessage("收到消息了：" + message));
		}
	}
}
