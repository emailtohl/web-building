package com.github.emailtohl.building.site.mail;

import static org.junit.Assert.assertEquals;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.emailtohl.building.bootspring.SpringUtils;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;

public class EmailServiceTest {
	private GreenMail greenMail;

	@Before
	public void startMailServer() throws Exception {
		greenMail = new GreenMail(ServerSetup.SMTP);
		greenMail.setUser("emailtohl@163.com", "123456");
		greenMail.start();
	}

	@Test
	public void testSendChangePasswordEmail() throws MessagingException {
		EmailService service = SpringUtils.context.getBean(EmailService.class);
		String subject = "Test Subject";
		String htmlText = "<h3>Test</h3>";
		service.sendMail("emailtohl@163.com", subject, htmlText);

		greenMail.waitForIncomingEmail(2000, 1);

		Message[] msgs = greenMail.getReceivedMessages();
		assertEquals(1, msgs.length);
		assertEquals(subject, msgs[0].getSubject());
		assertEquals(htmlText, GreenMailUtil.getBody(msgs[0]).trim());
	}

	@After
	public void stopMailServer() throws Exception {
		greenMail.stop();
	}
}
