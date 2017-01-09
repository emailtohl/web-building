package com.github.emailtohl.building.message.cluster;

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
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.stereotype.Component;

import com.github.emailtohl.building.message.event.ClusterEvent;

//spring感知到事件时，要使用消息广播器，提供applicationEventMulticaster名字供spring识别
@Component("applicationEventMulticaster")
public class ClusterEventMulticaster extends SimpleApplicationEventMulticaster {
	private static final Logger log = LogManager.getLogger();

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
					this.publishClusteredEvent((ClusterEvent) event);
			} catch (Exception e) {
				log.error("Failed to broadcast distributable event to cluster.", e);
			}
		}
	}

	protected void publishClusteredEvent(ClusterEvent event) {
		synchronized (this.endpoints) {
			for (ClusterMessagingEndpoint endpoint : this.endpoints)
				endpoint.send(event);
		}
	}

	protected void registerEndpoint(ClusterMessagingEndpoint endpoint) {
		if (!this.endpoints.contains(endpoint)) {
			synchronized (this.endpoints) {
				this.endpoints.add(endpoint);
			}
		}
	}

	protected void deregisterEndpoint(ClusterMessagingEndpoint endpoint) {
		synchronized (this.endpoints) {
			this.endpoints.remove(endpoint);
		}
	}

	protected void registerNode(String endpoint) {
		log.info("Connecting to cluster node {}.", endpoint);
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		try {
			ClusterMessagingEndpoint bean = this.context.getAutowireCapableBeanFactory()
					.createBean(ClusterMessagingEndpoint.class);
			container.connectToServer(bean, new URI(endpoint));
			log.info("Connected to cluster node {}.", endpoint);
		} catch (DeploymentException | IOException | URISyntaxException e) {
			log.error("Failed to connect to cluster node {}.", endpoint, e);
		}
	}

	protected final void handleReceivedClusteredEvent(ClusterEvent event) {
		event.setRebroadcasted();
		this.multicastEvent(event);
	}

	@PreDestroy
	public void shutdown() {
		synchronized (this.endpoints) {
			for (ClusterMessagingEndpoint endpoint : this.endpoints)
				endpoint.close();
		}
	}
}
