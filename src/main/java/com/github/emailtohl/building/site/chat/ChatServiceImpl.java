package com.github.emailtohl.building.site.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
/**
 * 聊天程序接口的实现类，受spring管理
 * @author HeLei
 */
@Service
public class ChatServiceImpl implements ChatService {
	private static final Logger log = LogManager.getLogger();
	private static final Map<String, ChatMessage> map = new ConcurrentHashMap<String, ChatMessage>();

	@Override
	public void save(String username, ChatMessage msg) {
		map.put(username, msg);
		log.info("username: " + username + "\n" + msg);
	}

}
