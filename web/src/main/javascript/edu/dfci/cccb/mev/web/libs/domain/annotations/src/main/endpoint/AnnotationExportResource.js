define(["lodash"], function(_){ "use strict";
    var component = function($http, $q, $stateParams, AnnotationProjectIdResource){
        var source = {
            export: function(datasetId, dimension){
                datasetId = $stateParams.datasetId || datasetId;

                var url="/annotations/"+datasetId+"/annotation/"+dimension+"/new/dataset/command/core/export-project/"+datasetId+".google-refine.tar.gz";
                return AnnotationProjectIdResource.get(dimension, datasetId)
                    .then(function(response){
                        if(response.project<=0){
                            console.error("Could not find " + dimension + "for dataset " + datasetId + " at " + url);
                            throw new Error("Could not find " + dimension + "for dataset " + datasetId + " at " + url);
                        }
                        return $http.post(url,
                            {},
                            {
                                params: {
                                    project: response.project
                                },
                                responseType: "arraybuffer",
                                headers: {
                                    // "Accept": "application/octet-stream",
                                    "Accept": "application/x-gzip"
                                }
                            });
                    })

            },
            import: function(datasetId, dimension, data){
                var deferred = $q.defer();

                var url="/annotations/"+datasetId+"/annotation/"+dimension+"/import";
                var formdata = new FormData();
                formdata.append('upload', data);
                var xhr = new XMLHttpRequest();
                xhr.upload.addEventListener("progress", function (e) {
                    return;
                });
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 ) {
                        if(xhr.status == 200)
                            deferred.resolve(200)
                        else
                            deferred.reject(xhr.status)
                    };
                };
                xhr.open("POST", url, true);
                xhr.send(formdata);
                return deferred.promise;
            }
        };
        _.assign(this, source);  
    };
    component.$name = "AnnotationExportResource";
    component.$provider = "service";
    component.$inject=["$http", "$q", "$stateParams", "AnnotationProjectIdResource"];
    return component;
});