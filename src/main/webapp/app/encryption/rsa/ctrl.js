define(['encryption/module', 'common/service/myrsa', 'encryption/service'], function(encryptionModule, myrsa) {
	return encryptionModule
	.controller('RsaCtrl', ['$scope', '$http', '$state', 'encryptionService', 'util',
	                                function($scope, $http, $state, service, util) {
		var self = this;
		self.bitLength = 1024;
		
		if (localStorage.privateKey) {
			self.isLocalStorage = true;
			self.privateKey = localStorage.privateKey;
			self.publicKey = localStorage.publicKey;
		} else {
			self.isLocalStorage = false;
		}
		
		self.generateKeys = function() {
			var keys = myrsa.getKeyPairs(self.bitLength);
			self.publicKey = keys[0];
			self.privateKey = keys[1];
			if (confirm('是否将密钥存入浏览本地缓存中？注意：请确保使用环境的安全！')) {
				self.isLocalStorage = true;
				localStorage.publicKey = self.publicKey;
				localStorage.privateKey = self.privateKey;
			} else {
				self.isLocalStorage = false;
				delete localStorage.publicKey;
				delete localStorage.privateKey;
			}
		};
		self.uploadPublicKey = function() {
			service.uploadPublicKey(self.publicKey).success(function(data) {
				alert('公钥上传成功');
			});
		};
		self.cleanLocalStorage = function() {
			delete localStorage.publicKey;
			delete localStorage.privateKey;
			self.isLocalStorage = false;
		};
		self.deletePublicKey = function() {
			service.deletePublicKey().success(function(data) {
				
			});
		};
		self.submitEncryption = function() {
			if (self.testMessage && self.serverPublicKey) {
				var ciphertext = myrsa.encrypt(self.testMessage, self.serverPublicKey);
				service.secret(ciphertext).success(function(data) {
					alert('提交成功');
				});
			}
		}
		
		service.getServerPublicKey().success(function(data) {
			self.serverPublicKey = data.serverPublicKey;
		});
		service.testMessage().success(function(data) {
			console.log(data);
			if (self.privateKey && data.ciphertext) {
				self.testMessage = myrsa.decrypt(data.ciphertext, self.privateKey);
			} else {
				self.testMessage = data.ciphertext;
			}
		});
		
		
		test();
		function test() {
			var plaintext = "滚滚长江东逝水，浪花淘尽英雄。\r\n" + 
			"是非成败转头空。\r\n" + 
			"青山依旧在，几度夕阳红。\r\n" + 
			"白发渔樵江渚上，惯看秋月春风。\r\n" + 
			"一壶浊酒喜相逢。\r\n" + 
			"古今多少事，都付笑谈中。";
			var keys = myrsa.getKeyPairs(512);
			var publicKey = keys[0];
			var privateKey = keys[1];
			var c = myrsa.encrypt(plaintext, publicKey);
			console.log(c);
			var r = myrsa.decrypt(c, privateKey);
			console.log(r);
		}
	}])
	;
});