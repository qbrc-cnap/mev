define(["mui", "lodash"], function(ng, _){
	/* Random Data Generator (took from nvd3.org) */
    var service = function mevEnrichmentDataAdaptor(){
		return function(input){
            return input.map(function(item){
                return _.extend(item, {
                    getRatio: function(){
                        return Math.round((item.significantGenes / item.annotatedGenes) * 1000) / 10000;
                    },
                    getTotal: function(){
                        return item.annotatedGenes;
                    },
                    getMatched: function(){
                    	return item.significantGenes;
                    },
                    getName: function(){
                    	return item.goTerm;
                    },
                    getPValue: function(){
                    	return item.pValue;
                    }
                });
            });
        };
	};
	service.$name="mevEnrichmentDataAdaptor";
	service.$provider="factory";
	service.$inject=[];
	return service;
});