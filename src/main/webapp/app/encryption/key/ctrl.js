define(['encryption/module', 'encryption/service', 'lib/cryptico/cryptico.min'], function(encryptionModule) {
	return encryptionModule
	.controller('KeyCtrl', ['$scope', '$http', '$state', 'encryptionService', 'util',
	                                function($scope, $http, $state, service, util) {
		var self = this;
		var PassPhrase = 'hello cryptico';
		var Bits = 1024;
		var MattsRSAkey = cryptico.generateRSAKey(PassPhrase, Bits);
		var MattsPublicKeyString = cryptico.publicKeyString(MattsRSAkey);
		console.log(MattsPublicKeyString);
	}])
	;
});