package com.github.emailtohl.building.common.security;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 用于文件加密解密的工具，加解密文件用的是AES算法，AES的密钥用RSA算法加密
 * @author HeLei
 */
public class Crypt {
	private static final Logger logger = LogManager.getLogger();
	private Hex hex = new Hex();
	
	/**
	 * 创建RSA的密钥对
	 * @param length
	 * @return
	 */
	public KeyPair createKeyPairs(int length) {
		KeyPairGenerator pairgen = null;
		try {
			pairgen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			logger.fatal("RSA密钥创建失败", e);
		}
		SecureRandom random = new SecureRandom();
		pairgen.initialize(length, random);
		KeyPair keyPair = pairgen.generateKeyPair();
		return keyPair;
	}
	
	/**
	 * 创建RSA的密钥对
	 * @param length
	 * @param publicKeyFile
	 * @param privateKeyFile
	 */
	public void createKeyPairs(int length, File publicKeyFile, File privateKeyFile) {
		KeyPairGenerator pairgen = null;
		try {
			pairgen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			logger.fatal("RSA密钥创建失败", e);
		}
		SecureRandom random = new SecureRandom();
		pairgen.initialize(length, random);
		KeyPair keyPair = pairgen.generateKeyPair();
		try (ObjectOutputStream outPublicKey = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
				ObjectOutputStream outPrivateKey = new ObjectOutputStream(new FileOutputStream(privateKeyFile))) {
			outPublicKey.writeObject(keyPair.getPublic());
			outPrivateKey.writeObject(keyPair.getPrivate());
			logger.debug("密钥创建成功");
		} catch (IOException e) {
			logger.fatal("密钥创建失败", e);
		}
	}
	
	/**
	 * 加密文件
	 * @param inFile
	 * @param outFile
	 * @param publicKeyFile
	 */
	public void encrypt(File inFile, File outFile, File publicKeyFile) {
		// wrap with RSA public key
		try (ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(publicKeyFile));
				DataOutputStream out = new DataOutputStream(new FileOutputStream(outFile));
				InputStream in = new FileInputStream(inFile)) {

			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			SecureRandom random = new SecureRandom();
			keygen.init(random);
			SecretKey key = keygen.generateKey();

			Key publicKey = (Key) keyIn.readObject();
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.WRAP_MODE, publicKey);
			byte[] wrappedKey = cipher.wrap(key);
			out.writeInt(wrappedKey.length);
			out.write(wrappedKey);

			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			crypt(in, out, cipher);
		} catch (IOException | ClassNotFoundException | GeneralSecurityException e) {
			logger.fatal("加密失败", e);
		}
	}
	
	/**
	 * 解密文件
	 * @param inFile
	 * @param outFile
	 * @param privateKeyFile
	 */
	public void decrypt(File inFile, File outFile, File privateKeyFile) {
		try (DataInputStream in = new DataInputStream(new FileInputStream(inFile));
				ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(privateKeyFile));
				OutputStream out = new FileOutputStream(outFile)) {
			int length = in.readInt();
			byte[] wrappedKey = new byte[length];
			in.read(wrappedKey, 0, length);

			// unwrap with RSA private key
			Key privateKey = (Key) keyIn.readObject();

			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.UNWRAP_MODE, privateKey);
			Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);

			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);

			crypt(in, out, cipher);
		} catch (IOException | ClassNotFoundException | GeneralSecurityException e) {
			logger.fatal("解密失败", e);
		}
	}

	/**
	 * Uses a cipher to transform the bytes in an input stream and sends the
	 * transformed bytes to an output stream.
	 * 
	 * @param in the input stream
	 * @param out the output stream
	 * @param cipher the cipher that transforms the bytes
	 */
	public void crypt(InputStream in, OutputStream out, Cipher cipher) throws IOException,
			GeneralSecurityException {
		int blockSize = cipher.getBlockSize();
		int outputSize = cipher.getOutputSize(blockSize);
		byte[] inBytes = new byte[blockSize];
		byte[] outBytes = new byte[outputSize];

		int inLength = 0;
		boolean more = true;
		while (more) {
			inLength = in.read(inBytes);
			if (inLength == blockSize) {
				int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
				out.write(outBytes, 0, outLength);
			} else
				more = false;
		}
		if (inLength > 0)
			outBytes = cipher.doFinal(inBytes, 0, inLength);
		else
			outBytes = cipher.doFinal();
		out.write(outBytes);
	}

	public String encrypt(String plaintext, PublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] ciphertextByteArray = cipher.doFinal(plaintext.getBytes());
			String ciphertext = hex.encodeHexStr(ciphertextByteArray);
			return ciphertext;
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			throw new RuntimeException("加密失败", e);
		}
	}
	
	public String decrypt(String ciphertext, PrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] ciphertextByteArray = hex.decodeHex(ciphertext.toCharArray());
			byte[] plaintextByteArray = cipher.doFinal(ciphertextByteArray);
			return new String(plaintextByteArray);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			throw new RuntimeException("加密失败", e);
		}
	}
	
}
