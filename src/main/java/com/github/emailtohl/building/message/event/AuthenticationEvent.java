package com.github.emailtohl.building.message.event;

import java.io.Serializable;

public abstract class AuthenticationEvent extends ClusterEvent {
	private static final long serialVersionUID = -759325995400716933L;

	public AuthenticationEvent(Serializable source) {
		super(source);
	}
}
