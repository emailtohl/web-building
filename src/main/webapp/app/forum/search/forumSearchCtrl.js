define(['forum/module', 'forum/search/service'], function(forumModule) {
	return forumModule
	.controller('ForumSearchCtrl', ['$scope', '$http', '$state', 'forumSearchService',
	                                function($scope, $http, $state, forumSearchService) {
		var self = this;
		$scope.getAuthentication();
		console.log($state.params);
		var queryInput = $('form[name="fulltextsearch"]').find('input[name="search"]');
		
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
				if (editor.name != 'editor1') {
					editor.setReadOnly(true);
				}
			});
		};
		
		self.search = function(pageNumber) {
			var query = queryInput.val();
			forumSearchService.search(query, pageNumber).success(function(data) {
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