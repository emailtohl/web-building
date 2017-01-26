package com.github.emailtohl.building.common.encryption.myrsa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EncipherTest {

	@Test
	public void test() {
		String plaintext = "滚滚长江东逝水，浪花淘尽英雄。\r\n" + 
				"是非成败转头空。\r\n" + 
				"青山依旧在，几度夕阳红。\r\n" + 
				"白发渔樵江渚上，惯看秋月春风。\r\n" + 
				"一壶浊酒喜相逢。\r\n" + 
				"古今多少事，都付笑谈中。";
		Encipher encipher = new Encipher();
		String[] keys = encipher.getKeyPairs(512);
		String publicKey = keys[0];
		String privateKey = keys[1];
		String ciphertext = encipher.encrypt(plaintext, publicKey);
		System.out.println(ciphertext);
		String recovery = encipher.decrypt(ciphertext, privateKey);
		assertEquals(plaintext, recovery);
	}

}
