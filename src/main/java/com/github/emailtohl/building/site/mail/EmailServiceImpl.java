package com.github.emailtohl.building.site.mail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

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
import freemarker.template.Template;
import freemarker.template.TemplateException;
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
			MimeMessageHelper msgHelper = new MimeMessageHelper(msg, "UTF-8");
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
	public void enableUser(String url, String email) {
		Map<String, String> root = new HashMap<String, String>();
		root.put("url", url);
		try (Writer out = new StringWriter(2048)) {
			Template template = configuration.getTemplate("enableUser.ftl");
			template.process(root, out);
			String htmlText = out.toString();
			sendMail(email, "激活账号", htmlText);
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
			logger.catching(e);
		}
	}

	@Override
	public void updatePassword(String url, String email, String token, String _csrf) {
		Map<String, String> root = new HashMap<String, String>();
		root.put("url", url);
		root.put("email", email);
		root.put("token", token);
		root.put("_csrf", _csrf);
		try (Writer out = new StringWriter(2048)) {
			Template template = configuration.getTemplate("updatePassword.ftl");
			template.process(root, out);
			String htmlText = out.toString();
			sendMail(email, "修改密码", htmlText);
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
			logger.catching(e);
		}
	}

	
}
