package com.github.emailtohl.building.common.security;

import static org.junit.Assert.assertEquals;

import java.security.KeyPair;

import org.junit.Test;

public class CryptTest {

	@Test
	public void testCryptUtil() {
		Crypt c = new Crypt();
		KeyPair k = c.createKeyPairs(2048);
		String plaintext = "待加密文本";
		System.out.println("加密前： " + plaintext);
		String encodeStr = c.encrypt(plaintext, k.getPublic());
		System.out.println("加密后： " + encodeStr);
		String decodeStr = c.decrypt(encodeStr, k.getPrivate());
		System.out.println("解密后： " + decodeStr);
		assertEquals(plaintext, decodeStr);
	}

}
