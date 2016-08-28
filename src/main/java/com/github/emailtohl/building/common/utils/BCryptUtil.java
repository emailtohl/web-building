package com.github.emailtohl.building.common.utils;

import java.security.SecureRandom;

import org.springframework.security.crypto.bcrypt.BCrypt;

public final class BCryptUtil {
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final int HASHING_ROUNDS = 10;
	
	public static String hash(String plainText) {
		String salt = BCrypt.gensalt(HASHING_ROUNDS, RANDOM);
		return BCrypt.hashpw(plainText, salt);
	}
	
	public static boolean checkpw(String plainText, String hashedText) {
		return BCrypt.checkpw(plainText, hashedText);
	}
}
