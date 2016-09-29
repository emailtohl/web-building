package com.github.emailtohl.building.site.chat;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.github.emailtohl.building.websocket.Configurator;
import com.google.gson.Gson;
/**
 * websocket，聊天程序的服务端
 * @author Helei
 */
@ServerEndpoint(value = "/chat/{username}", configurator = Configurator.class)
public class ChatEndpoint {
	private String username;
	private Session session;
	private HttpSession httpSession;
	private ChatService chatService;
	private Gson gson;
	private static final Map<String, Session> map = new ConcurrentHashMap<String, Session>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username, EndpointConfig config) {
		this.username = username;
		this.session = session;
		// 在配置类中获取HttpSession
		this.httpSession = Configurator.getExposedSession(session);
		// websocket属于容器管理，要使用spring管理的Bean，需要先获取spring容器
		WebApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(httpSession.getServletContext());
		chatService = context.getBean(ChatService.class);
		gson = context.getBean(Gson.class);
		map.put(session.getId(), session);
	}

	@OnMessage
	public void onMessage(String message) throws IOException {
		ChatMessage msg = new ChatMessage();
		msg.setTimestamp(Instant.now());
		msg.setUser(username);
		msg.setUserContent(message);
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
				e.printStackTrace();
			}
		}
		map.remove(session.getId());
	}

	@OnError
	public void onError(Throwable e) {

	}
}
