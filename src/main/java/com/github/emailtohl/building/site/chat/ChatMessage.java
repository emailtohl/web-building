package com.github.emailtohl.building.site.chat;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
/**
 * 被websocket序列化的类
 * @author HeLei
 */
public class ChatMessage implements Cloneable, Serializable {
	private static final long serialVersionUID = -8697529922899865254L;
	private Instant timestamp;
	private Type type;
	private String user;
	private String contentCode;
	private Object[] contentArguments;
	private String localizedContent;
	private String userContent;
	private String iconSrc;

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getContentCode() {
		return contentCode;
	}

	public void setContentCode(String contentCode) {
		this.contentCode = contentCode;
	}

	public Object[] getContentArguments() {
		return contentArguments;
	}

	public void setContentArguments(Object... contentArguments) {
		this.contentArguments = contentArguments;
	}

	public String getLocalizedContent() {
		return localizedContent;
	}

	public void setLocalizedContent(String localizedContent) {
		this.localizedContent = localizedContent;
	}

	public String getUserContent() {
		return userContent;
	}

	public void setUserContent(String userContent) {
		this.userContent = userContent;
	}
	
	public String getIconSrc() {
		return iconSrc;
	}

	public void setIconSrc(String iconSrc) {
		this.iconSrc = iconSrc;
	}

	@Override
	protected ChatMessage clone() {
		try {
			return (ChatMessage) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Impossible clone not supported.", e);
		}
	}

	public static enum Type {
		STARTED, JOINED, ERROR, LEFT, TEXT
	}

	@Override
	public String toString() {
		return "ChatMessage [timestamp=" + timestamp + ", type=" + type + ", user=" + user + ", contentCode="
				+ contentCode + ", contentArguments=" + Arrays.toString(contentArguments) + ", localizedContent="
				+ localizedContent + ", userContent=" + userContent + ", iconSrc=" + iconSrc + "]";
	}

}
