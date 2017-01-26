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
			testMessage : function() {
				return $http.get('encryption/testMessage');
			},
		};
	}]);
});