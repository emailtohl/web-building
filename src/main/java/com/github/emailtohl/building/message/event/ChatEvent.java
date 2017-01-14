package com.github.emailtohl.building.message.event;

import java.io.Serializable;

import com.github.emailtohl.building.site.chat.ChatMessage;

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
