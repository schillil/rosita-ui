<g:if test="${workflowStep == '5' || workflowStep == '10'}">
	This step requires Jasper reports to be run.<br/><br/>
	
	<a target="_blank" class="anchor" href="${jasperUrl}">Run Jasper reports here</a><br/><br/>
	
	After the reports have been run, <span class="anchor" onclick="confirmStep(JOB_ID, ${workflowStep}, ${stepId})">click here to resume the job</span>.
</g:if>
<g:elseif test="${workflowStep == '6'}">
	<g:if test="${hasUnmapped}">
		Unmapped concept values were found!<br/><br/>
		
		<a class="anchor" href="/rosita/conceptMap/export">Export unmapped values</a><br/>
		<a class="anchor" href="/rosita/conceptMap/export?all=true">Export all values</a><br/><br/>
		
		<span class="anchor" onclick="confirmStep(JOB_ID, ${workflowStep}, ${stepId})">Mark this step as completed</span><br/><br/>
		<span class="anchor" onclick="skipStep(JOB_ID, ${workflowStep}, ${stepId})">Skip this step</span><br/><br/>
		
	</g:if>
	<g:else>
		No unmapped concept values were found.<br/><br/>
		
		<span class="anchor" onclick="skipStep(JOB_ID, ${workflowStep}, ${stepId})">Skip this step</span>
	</g:else>
</g:elseif>
<g:elseif test="${workflowStep == '7'}">
	Select a CSV file to import.<br/><br/>
	
	<g:if test="${files}">
		<g:select name="csvPicker" style="width: 90%" from="${files}" noSelection="${['':'Select...']}" /><br/>
	</g:if>
	<span class="anchor" onclick="runImport($j('#csvPicker').val(), ${stepId})">Import this file</span><br/><br/>
	
	<span class="anchor" onclick="skipStep(JOB_ID, ${workflowStep}, ${stepId})">Skip this step</span><br/><br/>
</g:elseif>