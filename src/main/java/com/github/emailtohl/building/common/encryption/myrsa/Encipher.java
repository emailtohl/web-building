package com.github.emailtohl.building.common.encryption.myrsa;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 本加密解密类主要考虑与前端JavaScript能识别的编码方式。
 * 
 * 在前端JavaScript中，可识别Unicode编码的字符，所以后端的加密解密也需基于Unicode编码进行。
 * 
 * 加密和解密的算法需要在前端JavaScript也实现一份。
 * 
 * @author HeLei
 * @date 2017.01.24
 */
public class Encipher {
	
	/**
	 * 将明文字符串转为Unicode编码的字符数组
	 * @param text
	 * @return
	 */
	int[] getUnicode(String text) {
		int[] unicodes = new int[text.length()];
		for (int i = 0; i < text.length(); i++) {
			unicodes[i] = text.codePointAt(i);
		}
		return unicodes;
	}
	
	/**
	 * 将Unicode编码的字符数组转成字符串
	 * @param unicodes
	 * @return
	 */
	String getString(int[] unicodes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < unicodes.length; i++) {
			sb.append((char) unicodes[i]);
		}
		return sb.toString();
	}
	
	/**
	 * 将字符串文本转成一个BigInteger，并且保留当初数组中的每一个切分信息
	 * @param text
	 * @return
	 */
	private Code encode(String text) {
		Code code = new Code();
		LinkedList<Integer> splitPoints = new LinkedList<>();
		int splitPoint = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			String c = String.valueOf(text.codePointAt(i));// 字符转成Unicode码
			sb.append(c);
			splitPoint += c.length();
			splitPoints.add(splitPoint);
		}
		code.m = new BigInteger(sb.toString());
		code.splitPoints = splitPoints;
		return code;
	}
	
	/**
	 * 将code对象解析为原始字符串
	 * @param code
	 * @return
	 */
	private String decode(Code code) {
		String str = code.m.toString();
		List<Integer> unicodes = new ArrayList<>();
		Integer beginIndex = 0, endIndex;
		while ((endIndex = code.splitPoints.pollFirst()) != null) {
			String unicode = str.substring(beginIndex, endIndex);
			unicodes.add(Integer.valueOf(unicode));
			beginIndex = endIndex;
		}
		StringBuilder sb = new StringBuilder();
		for (Integer unicode : unicodes) {
			int u = unicode;
			sb.append((char) u);
		}
		return sb.toString();
	}
	
	/**
	 * 将原code加密成新的code
	 * 
	 * 算法思路如下：
	 * m是数字化的明文，使用RSA加密时，m一定要小于模n
	 * 故先将m转成两个小于n的m1，m2，然后分别加密m1和m2成密文c1，c2
	 * 
	 * @param src
	 * @param keyPairs
	 * @return
	 */
	private Code crypt(Code src, KeyPairs keyPairs) {
		Code dest = new Code();
		BigInteger m = src.m,
				e = keyPairs.getPublicKey(), 
				n = keyPairs.getModule(),
				m1, m2, k, c1, c2;
		// 将明文m拆分为m = k*m1 + m2，保证m1，m2一定小于n，如此可以分别对m1，m2加密
		m1 = n.shiftRight(1);// m1一定小于n
		BigInteger[] divideAndRemainder = m.divideAndRemainder(m1);
		k = divideAndRemainder[0];
		m2 = divideAndRemainder[1];
		c1 = m1.modPow(e, n);
		c2 = m2.modPow(e, n);
		dest.splitPoints = src.splitPoints;
		dest.k = k;
		dest.c1 = c1;
		dest.c2 = c2;
		return dest;
	}
	
	/**
	 * 将字符串加密成code
	 * @param text
	 * @param keyPairs 只使用其公钥和模，不需要私钥，私钥字段可为null
	 * @return
	 */
	public Code crypt(String text, KeyPairs keyPairs) {
		Code code = encode(text);
		return crypt(code, keyPairs);
	}
	
	/**
	 * 根据加密规则解密code
	 * @param code
	 * @param keyPairs 只使用其私钥和模，公钥可为null
	 * @return
	 */
	public String decrypt(Code code, KeyPairs keyPairs) {
		BigInteger c1 = code.c1, c2 = code.c2, k = code.k,
				d = keyPairs.getPrivateKey(), 
				n = keyPairs.getModule(),
				m1, m2, m;
		m1 = c1.modPow(d, n);
		m2 = c2.modPow(d, n);
		m = k.multiply(m1).add(m2);
		Code result = new Code();
		result.m = m;
		result.splitPoints = code.splitPoints;
		return decode(result);
	}
	
	/**
	 * 加密后的信息存放对象
	 */
	public class Code implements Serializable {
		private static final long serialVersionUID = 2807424294564280181L;
		BigInteger m, k, m1, m2, c1, c2;
		LinkedList<Integer> splitPoints;
		public BigInteger getM() {
			return m;
		}
		public void setM(BigInteger m) {
			this.m = m;
		}
		public BigInteger getK() {
			return k;
		}
		public void setK(BigInteger k) {
			this.k = k;
		}
		public BigInteger getM1() {
			return m1;
		}
		public void setM1(BigInteger m1) {
			this.m1 = m1;
		}
		public BigInteger getM2() {
			return m2;
		}
		public void setM2(BigInteger m2) {
			this.m2 = m2;
		}
		public BigInteger getC1() {
			return c1;
		}
		public void setC1(BigInteger c1) {
			this.c1 = c1;
		}
		public BigInteger getC2() {
			return c2;
		}
		public void setC2(BigInteger c2) {
			this.c2 = c2;
		}
		public LinkedList<Integer> getSplitPoints() {
			return splitPoints;
		}
		public void setSplitPoints(LinkedList<Integer> splitPoints) {
			this.splitPoints = splitPoints;
		}
	}
	
}
