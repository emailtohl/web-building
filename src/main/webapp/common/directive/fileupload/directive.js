/**
 * 文件上传指令
 * 可在上传成功后执行回调，例如在回调中弹出上传成功或失败的模态框
 * author HeLei
 */
define([ 'common/module', 'common/service/util' ], function(commonModule) {
	commonModule.directive('upload', [ 'util', function(util) {
		/**
		 * 在请求中添加csrf令牌
		 * 有的是在请求表单数据中添加，有的是在cookie或其他请求头中添加，或者令牌名都有所不同
		 * 根据后台识别的方式进行添加
		 */
		function csrf(xhr) {
			var token = util.getCookie('XSRF-TOKEN');
			if (token) {
				xhr.setRequestHeader('X-XSRF-TOKEN', token);
			}
		}
		
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
//					var formData = util.getFormData(element);
					var formData = new FormData(element);
					var xhr = new XMLHttpRequest();
					// 一般来说，xhr.onprogress监控的是下载时的进度
					// xhr.upload.onprogress监控的是上传进度
					if ('onprogress' in xhr.upload) {
						var $p = $element.find('.progress-bar');
						if ($p.length === 0) {
							$p = angular.element(
								'<div class="progress">\
					  				<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">0%</div>\
								</div>');
							$element.find('input[type="file"]').after($p);
						}
						xhr.upload.addEventListener('progress', function(event) {
							if (event.lengthComputable) {
								var v = Math.round(100 * event.loaded / event.total);
								$element.find('.progress-bar').each(function(index) {
									$(this).css({width : v + '%'}).attr('aria-valuenow', v).text(v + '%')
									if (v == 100) {
										$(this).removeClass('active').removeClass('progress-bar-striped');
									}
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
					csrf(xhr);
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
				
				// Angular不能实时刷新<input type="file">的onchange事件，需要手动添加
				$element.find('[type="file"]').on('change', function(event) {
					// 强制执行Angular的事件循环更新
					$element.scope().$digest();
				});
			}
		};
	} ]);
});