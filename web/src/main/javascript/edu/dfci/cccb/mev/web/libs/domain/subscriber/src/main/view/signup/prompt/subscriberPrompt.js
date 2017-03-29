define([], function(){
   function subscriberPrompt(mevSubscriberModal, mevSubscriber){
       return function() {
           if(!mevSubscriber.exists())
               mevSubscriberModal()
                   .then(function(response){
                       mevSubscriber.save(response);
                   })
                   .catch(function(response){
                       mevSubscriber.save(response);
                   });
       }
   }
   subscriberPrompt.$name="mevSubscriberPrompt";
   subscriberPrompt.$inject=["mevSubscriberModal", "mevSubscriber"];
   subscriberPrompt.$provider="factory";
   return subscriberPrompt;
});