package com.github.emailtohl.building.message.subject;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import com.github.emailtohl.building.message.event.ClusterEvent;
/**
 * 负责集群间的websocket通信端，服务端和客户端处理逻辑相似，估共用本类
 * @author HeLei
 * @date 2017.02.04
 */
@ServerEndpoint(value = "/services/messaging/{securityCode}", 
	encoders = { ClusterMessagingEndpoint.Codec.class }, 
	decoders = { ClusterMessagingEndpoint.Codec.class }, 
	configurator = SpringConfigurator.class)
@ClientEndpoint(encoders = { ClusterMessagingEndpoint.Codec.class }, 
	decoders = { ClusterMessagingEndpoint.Codec.class })
public class ClusterMessagingEndpoint {
	private static final Logger logger = LogManager.getLogger();

	private Session session;

	@Inject
	ClusterEventMulticaster multicaster;

	@OnOpen
	public void open(Session session) {
		Map<String, String> parameters = session.getPathParameters();
		if (parameters.containsKey("securityCode") && !ClusterManager.SECURITY_CODE.equals(parameters.get("securityCode"))) {
			try {
				logger.error("Received connection with illegal code {}.", parameters.get("securityCode"));
				session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Illegal Code"));
			} catch (IOException e) {
				logger.warn("Failed to close illegal connection.", e);
			}
		} else {
			logger.info("Successful connection onOpen.");
			this.session = session;
			multicaster.registerEndpoint(this);
		}
	}

	@OnMessage
	public void receive(ClusterEvent message) {
		multicaster.handleReceivedClusteredEvent(message);
	}

	public void send(ClusterEvent message) {
		try {
			session.getBasicRemote().sendObject(message);
		} catch (IOException | EncodeException e) {
			logger.error("Failed to send message to adjacent node.", e);
		}
	}

	@OnClose
	public void close() {
		logger.info("Cluster node connection closed.");
		multicaster.deregisterEndpoint(this);
		if (session.isOpen()) {
			try {
				session.close();
			} catch (IOException e) {
				logger.warn("Error while closing cluster node connection.", e);
			}
		}
	}

	public static class Codec implements Encoder.BinaryStream<ClusterEvent>, Decoder.BinaryStream<ClusterEvent> {
		@Override
		public ClusterEvent decode(InputStream stream) throws DecodeException, IOException {
			try (ObjectInputStream input = new ObjectInputStream(stream)) {
				return (ClusterEvent) input.readObject();
			} catch (ClassNotFoundException e) {
				throw new DecodeException((String) null, "Failed to decode.", e);
			}
		}

		@Override
		public void encode(ClusterEvent event, OutputStream stream) throws IOException {
			try (ObjectOutputStream output = new ObjectOutputStream(stream)) {
				output.writeObject(event);
			}
		}

		@Override
		public void init(EndpointConfig endpointConfig) {
		}

		@Override
		public void destroy() {
		}
	}
}
