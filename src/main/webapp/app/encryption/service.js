define(['encryption/module', 'common/context' ], function(encryptionModule) {
	return encryptionModule.factory('encryptionService', [ '$http', 'util', function($http, util) {
		return {
			uploadPublicKey : function(publicKey, module) {
				return $http({
					method : 'POST',
					url  : 'encryption/publicKey',
					data : 'publicKey=' + publicKey,
					headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8' }
				});
			},
			deletePublicKey : function() {
				return $http['delete']('encryption/publicKey');
			},
			getServerPublicKey : function() {
				return $http.get('encryption/serverPublicKey');
			},
			secret : function(ciphertext) {
				return $http({
					method : 'POST',
					url  : 'encryption/secret',
					data : 'ciphertext=' + ciphertext,
					headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8' }
				});
			},
			testMessage : function() {
				return $http.get('encryption/testMessage');
			},
		};
	}]);
});