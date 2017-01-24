define(['encryption/module', 'common/context', 'lib/cryptico/cryptico.min' ], function(encryptionModule) {
	return encryptionModule.factory('encryptionService', [ '$http', 'util', function($http, util) {
		return {
			/**
			 * 获取RSA对象
			 */
			getRsa : function(bitLength) {
				var passPhrase = Math.random().toString();
				return cryptico.generateRSAKey(passPhrase, bitLength);
			},
			/**
			 * 加密
			 */
			
		};
	}]);
});