define(['angular', 'uirouter', 'common/context', 'angular-file-uploader', 'angular-cookies'], function(angular) {
	return angular.module('testModule', ['ui.router', 'commonModule', 'angularFileUpload', 'ngCookies']);
});