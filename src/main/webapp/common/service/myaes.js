/**
 * 基于cryptico，不过cryptico不支持中文，包装后让其支持中文
 * @author HeLei
 * @date 2017.01.26
 */
define(['lib/cryptico/cryptico.min', 'lib/base64/base64.min'], function() {
	return {
		getKey : getKey,
		encrypt : encrypt,
		decrypt : decrypt
	};
	
	/**
	 * Generate a random key for the AES-encrypted message
	 * @return AES密钥
	 */
	function getKey() {
		return cryptico.generateAESKey();
	}
	
	/**
	 * 用AES加密明文
	 * 
	 * @param plaintext 明文
	 * @param key 密钥
	 * @return 密文
	 */
	function encrypt(plaintext, key) {
		var pm = encode(plaintext), cm = {};
		cm.c = cryptico.encryptAESCBC(pm.m, key);
		cm.splitPoints = pm.splitPoints;
		json = JSON.stringify(cm);
		return Base64.encode(json);
	}
	
	/**
	 * 用AES解密密文
	 * 
	 * @param ciphertext 密文
	 * @param key 私钥
	 * @return 明文
	 */
	function decrypt(ciphertext, key) {
		var json = Base64.decode(ciphertext);
		var cm = JSON.parse(json)/*密文数据模型*/, pm = {}/*明文数据模型*/;
		pm.m = cryptico.decryptAESCBC(cm.c, key);
		pm.splitPoints = cm.splitPoints;
		return decode(pm);
	}
	
	/**
	 * 将字符串文本转成一个字符串，并且保留当初数组中的每一个切分信息
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