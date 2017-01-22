/**
 * 我的工具箱，不依赖任何库
 * 需要注意的是，使用了很多HTML5特性，不支持低版本浏览器
 * version v2.0
 * author HeLei
 * date 2016.06.25
 */
var mine = (function(){
	/**
	 * o是一个将要被扩展的对象，该方法接收多个参数，并把所有的参数属性复制进o对象的属性中
	 */
	function extend(o) {
		for (var i = 1; i < arguments.length; i++) {// 将第二个及以后的参数合并到第一个参数对象中
			var source = arguments[i];
			for (var prop in source)
				o[prop] = source[prop];
		}
		return o;
	}
	
	/**
	 * 用于回调函数中
	 */
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
	
	/**
	 * 判断AJAX状态为OK
	 */
	function ajaxok(status) {
		return (status >= 200 && status < 300) || status == 304;
	}
	
	/**
	 * 注册进度监听器
	 * xhr：XMLHttpRequest对象， progressListener：用户传入的进度监听回调函数， promise：若它的abort被改为true，则终止AJAX
	 */
	function registerProgressListener(xhr, progressListener, promise) {
		function percent(e) {
			if (e.lengthComputable) {
				progressListener(Math.round(100 * e.loaded / e.total));
			}
			// 若在进度过程中中断了上传，则中止AJAX
			if (promise.abort) {
				xhr.abort();
			}
		}
		// 一般来说，xhr.onprogress监控的是下载时的进度
		// xhr.upload.onprogress监控的是上传进度
		if ('onprogress' in xhr)
			xhr.addEventListener('progress', percent, false);
		if ('onprogress' in xhr.upload)
			xhr.upload.addEventListener('progress', percent, false);
	}
	
	/**
	 * 传入参数对象，调用ajax请求
	 */
	function ajax(options) {
		var xhr, configuration, promise, prop, useMethod;
		xhr  = new XMLHttpRequest();
		promise = new Promise();
		// 默认配置项
		configuration = {
			url : '',
			requestBody : {},
			/*
			 * 默认以POST方式发送请求，除此之外还可以使用GET/PUT/DELETE/HEAD/OPTIONS
			 * 其中，POST、PUT（PATCH与PUT类似，区别是PUT是“替换”，PATCH是“修改”）有请求体
			 */
			method : 'POST',
			/*
			 * "Content-Type"表示post打包的数据格式
			 * 默认以表单方式提交数据："application/x-www-form-urlencoded;charset=utf-8"
			 * 如果需要上传二进制文件可改为"multipart/form-data"，相当于表单中的enctype="multipart/form-data"
			 */
			headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'},
			overrideMimeType : null, // 让XMLHttpRequest忽略“Content-Type”头而使用指定的类型，如：'text/plain; charset=utf-8'
			async : true, // 如果下载完html后需执行某些操作，可以使用同步方式
			callBack : null,
			progressListener : null //进度监听器
		};
		// 将默认配置扩展到自定义配置
		extend(configuration, options);
		// 关于传输进度的
		if (typeof configuration.progressListener === 'function') {
			registerProgressListener(xhr, configuration.progressListener, promise);
		}
		// 打开连接
		xhr.open(configuration.method, configuration.url, configuration.async);
		// 监听网络状态变化
		xhr.onreadystatechange = function() {
			if (xhr.readyState === XMLHttpRequest.DONE) {
				if (typeof configuration.callBack === 'function')
					configuration.callBack(xhr);
				if (ajaxok(xhr.status))
					promise.invokeSuccess(xhr);
				else
					promise.invokeError(xhr);
			}
		};
		// 在发送前设置http头信息
		for (prop in configuration.headers) {
			if (configuration.headers.hasOwnProperty(prop))
				xhr.setRequestHeader(prop, configuration.headers[prop]);
		}
		if (configuration.overrideMimeType)
			xhr.overrideMimeType(configuration.overrideMimeType);
		
		// 发送请求
		useMethod = configuration.method.toUpperCase();
		if (useMethod === 'POST' || useMethod === 'PUT')
			xhr.send(configuration.requestBody);
		else
			xhr.send(null);
		
		return promise;
	}
	
	/**
	 * 发起HTTP GET请求
	 * 若不传入callback，可使用返回的promise对象
	 * 注意：采用callback方式需要自行判断访问出错或异常的情况
	 */
	function get(url, callback) {
		var xhr = new XMLHttpRequest(), promise = new Promise();
		xhr.open('GET', url);
		xhr.onreadystatechange = function() {
			if (xhr.readyState !== XMLHttpRequest.DONE)
				return;
			if (typeof callback === 'function')
				callback(xhr);
			if (ajaxok(xhr.status)) {
				promise.invokeSuccess(xhr);
			} else {
				promise.invokeError(xhr);
			}
		};
		xhr.send(null);
		return promise;
	}
	
	/**
	 * 发起HTTP POST请求
	 * 若不传入callback，可使用返回的promise对象
	 * progressListener是一个回调函数，主要用于显示文件上传进度
	 * 注意：采用callback方式需要自行判断访问出错或异常的情况
	 */
	function post(url, data, callback, progressListener) {
		var xhr = new XMLHttpRequest(), promise = new Promise(), formData, name, value;
		if (data.localName === 'form') {
			formData = new FormData(data);
		} else if (data instanceof FormData) {
			formData = data;
		} else {
			formData = new FormData();
			if (data && typeof data === 'object') {
				for (name in data) {
					if (!data.hasOwnProperty(name))
						continue; // 跳过继承的属性
					value = data[name];
					if (typeof value === 'function')
						continue; // 跳过方法
					// 每个属性作为request的一部分，这里还允许添加文件对象
					formData.append(name, value); // 以名/值对添加进formData
				}
			}
		}
		// 关于传输进度的
		if (typeof progressListener === 'function') {
			registerProgressListener(xhr, progressListener, promise);
		}
		xhr.open('POST', url); 
		xhr.onreadystatechange = function() {
			if (xhr.readyState !== XMLHttpRequest.DONE)
				return;
			if (typeof callback === 'function')
				callback(xhr);
			if (ajaxok(xhr.status)) {
				promise.invokeSuccess(xhr);
			} else {
				promise.invokeError(xhr);
			}
		};
		/*
		 * 使用formData对象不需要再设置
		 * xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		 */ 
		xhr.send(formData);
		return promise;
	}
	
	/**
	 * 使用JSON编码主体来发起HTTP POST
	 * 若不传入callback，可使用返回的promise对象
	 */
	function postJSON(url, json, callback) {
		var xhr = new XMLHttpRequest(), promise = new Promise();
		xhr.open("POST", url);
		xhr.onreadystatechange = function() {
			if (xhr.readyState !== XMLHttpRequest.DONE)
				return;
			if (typeof callback === 'function')
				callback(xhr);
			if (ajaxok(xhr.status)) {
				promise.invokeSuccess(xhr);
			} else {
				promise.invokeError(xhr);
			}
		};
		xhr.setRequestHeader("Content-Type", "application/json");
		xhr.send(json);
		return promise;
	}
	
	/**
	 * 将对象的名值对转成URL请求参数 可用于GET请求，也可以用于POST的requestBody，不过注意要将XMLHttpRequest对象的请求头设置为
	 * 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	 */
	function encodeUrlParams(data) {
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
	}
	
	/**
	 * 分析表单dom元素，然后对转成URL请求参数
	 */
	function serialize(form) {
		var parts = [], field = null, i, len, j, optLen, option, optValue;
		for (i = 0, len = form.elements.length; i < len; i++) {
			field = form.elements[i];
			switch (field.type) {
			case 'select-one':
			case 'select-multiple':
				if (field.name.length) {
					for (j = 0, optLen = field.options.length; j < optLen; j++) {
						option = field.options[j];
						if (option.selected) {
							optValue = '';
							if (option.hasAttribute) {
								optValue = (option.hasAttribute('value') ? option.value
										: option.text);
							} else {
								optValue = (option.attributes['value'].specified ? option.value
										: option.text);
							}
							parts.push(encodeURIComponent(field.name) + '='
									+ encodeURIComponent(optValue));
						}
					}
				}
				break;
			case undefined: // 字段集
			case 'file': // 文件输入
			case 'submit': // 提交按钮
			case 'reset': // 重置按钮
			case 'button': // 自定义按钮
				break;
			case 'radio': // 单选按钮
			case 'checkbox': // 复选框
				if (!field.checked) {
					break;
				}
				/* 执行默认操作 */
			default:
				// 不包含没有名字的表单字段
				if (field.name.length) {
					parts.push(encodeURIComponent(field.name) + '='
							+ encodeURIComponent(field.value));
				}
			}
		}
		return parts.join('&');
	}	
	
	/**
	 * 分析表单dom元素，然后对转成FormData对象
	 */
	function getFormData(form) {
		var formData = new FormData(), field = null, i, len, j, optLen, option, optValue, files;
		for (i = 0, len = form.elements.length; i < len; i++) {
			field = form.elements[i];
			switch (field.type) {
			case 'select-one':
			case 'select-multiple':
				if (field.name.length) {
					for (j = 0, optLen = field.options.length; j < optLen; j++) {
						option = field.options[j];
						if (option.selected) {
							optValue = '';
							if (option.hasAttribute) {
								optValue = (option.hasAttribute('value') ? option.value
										: option.text);
							} else {
								optValue = (option.attributes['value'].specified ? option.value
										: option.text);
							}
							formData.append(field.name, optValue);
						}
					}
				}
				break;
			case undefined: // 字段集
			case 'file': // 文件输入
				// 不包含没有名字的表单字段
				if (field.name.length) {
					files = field.files;
					if (files) {
						if (files.length > 1) {
							for (j = 0; j < files.length; j++) {
								// 若有多个文件，就已文件名作为name
								formData.append(files[j].name, files[j]);
							}
						} else if (files.length === 1) {
							formData.append(field.name, files[0]);
						}
					}
				}
				break;
			case 'submit': // 提交按钮
			case 'reset': // 重置按钮
			case 'button': // 自定义按钮
				break;
			case 'radio': // 单选按钮
			case 'checkbox': // 复选框
				if (!field.checked) {
					break;
				}
				/* 执行默认操作 */
			default:
				// 不包含没有名字的表单字段
				if (field.name.length) {
					formData.append(field.name, field.value);
				}
			}
		}
		return formData;
	}
	
	/**
	 * 在浏览器本地添加cookie值
	 */
	function setCookie(c_name, value, expiredays) {
		var exdate = new Date();
		exdate.setDate(exdate.getDate() + expiredays);
		document.cookie = c_name + '=' + encodeURIComponent(value) + ((expiredays == null) ? '' : '; expires=' + exdate.toGMTString());
	}
	
	/**
	 * 根据cookie名获取cookie值
	 */
	function getCookie(c_name) {
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
	}
	
	/**
	 * 以对象名值对形式返回document的cookies
	 */
	function getCookies() {
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
	}
	
	/**
	 * 浅克隆一个新的对象，参数是一个对象，而非构造函数
	 */
	function object(p) {
		if (p == null)
			throw TypeError();
		if (Object.create)
			return Object.create(p);
		var t = typeof p;
		if (t !== 'object' && t !== 'function')
			throw TypeError();
		function F() {
		}
		;
		F.prototype = p;
		return new F();
	}
	
	/**
	 * 继承一个构造函数，参数是函数，而非对象
	 */
	function inherit(SuperType, args) {
		if (typeof SuperType !== 'function')
			throw TypeError();
		function SubType() {
		}
		SubType.prototype = new SuperType(args);
		SubType.prototype.constructor = SubType;
		return SubType;
	}
	
	/**
	 * 获取参数的类型的字符串表示
	 */
	function type(o) {
	    var t, c, n;  // type, class, name

	    // 特殊情况，是null时:
	    if (o === null) return 'null';

	    // 另外一种特殊情况: NaN 是唯一一个自身不相等的值:
	    if (o !== o) return 'nan';

	    // 除对象（"object"）外，typeof能返回该参数的类型：
	    // "undefined"、"function"、"string"、"boolean"、"number"
	    // 这可识别出原始值的类型和函数
	    if ((t = typeof o) !== 'object') return t;

	    // 根据Object原型的toString信息获取该类的名字
		function classof(o) {
		    return Object.prototype.toString.call(o).slice(8,-1);
		};
	    // 返回对象的类名，除非值为"Object"
	    // 这种方式可以识别出大多数的内置对象
	    if ((c = classof(o)) !== 'Object') return c;

	    // 到了这一步还没查出对象的类型的话，就尝试从其构造函数中查找信息
	    if (o.constructor && typeof o.constructor === 'function' &&
	        (n = o.constructor.getName())) return n;

	    // 其他类型均无法识别，一律返回"Object"
	    return 'Object';
	}
	
	/**
	 * 传递函数给whenReady()，作为一个注册的函数，当文档解析完毕且操作准备就绪时便执行
	 * 注册函数将作为document的方法调用，所以注册函数中的this引用的就是document
	 * DOMContentLoad，readystatechange或load事件发生时会触发注册函数
	 * 一旦文档准备就绪，所有函数将被调用，这个时候，任何传递给whenReady()的函数都将立即调用
	 */
	var whenReady = (function(f) { // 这个函数返回whenReady()函数
		var funcs = []; // 注册进来的函数队列，一旦获得DOMContentLoad，readystatechange或load事件，则全部执行
		var ready = false;
		// 当document准备就绪时，调用此事件处理程序
		function handler(e) {
			// 如果已经运行过一次，则只需返回
			if (ready)
				return;
			// 如果发生readystatechange事件，但状态不是“complete”的话，那么document尚未准备好
			if (e.type === 'readystatechange' && document.readyState !== 'complete')
				return;
			/*
			 * 运行所有被注册进来的函数 注意每次都需计算funcs.length，这是为了防止注册函数在调用中会导致注册更多的函数
			 */
			for (var i = 0; i < funcs.length; i++)
				funcs[i].call(document); // 作为document的方法被调用，相当于document.funcs[i]()
			// 现在设置ready标识为true，并移除所有函数
			ready = true;
			funcs = null;
		}
		// 将handler(e)函数设置为DOMContentLoad，readystatechange和load事件的监听程序
		if (document.addEventListener) {
			document.addEventListener('DOMContentLoaded', handler, false);
			document.addEventListener('readystatechange', handler, false);
			window.addEventListener('load', handler, false);
		} else if (document.attachEvent) {
			document.attachEvent('onreadystatechange', handler);
			window.attachEvent('onload', handler);
		}
		// 最后返回whenReady函数
		return function(f) {
			if (ready)
				f.call(document); // 如果document的状态是已完成，则立即执行
			else
				funcs.push(f); // 否则添加进注册函数的队列，等待document的状态完成之后再执行
		}
	}());
	
	/**
	 * 与whenReady相似，用于兼容那些不支持DOMContentLoad，readystatechange和load事件的老式浏览器
	 * 如果文档已经加载完毕了，那么直接用window.setTimeout异步执行 如果文档还在加载中就调用了本方法，则需等到文档加载完毕后执行
	 */
	var onLoad = (function() {
		function onLoad(f) {
			if (onLoad.loaded)
				window.setTimeout(f, 0);
			else if (window.addEventListener)
				window.addEventListener('load', f, false);
			else if (window.attachEvent)
				window.attachEvent('onload', f);
		}
		onLoad.loaded = false; // 此时还在文档还处于加载期，故先将load属性定义为false，防止此时被其他地方调用
		onLoad(function() {
			onLoad.loaded = true;
		}); // 注册一个函数，一旦被注册的函数被调用，标记就得到改变
		return onLoad;
	}());
	
	/**
	 * 根据指定表格每行第n个单元格的值，对第一个<tbody>中的行进行排序
	 * 如果存在comparator函数则使用它，否则按字母表顺序排序
	 */
	function sortrows(table, n, comparator) {
		var tbody = table.tBodies[0]; // 第一个 <tbody>; 可能是隐式创建
		var rows = tbody.getElementsByTagName('tr'); // 选择该<tbody>中的所有行
		rows = Array.prototype.slice.call(rows, 0); // 使用数组的方法返回一个新数组（快照）用于操作，不至于原先行顺序被改变
		
		// 基于第n个<td>元素的值进行排序
		rows.sort(function(row1, row2) {
			var cell1, cell2, ele1, ele2, val1, val2;
			cell1 = row1.getElementsByTagName('td')[n]; // 获得第n个单元格
			cell2 = row2.getElementsByTagName('td')[n]; // 两行都是
			ele1 = cell1.querySelector('[value]');// 先探测该单元格中是否有含[value]属性的元素
			if (ele1)
				val1 = ele1.value;
			else
				val1 = cell1.textContent || cell1.innerText; // 获得文本内容
			// 同样的操作
			ele2 = cell2.querySelector('[value]');
			if (ele2)
				val2 = ele2.value;
			else
				val2 = cell2.textContent || cell2.innerText;
			if (comparator)
				return comparator(val1, val2); // 进行比较
			else
				return val1 < val2 ? -1 : (val1 > val2 ? 1 : 0);
		});
		
		/*
		 * 在<tbody>中按它们的顺序把行添加到最后
		 * 这将自动把它们从当前位置移走，故没必要预先删除它们
		 * 如果<tbody>中还有除了<tr>的其他元素，这些节点将会悬浮到顶部位置
		 */
		for (var i = 0; i < rows.length; i++)
			tbody.appendChild(rows[i]);
	}
	
	/**
	 * 查找表格的<th>元素（假设只有一行），让它们可单击，以便以便单击列标题，按该列对行排序
	 */
	function makeSortable(table) {
		var headers = table.getElementsByTagName('th');
		for (var i = 0; i < headers.length; i++) {
			(function(n) { // 注：为了给多个闭包传入参数，注意外部变量不能改变
				headers[i].onclick = function() {
					sortrows(table, n);
				};
			}(i)); // 将i的值赋值给嵌套函数的形参n
		}
	}
	
	/**
	 * 对元素产生抖动效果 第一个参数是元素对象或元素的id 如果第二个参数是函数，以e为参数，它将在动画结束时调用
	 * 第三个参数是指定e抖动的距离，默认是5像素 第四个参数指定抖动多久，默认是500毫秒
	 */
	function shake(e, distance, time, oncomplete) {
		if (typeof e === 'string')
			e = document.querySelector(e);
		if (!time)
			time = 500;
		if (!distance)
			distance = 5;
		var originalStyle = e.style.cssText; // 先保存原有的css样式
		e.style.position = 'relative'; // 使元素e相对定位
		var start = (new Date()).getTime(); // 这是动画开始的时间
		animate(); // 动画开始
		function animate() {
			var now = (new Date()).getTime(); // 获取当前时间
			var elapsed = now - start; // 从动画开始执行以来消耗了多久的时间
			var fraction = elapsed / time; // 消耗时间是动画总时间的百分之几
			if (fraction < 1) {
				/*
				 * 如果动画还未完成 作为动画完成比例的函数，计算e的x位置 使用正弦函数将完成比例乘以4pi 所以，它会往来两次
				 */
				var x = distance * Math.sin(fraction * 4 * Math.PI);
				e.style.left = x + 'px';
				/*
				 * 在25毫秒后或在总时间的最后尝试再次运行函数 目的是为了产生每秒40帧的动画
				 */
				setTimeout(animate, Math.min(25, time - elapsed));
			} else { // 否则动画完成
				e.style.cssText = originalStyle // 最后恢复元素开始时的样式
				if (oncomplete)
					oncomplete(e); // 调用回调函数
			}
		}
	}
	
	/**
	 * 切换css样式
	 */
	function toggle(selector, className) {
		var elements = document.querySelectorAll(selector);
		for (var i = 0; i < elements.length; i++)
			elements[i].classList.toggle(className);
	}
	
	/**
	 * 图片翻转
	 * 创建图片翻转效果，将此模块引入到HTML文件中，然后在任意<img>元素上使用data-rollover属性来指定翻转图片的URL即可，如下所示：
	 * <img src="normal_image.png" data-rollover="rollover_image.png">
	 */
	function rollover() {
		// 所有逻辑都在一个匿名函数中：不定义任何符合
		// 遍历所有图片，查找data-rollover属性
		for (var i = 0; i < document.images.length; i++) {
			var img = document.images[i];
			var rollover = img.getAttribute('data-rollover');
			if (!rollover)
				continue; // 跳过没有data-rollover属性的图片
			// 将翻转的图片先下载下来，浏览器会缓存它
			(new Image()).src = rollover;
			// 定义一个属性来标识默认的图片URL
			img.setAttribute('data-rollout', img.src);
			// 注册事件处理函数来创建翻转效果
			img.onmouseover = function() {
				this.src = this.getAttribute('data-rollover');
			};
			img.onmouseout = function() {
				this.src = this.getAttribute('data-rollout');
			};
		}
	}
	
	/**
	 * 创建页码按钮
	 */
	function createPageItems(nav, totalPage, pageNum) {
		var i, j, ul, prev, next, items = '', promise = new Promise();
		ul = document.createElement('ul');
		ul.className = 'pagination';
		prev = '<li><a href="javascript:void(0)" aria-label="Previous"><span aria-hidden="true">«</span></a></li>';
		next = '<li><a href="javascript:void(0)" aria-label="Next"><span aria-hidden="true">»</span></a></li>'
		for (i = 1; i <= totalPage; i++) {
			items += '<li class="' + (i == pageNum ? 'active' : '') + '" data-pagenum="' + i + '"><a href="javascript:void(0)">' + i + '</a></li>';
		}
		ul.innerHTML = prev + items + next;
		items = ul.getElementsByTagName('li');
		prev = items[0];
		next = items[items.length - 1];
		if (pageNum <= 1) {
			pageNum = 1;
			prev.classList.add('disabled');
		}
		if (pageNum >= totalPage) {
			pageNum = totalPage;
			next.classList.add('disabled');
		}
		for (i = 1; i < items.length - 1; i++) {
			items[i].onclick = function() {
				pageNum = parseInt(this.getAttribute('data-pagenum'));
				promise.invoke(pageNum);
				setActive();
			};
		}
		prev.onclick = function() {
			if (pageNum > 1) {
				pageNum--;
				promise.invoke(pageNum);
				setActive();
			}
		};
		next.onclick = function() {
			if (pageNum < totalPage) {
				pageNum++;
				promise.invoke(pageNum);
				setActive();
			}
		};
		nav.appendChild(ul);
		
		/**
		 * 允许回调的对象
		 */
		function Promise() {
			var callbackfun, self = this;
			this.onclick = function(callback) {
				if (typeof callback !== 'function') {
					throw new TypeError('参数不是function');
				}
				callbackfun = callback;
				return self;
			};
			this.invoke = function(pageNum) {
				if (callbackfun) {
					callbackfun(pageNum);
				}
			};
		}
		/**
		 * 设置激活的样式
		 */
		function setActive() {
			for (var i = 1; i < items.length - 1; i++) {
				if (i == pageNum) {
					items[i].classList.add('active');
				} else {
					items[i].classList.remove('active');
				}
			}
			prev.classList.remove('disabled');
			next.classList.remove('disabled');
			if (pageNum == 1) {
				prev.classList.add('disabled');
			}
			if (pageNum == totalPage) {
				next.classList.add('disabled');
			}
		}
		return promise;
	}
	
	/**
	 * 根据总行数和每页大小计算总页数
	 */
	function countTotalPage(totalRow, pageSize) {
		return (totalRow + pageSize - 1) / pageSize;
	}

	/**
	 * 主要用于ajax回调函数中，当加载了网页片段后，可掉此方法加载<script>标签或<link>，这以文件后缀确定
	 */
	function loadasync(url, onload) {
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
	}
	
	return {
		/**
		 * 参数：o
		 * 说明：o是一个将要被扩展的对象，该方法接收多个参数，并把所有的参数属性复制进o对象的属性中
		 */ 	
		extend : extend,
		/**
		 * 参数：options
		 * 说明：传入参数对象，调用ajax请求
		 */
		ajax : ajax,
		/**
		 * 参数：url, callback
		 * 说明：发起HTTP GET请求
		 */
		get : get,
		/**
		 * 参数：url, data, callback, progressListener
		 * 说明：发起HTTP POST请求
		 */
		post : post,
		/**
		 * 参数：url, json, callback
		 * 说明：使用JSON编码主体来发起HTTP POST
		 */
		postJSON : postJSON,
		/**
		 * 参数：data
		 * 说明：将对象的名值对转成URL请求参数 可用于GET请求，也可以用于POST的requestBody
		 */
		encodeUrlParams : encodeUrlParams,
		/**
		 * 参数：form
		 * 说明：分析表单dom元素，然后对转成URL请求参数
		 */
		serialize : serialize,
		/**
		 * 参数：form
		 * 说明：分析表单dom元素，然后对转成FormData对象
		 */
		getFormData : getFormData,
		/**
		 * 参数：c_name, value, expiredays
		 * 说明：在浏览器本地添加cookie值
		 */
		setCookie : setCookie,
		/**
		 * 参数：c_name
		 * 说明：根据cookie名获取cookie值
		 */
		getCookie : getCookie,
		/**
		 * 参数：无
		 * 说明：以对象名值对形式返回document的cookies
		 */
		getCookies : getCookies,
		/**
		 * 参数：p
		 * 说明：浅克隆一个新的对象，参数是一个对象，而非构造函数
		 */
		object : object,
		/**
		 * 参数：F
		 * 说明：继承一个构造函数，参数是函数，而非对象
		 */
		inherit : inherit,
		/**
		 * 参数：o
		 * 说明：获取参数的类型的字符串表示
		 */
		type : type,
		/**
		 * 参数：f
		 * 说明：document加载完毕后，执行传入的函数
		 */
		whenReady : whenReady,
		/**
		 * 参数：f
		 * 说明：与whenReady相似，用于兼容那些不支持DOMContentLoad，readystatechange和load事件的老式浏览器
		 */
		onLoad : onLoad,
		/**
		 * 参数：table, n, comparator
		 * 说明：根据指定表格每行第n个单元格的值，对第一个<tbody>中的行进行排序
		 */
		sortrows : sortrows,
		/**
		 *  参数：table
		 *  说明：查找表格的<th>元素（假设只有一行），让它们可单击，以便以便单击列标题，按该列对行排序
		 */
		makeSortable : makeSortable,
		/**
		 * 参数：e, distance, time, oncomplete
		 * 说明：对元素产生抖动效果
		 */
		shake : shake,
		/**
		 * 参数：selector, className
		 * 说明：切换css样式
		 */
		toggle : toggle,
		/**
		 * 参数：无
		 * 说明：图片翻转
		 */
		rollover : rollover,
		/**
		 * 参数：nav, totalPage, pageNum
		 * 说明：创建页码按钮
		 */
		createPageItems : createPageItems,
		/**
		 * 参数：totalRow, pageSize
		 * 说明：根据总行数和每页大小计算总页数
		 */
		countTotalPage : countTotalPage,
		/**
		 * 参数：url
		 * 说明：异步下载js文件或css
		 */
		loadasync : loadasync,
	};
	
}());