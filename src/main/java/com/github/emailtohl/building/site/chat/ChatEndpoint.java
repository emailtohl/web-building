package com.github.emailtohl.building.site.chat;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.emailtohl.building.websocket.Configurator;
import com.google.gson.Gson;
/**
 * websocket，聊天程序的服务端
 * @author HeLei
 */
@ServerEndpoint(value = "/chat/{username}", configurator = Configurator.class)
public class ChatEndpoint {
	private static Logger logger = LogManager.getLogger();
	private static final Map<String, Session> map = new ConcurrentHashMap<String, Session>();
	private String username;
	private Session session;
	@SuppressWarnings("unused")
	private HttpSession httpSession;
	@Inject
	private ChatService chatService;
	@Inject
	private Gson gson;

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username, EndpointConfig config) throws IOException {
		this.username = username;
		this.session = session;
		// 在配置类中获取HttpSession
		this.httpSession = Configurator.getExposedSession(session);
		// websocket属于容器管理，要使用spring管理的Bean，需要先获取spring容器
//		WebApplicationContext context = WebApplicationContextUtils
//				.getRequiredWebApplicationContext(httpSession.getServletContext());
//		chatService = context.getBean(ChatService.class);
//		gson = context.getBean(Gson.class);
		map.put(session.getId(), session);
	}

	@OnMessage
	public void onMessage(String message) throws IOException {
		ChatMessage msg = new ChatMessage();
		msg.setTimestamp(Instant.now());
		msg.setUser(username);
		Message m = gson.fromJson(message, Message.class);
		msg.setUserContent(m.getMessage());
		msg.setIconSrc(m.getIconSrc());
		String str = gson.toJson(msg);
		for (Session s : map.values()) {
			/*if (s.equals(session)) {
				continue;
			}*/
			s.getBasicRemote().sendText(str);
		}
		
		chatService.save(username, msg);
	}

	@OnClose
	public void onClose(CloseReason reason) {
		String str = "goodbye username: " + username + "  reason: " + reason.getReasonPhrase();
		for (Session s : map.values()) {
			if (s.equals(session)) {
				continue;
			}
			try {
				s.getBasicRemote().sendText(str);
			} catch (IOException e) {
				logger.debug(e);
				map.remove(s.getId());
			}
		}
		map.remove(session.getId());
	}

	@OnError
	public void onError(Throwable e) {
		logger.info(e);
		map.remove(session.getId());
	}
	
	/**
	 * 对应前端传输来的字段
	 * @author HeLei
	 *
	 */
	class Message {
		String message;
		String iconSrc;
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getIconSrc() {
			return iconSrc;
		}
		public void setIconSrc(String iconSrc) {
			this.iconSrc = iconSrc;
		}
	}
}
