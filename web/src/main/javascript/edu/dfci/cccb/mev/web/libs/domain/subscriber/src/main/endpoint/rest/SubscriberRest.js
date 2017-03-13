define(["lodash"], function(_){
    var SelectionRest = function ($resource, $http, $rootScope, $q) {

        var resource = $resource('/subscriber', {
            'format': 'json'
        }, {
            'getAll': {
                'method': 'GET'
            },
            'put': {
                'method': 'PUT',
            },
            'delete': {
                'method': 'DELETE'
            }
        });

//    	return resource;
        var SubscriberResource = Object.create(resource);
        SubscriberResource.put = function (params, data, callback) {
            var result = resource.put(params, data, callback);
            result.$promise.then(function (response) {
                $rootScope.$broadcast("mui:subscriber:added", params, data, response);
            });
            return result;
        };
        return SubscriberResource;
    }
    SelectionRest.$inject=['$resource', '$http', '$rootScope', '$q'];
    SelectionRest.$name="mevSubscriberRest";
    SelectionRest.$provider="service";
    return SelectionRest;

});