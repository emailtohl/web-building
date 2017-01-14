package com.github.emailtohl.building.message.observer;

import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_PRODUCTION;
import static com.github.emailtohl.building.config.RootContextConfiguration.PROFILE_QA;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.github.emailtohl.building.message.event.ChatEvent;

@Profile({ PROFILE_PRODUCTION, PROFILE_QA })
@Service
public class ChatEventInterestedParty implements ApplicationListener<ChatEvent> {
	private static final Logger log = LogManager.getLogger();
	
	@Override
	public void onApplicationEvent(ChatEvent event) {
		log.info("Chat event for source {} received in chat {}.", event.getSource(),
				event.getMessage());
	}

}
