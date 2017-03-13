define(["mui", 	"mev-mock", "mev-subscriber", "bootstrap", "bootstrap/dist/css/bootstrap.min.css", "angular-ui-bootstrap"
	], function(ng, ngMock, TextParam, SelectParam, IntegerParam, DecimalParam, mevAnalysis){"use strict";
	var demo = ng.module("demoSubscriber", arguments, arguments)
	.run(["$state", function($state){
		 $state.go('mock');
	}])
	.controller("demoCtrl", ["$scope", "mevSubscriberModal", "mevSubscriber", "mevSubscriberPrompt",
	function(scope, mevSubscriberModal, mevSubscriber, mevSubscriberPrompt){
		scope.$on("mev.subscribe.form.ok", function($event, fields){
			console.debug("mev.subscribe.form.ok", fields, arguments)
			mevSubscriber.save(fields);
		});
		scope.$on("mev.subscribe.form.cancel", function($event, fields){
			console.debug("mev.subscribe.form.cancel", arguments)
			mevSubscriber.save(fields);
		});
		scope.prompt=function(){
			mevSubscriberPrompt();
		};
		scope.clear=function(){
			mevSubscriber.clear();
		}
	}]);
	ng.element(document).ready(function(){
		ng.bootstrap(document, [demo.name]);
	});
});