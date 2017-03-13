define(["mui", "./subscriberModal.tpl.html"], function(ng, template){
   function emailPrompt($state, $modal){
       return function(){
           return $modal.open({
               template: template,
               controller: ["$scope", function(scope){
                   scope.email="";
                   scope.$on("mev.subscribe.form.ok", function($event, fields){
                       console.debug("mev.subscribe.form.ok", fields, arguments)
                       scope.$close(fields);
                   });
                   scope.$on("mev.subscribe.form.cancel", function($event, fields){
                       console.debug("mev.subscribe.form.cancel", arguments)
                       scope.$close(fields);
                   });
               }]
           }).result;
       }
   }
   emailPrompt.$name="mevSubscriberModal";
   emailPrompt.$provider="factory";
   emailPrompt.$inject=["$state", "$modal", "$q"];
   return emailPrompt;
});