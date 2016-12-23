define(['forum/module', 'forum/search/service'], function(forumModule) {
	return forumModule
	.controller('ForumSearchCtrl', ['$scope', '$http', '$state', 'forumSearchService',
	                                function($scope, $http, $state, forumSearchService) {
		var self = this;
		$scope.getAuthentication();
		var queryInput = $('form[name="fulltextsearch"]').find('input[name="search"]');
		
		if ($state.params.query && $state.params.query.trim()) {
			forumSearchService.search($state.params.query, 1).success(function(data) {
				self.pager = data;
			});
		} else {
			forumSearchService.getPager(1).success(function(data) {
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
			if (query && query.trim()) {
				forumSearchService.search(query, pageNumber).success(function(data) {
					self.pager = data;
				});
			} else {
				forumSearchService.getPager(pageNumber).success(function(data) {
					self.pager = data;
				});
			}
		};
		
		self.getIconSrc = function(obj) {
			return obj && obj.user && obj.user.iconSrc;
		};
		self['delete'] = function(id, $event) {
			$event.stopPropagation();
			if (confirm('确定删除吗？')) {
				forumSearchService['delete'](id).success(function(data) {
					forumSearchService.getPager(1).success(function(data) {
						self.pager = data;
					});
				});
			}
		};
	}]);
});