define(["mui", "mev-domain-common", "angular-ui-router", "ui-router-extras",
    "./endpoint/rest/SubscriberRest",
    "./model/Subscriber",
    "./router/SubscriberState",
    "./view/signup/form/subscriberSignupDirective",
    "./view/signup/modal/subscriberModal",
    "./view/signup/prompt/subscriberPrompt",
    "./util/cookieUtil"], function(ng){
    return ng.module("mev-subscriber", ["ct.ui.router.extras"], arguments);
});