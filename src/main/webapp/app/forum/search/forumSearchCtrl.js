define(['forum/module', 'forum/search/service'], function(forumModule) {
	return forumModule
	.controller('ForumSearchCtrl', ['$scope', '$http', '$state', 'forumSearchService',
	                                function($scope, $http, $state, forumSearchService) {
		var self = this;
		$scope.getAuthentication();
		console.log($state.params);
		if ($state.params.query) {
			forumSearchService.search($state.params.query, 1).success(function(data) {
				self.pager = data;
			});
		} else {
			forumSearchService.get(1).success(function(data) {
				self.pager = data;
			});
		}
		
		self.renderFinish = function() {
			CKEDITOR.replaceAll($('textarea'));
			CKEDITOR.on('instanceReady', function(ev) {
				var editor = ev.editor;
				editor.setReadOnly(true);
			});
		};
		
		self.search = function(pageNumber) {
			forumSearchService.get(pageNumber).success(function(data) {
				self.pager = data;
			});
		};
		
		self.getIconSrc = function(obj) {
			return obj.entity
			&& obj.entity.user
			&& obj.entity.user.iconSrc;
		};
	}]);
});