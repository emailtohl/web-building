package com.github.emailtohl.building.message.event;

public class LogoutEvent extends AuthenticationEvent {
	private static final long serialVersionUID = 4677549694759529069L;

	public LogoutEvent(String username) {
		super(username);
	}
}
