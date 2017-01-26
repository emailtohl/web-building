define(['encryption/module',
        'encryption/rsa/ctrl',
        'encryption/aes/ctrl',
        ], function(encryptionModule) {
	return encryptionModule.config(function($stateProvider) {
		$stateProvider
		.state('encryption', {
			'abstract' : 'true',
			url : '/encryption',
			template : '<div ui-view></div>'
		})
		.state('encryption.rsa', {
			url : '/rsa',
			templateUrl : 'app/encryption/rsa/template.html',
			controller : 'RsaCtrl as ctrl'
		})
		.state('encryption.aes', {
			url : '/aes',
			templateUrl : 'app/encryption/aes/template.html',
			controller : 'AesCtrl as ctrl'
		})
		;
	});
});