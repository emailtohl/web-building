/**
 * 自定义RSA实现，与后端使用同样编码和加解密协议
 * @author HeLei
 * @date 2017.01.24
 */
define(['lib/cryptico/cryptico.min', 'lib/base64/base64.min'], function() {
	return {
		getKeyPairs : getKeyPairs,
		encrypt : encrypt,
		decrypt : decrypt
	};
	
	/**
	 * 根据bit长度创建密钥对，为了便于序列化传输，先将密钥对象转成json格式，然后再编码为Unicode
	 * @param bitLength 密钥的长度
	 * @return 密钥对数组，keys[0]是公钥，keys[1]是私钥
	 */
	function getKeyPairs(bitLength) {
		var passPhrase, keys, json, publicKey, privateKey;
		passPhrase = new String(Math.random());
		keys = cryptico.generateRSAKey(passPhrase, bitLength);
		json = JSON.stringify({
			publicKey : keys.e.toString(),
			module : keys.n.toString()
		});
		publicKey = Base64.encode(json);
		json = JSON.stringify({
			privateKey : keys.d.toString(),
			module : keys.n.toString()
		});
		privateKey = Base64.encode(json);
		return [publicKey, privateKey];
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
	function encrypt(plaintext, publicKey) {
		var pm = encode(plaintext)/*明文数据模型*/, cm = {}/*密文数据模型*/;
		var json = Base64.decode(publicKey);
		var key = JSON.parse(json);
		var m = new BigInteger(pm.m), e = new BigInteger(key.publicKey), 
		n = new BigInteger(key.module), m1, m2, k, c1, c2, divideAndRemainder;
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
		cm.splitPoints = pm.splitPoints;
		cm.k = k.toString();
		cm.c1 = c1.toString();
		cm.c2 = c2.toString();
		json = JSON.stringify(cm);
		return Base64.encode(json);
	}
	
	/**
	 * RSA解密，与加密操作反向
	 * 
	 * @param ciphertext 密文
	 * @param privateKey 私钥
	 * @return 明文
	 */
	function decrypt(ciphertext, privateKey) {
		var json = Base64.decode(ciphertext);
		var cm = JSON.parse(json)/*密文数据模型*/, pm = {}/*明文数据模型*/;
		json = Base64.decode(privateKey);
		var key = JSON.parse(json);
		var c1 = new BigInteger(cm.c1), c2 = new BigInteger(cm.c2), 
		k = new BigInteger(cm.k), d = new BigInteger(key.privateKey), 
		n = new BigInteger(key.module), m1, m2, m;
		if (BigInteger.ZERO.equals(k))// k为0，m1是什么都无所谓，因为乘积仍然是0
			m1 = BigInteger.ZERO;
		else
			m1 = c1.modPow(d, n);
		m2 = c2.modPow(d, n);
		m = k.multiply(m1).add(m2);
		pm.m = m.toString();
		pm.splitPoints = cm.splitPoints;
		return decode(pm);
	}
	
	/**
	 * 将字符串文本转成一个BigInteger，并且保留当初数组中的每一个切分信息
	 * 
	 * @param text 原字符串文本
	 * @return 一个可序列化的model对象
	 */
	function encode(text) {
		var model = {}, splitPoints = [], splitPoint = 0, s = '', i, u;
		for (i = 0; i < text.length; i++) {
			u = new String(text.codePointAt(i));// 字符转成Unicode码
			s += u;
			splitPoint += u.length;
			splitPoints.push(splitPoint);
		}
		model.m = s;
		model.splitPoints = splitPoints;
		return model;
	}

	/**
	 * 将model对象解析为原始字符串
	 * 
	 * @param model 编码的对象
	 * @return 文本字符串
	 */
	function decode(model) {
		var unicodes = [], beginIndex = 0, endIndex, unicode, copy = [], i;
		for (i = 0; i < model.splitPoints.length; i++) {
			copy.push(model.splitPoints[i]);
		}
		while ((endIndex = copy.shift())) {
			unicode = model.m.substring(beginIndex, endIndex);
			unicodes.push(String.fromCharCode(unicode));
			beginIndex = endIndex;
		}
		return unicodes.join('');
	}
	
});