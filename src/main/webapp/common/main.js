/**
 * main.js这个文件做的事情就是：由requirejs异步载入所有文件
 * @author HeLei
 * @date 2017.02.04
 */
require.config({
	/**
	 * baseUrl：所有模块的查找根路径，当加载纯.js文件(依赖字串以/开头，或者以.js结尾，或者含有协议)，不会使用baseUrl。
	 * 如未显式设置baseUrl，则默认值是加载require.js的HTML所处的位置。如果用了data-main属性，则该路径就变成baseUrl。
	 */
	baseUrl : 'app/',
	/**
	 * paths ：path映射那些不直接放置于baseUrl下的模块名。
	 * 设置path时起始位置是相对于baseUrl的，除非该path设置以"/"开头或含有URL协议（如http:）。
	 */
	paths : {
		// 配置不在baseUrl下的路径
		lib : '../lib',
		common : '../common',
		test : '../common/test',
		pdfjs : '../lib/pdfjs',
		
		// 配置baseUrl下的文件
//		'mine' : '../lib/mine',
		'jquery' : '../lib/jquery/jquery-2.2.3.min',
		'jqueryui' : '../lib/jqueryui/jquery-ui.min',
		'knob' : '../lib/knob/jquery.knob',
		'bootstrap' : '../lib/bootstrap/js/bootstrap.min',
		'adminLTE' : '../lib/AdminLTE/js/app.min',
		'angular' : '../lib/angular/angular.min',
		'angular-animate' : '../lib/angular/angular-animate.min',
		'angular-cookies' : '../lib/angular/angular-cookies.min',
		'uirouter' : '../lib/angular/ui-router',
		'bootstrap-slider' : '../lib/bootstrap-slider/bootstrap-slider',
		'bootstrap-wysihtml5' : '../lib/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min',
		'chartjs' : '../lib/chartjs/Chart.min',
		'ckeditor' : '../lib/ckeditor/ckeditor',
		'colorpicker' : '../lib/colorpicker/bootstrap-colorpicker.min',
		'jquery-datatables' : '../lib/datatables/jquery.dataTables.min',
		'datatables' : '../lib/datatables/dataTables.bootstrap.min',
		'datepicker' : '../lib/datepicker/bootstrap-datepicker',
		'datepicker-zh' : '../lib/datepicker/locales/bootstrap-datepicker.zh-CN',
		'datetimepicker' : '../lib/bootstrap-datetimepicker/bootstrap-datetimepicker.min',
		'datetimepicker-zh' : '../lib/bootstrap-datetimepicker/zh-cn',
		'moment' : '../lib/moment/moment',
		'daterangepicker' : '../lib/daterangepicker/daterangepicker',
		'fastclick' : '../lib/fastclick/fastclick.min',
		'fullcalendar' : '../lib/fullcalendar/fullcalendar.min',
		'iCheck' : '../lib/iCheck/icheck.min',
		'jvectormap' : '../lib/jvectormap/jquery-jvectormap-1.2.2.min',
		'jvectormap-world' : '../lib/jvectormap/jquery-jvectormap-world-mill-en',
		'knob' : '../lib/knob/jquery.knob',
		'raphael' : '../lib/morris/raphael-min',
		'morris' : '../lib/morris/morris.min',
		'pace' : '../lib/pace/pace.min',
		'select2' : '../lib/select2/select2.full.min',
		'slimScroll' : '../lib/slimScroll/jquery.slimscroll.min',
		'sparkline' : '../lib/sparkline/jquery.sparkline.min',
		'timepicker' : '../lib/timepicker/bootstrap-timepicker.min',
		'ztree' : '../lib/ztree/jquery.ztree.all.min',
	},
	/**
	 * shim: 配置在脚本/模块外面并没有使用RequireJS的函数依赖并且初始化函数。
	 * 假设underscore并没有使用RequireJS定义，但是你还是想通过RequireJS来使用它，那么你就需要在配置中把它定义为一个shim。
	 * shim配置仅设置了代码的依赖关系，想要实际加载shim指定的或涉及的模块，仍然需要一个常规的require/define调用。设置shim本身不会触发代码的加载。
	 * deps：加载依赖关系数组
	 * exports：暴露为全局变量
	 */
	shim : {
		'jquery' : {
			exports : '$'
		},
		'jqueryui' : {
			deps : [ 'jquery' ],
			exports : 'jqueryui'
		},
		'knob' : {
			deps : [ 'jqueryui' ],
			exports : 'knob'
		},
		'bootstrap' : {
			deps : [ 'jquery' ],
			exports : 'bootstrap'
		},
		'adminLTE' : {
			deps : [ 'bootstrap' ]
		},
		'angular' : {
			deps : [ 'jquery' ],
			exports : 'angular'
		},
		'angular-animate' : {
			deps : [ 'angular' ],
			exports : 'ngAnimate'
		},
		'angular-cookies' : {
			deps : [ 'angular' ],
			exports : 'ngCookies'
		},
		'uirouter' : {
			deps : [ 'angular' ],
			exports : 'uirouter'
		},
		// 插件：
		'bootstrap-slider' : {
			deps : [ 'bootstrap' ],
			exports : 'bootstrapSlider'
		},
		'bootstrap-wysihtml5' : {
			deps : [ 'bootstrap' ],
			exports : 'bootstrapwysihtml5'
		},
		'chartjs' : {
			deps : [ 'jquery' ],
			exports : 'chartjs'
		},
		'ckeditor' : {
			deps : [ 'jquery' ],
			exports : 'ckeditor'
		},
		'colorpicker' : {
			deps : [ 'bootstrap' ],
			exports : 'colorpicker'
		},
		'jquery-datatables' : {
			deps : [ 'jquery' ],
			exports : 'jqueryDatatables'
		},
		'datatables' : {
			deps : [ 'jquery-datatables' ],
			exports : 'datatables'
		},
		'datepicker' : {
			deps : [ 'jquery' ],
			exports : 'datepicker'
		},
		'datepicker-zh' : {
			deps : [ 'datepicker' ]
		},
		'datetimepicker' : {
			deps : [ 'moment' ],
			exports : 'datepicker'
		},
		'datetimepicker-zh' : {
			deps : [ 'datetimepicker' ]
		},
		'moment' : {
			deps : [ 'jquery' ],
			exports : 'moment'
		},
		'daterangepicker' : {
			deps : [ 'moment' ],
			exports : 'daterangepicker'
		},
		'fastclick' : {
			deps : [ 'jquery' ],
			exports : 'fastclick'
		},
		'fullcalendar' : {
			deps : [ 'jquery' ],
			exports : 'fullcalendar'
		},
		'iCheck' : {
			deps : [ 'jquery' ],
			exports : 'iCheck'
		},
		'jvectormap' : {
			deps : [ 'jquery' ],
			exports : 'jvectormap'
		},
		'jvectormap-world' : {
			deps : [ 'jvectormap' ],
			exports : 'jvectormapWorld'
		},
		'knob' : {
			deps : [ 'jquery' ],
			exports : 'knob'
		},
		'raphael' : {
			deps : [ 'bootstrap' ],
			exports : 'raphael'
		},
		'morris' : {
			deps : [ 'raphael' ],
			exports : 'morris'
		},
		'pace' : {
			deps : [ 'jquery' ],
			exports : 'pace'
		},
		'select2' : {
			deps : [ 'jquery' ],
			exports : 'select2'
		},
		'slimScroll' : {
			deps : [ 'jquery' ],
			exports : 'slimScroll'
		},
		'sparkline' : {
			deps : [ 'jquery' ],
			exports : 'sparkline'
		},
		'timepicker' : {
			deps : [ 'jquery' ],
			exports : 'timepicker'
		},
		'ztree' : {
			deps : [ 'jquery' ],
			exports : 'ztree'
		},
	},
	// Do not use RequireJS' paths option to configure the path to CodeMirror, since it will break loading submodules through relative paths. Use the packages configuration option instead
	packages : [ {
		name : "codemirror",
		location : "../lib/codemirror",// codemirror base 的目录
		main : "lib/codemirror"// 这是相对于codemirror base目录主程序所在的位置
	} ],
});

require([ 'jquery', 'jqueryui', 'bootstrap', 'adminLTE' ], function($) {
	//Resolve conflict in jQuery UI tooltip with Bootstrap tooltip
	$.widget.bridge('uibutton', $.ui.button);
});

/**
 * 非声明ng-app方式启动AngularJS.
 * 当app的各个组件准备就绪，且路由粘合完成后，就启动整个angular应用
 */
require([ 'angular', 'uirouter', 'app', 'router' ], function(angular) {
	angular.element(document).ready(function() {
		angular.bootstrap(document, [ 'webBuilding' ]);
	});
});
