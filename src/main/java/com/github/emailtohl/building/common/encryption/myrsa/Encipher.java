package com.github.emailtohl.building.common.encryption.myrsa;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	KeyGenerator kg = new KeyGenerator();
	Base64.Encoder encoder = Base64.getEncoder();
	Base64.Decoder decoder = Base64.getDecoder();
	Gson gson = new Gson();
	
	/**
	 * 根据bit长度创建密钥对，为了便于序列化传输，先将密钥对象转成json格式，然后再编码为Unicode
	 * @param bitLength 密钥的长度
	 * @return 密钥对数组，keys[0]是公钥，keys[1]是私钥
	 */
	public String[] getKeyPairs(int bitLength) {
		KeyPairs keys = kg.generateKeys(bitLength);
		Map<String, String> map = new HashMap<String, String>();
		map.put("publicKey", keys.getPublicKey().toString());
		map.put("module", keys.getModule().toString());
		String publicKey = gson.toJson(map);
		publicKey = encoder.encodeToString(publicKey.getBytes());
		map.remove("publicKey");
		map.put("privateKey", keys.getPrivateKey().toString());
		String privateKey = gson.toJson(map);
		privateKey = encoder.encodeToString(privateKey.getBytes());
		return new String[] {publicKey, privateKey};
	}
	
	/**
	 * RSA加密，算法如下：
	 * 1. 先将明文的每一个字符进行Unicode编码，然后拼接在一起并记录下拼接的节点，就形成了明文的数据模型（model）。
	 * 2. 明文数据模型中的m属性即为待加密的对象，在RSA中由于m一定要小于模n，故还需对m进行预处理。
	 * 3. 将m拆分为m = k*m1 + m2，这样就可以保证m1，m2一定小于n，分别对m1，m2加密。
	 * 4. 将加密结果存储于新的数据模型中，先序列化为json，再转码为Unicode编码。
	 * 
	 * @param plaintext 明文
	 * @param publicKey 公钥
	 * @return 密文
	 */
	public String encrypt(String plaintext, String publicKey) {
		Model pm = encode(plaintext);// 明文数据模型
		Model cm = new Model();// 密文数据模型
		String json = new String(decoder.decode(publicKey));
		KeyPairs key = gson.fromJson(json, KeyPairs.class);
		BigInteger m = new BigInteger(pm.m),
				e = key.getPublicKey(), 
				n = key.getModule(),
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
		cm.splitPoints = pm.splitPoints;
		cm.k = k.toString();
		cm.c1 = c1.toString();
		cm.c2 = c2.toString();
		json = gson.toJson(cm);
		return encoder.encodeToString(json.getBytes());
	}
	
	/**
	 * RSA解密，与加密操作反向
	 * 
	 * @param ciphertext 密文
	 * @param privateKey 私钥
	 * @return 明文
	 */
	public String decrypt(String ciphertext, String privateKey) {
		String json = new String(decoder.decode(ciphertext));
		Model cm = gson.fromJson(json, Model.class);// 密文数据模型
		Model pm = new Model();// 明文数据模型
		json = new String(decoder.decode(privateKey));
		KeyPairs key = gson.fromJson(json, KeyPairs.class);
		BigInteger c1 = new BigInteger(cm.c1), c2 = new BigInteger(cm.c2), 
				k = new BigInteger(cm.k),
				d = key.getPrivateKey(), 
				n = key.getModule(),
				m1, m2, m;
		if (BigInteger.ZERO.equals(k))// k为0，m1是什么都无所谓，因为乘积仍然是0
			m1 = BigInteger.ZERO;
		else
			m1 = c1.modPow(d, n);
		m2 = c2.modPow(d, n);
		m = k.multiply(m1).add(m2);
		pm.m = m.toString();
		pm.splitPoints = cm.splitPoints;
		return decode(pm);// 最后将明文的数据模型转成明文
	}
	
	/**
	 * 将字符串文本转成一个BigInteger，并且保留当初数组中的每一个切分信息
	 * @param text 原字符串文本
	 * @return 一个可序列化的code对象
	 */
	private Model encode(String text) {
		Model code = new Model();
		LinkedList<Integer> splitPoints = new LinkedList<Integer>();
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
	 * @param model
	 * @return
	 */
	private String decode(Model model) {
		List<Integer> unicodes = new ArrayList<Integer>();
		Integer beginIndex = 0, endIndex;
		// 下面要使用改变参数的方法，故使用副本进行操作
		LinkedList<Integer> copy = new LinkedList<Integer>(model.splitPoints);
		while ((endIndex = copy.pollFirst()) != null) {
			String unicode = model.m.substring(beginIndex, endIndex);
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
	 * 加密后的信息存放对象
	 */
	@SuppressWarnings("unused")
	private class Model implements Serializable {
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
	}
	
}
