package com.github.emailtohl.building.common.encryption.myrsa;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

/**
 * 为了让前后端在加解密的规则上达成一致协议，设计本类。
 * 
 * 前端JavaScript可识别Unicode编码的字符，所以后端的加密解密也需基于Unicode编码进行。
 * 
 * 加解密面向的数据结构是内部类Code：
 * m是将明文Unicode编码并连接在一起的BigInteger，splitPoints则保存着连接点的信息
 * m1,m2,k和m的关系是：m = k * m1 + m2
 * c1,c2 则分别是RSA加密后的m1和m2
 * 
 * JavaScript实现依赖于库jsbn，该库为JavaScript创建了一个与Java相似API的BigInteger，
 * 另外cryptico.js库可直接创建出RSA的密钥对，由于cryptico加解密协议是私有实现不能与后台通信，且不能加解密中文编码，故只使用其生成RSA密钥的方法
 * 
 * @author HeLei
 * @date 2017.01.24
 */
public class Encipher {
	private static final Logger logger = LogManager.getLogger();
	Base64.Encoder encoder = Base64.getEncoder();
	Base64.Decoder decoder = Base64.getDecoder();
	Gson gson = new Gson();
	
	/**
	 * 未在本类中用到
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
	 * 未在本类中用到
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
	 * @param text 原字符串文本
	 * @return 一个可序列化的code对象
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
		code.m = sb.toString();
		code.splitPoints = splitPoints;
		return code;
	}
	/* JavaScript实现
	function encode(text) {
		var code = {}, splitPoints = [], splitPoint = 0, s = '', i, u;
		for (i = 0; i < text.length; i++) {
			u = new String(text.codePointAt(i));// 字符转成Unicode码
			s += u;
			splitPoint += u.length;
			splitPoints.push(splitPoint);
		}
		code.m = s;
		code.splitPoints = splitPoints;
		return code;
	}
	*/
	
