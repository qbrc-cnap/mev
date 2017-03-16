define(["lodash", "./subscriberSignup.tpl.html", "./subscriberSignup.less"], function(_, template){
    var directive = function(){

        return {
            restrict : 'CEA',
            template : template,
            scope: {
              cbOk: "&mevOk",
              cbCancel: "&mevCancel"
            },
            controllerAs: "vm",
            controller: ["$scope", function(scope){
                var _self=this;
                function validate() {
                    this.errors.length=0;
                    if (_.isEmpty(this.fields.email)
                        || this.fields.email.indexOf("@")<0) {
                        this.errors.push({
                            message: "Please provide a valid email"
                        });
                    }
                    return this.errors.length===0;
                }
                function ok(){
                    if(this.validate()){
                        this.fields.signMeUp=true;
                        scope.$emit("mev.subscribe.form.ok", _.cloneDeep(this.fields));
                    }
                }
                function cancel(){
                    scope.$emit("mev.subscribe.form.cancel", _.cloneDeep(this.fields));
                }
                _.assign(_self, {
                    fields: {
                        name: "",
                        email: "",
                        signMeUp: false,
                        doNotPrompt: false
                    },
                    errors: [],
                    validate: validate.bind(_self),
                    ok: ok.bind(_self),
                    cancel: cancel.bind(_self)
                });
            }],
            link : function(scope, elem, attrs) {
            }
        };
    };
    directive.$name="mevSubscriberForm";
    directive.$inject=[];
    directive.$provider="directive";
    return directive;
});

