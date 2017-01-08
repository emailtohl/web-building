package com.github.emailtohl.building.message.event;

public class LoginEvent extends AuthenticationEvent {
	private static final long serialVersionUID = 3451596905017124116L;

	public LoginEvent(String username) {
		super(username);
	}
}
