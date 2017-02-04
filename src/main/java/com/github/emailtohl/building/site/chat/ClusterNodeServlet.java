package com.github.emailtohl.building.site.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.Session;

import com.github.emailtohl.building.site.chat.ChatMessage.Type;
/**
 * websocket，模拟群集的客户端点
 * 它同时也是一个servlet，首先接收前端传来的消息，然后将消息推送给后台服务端
 * @author HeLei
 * @date 2017.02.04
 */
@WebServlet(initParams = {
		@WebInitParam(name = "nodeId", value = "clientNode", description = "websocket终端") }, description = "利用websocket组建群集", urlPatterns = {
				"/chat/node" })
@ClientEndpoint
public class ClusterNodeServlet extends HttpServlet {
	private static final long serialVersionUID = 3217442189883197242L;
	private Session session;
	private String nodeId;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ClusterNodeServlet() {
		super();
	}

	@Override
	public void init() throws ServletException {
		this.nodeId = getInitParameter("nodeId");
		String path = getServletContext().getContextPath() + "/chat/clusterNodeSocket/" + this.nodeId;
		try {
			URI uri = new URI("ws", "localhost:8080", path, null, null);
			// websocket的session
			this.session = ContainerProvider.getWebSocketContainer().connectToServer(this, uri);
		} catch (URISyntaxException | IOException | DeploymentException e) {
			throw new ServletException("Cannot connect to " + path + ".", e);
		}
	}

	@Override
	public void destroy() {
		try {
			this.session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.destroy();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ChatMessage message = new ChatMessage();
		message.setUser("server");
		message.setTimestamp(Instant.now());
		message.setUserContent(UUID.randomUUID().toString());
		message.setType(Type.TEXT);
		try (OutputStream output = this.session.getBasicRemote().getSendStream();
				ObjectOutputStream stream = new ObjectOutputStream(output)) {
			stream.writeObject(message);
		}
		response.getWriter().append("OK");
	}

	@OnMessage
	public void onMessage(InputStream input) {
		try (ObjectInputStream stream = new ObjectInputStream(input)) {
			ChatMessage message = (ChatMessage) stream.readObject();
			System.out.println("ClusterNodeServlet接收信息: " + message);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@OnClose
	public void onClose(CloseReason reason) {
		CloseReason.CloseCode code = reason.getCloseCode();
		if (code != CloseReason.CloseCodes.NORMAL_CLOSURE) {
			System.err.println("ERROR: WebSocket connection closed unexpectedly;" + " code = " + code + ", reason = "
					+ reason.getReasonPhrase());
		}
	}
}
