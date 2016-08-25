package com.github.emailtohl.building.common.utils;

import java.security.SecureRandom;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BCryptUtil {
	private final SecureRandom RANDOM = new SecureRandom();
	private final int HASHING_ROUNDS = 10;
	
	public String hash(String plainText) {
		String salt = BCrypt.gensalt(HASHING_ROUNDS, RANDOM);
		return BCrypt.hashpw(plainText, salt);
	}
	
	public boolean checkpw(String plainText, String hashedText) {
		return BCrypt.checkpw(plainText, hashedText);
	}
}
