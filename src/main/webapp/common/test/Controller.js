define(['angular', 'test/module'], function(angular) {
	return angular.module('testModule')
	.controller('TestDatetimepickerCtrl', ['$scope', '$http', '$state', function($scope, $http, $state) {
		var self = this;
		var moment = require('moment');
		self.date = moment();
		self.test = {test1:1};
		self.testList = [{a:1,b:2}, {a:1,b:2}, {a:1,b:2}];
		
		self.to = '2016-02-05';
		self.from = '2016-01-16';
		
	}])
	.controller('TestSelect2Ctrl', ['$scope', '$http', '$state', function($scope, $http, $state) {
		var self = this;
		self.stringList = ['abc', 'bcd', 'efg', 'hij', 'klm', 'nop', 'qrs', 'tuv', 'wxyz'];
		self.objList = [{a:1,b:2}, {a:3,b:4}, {a:5,b:6}];
		self.strval = 'efg';
		self.strvalMultiple = ['bcd', 'efg', 'hij'];
		self.objval = {a:3,b:4};
		self.objs = [{a:1,b:2}, {a:5,b:6}];
		self.onChange = function(value) {
			console.log('select值改为：' + value);
		};
	}])
	.controller('TestFileuploadCtrl', ['$rootScope', '$scope', '$http', '$state', function($rootScope, $scope, $http, $state) {
		var self = this;
		self.modal = {
			header : '上传结果',
			body : '',
		};
		self.action = function() {
			$('.modal').modal('hide');
		}
		self.callbackfun = function(msg) {
			self.modal.body = msg;
			angular.element(document.getElementById('myModal')).scope().$digest();
			$('.modal').modal('show');
		};
		self.fileInvalid = function() {
			var file1, file2;
			file1 = $('#exampleInputFile1').val();
			file2 = $('#exampleInputFile2').val();
			return file1 && file2 ? false : true;
		}
		self.langList = ["ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby", "Scala", "Scheme"];
	}])
	.controller('TestPagerCtrl', ['$scope', '$http', '$state', function($scope, $http, $state) {
		var self = this;
		var buttonCount = 5, pageSize = 20;
		self.params = {'foo' : 'foo', 'bar' : 'bar'};
		self.query = function(pageNumber) {
			self.params.pageNumber = pageNumber;
			console.log('当前查询条件：');
			console.log(self.params);
			self.pager = getPager();
			console.log(self.pager);
		}
		self.pager = getPager();
		
		function getPager() {
			var totalRowNum = Math.floor(Math.random() * 200);
			var totalPageNum = Math.floor((totalRowNum + pageSize - 1) / pageSize);
			var currentPage = Math.floor(totalPageNum * Math.random()) || 1;
			var offset = (currentPage - 1) * pageSize;// offset在数据库中的序号是以0开始
			var endIndex = offset + pageSize;
			var dataList = [];
			for (var i = offset; i < endIndex; i++) {
				var obj = {
					id : i,
					'Rendering engine' : 'Webkit',
					'Browser' : 'Internet Explorer 4.0',
					'Platform' : 'Win 95+',
					'Engine version' : '4',
					'CSS grade' : 'X'
				};
				dataList.push(obj);
			}
			return {
				totalElements : totalRowNum,// 总行数
				totalPages : totalPageNum,// 总页数
				pageNumber : currentPage,// 当前页码
				pageSize : pageSize,// 每页最大行数
				content : dataList// 存储查询结果
			};
		}
	}])
	.controller('TestModalCtrl', ['$rootScope', '$scope', '$http', '$state', function($rootScope, $scope, $http, $state) {
		var self = this;
		self.title = 'hello world';
		self.openModal = function() {
			self.open = true;
		}
		self.confirm = function() {
			self.open = false;
		};
	}])
	.controller('TestZtreeCtrl', ['$rootScope', '$scope', '$http', '$state', 'ztreeutil', function($rootScope, $scope, $http, $state, ztreeutil) {
		var self = this;
		var file1 = 'abc/bcd/eee.exe', file2 = 'abc/edf/bbb.exe', file3 = 'zzz/yyy.exe';
		var nodes = [];
		ztreeutil.setNodes(file1, nodes);
		ztreeutil.setNodes(file2, nodes);
		ztreeutil.setNodes(file3, nodes);
		self.nodes = nodes;
		self.abc = file1;
		self.bcd = {a:1, b:2};
		self.edf = [{a:1, b:2} , {c:3, d:4}];
		self.click = function(event, treeId, treeNode) {
			if (treeNode.isParent) {
				console.log('目录：');
			} else {
				console.log('文件：');
			}
			console.log(treeNode.tId + ", " + treeNode.name);
		}
		self.printNodes = function() {
			console.log(self.nodes);
		}
	}])
	;
});