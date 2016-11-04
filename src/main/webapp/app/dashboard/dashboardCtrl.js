define(['angular', 'dashboard/module', 'sparkline', 'knob'], function(angular) {
	return angular.module('dashboardModule')
	.controller('DashboardCtrl', ['$scope', '$http', function($scope, $http) {
		var self = this;
		// AdminLTE dashboard demo (This is only for demo purposes)
		// AdminLTE for demo purposes
		require(['dashboard/dashboard2', 'dashboard/demo'], function() {});
		
		self.chatlist = [];
		
		$scope.getAuthentication(function(data) {
			var callee = arguments.callee;
			if (!data || !data.username) {
				return;
			}
			var url = 'ws://' + window.location.host + '/building/chat/' + data.username;
			var connection = new WebSocket(url);
			
			connection.onopen = function() {
				console.log('打开连接');
			};
			
			connection.onmessage = function(e) {
				var data = JSON.parse(e.data);
				var time = (new Date(data.timestamp.seconds)).toString();
				$scope.$apply(function() {
					self.chatlist.push({
						name : data.user,
						timestamp : data.timestamp,
						message : data.userContent,
						time : time
					});
				});
			};
			
			connection.onclose = function(e) {
		    	console.log('WebSocketClosed! ' + e.data);
		    }

			connection.onerror = function(e) {
		    	alert('WebSocketError! ' + e.data);
		    }
			
			self.send = function() {
		    	if (connection.readyState != WebSocket.OPEN) {
					alert('WebSocket is Not Open, current state is： ' + connection.readyState);
					callee(data);
					return;
				}
		    	connection.send(self.message); // 通过套接字传递该内容
		    	self.message = '';
			};
			
			
			$(".knob").knob().trigger('change');
		});
	}]);
});