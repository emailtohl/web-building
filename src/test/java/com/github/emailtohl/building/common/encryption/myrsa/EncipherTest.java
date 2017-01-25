package com.github.emailtohl.building.common.encryption.myrsa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.emailtohl.building.common.encryption.myrsa.Encipher.Code;
import com.google.gson.Gson;

public class EncipherTest {
	String plaintext = "滚滚长江东逝水，浪花淘尽英雄。\r\n" + 
			"是非成败转头空。\r\n" + 
			"青山依旧在，几度夕阳红。\r\n" + 
			"白发渔樵江渚上，惯看秋月春风。\r\n" + 
			"一壶浊酒喜相逢。\r\n" + 
			"古今多少事，都付笑谈中。";
	
	KeyPairs keys = new KeyGenerator().generateKeys(128);
	Encipher encipher = new Encipher();
	Gson gson = new Gson();

	@Test
	public void test1() {
		Code code = encipher.encrypt(plaintext, keys);
		String ciphertext = gson.toJson(code);
		System.out.println(ciphertext);
		
		Code code2 = gson.fromJson(ciphertext, Code.class);
		String restore = encipher.decrypt(code2, keys);
		
		assertEquals(restore, plaintext);
		
		plaintext = "短";
		code = encipher.encrypt(plaintext, keys);
		ciphertext = gson.toJson(code);
		System.out.println(ciphertext);
		code2 = gson.fromJson(ciphertext, Code.class);
		restore = encipher.decrypt(code2, keys);
		assertEquals(restore, plaintext);
	}
	
	@Test
	public void test2() {
		String ciphertext = encipher.encrypt(plaintext, keys.getPublicKey().toString(), keys.getModule().toString());
		System.out.println(ciphertext);
		
		String recovery = encipher.decrypt(ciphertext, keys.getPrivateKey().toString(), keys.getModule().toString());
		System.out.println(recovery);
		assertEquals(recovery, plaintext);
	}
	
	@Test
	public void test3() {
		String encodePublicKey = encipher.getEncodePublicKey(keys);
		String encodePrivateKey = encipher.getEncodePrivateKey(keys);
		String ciphertext = encipher.encrypt(plaintext, encodePublicKey);
		System.out.println(ciphertext);
		String recovery = encipher.decrypt(ciphertext, encodePrivateKey);
		assertEquals(plaintext, recovery);
	}

}
