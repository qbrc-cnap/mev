define(["lodash", "d3", "vega", "./mevNetwork.vegaspec.json"], function(_, d3, vg, specJson){
    var directive = function mevNetworkDirective(){
        return {
            restrict: "AEC",
            scope: {
                config: "=mevNetworkGraph"
            },
            // template: "<div vega spec=\"vm.spec\"  vega-renderer=\"renderer\"></div>",
            template: "<div></div>",
            controller: ["$scope", function(scope){
                if(!scope.config.renderer)
                    scope.config.renderer = "canvas";

                _.defaultsDeep(scope.config, {
                    edge: {
                        field: "edges",
                        source: { field: "source" },
                        target: { field: "target" },
                        weight: {
                            field: "value",
                            scale: {
                                "name": "weight",
                                "type": "linear",
                                "domain": {
                                    "data": "edges", "field": "value"
                                },
                                "range": [1, 10]
                            }
                        }
                    },
                    node: {
                        color: {
                            field: "group",
                            value: "red"                             
                        },
                        tooltip: {
                            fields: [
                                {
                                    "name": "name",
                                    "label": "Name"
                                }
                            ]
                        }
                    }
                });

                var spec = specJson;
                //set spec globals
                spec.height = (scope.config.height || spec.height);
                spec.width = (scope.config.width || spec.width);
                spec.padding = (scope.config.padding || spec.padding);

                //config edges
                //apply edge interface to edge rows
                var edges = scope.config.data[scope.config.edge.field].map(function(edge){
                    var newEdge = edge;
                    if(scope.config.edge.source && _.isUndefined(newEdge.source))
                        Object.defineProperty(newEdge, "source", {
                            enumerable: true,
                            get: function(){
                                return this[scope.config.edge.source.field];
                            },
                            set: function(val){return this[scope.config.edge.source.field]=val}
                        });
                    if(scope.config.edge.target && _.isUndefined(newEdge.target))
                        Object.defineProperty(newEdge, "target", {
                            enumerable: true,
                            get: function(){
                                return this[scope.config.edge.target.field];
                            },
                            set: function(val){return this[scope.config.edge.target.field]=val}
                        });
                    if(scope.config.edge.weight && _.isUndefined(newEdge.weight))
                        Object.defineProperty(newEdge, "weight", {
                            enumerable: true,
                            get: function(){return this[scope.config.edge.weight.field]},
                            set: function(val){return this[scope.config.edge.weight.field]=val}
                        });
                    return newEdge;
                });
                //set edge data values
                _.assign(spec.data.find(function(item){
                    return item.name === "edges";
                }), {
                    values: edges
                });
                //set edge scale 
                spec.scales.push(scope.config.edge.weight.scale);
                //set edge marks
                _.assign(spec.marks.find(function(mark){
                    return mark.name === "edge"
                }).properties.update, {
                   "strokeWidth": { "scale": "weight", "field": "weight"}
                });
                
                //config nodes
                //apply node interface to node rows
                var nodes = scope.config.data.nodes.map(function(node){
                    var newNode = node;
                    if(scope.config.node.color && _.isUndefined(newNode.color))
                        Object.defineProperty(newNode, "color", {
                            enumerable: true,
                            get: function(){return this[scope.config.node.color.field];},
                            set: function(val){this[scope.config.node.color.field]=val;}
                        });
                    if(scope.config.node.shape && _.isUndefined(newNode.shape))
                        Object.defineProperty(newNode, "shape", {
                            enumerable: true,
                            get: function(){return this[scope.config.node.shape.field];},
                            set: function(val){this[scope.config.node.shape.field]=val;}
                        });
                    if(scope.config.node.size && _.isUndefined(newNode.size))
                        Object.defineProperty(newNode, "size", {
                            enumerable: true,
                            get: function(){return this[scope.config.node.size.field];},
                            set: function(val){this[scope.config.node.size.field]=val;}
                        });
                    return newNode;

                });
                //set data values
                _.assign(spec.data.find(function(item){
                    return item.name === "nodes";
                }), {
                    values: nodes
                });
                //color: set scale
                if(scope.config.node.color.scale){
                    spec.scales.push(scope.config.node.color.scale);
                }
                //color: set marks
                _.assign(spec.marks.find(function(mark){
                    return mark.name === "node"
                }).properties.update, {
                    "strokeWidth": scope.config.node.color.scale
                        ? { "scale": "weight", "field": "weight"}
                        : { "value": scope.config.node.color.value }
                });
                //node tooltip
                _.assign(spec.data.find(function(data){
                    return data.name === "tooltip";
                }),
                {
                    "values":
                    scope.config.node.tooltip.fields
                });

                scope.vm = {
                    spec: spec
                };
                console.debug("spec", spec);
            }],
            link: function(scope, elm, attr, ctrl){
                function parse(spec, renderer) {
                    vg.parse.spec(spec, function(error, chart) {
                        var view = chart({
                            el: elm[0],
                            renderer: renderer || "canvas"
                        }).update();

                        view.onSignal("hoverNode", function(event, item){
                            console.debug(event, item);
                            console.debug("activeNode", view.data("activeNode").values());
                        });
                    });
                }
                parse(scope.vm.spec, scope.renderer);
            }
        }
    };
    directive.$name="mevNetworkGraph";
    directive.$provider="directive";
    directive.$inject=[];
    return directive;
});