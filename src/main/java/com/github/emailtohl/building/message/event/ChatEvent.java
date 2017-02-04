package com.github.emailtohl.building.message.event;

import java.io.Serializable;

import com.github.emailtohl.building.site.chat.ChatMessage;
/**
 * 聊天相关的事件
 * @author HeLei
 * @date 2017.02.04
 */
public class ChatEvent extends AuthenticationEvent {
	private static final long serialVersionUID = -1620174996342472535L;
	private ChatMessage message;
	
	public ChatEvent(Serializable source) {
		super(source);
	}

	public ChatMessage getMessage() {
		return message;
	}

	public void setMessage(ChatMessage message) {
		this.message = message;
	}

}
