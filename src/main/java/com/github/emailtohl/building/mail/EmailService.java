package com.github.emailtohl.building.mail;

public interface EmailService {
	void sendMail(String to, String subject, String htmlText);
}
