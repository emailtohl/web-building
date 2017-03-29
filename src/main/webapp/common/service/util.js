/**
 * 工具类，提供基础功能
 * @author HeLei
 * @date 2017.02.04
 */
define([ 'common/module' ], function(commonModule) {
	function Promise() {
		var successFun, errorFun, self = this;
		this.success = function(callback) {
			if (typeof(callback) !== 'function')
				throw new TypeError('请传入成功后的回调函数');
			successFun = callback;
			return self;
		};
		this.error = function(callback) {
			if (typeof(callback) !== 'function')
				throw new TypeError('请传入失败后的回调函数');
			errorFun = callback;
			return self;
		};
		this.invokeSuccess = function(data) {
			if (successFun)
				successFun(data);
			return self;
		};
		this.invokeError = function(data) {
			if (errorFun)
				errorFun(data);
			return self;
		};
		// 用于文件上传时的中断
		this.abort = false;
	}
	return commonModule.factory('util', [ function() {
		return {
			loadasync : function(url, onload) {
				var partition, suffix, head, promise = new Promise();
				function callback(e) {
					if (onload instanceof Function)
						onload(e);
					promise.invokeSuccess(e);
				}
				function loadasync_js(url) {
					var script = document.createElement('script');
					script.onload = callback;
					script.src = url;
					var exist = head.querySelector('script[src="' + url + '"]');
					if (exist)
						head.removeChild(exist);
					head.appendChild(script);
				}
				function loadasync_css(url) {
					var css = document.createElement('link');
					css.onload = callback;
					css.href = url;
					css.rel = "stylesheet";
					css.type = "text/css";
					var exist = head.querySelector('link[href="' + url + '"]');
					if (exist)
						head.removeChild(exist);
					head.appendChild(css);
				}
				partition = url && url.search(/.\w+$/);
				if (!partition) {
					// 如果url是空，或者没找到“.”分割的文件，又或者“.”在第一位，都视为非法的地址，直接return
					return;
				}
				head = document.getElementsByTagName('head')[0];
				suffix = url.slice(partition + 1);
				if (suffix === 'js') {
					loadasync_js(url);
				} else if (suffix === 'css') {
					loadasync_css(url)
				}
				return promise;
			},
			/**
			 * 将对象的名值对转成URL请求参数 可用于GET请求，也可以用于POST的requestBody，不过注意要将XMLHttpRequest对象的请求头设置为
			 * 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
			 */
			encodeUrlParams : function(data) {
				var pairs, name, value;
				if (!data)
					return '';
				pairs = [];
				for (name in data) {
					if (!data.hasOwnProperty(name))
						continue;
					if (!data[name] || typeof data[name] === 'function')
						continue;
					value = data[name].toString();
					name = encodeURIComponent(name);
					value = encodeURIComponent(value);
					pairs.push(name + '=' + value);
				}
				return pairs.join('&');
			},
			/**
			 * 使用POST方法发送multipart/form-data请求主体
			 */
			postFormData : function(url, data, callback) {
				if (typeof FormData === 'undefined')
					throw new Error('FormData is not implemented');

				var request = new XMLHttpRequest();
				request.open('POST', url);
				request.onreadystatechange = function() {
					if (request.readyState === 4 && callback) // 当响应完成时
						callback(request); // 调用回调函数
				};
				var formdata = new FormData();
				for ( var name in data) {
					if (!data.hasOwnProperty(name))
						continue; // 跳过继承的属性
					var value = data[name];
					if (typeof value === 'function')
						continue; // 跳过方法
					// 每个属性作为request的一部分，这里还允许添加文件对象
					formdata.append(name, value); // 以名/值对添加进formdata
				}
				// 在multipart/form-data请求主体中发送名/值对，每对都是请求的一部分
				// 注意，当传入FormData对象时，send()方法会自动设置Content-Type头信息
				request.send(formdata);
			},
			/**
			 * 传入一个dom元素，本方法将查询该元素下含有name属性的子元素，并创建出一个formData对象
			 * 注意：表单值经过encodeURIComponent编码，到服务器端需解码，如使用java的URLDecode.decode(value, "UTF-8")
			 */
			getFormData : function(element) {
				if (typeof FormData === 'undefined') {
					throw new Error('FormData is not implemented');
				}
				var eles, e, i, j, name, value, opts, files, formdata = new FormData();
				eles = element.querySelectorAll('[name]');
				for (i = 0; i < eles.length; i++) {
					e = eles[i];
					if (e.value == null) {
						continue; // 注意空字符串这种情况
					}
					if (('radio' === e.type || 'checkbox' === e.type) && !e.checked) {
						continue;
					}
					if ('select-multiple' === e.type) {
						opts = e.querySelectorAll('option');
						for (j = 0; j < opts.length; j++) {
							if (opts[j].selected) {
								name = encodeURIComponent(e.name.replace(/\s/g, '+'));// 在URL中空格用“+”代替
								value = encodeURIComponent(opts[j].value.replace(/\s/g, '+'));
								formdata.append(name, value);
							}
						}
					} else if ('file' === e.type) {
						name = encodeURIComponent(e.name.replace(/\s/g, '+'));// 在URL中空格用“+”代替
						files = e.files;
						if (files) {
							if (files.length > 1) {
								for (j = 0; j < files.length; j++) {
									formdata.append(name + '_' + j, e.files[j]);
								}
							} else if (files.length === 1) {
								formdata.append(name, e.files[0]);
							}
						}
					} else {
						name = encodeURIComponent(e.name.replace(/\s/g, '+'));// 在URL中空格用“+”代替
						value = encodeURIComponent(e.value.replace(/\s/g, '+'));
						formdata.append(name, value);
					}
				}
				return formdata;
			},
			/**
			 * 在浏览器本地添加cookie值
			 */
			setCookie : function(c_name, value, expiredays) {
				var exdate = new Date();
				exdate.setDate(exdate.getDate() + expiredays);
				document.cookie = c_name + '=' + encodeURIComponent(value) + ((expiredays == null) ? '' : '; expires=' + exdate.toGMTString());
			},
			/**
			 * 根据cookie名获取cookie值
			 */
			getCookie : function(c_name) {
				if (document.cookie.length > 0) {
					c_start = document.cookie.indexOf(c_name + '=');
					if (c_start != -1) {
						c_start = c_start + c_name.length + 1;
						c_end = document.cookie.indexOf(';', c_start);
						if (c_end == -1) {
							c_end = document.cookie.length;
						}
						return decodeURIComponent(document.cookie.substring(c_start, c_end));
					}
				}
				return '';
			},
			/**
			 * 以对象名值对形式返回document的cookies
			 */
			getCookies : function() {
				var cookies = {};
				var all = document.cookie;
				if (all === '')
					return cookies;
				var list = all.split('; ');
				for (var i = 0; i < list.length; i++) {
					var cookie = list[i];
					var p = cookie.indexOf("=");
					var name = cookie.substring(0, p);
					var value = cookie.substring(p + 1);
					value = decodeURIComponent(value);
					cookies[name] = value;
				}
				return cookies;
			},
			/**
			 * 在cookie中获取Spring security写入cookie中的token
			 */
			getCsrfToken : function() {
				return this.getCookie('XSRF-TOKEN');
			},
		};
	} ]);
});