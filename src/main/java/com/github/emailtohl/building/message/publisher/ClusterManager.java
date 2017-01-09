package com.github.emailtohl.building.message.publisher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

@Service
public class ClusterManager implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger log = LogManager.getLogger();
	public static final String SECURITY_CODE = "a83teo83hou9883hha9";
	public static final String RESPONSE_OK = "ok";
	
	private static final String HOST;
	private static final int PORT = 6789;
	private static final InetAddress GROUP;
	static {
		try {
			HOST = InetAddress.getLocalHost().getHostAddress();
			GROUP = InetAddress.getByName("224.0.0.3");
		} catch (UnknownHostException e) {
			throw new FatalBeanException("Could not initialize IP addresses.", e);
		}
	}

	private final Object mutex = new Object();
	private volatile boolean initialized = false, destroyed = false;
	private String pingUrl, messagingUrl;
	private MulticastSocket socket;
	private Thread listener;

	@Inject
	ServletContext servletContext;
	@Inject
	ClusterEventMulticaster multicaster;

	@PostConstruct
	public void listenForMulticastAnnouncements() throws Exception {
		this.pingUrl = "http://" + HOST + ":8080" + this.servletContext.getContextPath() + "/ping";
		this.messagingUrl = "ws://" + HOST + ":8080" + this.servletContext.getContextPath()
				+ "/services/messaging/" + SECURITY_CODE;

		synchronized (this.mutex) {
			this.socket = new MulticastSocket(PORT);
			this.socket.joinGroup(GROUP);
			this.listener = new Thread(this::listen, "cluster-listener");
			this.listener.start();
		}
	}

	private void listen() {
		byte[] buffer = new byte[2048];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		while (true) {
			try {
				this.socket.receive(packet);
				String url = new String(buffer, 0, packet.getLength());
				if (url.length() == 0)
					log.warn("Received blank multicast packet.");
				else if (url.equals(this.messagingUrl))
					log.info("Ignoring our own multicast packet.");
				else
					this.multicaster.registerNode(url);
			} catch (IOException e) {
				if (!this.destroyed)
					log.error(e);
				return;
			}
		}
	}

	@PreDestroy
	public void shutDownMulticastConnection() throws IOException {
		this.destroyed = true;
		try {
			this.listener.interrupt();
			this.socket.leaveGroup(GROUP);
		} finally {
			this.socket.close();
		}
	}

	@Async
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (this.initialized)
			return;
		this.initialized = true;

		try {
			URL url = new URL(this.pingUrl);
			log.info("Attempting to connect to self at {}.", url);
			int tries = 0;
			while (true) {
				tries++;
				URLConnection connection = url.openConnection();
				connection.setConnectTimeout(100);
				try (InputStream stream = connection.getInputStream()) {
					String response = StreamUtils.copyToString(stream, StandardCharsets.UTF_8);
					if (response != null && response.equals(RESPONSE_OK)) {
						log.info("Broadcasting multicast announcement packet.");
						DatagramPacket packet = new DatagramPacket(this.messagingUrl.getBytes(),
								this.messagingUrl.length(), GROUP, PORT);
						synchronized (this.mutex) {
							this.socket.send(packet);
						}
						return;
					} else
						log.warn("Incorrect response: {}", response);
				} catch (Exception e) {
					if (tries > 120) {
						log.fatal("Could not connect to self within 60 seconds.", e);
						return;
					}
					Thread.sleep(400L);
				}
			}
		} catch (Exception e) {
			log.fatal("Could not connect to self.", e);
		}
	}
}
