package com.github.emailtohl.building.site.mail;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
/**
 * 邮件服务service
 * @author HeLei
 */
@Service
public class EmailServiceImpl implements EmailService {
	private static final Logger logger = LogManager.getLogger();
	@Inject
	JavaMailSender mailSender;
	@Value("${mail.from}")
	String from;

	public void sendMail(String to, String subject, String htmlText) {
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper msgHelper = new MimeMessageHelper(msg);
			msgHelper.setFrom(from);
			msgHelper.setTo(to);
			msgHelper.setSubject(subject);
			msgHelper.setText(htmlText, true);
			mailSender.send(msg);
		} catch (MessagingException e) {
			logger.warn("Faild to send mail.", e);
		}
	}
}
