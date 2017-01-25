define(['encryption/module', 'common/service/myrsa', 'encryption/service'], function(encryptionModule, myrsa) {
	return encryptionModule
	.controller('KeyCtrl', ['$scope', '$http', '$state', 'encryptionService', 'util',
	                                function($scope, $http, $state, service, util) {
		var self = this;
		self.bitLength = 1024;
		
		if (localStorage.myrsakeys) {
			self.isLocalStorage = true;
			self.myrsakeys = JSON.parse(localStorage.myrsakeys);
		} else {
			self.isLocalStorage = false;
			self.myrsakeys = {};
		}
		
		self.generateKeys = function() {
			self.myrsakeys = myrsa.generateKeys(self.bitLength);
			self.encodePublicKey = myrsa.getEncodePublicKey(keyPairs);
			self.encodePrivateKey = myrsa.getEncodePrivateKey(keyPairs);
			if (confirm('是否将密钥存入浏览本地缓存中？注意：请确保使用环境的安全！')) {
				self.isLocalStorage = true;
				localStorage.encodePublicKey = self.encodePublicKey;
				localStorage.encodePrivateKey = self.encodePrivateKey;
			} else {
				self.isLocalStorage = false;
				delete localStorage.encodePublicKey;
				delete localStorage.encodePrivateKey;
			}
		};
		self.uploadPublicKey = function() {
			service.uploadPublicKey(self.myrsakeys.publicKey, self.myrsakeys.module).success(function(data) {
				alert('公钥上传成功');
			});
		};
		self.deletePublicKey = function() {
			service.deletePublicKey().success(function(data) {
				
			});
		};
		
		service.testMessage().success(function(data) {
			console.log(data);
			self.testMessage = myrsa.decrypt(data.ciphertext, self.myrsakeys.privateKey, self.myrsakeys.module);
			
		});
//		test();
		
		
		function test() {
			var keys = myrsa.generateKeys(512);
			console.log(keys);
			var plaintext = "滚滚长江东逝水，浪花淘尽英雄。\r\n" + 
			"是非成败转头空。\r\n" + 
			"青山依旧在，几度夕阳红。\r\n" + 
			"白发渔樵江渚上，惯看秋月春风。\r\n" + 
			"一壶浊酒喜相逢。\r\n" + 
			"古今多少事，都付笑谈中。";
			var c = myrsa.encrypt(plaintext, keys);
			var c_json = JSON.stringify(c);
			console.log(c_json);
			var r = myrsa.decrypt(JSON.parse(c_json), keys);
			console.log(r);
		}
		
	}])
	;
});