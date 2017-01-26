define(['encryption/module', 'common/service/myrsa', 'encryption/service'], function(encryptionModule, myrsa) {
	return encryptionModule
	.controller('AesCtrl', ['$scope', '$http', '$state', 'encryptionService', 'util',
	                                function($scope, $http, $state, service, util) {
		var self = this;
		self.bitLength = 256;
		self.bitEnum = [{name : '128 bit', value : 128}, {name : '256 bit', value : 256}, {name : '512 bit', value : 512}];
		
		if (localStorage.aeskey) {
			self.isLocalStorage = true;
			self.aeskey = localStorage.aeskey;
		} else {
			self.isLocalStorage = false;
		}
		
		self.generateKeys = function() {
			
		};
		self.uploadAesKey = function() {
			
		};
		self.deleteAesKey = function() {
			
		};
		
		service.testMessage().success(function(data) {
			
		});
		
	}])
	;
});