<!-- Modal -->
<div id="{{bindid.replace('#', '')}}" class="modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">

			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				<h3 id="myModalLabel">{{header}}</h3>
			</div>
	
			<div class="modal-body">
				<foo ng-transclude></foo>
			</div>
			
		</div>
	</div>
</div>