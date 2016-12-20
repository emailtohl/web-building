define(['angular', 'dashboard/module', 'sparkline', 'knob'], function(angular) {
	return angular.module('dashboardModule')
	.controller('DashboardCtrl', ['$scope', '$http', '$state', '$cookies', function($scope, $http, $state, $cookies) {
		var self = this, isHttps = window.location.protocol == 'https:' ? true : false;
		// AdminLTE dashboard demo (This is only for demo purposes)
		// AdminLTE for demo purposes
		require(['dashboard/dashboard2', 'dashboard/demo'], function() {});
		self.chatlist = [];
		
		/**
		 * 向获取身份认证方法中注册聊天程序.
		 */
		$scope.getAuthentication(function(data) {
			var callee = arguments.callee;
			if (data && data.username) {
				var url = (isHttps ? 'wss://' : 'ws://') + window.location.host + '/building/chat/' + data.username;
				var connection = new WebSocket(url);
				
				connection.onopen = function(e) {
					console.log('打开聊天连接');
				};
				
				connection.onmessage = function(e) {
					var data = JSON.parse(e.data);
					var time = (new Date(data.timestamp.seconds)).toString();
					$scope.$apply(function() {
						self.chatlist.push({
							name : data.user,
							timestamp : data.timestamp,
							message : data.userContent,
							time : time,
							iconSrc : data.iconSrc
						});
					});
					
					// 划动到底部
					var container = $('.direct-chat-messages');
					var h = container.scrollParent().height() + container.height();
					container.scrollTop(h);
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
			    	var msg = JSON.stringify({
			    		message : self.message,
			    		iconSrc : $scope.getIconSrc()
			    	});
			    	connection.send(msg); // 通过套接字传递该内容
			    	self.message = '';
				};
			}
		});
		
		
		/**
		 * 获取系统信息
		 */
		self.systemInfo = {};
		(function SystemInfo() {
			var url = (isHttps ? 'wss://' : 'ws://') + window.location.host + '/building/systemInfo';
			var connection = new WebSocket(url);
			var $knob = $(".knob"), isCreated = false;
			
			connection.onopen = function(e) {
				console.log('打开系统信息连接');
			};
			
			var cpuPoints = [], $cpu = $('span#cpuInfo'), memoryPoints = [], $memory = $('span#memoryInfo'), swapPoints = [], $swap = $('span#swapInfo'), mpoints_max = 30;
			connection.onmessage = function(e) {
				if (!$state.includes('dashboard'))
					return;
				var data = JSON.parse(e.data);
				if (data.getFreePhysicalMemorySize && data.getTotalPhysicalMemorySize) {
					self.systemInfo.memory = (data.getFreePhysicalMemorySize / data.getTotalPhysicalMemorySize) * 100;
					memoryPoints.push(self.systemInfo.memory);
					if (memoryPoints.length > mpoints_max)
						memoryPoints.splice(0, 1);
					$memory.sparkline(memoryPoints);
				}
				if (data.getFreeSwapSpaceSize && data.getTotalSwapSpaceSize) {
					self.systemInfo.swap = (data.getFreeSwapSpaceSize / data.getTotalSwapSpaceSize) * 100;
					swapPoints.push(self.systemInfo.swap);
					if (swapPoints.length > mpoints_max)
						swapPoints.splice(0, 1);
					$swap.sparkline(swapPoints);
				}
				if (data.getSystemCpuLoad) {
					self.systemInfo.cpu = data.getSystemCpuLoad * 100;
					cpuPoints.push(self.systemInfo.cpu);
					if (cpuPoints.length > mpoints_max)
						cpuPoints.splice(0, 1);
					$cpu.sparkline(cpuPoints);
				}
				$scope.$apply(function() {
					if (isCreated) {
						$knob.trigger('change');
					} else {
						$knob.knob().trigger('change');
						isCreated = true;
					}
				});
			};
			
			connection.onclose = function(e) {
		    	console.log('WebSocketClosed! ' + e.data);
		    }

			connection.onerror = function(e) {
		    	alert('WebSocketError! ' + e.data);
		    }
		})();
		
		
		
		
		  /**
		   ** Draw the little mouse speed animated graph
		   ** This just attaches a handler to the mousemove event to see
		   ** (roughly) how far the mouse has moved
		   ** and then updates the display a couple of times a second via
		   ** setTimeout()
		   **/
		  (function drawMouseSpeedDemo() {
		    var mrefreshinterval = 500; // update display every 500ms
		    var lastmousex = -1;
		    var lastmousey = -1;
		    var lastmousetime;
		    var mousetravel = 0;
		    var mpoints = [];
		    var mpoints_max = 30;
		    $('html').mousemove(function (e) {
		      var mousex = e.pageX;
		      var mousey = e.pageY;
		      if (lastmousex > -1) {
		        mousetravel += Math.max(Math.abs(mousex - lastmousex), Math.abs(mousey - lastmousey));
		      }
		      lastmousex = mousex;
		      lastmousey = mousey;
		    });
		    var mdraw = function () {
		      var md = new Date();
		      var timenow = md.getTime();
		      if (lastmousetime && lastmousetime != timenow) {
		        var pps = Math.round(mousetravel / (timenow - lastmousetime) * 1000);
		        mpoints.push(pps);
		        if (mpoints.length > mpoints_max)
		          mpoints.splice(0, 1);
		        mousetravel = 0;
		        $('#mousespeed').sparkline(mpoints, {width: mpoints.length * 2, tooltipSuffix: ' pixels per second'});
		      }
		      lastmousetime = timenow;
		      setTimeout(mdraw, mrefreshinterval);
		    };
		    // We could use setInterval instead, but I prefer to do it this way
		    setTimeout(mdraw, mrefreshinterval);
		  })();
	}]);
});