<g:if test="${workflowStep == '1'}">

	<b>${latestOutput}</b><br/><br/>

	To resolve this problem, either:
	<ul>
		<li>Ensure that this file exists and can be read</li>
		<li>Cancel this job and restart with a different filename</li>
	</ul>
	<span class='anchor' onclick='resumeJob(${jobId}, ${workflowStep})'>Retry</span> this step

</g:if>
<g:elseif test="${workflowStep == '2'}">
	
	<b>Validation encountered ${errors} errors!</b><br/><br/>
	
	To resolve this problem:
	<ul>
		<li>Ensure that the source file conforms to the given schema</li>
	</ul>
	<a class='anchor' href='/rosita/validationError?from=show&id=${stepId}'>View</a> the validation errors<br/>
	<span class='anchor' onclick='resumeJob(${jobId}, ${workflowStep})'>Retry</span> this step
</g:elseif>
<g:elseif test="${workflowStep == '7'}">
	
	<b>Problems were encountered during the import.</b><br/><br/>
	
	To resolve this problem:
	<ul>
		<li>Check the console output and correct any fields that are flagged.</li>
	</ul>
	<g:if test="${files}">
		<g:select name="csvPicker" style="width: 90%" from="${files}" noSelection="${['':'Select...']}" /><br/>
	</g:if>
	<span class="anchor" onclick="runImport($j('#csvPicker').val(), ${stepId})">Retry with this file</span><br/><br/>
	
	<span class="anchor" onclick="skipStep(JOB_ID, ${workflowStep}, ${stepId})">Skip</span> this step<br/><br/>
</g:elseif>
<g:else>
	<b>This step failed to complete.</b><br/><br/>
	
	Check the console output for more information.<br/><br/>
	
	<span class='anchor' onclick='resumeJob(${jobId}, ${workflowStep})'>Retry</span> this step
</g:else>