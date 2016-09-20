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

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Version;
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
	Configuration configuration;
	
	public EmailServiceImpl() {
		super();
		Version version = new Version(2, 3, 23);
		configuration = new Configuration(version);
		configuration.setClassForTemplateLoading(EmailService.class, "ftl");
		configuration.setObjectWrapper(new DefaultObjectWrapper(version));
		configuration.setDefaultEncoding("UTF-8");
		configuration.setClassicCompatible(true);// 处理空值为空字符串
	}

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

	@Override
	public void enableUser(String url, String email, long id) {
		
	}

	@Override
	public void forgetPassword(String url, String email, String _csrf) {
		
	}
	
}
