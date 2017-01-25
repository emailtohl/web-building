define(['encryption/module', 'common/context' ], function(encryptionModule) {
	return encryptionModule.factory('encryptionService', [ '$http', 'util', function($http, util) {
		return {
			uploadPublicKey : function(publicKey, module) {
				return $http.post('encryption/publicKey', {publicKey : publicKey, module : module});
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