	/**
	 * 将code对象解析为原始字符串
	 * @param code
	 * @return
	 */
	private String decode(Code code) {
		String str = code.m.toString();
		List<Integer> unicodes = new ArrayList<>();
		Integer beginIndex = 0, endIndex;
		// 下面要使用改变参数的方法，故使用副本进行操作
		LinkedList<Integer> copy = new LinkedList<Integer>(code.splitPoints);
		while ((endIndex = copy.pollFirst()) != null) {
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
	/* JavaScript实现
	function decode(code) {
		var unicodes = [], beginIndex = 0, endIndex, unicode, copy = [], i;
		for (i = 0; i < code.splitPoints.length; i++) {
			copy.push(code.splitPoints[i]);
		}
		while ((endIndex = copy.shift())) {
			unicode = code.m.substring(beginIndex, endIndex);
			unicodes.push(String.fromCharCode(unicode));
			beginIndex = endIndex;
		}
		return unicodes.join('');
	}
	*/
	
	/**
	 * 将原code加密成新的code
	 * 
	 * 算法思路如下：
	 * m是数字化的明文，使用RSA加密时，m一定要小于模n
	 * 故先将m转成两个小于n的m1，m2，然后分别加密m1和m2成密文c1，c2
	 * 
	 * @param src 已被数字化编码的明文
	 * @param publicKey 密钥对象，只使用其中的publicKey和module属性
	 * @return 加密的可序列化的code对象
	 */
	private Code crypt(Code src, KeyPairs publicKey) {
		Code dest = new Code();
		BigInteger m = new BigInteger(src.m),
				e = publicKey.getPublicKey(), 
				n = publicKey.getModule(),
				m1, m2, k, c1, c2;
		// 将明文m拆分为m = k*m1 + m2，保证m1，m2一定小于n，如此可以分别对m1，m2加密
		m1 = n.shiftRight(1);// m1一定小于n
		BigInteger[] divideAndRemainder = m.divideAndRemainder(m1);
		k = divideAndRemainder[0];
		m2 = divideAndRemainder[1];
		if (BigInteger.ZERO.equals(k))// k如果为0，说明m本身就小于n，c1是什么就无所谓了，因为乘积仍然是0
			c1 = BigInteger.ZERO;
		else
			c1 = m1.modPow(e, n);
		c2 = m2.modPow(e, n);
		dest.splitPoints = src.splitPoints;
		dest.k = k.toString();
		dest.c1 = c1.toString();
		dest.c2 = c2.toString();
		return dest;
	}
	
	/* JavaScript实现
	function cryptCode(srcCode, publicKey) {
		var m = new BigInteger(srcCode.m), e = new BigInteger(publicKey.publicKey), 
		n = new BigInteger(publicKey.module), m1, m2, k, c1, c2, 
		divideAndRemainder, destCode = {};
		// 将明文m拆分为m = k*m1 + m2，保证m1，m2一定小于n，如此可以分别对m1，m2加密
		m1 = n.shiftRight(1);// m1一定小于n
		divideAndRemainder = m.divideAndRemainder(m1);
		k = divideAndRemainder[0];
		m2 = divideAndRemainder[1];
		if (BigInteger.ZERO.equals(k))// k如果为0，说明m本身就小于n，c1是什么就无所谓了，因为乘积仍然是0
			c1 = BigInteger.ZERO;
		else
			c1 = m1.modPow(e, n);
		c2 = m2.modPow(e, n);
		destCode.splitPoints = srcCode.splitPoints;
		destCode.k = k.toString();
		destCode.c1 = c1.toString();
		destCode.c2 = c2.toString();
		return destCode;
	}
	*/
	
	/**
	 * 将字符串加密成code
	 * @param plaintext 明文字符串
	 * @param publicKey 只使用其公钥和模，不需要私钥，私钥字段为null
	 * @return 加密的可序列化的code对象
	 */
	public Code encrypt(String plaintext, KeyPairs publicKey) {
		Code code = encode(plaintext);
		return crypt(code, publicKey);
	}
	
	/* JavaScript实现
	function encrypt(plaintext, publicKey) {
		var code = encode(plaintext);
		return encryptCode(code, publicKey);
	}
	*/
	
	/**
	 * 根据加密规则解密code
	 * @param code
	 * @param privateKey 只使用其私钥和模，公钥为null
	 * @return 原字符串文本
	 */
	public String decrypt(Code code, KeyPairs privateKey) {
		BigInteger c1 = new BigInteger(code.c1), c2 = new BigInteger(code.c2), 
				k = new BigInteger(code.k),
				d = privateKey.getPrivateKey(), 
				n = privateKey.getModule(),
				m1, m2, m;
		if (BigInteger.ZERO.equals(k))// k为0，m1是什么都无所谓，因为乘积仍然是0
			m1 = BigInteger.ZERO;
		else
			m1 = c1.modPow(d, n);
		m2 = c2.modPow(d, n);
		m = k.multiply(m1).add(m2);
		Code result = new Code();
		result.m = m.toString();
		result.splitPoints = code.splitPoints;
		return decode(result);
	}
	
	/* JavaScript实现
	function decrypt(code, privateKey) {
		var c1 = new BigInteger(code.c1), c2 = new BigInteger(code.c2), 
		k = new BigInteger(code.k), d = new BigInteger(privateKey.privateKey), 
		n = new BigInteger(privateKey.module), m1, m2, m, destCode = {};
		if (BigInteger.ZERO.equals(k))// k为0，m1是什么都无所谓，因为乘积仍然是0
			m1 = BigInteger.ZERO;
		else
			m1 = c1.modPow(d, n);
		m2 = c2.modPow(d, n);
		m = k.multiply(m1).add(m2);
		destCode.m = m.toString();
		destCode.splitPoints = code.splitPoints;
		return decode(destCode);
	}
	*/
	
	/**
	 * 封装public Code encrypt(String text, KeyPairs publicKey);
	 * 对外部使用更友好清晰
	 * @param plaintext 明文
	 * @param publicKey 公钥
	 * @param module 模
	 * @return 加密后的信息
	 */
	public String encrypt(String plaintext, String publicKey, String module) {
		KeyPairs keys = new KeyPairs();
		keys.setPublicKey(new BigInteger(publicKey));
		keys.setModule(new BigInteger(module));
		Code c = encrypt(plaintext, keys);
		String json = gson.toJson(c);
		return encoder.encodeToString(json.getBytes());
	}
	
	/**
	 * 封装String decrypt(Code code, KeyPairs privateKey);
	 * 对外部使用更友好清晰
	 * @param ciphertext 字符串密文
	 * @param privateKey 私钥
	 * @param module 模
	 * @return 解密后的信息
	 */
	public String decrypt(String ciphertext, String privateKey, String module) {
		try {
			String json = new String(decoder.decode(ciphertext));
			Code c = gson.fromJson(json, Code.class);
			KeyPairs keys = new KeyPairs();
			keys.setPrivateKey(new BigInteger(privateKey));
			keys.setModule(new BigInteger(module));
			return decrypt(c, keys);
		} catch (Exception e) {
			String msg = "解密失败，密文信息可能已损坏!";
			logger.error(msg, e);
			throw new IllegalArgumentException(msg, e);
		}
	}
	
	
	/**
	 * 加密后的信息存放对象
	 */
	public class Code implements Serializable {
		private static final long serialVersionUID = 2807424294564280181L;
		// 定义为String类型是为了防止前端JavaScript的JSON.parse函数将数字转成指数形式
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
		@Override
		public String toString() {
			return "Code [m=" + m + ", k=" + k + ", m1=" + m1 + ", m2=" + m2 + ", c1=" + c1 + ", c2=" + c2
					+ ", splitPoints=" + splitPoints + "]";
		}
	}
	
}
