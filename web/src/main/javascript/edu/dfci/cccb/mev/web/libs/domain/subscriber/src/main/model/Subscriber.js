define(["lodash"], function(_){"use strict";
    var service = function($http, $q, mevCookieUtil, mevSubscriberRest){
        function save(obj){
            if(obj.signMeUp){
                //rest
                mevSubscriberRest.put({}, obj)
                mevCookieUtil.write("mev.subscriber", true);
            }else{
                mevCookieUtil.write("mev.subscriber", false);
            }
        }
        function find(){
            return mevCookieUtil.read("mev.subscriber") || mevCookieUtil.read("quickstart_user");
        }
        function exists(){
            return !_.isNil(this.find());
        }
        function clear(){
            mevCookieUtil.erase("mev.subscriber")
        }
        _.assign(this, {
            save: save.bind(this),
            find: find.bind(this),
            exists: exists.bind(this),
            clear: clear.bind(this)
        });
    };
    service.$inject=["$http", "$q", "mevCookieUtil", "mevSubscriberRest"];
    service.$name="mevSubscriber";
    service.$provider="service";
    return service;
});