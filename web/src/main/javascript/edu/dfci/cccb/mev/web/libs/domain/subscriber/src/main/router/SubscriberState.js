define([], function() {

        var config = function ($stateProvider, $urlRouterProvider) {
            $urlRouterProvider.deferIntercept();
            $stateProvider
                .state("root.datasets.subscribe", {
                    url: "/subscribe",
                    parent: "root.datasets",
                    displayName: false
                });
            ;
        };
        config.$inject = ['$stateProvider', '$urlRouterProvider'];
        config.$provider="config";     
        return config;
});
