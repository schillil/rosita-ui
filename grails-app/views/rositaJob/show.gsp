<!DOCTYPE HTML5>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="layout_main" />
</head>
</html>
<body>
	<g:javascript>
	$j = jQuery.noConflict();
	
	function startValidation() {
		callAjax('validatorStatus', '/rosita/validator/start', {});
		callAjaxContinuous('validatorStatus', '/rosita/validator/status', {});
		UPDATE_VALIDATOR = true;
	}
	
	function stopValidation() {
		callAjax('validatorStatus', '/rosita/validator/stop', {});
		UPDATE_VALIDATOR = false;
	}
	
	</g:javascript>

	<g:set var="alreadyRunning" value="true"/>
	
	<h3>${rositaJobInstance.name}</h3>
	
	<g:each in="${workflowSteps}" var="step" status="i">
		<div class="stepstatus" id="stepstatus${i+1}">
			<%-- If the step has been created (has been successful or this is the current step) --%>
			<g:if test="${step}">
				<div id="stepicon${i+1}" class="stepicon ${step.state}">${workflowTitles[i].title}</div>
				<div id="stepmessage${i+1}" class="stepmessage">&nbsp;</div>
				<div id="stepconsole${i+1}" style="text-align: right">
					<tmpl:consoleLink id="${step.id}"/>
				</div>
			</g:if>
			<%-- If no first step, provide the Play button at the very start --%>
			<g:elseif test="${!step && i == 0}">
				<g:set var="alreadyRunning" value="${false}"/>
				<div id="stepicon${i+1}" class="stepicon play" onclick="startJob(${rositaJobInstance.id})">${workflowTitles[i].title}</div>
				<div id="stepmessage${i+1}" class="stepmessage">Click the Play icon to begin the ROSITA process.</div>
				<div id="stepconsole${i+1}" style="text-align: right">
				</div>
			</g:elseif>
			<g:else>
				<div id="stepicon${i+1}" class="stepicon">${workflowTitles[i].title}</div>
				<div id="stepmessage${i+1}" class="stepmessage">&nbsp;</div>
				<div id="stepconsole${i+1}" style="text-align: right">
				</div>
			</g:else>
		</div>
	</g:each>
	
	<div class="bigbutton">
		<a href="${createLink([action:'index', controller:'rositaJob'])}"><div style="cursor: pointer;" class="stepicon back">Job list</div></a>
	</div>
	
	<div class="bigbutton" id="restartButton">
		<div id="stepiconrestart" style="cursor: pointer;" class="stepicon failed" onclick="if (confirm('Are you sure you want to cancel this job?')) {cancelJob(${rositaJobInstance.id});}">Cancel job</div>
	</div>
	
	<g:javascript>
		JOB_ID = ${rositaJobInstance.id};
		<g:if test="${alreadyRunning}">
			startUpdating();
		</g:if>
	</g:javascript>
</body>