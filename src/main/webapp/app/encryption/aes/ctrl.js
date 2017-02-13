define(['encryption/module', 'common/service/myaes', 'encryption/service'], function(encryptionModule, myaes) {
	return encryptionModule
	.controller('AesCtrl', ['$scope', '$http', '$state', 'encryptionService', 'util',
	                                function($scope, $http, $state, service, util) {
		var self = this;
		
		if (localStorage.aesKey) {
			self.isLocalStorage = true;
			self.aesKey = localStorage.aesKey;
		} else {
			self.isLocalStorage = false;
		}
		
		self.getKey = function() {
			self.aesKey = myaes.getKey().join(',');
			if (confirm('是否将密钥存入浏览本地缓存中？注意：请确保使用环境的安全！')) {
				self.isLocalStorage = true;
				localStorage.aesKey = self.aesKey;
			} else {
				self.isLocalStorage = false;
				delete localStorage.aesKey;
			}
		};
		self.uploadAesKey = function() {
			
		};
		self.cleanLocalStorage = function() {
			delete localStorage.aesKey;
			self.isLocalStorage = false;
		};
		
		service.testMessage().success(function(data) {
			
		});
		
		self.plaintextChange = function() {
			self.ciphertext = myaes.encrypt(self.plaintext, self.aesKey.split(','));
		};
		
		self.ciphertextChange = function() {
			self.plaintext = myaes.decrypt(self.ciphertext, self.aesKey.split(','));
		};
		
		self.plaintext = "滚滚长江东逝水，浪花淘尽英雄。\r\n" + 
		"是非成败转头空。\r\n" + 
		"青山依旧在，几度夕阳红。\r\n" + 
		"白发渔樵江渚上，惯看秋月春风。\r\n" + 
		"一壶浊酒喜相逢。\r\n" + 
		"古今多少事，都付笑谈中。";
		
		if (self.aesKey)
			self.ciphertext = myaes.encrypt(self.plaintext, self.aesKey.split(','));
		else
			self.ciphertext = '';
	}])
	;
});