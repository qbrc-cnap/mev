define(["ng",
        "./survival/domain.analsyis.survival.module",
        "./topgo/domain.analsyis.topgo.module",
        "./pca/domain.analysis.pca.module",
        "./genesd/domain.analysis.genesd.module"],
function(ng){
	var module = ng.module("mui.domain.analysis", arguments);
	return module;
});
