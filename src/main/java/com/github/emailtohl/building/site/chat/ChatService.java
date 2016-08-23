package com.github.emailtohl.building.site.chat;
/**
 * 聊天程序的接口
 * @author Helei
 */
public interface ChatService {
	/**
	 * 存储聊天信息
	 * @param username
	 * @param msg
	 */
	void save(String username, ChatMessage msg);
}
