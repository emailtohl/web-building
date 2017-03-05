define(['cms/module', 'common/context' ], function(cmsModule) {
	return cmsModule.factory('commentService', [ '$http', 'util', function($http, util) {
		return {
			queryComments : function(query, page) {
				var param = {
					page : page,
					query : query,
				};
				param = util.encodeUrlParams(param);
				return $http.get('cms/comments' + (param ? '?' + param : ''));
			},
			findComment : function(id) {
				return $http.get('cms/comment/' + id);
			},
			deleteComment : function(id) {
				return $http['delete']('cms/comment/' + id);
			},
			approvedComment : function(commentId) {
				return $http.post('cms/approvedComment?commentId=' + commentId);
			},
			rejectComment : function(commentId) {
				return $http.post('cms/rejectComment?commentId=' + commentId);
			},
			
		};
	}]);
});