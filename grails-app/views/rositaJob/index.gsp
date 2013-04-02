<!DOCTYPE HTML5>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="layout_main" />
</head>
</html>
<body>

	<g:if test='${flash.message}'><div class='flashmessage'>${flash.message}</div></g:if>
	
	<g:if test="${jobInProgress}">
		<div class="stepstatus" id="stepstatus">
			<g:if test="${jobs[0].workflowStep == 0}">
				<div id="stepicon" class="stepicon play">Job awaiting start</div>
			</g:if>
			<g:else>
				<g:if test="${latestWorkflowStep.state == 'paused'}">
					<div id="stepicon" class="stepicon paused">${workflowTitles[jobs[0].workflowStep - 1].title} awaiting decision</div>
				</g:if>
				<g:elseif test="${latestWorkflowStep.state == 'failed'}">
					<div id="stepicon" class="stepicon failed">${workflowTitles[jobs[0].workflowStep - 1].title} has failed</div>
				</g:elseif>
				<%-- Treat running and success as still running --%>
				<g:else>
					<div id="stepicon" class="stepicon running">${workflowTitles[jobs[0].workflowStep - 1].title} in progress</div>
				</g:else>
			</g:else>
			<div class="stepmessage"><b>${jobs[0].name}</b> for file ${jobs[0].fileName}</div>
			<div class="stepmessage"><a class="anchor" href="${createLink([action:'show',controller:'rositaJob',id:jobs[0].id])}">Details</a></div>
		</div>
	</g:if>
	<g:else>
		<div class="stepstatus" id="stepstatus">
			<g:form name="create" action="create" method="post">
			<div id="stepicon" class="stepicon play">New job</div>
			<div class="stepmessage">
			<table width="100%"><tr><td>Name</td><td><g:textField name="name" style="width: 90%" /></td>
				<tr><td>File</td>
					<td>
						<g:if test="${browseFolder}">
							<div class="texttiny">Type a filename or select from the list below</div>
						</g:if>
						<g:textField name="filename" style="width: 90%" value="${latestFilename}"/>
						<g:if test="${browseFolder}">
							<g:select name="filenamePicker" style="width: 90%" from="${files}" noSelection="${['':'Select...']}" onchange="transferFilename('${browseFolder}')"/>
						</g:if>
					</td>
				</tr>
			</table>
			</div>
			<center><span class="anchor" onclick="$j('#create').submit()">Start new job</span></center>
			</g:form>
		</div>
	</g:else>
	
	<h3>Job History</h3>
	<table class="striped">
		<thead>
			<tr>
				<th>Name</th>
				<th>File</th>
				<th>Started on</th>
				<th>Finished on</th>
				<th>Outcome</th>
				<th>&nbsp;</th>
			</tr>
		</thead>
	<g:each in="${jobs}" var="job" status="i">
		<tr>
			<td>${job.name}</td>
			<td>${job.shortFilename}</td>
			<td><g:formatDate format="yyyy-MM-dd" date="${job.startDate}"/></td>
			<td><g:formatDate format="yyyy-MM-dd" date="${job.endDate}"/></td>
			<td style="line-height: 16px">
				<g:if test="${workflowTitles[job.workflowStep-1]?.title == 'Complete'}">
					<a href="${createLink([action:'history',controller:'rositaJob',id:job.id])}"><img src="${resource(dir:'images/icons',file:'check.png') }" width="16" height="16"/> Complete</a>
				</g:if>
				<g:elseif test="${jobInProgress && i == 0}">
					<a href="${createLink([action:'show',controller:'rositaJob',id:job.id])}"><img src="${resource(dir:'images/icons',file:'play.png') }" width="16" height="16"/> Current</a>
				</g:elseif>
				<g:else>
					<a href="${createLink([action:'history',controller:'rositaJob',id:job.id])}"><img src="${resource(dir:'images/icons',file:'close_delete_2.png') }" width="16" height="16"/> Cancelled</a>
				</g:else>
			</td>
			<td><a href="${createLink([action:'history',controller:'rositaJob',id:job.id])}"><img src="${resource(dir:'images/icons',file:'detail.png')}" width="16" height="16"/></a></td>
		</tr>	
	</g:each>
	</table>
	<br/>
	
	<div class="bigbutton" style="width: 300px">
		<a href="${createLink([action:'jobs', controller:'rositaJob'])}"><div style="cursor: pointer;" class="stepicon search">View more history</div></a>
	</div>
</body>