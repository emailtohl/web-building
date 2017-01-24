define(['encryption/module', 'common/service/myrsa', 'encryption/service'], function(encryptionModule, myrsa) {
	return encryptionModule
	.controller('KeyCtrl', ['$scope', '$http', '$state', 'encryptionService', 'util',
	                                function($scope, $http, $state, service, util) {
		var self = this;
		var keys = myrsa.generateKeys(512);
		console.log(keys);
		var plaintext = "滚滚长江东逝水，浪花淘尽英雄。\r\n" + 
		"是非成败转头空。\r\n" + 
		"青山依旧在，几度夕阳红。\r\n" + 
		"白发渔樵江渚上，惯看秋月春风。\r\n" + 
		"一壶浊酒喜相逢。\r\n" + 
		"古今多少事，都付笑谈中。";
		var c = myrsa.crypt(plaintext, keys);
		var c_json = JSON.stringify(c);
		console.log(c_json);
		var r = myrsa.decrypt(JSON.parse(c_json), keys);
		console.log(r);
		
	}])
	;
});