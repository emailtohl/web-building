package com.github.emailtohl.building.site.mail;

public interface EmailService {
	void sendMail(String to, String subject, String htmlText);
	void enableUser(String url, String email, long id);
	void forgetPassword(String url, String email, String _csrf);
}
