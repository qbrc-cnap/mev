define([], function(){
    
    return function(newval){
                        
        var self = this;
        
        if ( newval.type == "Hierarchical Clustering") {
            

            self.drawTree(self.DOM.topPanel,newval.root,'horizontal')

        } else if (newval.type == "K-means Clustering") {

            
            self.drawCluster(newval, self.DOM.topPanel);
        }
    }
})