package com.github.emailtohl.building.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
/**
 * 本类可以将容器中的session复制一份，事实上只是它们的引用
 * @author HeLei
 * @date 2017.02.04
 */
@Component
public class SessionRegistry {
	private final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<String, HttpSession>();

	private SessionRegistry() {
	}

	public void addSession(HttpSession session) {
		SESSIONS.put(session.getId(), session);
	}

	public void updateSessionId(HttpSession session, String oldSessionId) {
		synchronized (SESSIONS) {
			SESSIONS.remove(oldSessionId);
			addSession(session);
		}
	}

	public void removeSession(HttpSession session) {
		SESSIONS.remove(session.getId());
	}

	public List<HttpSession> getAllSessions() {
		return new ArrayList<HttpSession>(SESSIONS.values());
	}

	public int getNumberOfSessions() {
		return SESSIONS.size();
	}

}
