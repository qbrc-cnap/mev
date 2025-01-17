<div class="container-fluid">
	<form class="form-horizontal" role="form" >
	
		<div class="form-group">
			<label for="heatmapColoring" class="control-label">D3 Color Brewer Groups:</label>
			<select id="heatmapColoring" ng-model="currentColors.group" ng-options="selection for selection in availableColorGroups"></select>
	        </div>
	    
	</form>
</div>

<div class="container-fluid">
	<form class="form-horizontal" role="form" >
	
		<div class="form-group">
			<label for="heatmapColorMin" class="control-label">Color Ranges:</label>
        </div>
		<div class="form-group">
			<label for="heatmapColorMin" class="control-label">Min:</label>
			<input id="heatmapColorMin" ng-model="colorEdge.min"/>
        </div>
        <div class="form-group">
			<label for="heatmapColorAvg" class="control-label">Avg:</label>
			<input id="heatmapColorAvg" ng-model="colorEdge.avg"/>
        </div>
        <div class="form-group">
			<label for="heatmapColorMax" class="control-label">Max:</label>
			<input id="heatmapColorMax" ng-model="colorEdge.max"/>
        </div>
        
        <div id="heatmapColorSlider"></div>
		     
	</form>

</div>
