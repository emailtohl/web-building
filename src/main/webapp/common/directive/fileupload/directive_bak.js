define([ 'common/module', 'common/service/util' ], function(commonModule) {
	commonModule.directive('upload', [ 'util', function(util) {
		return {
			restrict : 'EA',
			scope : {
				uploadTo : '@',
				whenDone : '&'
			},
			link : function($scope, $element, $attrs) {
				$element.find('[type="submit"]').on('click', function(event) {
					event.preventDefault();
					var element = $element.get(0);
					var formData = util.getFormData(element);
					var xhr = new XMLHttpRequest();
					// 一般来说，xhr.onprogress监控的是下载时的进度
					// xhr.upload.onprogress监控的是上传进度
					if ('onprogress' in xhr.upload) {
						var $p = $element.find('progress');
						if ($p.length === 0) {
							$p = angular.element('<progress class="progress" max="100" value="0"></progress>');
							$element.find('input[type="file"]').after($p);
						}
						xhr.upload.addEventListener('progress', function(event) {
							if (event.lengthComputable) {
//								$p.get(0).value = Math.round(100 * event.loaded / event.total);
								$element.find('progress').each(function(index) {
									this.value = Math.round(100 * event.loaded / event.total);
								});
							}
						}, false);
					}
					xhr.open('POST', $scope.uploadTo, true);
					xhr.onreadystatechange = function() {
						if (xhr.readyState === 4) {
							callback(xhr); // 调用回调函数
						}
					};
					xhr.send(formData);
					/*
					 * <form upload when-done="ctrl.callbackfun(msg)">
					 * 使用时，页面的属性的“when-done”对应“$scope.whenDone”，“ctrl.callbackfun”对应控制器“self.callbackfun”
					 * 形参“msg”对应指令里面的“{msg : xhr.responseText}”
					 */
					function callback(xhr) {
						console.log(xhr.responseText);
						$scope.whenDone({msg : xhr.responseText});
					}
				});
			}
		};
	} ]);
});