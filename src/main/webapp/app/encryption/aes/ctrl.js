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
		
	}])
	;
});