package com.github.emailtohl.building.websocket;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;

public class Configurator extends ServerEndpointConfig.Configurator {
	private static final String HTTP_SESSION_KEY = "httpSession";
	private static final String PRINCIPAL_KEY = "principal";
	private static final String LOCALE_KEY = "locale";

	@Override
	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		super.modifyHandshake(config, request, response);
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		config.getUserProperties().put(HTTP_SESSION_KEY, httpSession);
		config.getUserProperties().put(PRINCIPAL_KEY, SecurityContextHolder.getContext().getAuthentication());
		config.getUserProperties().put(LOCALE_KEY, LocaleContextHolder.getLocale());
	}

	public static HttpSession getExposedSession(Session session) {
		return (HttpSession) session.getUserProperties().get(HTTP_SESSION_KEY);
	}

	public static Principal getExposedPrincipal(Session session) {
		return (Principal) session.getUserProperties().get(PRINCIPAL_KEY);
	}

	public static Locale getExposedLocale(Session session) {
		return (Locale) session.getUserProperties().get(LOCALE_KEY);
	}
}
