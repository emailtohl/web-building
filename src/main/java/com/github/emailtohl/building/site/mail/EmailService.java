package com.github.emailtohl.building.site.mail;

public interface EmailService {
	void sendMail(String to, String subject, String htmlText);
}
