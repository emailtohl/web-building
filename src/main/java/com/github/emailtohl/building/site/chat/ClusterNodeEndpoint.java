package com.github.emailtohl.building.site.chat;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.github.emailtohl.building.websocket.Configurator;
/**
 * websocket，模拟群集的服务端点
 * @author Helei
 */
@ServerEndpoint(value = "/chat/clusterNodeSocket/{nodeId}", encoders = ChatMessageCoder.class, decoders = ChatMessageCoder.class, configurator = Configurator.class)
public class ClusterNodeEndpoint {
	private static final List<Session> nodes = new CopyOnWriteArrayList<Session>();
	private Session session;
	private HttpSession httpSession;
	private Principal principal;
	private Locale locale;

	@OnOpen
	public void onOpen(Session session, @PathParam("nodeId") String nodeId) {
		System.out.println("ClusterNodeEndpoint  INFO: Node [" + nodeId + "] connected to cluster.");
		this.session = session;
		this.httpSession = Configurator.getExposedSession(session);
		this.principal = Configurator.getExposedPrincipal(session);
		this.locale = Configurator.getExposedLocale(session);
		nodes.add(session);
	}

	@OnMessage
	public void onMessage(ChatMessage message) {
		System.out.println("ClusterNodeEndpoint接收信息： " + message);
		ChatMessage send = new ChatMessage();
		send.setUser("server");
		send.setTimestamp(Instant.now());
		send.setLocalizedContent(locale.toString());
		send.setUserContent(principal.getName());
		send.setContentCode(httpSession.getId());
		System.out.println("ClusterNodeEndpoint发送信息： " + send);
		try {
			for (Session node : nodes) {
//				if (node != session)
					node.getBasicRemote().sendObject(send);
			}
		} catch (IOException | EncodeException e) {
			System.err.println("ClusterNodeEndpoint  ERROR: Exception when handling message on server");
			e.printStackTrace();
		}
	}

	@OnClose
	public void onClose(CloseReason reason, @PathParam("nodeId") String nodeId) {
		System.out.println("ClusterNodeEndpoint  INFO: Node [" + nodeId + "] disconnected.");
		nodes.remove(session);
		ChatMessage message = new ChatMessage();
		message.setUser("server");
		message.setTimestamp(Instant.now());
		try {
			for (Session node : ClusterNodeEndpoint.nodes)
				node.getBasicRemote().sendObject(message);
		} catch (IOException | EncodeException e) {
			System.err.println("ClusterNodeEndpoint  ERROR: Exception when notifying of left node");
			e.printStackTrace();
		}
	}
}

