package com.github.emailtohl.building.message.subject;

import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_PRODUCTION;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_QA;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.message.event.ClusterEvent;
/**
 * 通过继承Spring的事件广播器成为该广播器的装饰器，将集群事件通知给各个websocket端点
 * @author HeLei
 * @date 2017.02.04
 */
@Profile({ PROFILE_PRODUCTION, PROFILE_QA })
// applicationEventMulticaster这个名字是有意义的，spring会识别它并将其用作消息广播的Bean
@Service("applicationEventMulticaster")
public class ClusterEventMulticaster extends SimpleApplicationEventMulticaster {
	private static final Logger logger = LogManager.getLogger();

	private final Set<ClusterMessagingEndpoint> endpoints = new HashSet<>();

	@Inject
	ApplicationContext context;

	@Override
	public final void multicastEvent(ApplicationEvent event) {
		try {
			super.multicastEvent(event);
		} finally {
			try {
				if (event instanceof ClusterEvent && !((ClusterEvent) event).isRebroadcasted())
					publishClusteredEvent((ClusterEvent) event);
			} catch (Exception e) {
				logger.error("Failed to broadcast distributable event to cluster.", e);
			}
		}
	}
	
	@Override
	public final void multicastEvent(ApplicationEvent event, ResolvableType eventType) {
		try {
			super.multicastEvent(event, eventType);
		} finally {
			try {
				if (event instanceof ClusterEvent && !((ClusterEvent) event).isRebroadcasted())
					publishClusteredEvent((ClusterEvent) event);
			} catch (Exception e) {
				logger.error("Failed to broadcast distributable event to cluster.", e);
			}
		}
	}

	protected void publishClusteredEvent(ClusterEvent event) {
		synchronized (endpoints) {
			for (ClusterMessagingEndpoint endpoint : endpoints)
				endpoint.send(event);
		}
	}

	protected void registerEndpoint(ClusterMessagingEndpoint endpoint) {
		synchronized (endpoints) {
			endpoints.add(endpoint);
		}
	}

	protected void deregisterEndpoint(ClusterMessagingEndpoint endpoint) {
		synchronized (endpoints) {
			endpoints.remove(endpoint);
		}
	}

	protected void registerNode(String endpoint) {
		logger.info("Connecting to cluster node {}.", endpoint);
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		try {
			ClusterMessagingEndpoint bean = context.getAutowireCapableBeanFactory()
					.createBean(ClusterMessagingEndpoint.class);
			container.connectToServer(bean, new URI(endpoint));
			logger.info("Connected to cluster node {}.", endpoint);
		} catch (DeploymentException | IOException | URISyntaxException e) {
			logger.error("Failed to connect to cluster node {}.", endpoint, e);
		}
	}

	protected final void handleReceivedClusteredEvent(ClusterEvent event) {
		event.setRebroadcasted();
		multicastEvent(event);
	}

	@PreDestroy
	public void shutdown() {
		synchronized (endpoints) {
			for (ClusterMessagingEndpoint endpoint : endpoints)
				endpoint.close();
		}
	}
}
