package com.github.emailtohl.building.site.mail;

import javax.mail.MessagingException;

import com.github.emailtohl.building.bootspring.SpringUtils;

public class EmailServiceTest {

//	@Test
	public void testSendChangePasswordEmail() throws MessagingException {
		EmailService service = SpringUtils.context.getBean(EmailService.class);
		String subject = "Test Subject";
		String htmlText = "<h3>Test</h3>";
		service.sendMail("hltest@yeah.net", subject, htmlText);
	}

}
