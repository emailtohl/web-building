package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedList;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.emailtohl.building.common.encryption.myrsa.Encipher;
import com.github.emailtohl.building.common.encryption.myrsa.Encipher.Code;
import com.github.emailtohl.building.common.encryption.myrsa.KeyPairs;
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
	public void uploadPublicKey(@RequestBody KeyPairs keyPairs) {
		userService.setPublicKey(keyPairs.getPublicKey(), keyPairs.getModule());
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
		if (u == null)
			return null;
		BigInteger publicKey = new BigInteger(u.getPublicKey()), module = new BigInteger(u.getModule());
		if (publicKey == null || module == null)
			return null;
		KeyPairs k = new KeyPairs();
		k.setPublicKey(publicKey);
		k.setModule(module);
		Code c = encipher.crypt(plaintext, k);
		logger.debug(c);
		_Code _c = new _Code();
		_c.c1 = c.getC1().toString();
		_c.c2 = c.getC2().toString();
		_c.k = c.getK().toString();
		_c.splitPoints = c.getSplitPoints();
		String json = gson.toJson(_c);
		// 由于前端接收大数字会指数化破坏大数字的结构，所以转成Base64编码
		return json;
	}
	
	@SuppressWarnings("unused")
	private class _Code implements Serializable {
		private static final long serialVersionUID = 7237126675835172601L;
		String m, k, m1, m2, c1, c2;
		LinkedList<Integer> splitPoints;
		public String getM() {
			return m;
		}
		public String getK() {
			return k;
		}
		public String getM1() {
			return m1;
		}
		public String getM2() {
			return m2;
		}
		public String getC1() {
			return c1;
		}
		public String getC2() {
			return c2;
		}
		public LinkedList<Integer> getSplitPoints() {
			return splitPoints;
		}
	}
}
