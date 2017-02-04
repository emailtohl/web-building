package com.github.emailtohl.building.site.mail;
/**
 * 邮箱服务
 * @author HeLei
 * @date 2017.02.04
 */
public interface EmailService {
	void sendMail(String to, String subject, String htmlText);
	/**
	 * 通知激活用户
	 * @param url 激活的地址
	 * @param email 通知的用户
	 */
	void enableUser(String url, String email);
	
	/**
	 * 通知用户获取更新密码的页面
	 * @param url
	 * @param email
	 * @param token
	 * @param _csrf
	 */
	void updatePassword(String url, String email, String token, String _csrf);
}
