define(['encryption/module',
        'encryption/key/ctrl',
        ], function(encryptionModule) {
	return encryptionModule.config(function($stateProvider) {
		$stateProvider
		.state('encryption', {
			'abstract' : 'true',
			url : '/encryption',
			template : '<div ui-view></div>'
		})
		.state('encryption.key', {
			url : '/key',
			templateUrl : 'app/encryption/key/template.html',
			controller : 'KeyCtrl as ctrl'
		})
		;
	});
});