/**
 * 封装codemirror，可以执行文本对比
 * @author HeLei
 * @date 2017.02.04
 */
define([ 'common/module', 'codemirror/lib/codemirror', 'codemirror/mode/htmlmixed/htmlmixed', 'codemirror/addon/merge/merge', 'common/service/util' ], function(commonModule, CodeMirror) {
	var CodeMirrorClosure = CodeMirror;
	commonModule.directive('merge', [ 'util', function(util) {
		util.loadasync('lib/codemirror/lib/codemirror.css');
		util.loadasync('lib/codemirror/addon/merge/merge.css');
		var dv, editor;
		return {
			restrict : 'AE',
			scope : {
				origLeft : '@',
				origRight : '@'
			},
			templateUrl : 'common/directive/merge/template.html',
			require : 'ngModel',
			link : function($scope, $element, $attrs, ngModelCtrl) {
				var value, orig1, orig2, dv, panes, highlight = true, connect = null, collapse = false;
				value = ngModelCtrl.$viewValue;
				function initUI() {
					if (!value) {
						value = '';
					}
					origLeft = $scope.origLeft;
					origRight = $scope.origRight;
					if (!origLeft) {
						origLeft = '';
					}
					if (origRight) {
						panes = 3;
					} else {
						panes = 2;
					}
					var target = $element.find('div').get(0);
					target.innerHTML = '';
					target.parentElement.style.display = 'block';
					dv = CodeMirrorClosure.MergeView(target, {
						value : value,
					    origLeft: origLeft,
			    	    orig: panes == 3 ? origRight : null,
						lineNumbers : true,
						mode : 'text/html',
						highlightDifferences : true,
						collapseIdentical : false
					});
					editor = dv.edit;
					editor.on('change', function(codeMirror, changeObj) {
						$scope.$apply(function() {
							// Set the data within AngularJS
							ngModelCtrl.$setViewValue(codeMirror.doc.getValue());
						});
					});
				}
				initUI();
				// When data changes inside AngularJS
				// Notify the third party directive of the change
				ngModelCtrl.$render = function() {
					value = ngModelCtrl.$viewValue;
					initUI();
				};
			}
		};
		function toggleDifferences() {
			dv.setShowDifferences(highlight = !highlight);
		}
		function mergeViewHeight(mergeView) {
			function editorHeight(editor) {
				if (!editor)
					return 0;
				return editor.getScrollInfo().height;
			}
			return Math.max(editorHeight(mergeView.leftOriginal()),
					editorHeight(mergeView.editor()), editorHeight(mergeView
							.rightOriginal()));
		}
		function resize(mergeView) {
			var height = mergeViewHeight(mergeView);
			for (;;) {
				if (mergeView.leftOriginal())
					mergeView.leftOriginal().setSize(null, height);
				mergeView.editor().setSize(null, height);
				if (mergeView.rightOriginal())
					mergeView.rightOriginal().setSize(null, height);

				var newHeight = mergeViewHeight(mergeView);
				if (newHeight >= height)
					break;
				else
					height = newHeight;
			}
			mergeView.wrap.style.height = height + "px";
		}
	}]);
});