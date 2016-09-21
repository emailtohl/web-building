package com.github.emailtohl.building.site.mail;

public interface EmailService {
	void sendMail(String to, String subject, String htmlText);
	/**
	 * 通知激活用户
	 * @param url 激活的地址
	 * @param email 通知的用户
	 */
	void enableUser(String url, String email);
	
	/**
	 * 
	 * @param url
	 * @param email
	 * @param token
	 * @param _csrf
	 */
	void updatePassword(String url, String email, String token, String _csrf);
}
