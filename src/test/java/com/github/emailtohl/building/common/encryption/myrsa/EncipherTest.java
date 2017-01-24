package com.github.emailtohl.building.common.encryption.myrsa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.emailtohl.building.common.encryption.myrsa.Encipher.Code;
import com.google.gson.Gson;

public class EncipherTest {

	@Test
	public void testCryptAndDecrypt() {
		String plaintext = "滚滚长江东逝水，浪花淘尽英雄。\r\n" + 
							"是非成败转头空。\r\n" + 
							"青山依旧在，几度夕阳红。\r\n" + 
							"白发渔樵江渚上，惯看秋月春风。\r\n" + 
							"一壶浊酒喜相逢。\r\n" + 
							"古今多少事，都付笑谈中。";
		KeyGenerator kg = new KeyGenerator();
		Encipher e = new Encipher();
		Gson gson = new Gson();
		
		KeyPairs keys = kg.generateKeys(128);
		
		Code code = e.crypt(plaintext, keys);
		String ciphertext = gson.toJson(code);
		System.out.println(ciphertext);
		
		Code code2 = gson.fromJson(ciphertext, Code.class);
		String restore = e.decrypt(code2, keys);
		
		assertEquals(restore, plaintext);
		
		plaintext = "短";
		code = e.crypt(plaintext, keys);
		ciphertext = gson.toJson(code);
		System.out.println(ciphertext);
		code2 = gson.fromJson(ciphertext, Code.class);
		restore = e.decrypt(code2, keys);
		assertEquals(restore, plaintext);
	}

}
