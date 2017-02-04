package com.github.emailtohl.building.message.event;
/**
 * 登出相关的事件
 * @author HeLei
 * @date 2017.02.04
 */
public class LogoutEvent extends AuthenticationEvent {
	private static final long serialVersionUID = 4677549694759529069L;

	public LogoutEvent(String username) {
		super(username);
	}
}
