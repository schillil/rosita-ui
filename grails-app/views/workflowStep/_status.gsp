<g:if test="${workflowStep == '1'}">

	<g:if test="${messageType == 'SUCCESS'}">
		${latestOutput}
	</g:if>
	<g:else>
		Verifying:<br/>
		XML file <b>${filename}</b><br/>
		Schema <b>${schemaname}</b>
	</g:else>

</g:if>
<g:elseif test="${workflowStep == '2'}">

	<g:if test="${messageType == 'COMPLETE'}">
		Validation complete, gathering results...
	</g:if>
	<g:elseif test="${messageType == 'STATUS'}">
		<tmpl:bar numerator="${elements}" denominator="${totalElements}" color="green" label="Elements validated"/>
		<b>Location:</b> ${location}<br/>
		<b>Errors:</b> ${errors}
	</g:elseif>

</g:elseif>
<g:elseif test="${workflowStep == '3'}">

	<g:if test="${messageType == 'SUCCESS'}">
		Load complete, gathering results...
	</g:if>
	<g:elseif test="${taskType == 'TRUNCATE'}">
		<tmpl:bar numerator="${tables}" denominator="14" color="green" label="Truncated"/>
	</g:elseif>
	<g:elseif test="${messageType == 'STATUS'}">
		<tmpl:bar numerator="${elements}" denominator="${totalElements}" color="green" label="Elements loaded"/>
	</g:elseif>

</g:elseif>
<g:elseif test="${workflowStep == '4'}">

	<g:if test="${messageType == 'COMPLETE'}">
		Profiling complete, gathering results...
	</g:if>
	<g:elseif test="${messageType == 'STATUS'}">
		<tmpl:bar numerator="${latestOutput}" denominator="14" color="green" label="Validated"/>
	</g:elseif>

</g:elseif>
<g:elseif test="${workflowStep == '8'}">

	<g:if test="${messageType == 'COMPLETE'}">
		Processing complete, gathering results...
	</g:if>
	<g:elseif test="${messageType == 'STATUS'}">
		<tmpl:bar numerator="${latestOutput}" denominator="18" color="green" label="OMOP tables processed"/>
	</g:elseif>

</g:elseif>
<g:elseif test="${workflowStep == '9'}">

	<g:if test="${messageType == 'COMPLETE'}">
		Profiling complete, gathering results...
	</g:if>
	<g:elseif test="${messageType == 'STATUS'}">
		<tmpl:bar numerator="${latestOutput}" denominator="18" color="green" label="OMOP tables profiled"/>
	</g:elseif>

</g:elseif>
<g:elseif test="${workflowStep == '11'}">

	<g:if test="${messageType == 'SUCCESS'}">
		Publish complete, gathering results...
	</g:if>
	<g:elseif test="${messageType == 'STATUS'}">
		<g:if test="${latestOutput == 'TRUNCATE'}">
			Truncating...
		</g:if>
		<g:else>
			<tmpl:bar numerator="${latestOutput}" denominator="18" color="green" label="Tables published to GRID"/>
		</g:else>
	</g:elseif>

</g:elseif>
<g:else>
	Running...
</g:else>

<g:if test="${estimateMs}">
	<br/>
	<div class="texttiny"><b>Estimated time remaining:</b> <g:timeEstimateFromMs ms="${estimateMs}"/></div>
</g:if>