/**
 * 自定义RSA实现，与后端使用同样编码和加解密协议
 * @author HeLei
 * @date 2017.01.24
 */
define(['lib/cryptico/cryptico.min'], function() {
	return {
		generateKeys : generateKeys,
		crypt : crypt,
		decrypt : decrypt
	};
	/**
	 * 将明文字符串转为Unicode编码的字符数组
	 * @param text
	 * @return 可序列化的密钥对象
	 */
	function generateKeys(bitLength) {
		var passPhrase = new String(Math.random());
		var rsaKey = cryptico.generateRSAKey(passPhrase, bitLength);
		return {
			module : rsaKey.n.toString(),
			publicKey : rsaKey.e.toString(),
			privateKey : rsaKey.d.toString()
		};
	}

	/**
	 * 将字符串文本转成一个BigInteger，并且保留当初数组中的每一个切分信息
	 * 
	 * @param text 原字符串文本
	 * @return 一个可序列化的code对象
	 */
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

	/**
	 * 将code对象解析为原始字符串
	 * 
	 * @param code 编码的对象
	 * @return 文本字符串
	 */
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

	/**
	 * 将字符串加密成code
	 * @param text 明文字符串
	 * @param keyPairs 只使用其公钥和模，不需要私钥，私钥字段可为null
	 * @return 加密的可序列化的code对象
	 */
	function crypt(text, publicKey) {
		var code = encode(text);
		return cryptCode(code, publicKey);
	}

	/**
	 * 根据加密规则解密code
	 * @param code
	 * @param privateKey 只使用其私钥和模，公钥可为null
	 * @return 原字符串文本
	 */
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
});