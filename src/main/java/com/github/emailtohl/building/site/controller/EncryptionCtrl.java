package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.emailtohl.building.common.encryption.myrsa.Encipher;
import com.github.emailtohl.building.common.utils.SecurityContextUtil;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;
import com.google.gson.Gson;

@RestController
@RequestMapping("encryption")
public class EncryptionCtrl {
	private static final Logger logger = LogManager.getLogger();
	@Inject UserService userService;
	@Inject Gson gson;
	Encipher encipher = new Encipher();
	
	@RequestMapping(value = "publicKey", method = POST)
	public void uploadPublicKey(String publicKey) {
		userService.setPublicKey(publicKey);
	}
	
	@RequestMapping(value = "publicKey", method = DELETE)
	public void deletePublicKey() {
		userService.clearPublicKey();
	}
	
	@RequestMapping(value = "testMessage", method = GET)
	public String testMessage() {
		String plaintext = "滚滚长江东逝水，浪花淘尽英雄。\r\n" + 
				"是非成败转头空。\r\n" + 
				"青山依旧在，几度夕阳红。\r\n" + 
				"白发渔樵江渚上，惯看秋月春风。\r\n" + 
				"一壶浊酒喜相逢。\r\n" + 
				"古今多少事，都付笑谈中。";
		
		String email = SecurityContextUtil.getCurrentUsername();
		if (email == null)
			return null;
		User u = userService.getUserByEmail(email);
		if (u == null || u.getPublicKey() == null)
			return null;
		String ciphertext = encipher.encrypt(plaintext, u.getPublicKey());
		logger.debug(ciphertext);
		// 构造成json格式
		return "{\"ciphertext\":\"" + ciphertext + "\"}";
	}
}
