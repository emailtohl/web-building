package com.github.emailtohl.building.site.systemInfo;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.github.emailtohl.building.site.service.impl.SystemInfo;
import com.github.emailtohl.building.site.service.impl.SystemInfo.Observe;
import com.github.emailtohl.building.websocket.Configurator;
import com.google.gson.Gson;
/**
 * 系统信息监听程序，将系统信息主动推送到前台
 * @author HeLei
 * @date 2017.02.04
 */
@ServerEndpoint(value = "/systemInfo", configurator = Configurator.class)
public class SystemInfoWebSocket implements Observe {
	private static Logger logger = LogManager.getLogger();
	private Session session;
	private HttpSession httpSession;
	private Gson gson;
	private SystemInfo systemInfo;
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		// 在配置类中获取HttpSession
		this.httpSession = Configurator.getExposedSession(session);
		// websocket属于容器管理，要使用spring管理的Bean，需要先获取spring容器
		WebApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(httpSession.getServletContext());
		systemInfo = context.getBean(SystemInfo.class);
		systemInfo.register(this);
		gson = context.getBean(Gson.class);
	}
	
	@OnMessage
	public void onMessage(String message) throws IOException {
		session.getBasicRemote().sendText("收到了消息：" + message);
	}

	@OnClose
	public void onClose(CloseReason reason) {
		systemInfo.remove(this);
	}

	@OnError
	public void onError(Throwable e) {
		logger.info(e);
		systemInfo.remove(this);
	}

	@Override
	public void notify(Map<String, Object> info) {
		if (session == null)
			return;
		try {
			session.getBasicRemote().sendText(gson.toJson(info));
		} catch (Exception e) {
			logger.info("public void notify(Map<String, Object> info)    " + e);
		}
	}
}
