define([ 'cms/module', 'common/context' ], function(cmsModule) {
	return cmsModule.factory('cmsService', [ '$http', 'util', function($http, util) {
		return {
			/**
			 * 获取资源管理的根目录的数据结构
			 */
			getFileRoot : function() {
				return $http.get('fileUploadServer/root');
			},
			
			/**
			 * 创建一个目录
			 */
			createDir : function(dirName) {
				return $http.post('fileUploadServer/createDir', {dirName : dirName});
			},
			
			/**
			 * 为目录或文件改名
			 */
			reName : function(srcName, destName) {
				return $http.post('fileUploadServer/reName', {srcName : srcName, destName : destName});
			},
			
			/**
			 * 删除目录或文件
			 */
			'delete' : function(filename) {
				return $http({
					method : 'POST',
					url  : 'fileUploadServer/delete',
					data : 'filename=' + encodeURIComponent(filename),
					headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8' }
				});
			},
			
		};
	}]);
});