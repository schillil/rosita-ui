var UPDATE = false;
var AJAX_TIMER = null;
var JOB_ID = null;
var UPDATE_FREQUENCY = 1000;

function callAjax(element, url, params, big, callbackFunction) {
	//clearAndShowWorking(element, '', big);
	
	request = $j.ajax({
		url: url,
		type: 'POST',
		data: params
	});
	
	request.fail(function(jqXHR, textStatus) {
//		if (jqXHR.responseText) {
//			alert(jqXHR.responseText);
//		}
	});
	
	request.done(function(msg) {
		$j('#' + element).html(msg);
		callbackFunction();
	});
	
}

//Main update function... most of this has been standardized so is identical for each step
function updateScreen() {
	request = $j.ajax({
		url: '/rosita/rositaJob/status/' + JOB_ID,
		type: 'POST',
		data: {}
	});
	
	request.fail(function(jqXHR, textStatus) {
		alert(jqXHR.responseText);
	});
	
	request.done(function(msg) {
		if (msg['workflowStep'] == 1) {
			if (msg['status'] == 'failed') {
				showFailure(1);
				getErrorDisplay(JOB_ID, 1, msg);
				UPDATE = false;
			}
			else {
				showRunning(1);
				getStatusDisplay(JOB_ID, 1, msg);
			}
		}
		if (msg['workflowStep'] == 2) {
			$j('#stepmessage1').html("Files verified");
			if (msg['status'] == 'failed') {
				showFailure(2);
				getErrorDisplay(JOB_ID, 2, msg);
				UPDATE = false;
			}
			else {
				showSuccess(1);
				showRunning(2);
				getStatusDisplay(JOB_ID, 2, msg);
			}
		}
		if (msg['workflowStep'] == 3) {
			$j('#stepmessage2').html("Validation complete");
			if (msg['status'] == 'failed') {
				showFailure(3);
				getErrorDisplay(JOB_ID, 3, msg);
				UPDATE = false;
			}
			else {
				showSuccess(2);
				showRunning(3);
				getStatusDisplay(JOB_ID, 3, msg);
			}
		}
		if (msg['workflowStep'] == 4) {
			$j('#stepmessage3').html("Load complete");
			if (msg['status'] == 'failed') {
				showFailure(4);
				getErrorDisplay(JOB_ID, 4, msg);
				UPDATE = false;
			}
			else {
				showSuccess(3);
				showRunning(4);
				getStatusDisplay(JOB_ID, 4, msg);
			}
		}
		if (msg['workflowStep'] == 5) {
			$j('#stepmessage4').html("Profiling complete");
			if (msg['status'] == 'paused') {
				showSuccess(4);
				showPaused(5);
				getPausedDisplay(JOB_ID, 5, msg);
				UPDATE = false;
			}
		}
		if (msg['workflowStep'] == 6) {
			if (msg['status'] == 'failed') {
				showFailure(6);
				getErrorDisplay(JOB_ID, 6, msg);
				UPDATE = false;
			}
			else if (msg['status'] == 'paused') {
				showPaused(6);
				getPausedDisplay(JOB_ID, 6, msg);
				UPDATE = false;
			}
		}
		if (msg['workflowStep'] == 7) {
			if (msg['status'] == 'failed') {
				showFailure(7);
				getErrorDisplay(JOB_ID, 7, msg);
				UPDATE = false;
			}
			else if (msg['status'] == 'paused') {
				showPaused(7);
				getPausedDisplay(JOB_ID, 7, msg);
				UPDATE = false;
			}
			else {
				showRunning(7);
				getStatusDisplay(JOB_ID, 7, msg);
			}
		}
		if (msg['workflowStep'] == 8) {
			if (msg['status'] == 'failed') {
				showFailure(8);
				getErrorDisplay(JOB_ID, 8, msg);
				UPDATE = false;
			}
			else {
				if (!($j('#stepicon7').hasClass('skipped'))) {
					showSuccess(7);
					$j('#stepmessage7').html("Vocabulary import complete");
				}
				showRunning(8);
				getStatusDisplay(JOB_ID, 8, msg);
			}
		}
		if (msg['workflowStep'] == 9) {
			$j('#stepmessage8').html("Processing complete");
			if (msg['status'] == 'failed') {
				showFailure(9);
				getErrorDisplay(JOB_ID, 9, msg);
				UPDATE = false;
			}
			else {
				showSuccess(8);
				showRunning(9);
				getStatusDisplay(JOB_ID, 9, msg);				
			}
		}
		if (msg['workflowStep'] == 10) {
			$j('#stepmessage9').html("Profiling complete");
			if (msg['status'] == 'paused') {
				showSuccess(9);
				showPaused(10);
				getPausedDisplay(JOB_ID, 10, msg);
				UPDATE = false;
			}
		}
		if (msg['workflowStep'] == 11) {
			if (msg['status'] == 'failed') {
				showFailure(11);
				getErrorDisplay(JOB_ID, 11, msg);
				UPDATE = false;
			}
			else {
				showRunning(11);
				getStatusDisplay(JOB_ID, 11, msg);
			}
		}
		if (msg['workflowStep'] == 12) {
			$j('#stepmessage11').html("Publish complete");
			if (msg['status'] == 'failed') {
				showFailure(12);
				getErrorDisplay(JOB_ID, 12, msg);
				UPDATE = false;
			}
			else {
				showSuccess(11);
				showRunning(12);
				getStatusDisplay(JOB_ID, 12, msg);
			}
		}
		if (msg['workflowStep'] == 13) {
			showSuccess(12);
			showSuccess(13);
			$j('#stepmessage12').html("Backup complete");
			$j('#stepmessage13').html("Workflow complete!");
			UPDATE = false;
		}
		
		if (UPDATE) {
			startUpdating();
		}
		else {
			AJAX_TIMER = null;
		}
	});
}

function clearAndShowWorking(elementId, text, big) {
	$j('#' + elementId).empty();
	var src = "/mdm/images/spinner.gif";
	var size = 16;
	if (big) {
		src = "/mdm/images/spinnerbig.gif";
		size = 66;
	}
	var imageElement = $j('<img>', { 
	    src : src, 
	    width : size,
	    height : size,
	    alt : "Working",
	    title : "Working"
	});
	
	var divElement = $j('<div>', {
		style : "width: 100%; height: 100%; vertical-align: middle; text-align: center"
	});
	
	if (text) {
		$j('#' + elementId).append(divElement);
		divElement.append(imageElement).append(' ' + text);
	}
	else {
		$j('#' + elementId).append(divElement);
		divElement.append(imageElement);
	}
}

function startJob(jobId) {
	callAjax('nothing', '/rosita/rositaJob/start/' + JOB_ID, {}, false, startUpdating);
	showRunning(1);
	$j('#stepicon1').attr('onclick', '');
	$j('#stepmessage1').html("Starting...");
	$j('#stepmessage1').empty();
}

function restartJob(jobId) {
	showRunning('restart');
	$j('#stepiconrestart').attr('onclick', '');
	window.location = '/rosita/rositaJob/restart/' + JOB_ID 
}

function cancelJob(jobId) {
	showRunning('restart');
	$j('#stepiconrestart').attr('onclick', '');
	window.location = '/rosita/rositaJob/cancel/' + JOB_ID 
}

function resumeJob(jobId, stepId) {
	callAjax('nothing', '/rosita/rositaJob/start/' + JOB_ID, {step: stepId}, false, startUpdating);
	showRunning(stepId);
	$j('#stepicon' + stepId).attr('onclick', '');
	$j('#stepmessage' + stepId).html("Resuming...");
}

function runImport(filename, stepId) {
	if (filename == null || filename == '') {
		return;
	}
	callAjax('nothing', '/rosita/rositaJob/runImport/' + JOB_ID, {filename: filename, stepId: stepId}, false, startUpdating);
	showRunning(7);
	$j('#stepicon7').attr('onclick', '');
	$j('#stepmessage7').html("Importing...");
}

function confirmStep(jobId, stepNum, stepId) {
	callAjax('nothing', '/rosita/rositaJob/confirm/' + JOB_ID, {wfStep: stepId, step: stepNum}, false, startUpdating);
	showSuccess(stepNum);
	showRunning(stepNum+1);
	$j('#stepicon' + stepNum).attr('onclick', '');
	$j('#stepmessage' + stepNum).html("Confirmed");
}

function unpauseStep(jobId, stepNum, stepId) {
	callAjax('nothing', '/rosita/rositaJob/unpause/' + JOB_ID, {wfStep: stepId, step: stepNum}, false, startUpdating);
	showRunning(stepNum);
	$j('#stepicon' + stepNum).attr('onclick', '');
	$j('#stepmessage' + stepNum).html("Starting...");
}

function skipStep(jobId, stepNum, stepId) {
	callAjax('nothing', '/rosita/rositaJob/skip/' + JOB_ID, {wfStep: stepId, step: stepNum}, false, startUpdating);
	showSkipped(stepNum);
	showRunning(stepNum+1);
	$j('#stepicon' + stepNum).attr('onclick', '');
	$j('#stepmessage' + stepNum).html("Skipped");
}

function startUpdating() {
	UPDATE = true;
	AJAX_TIMER = setTimeout(function() {updateScreen();}, UPDATE_FREQUENCY);
}

function getBar(numerator, denominator, color, label) {
	var html = "";
	html += "<div class='progressbarborder' style='border-color: " + color + "'>";
	var barwidth = Math.round((numerator*540)/denominator);
	if (barwidth > 540) {barwidth = 540;}
	if (denominator == 0) {barwidth = 0;}
	html += "<div class='progressbar' style='background-color: " + color + "; width: " + barwidth + "px'>&nbsp;</div>"
	html += "</div>";
	html += "<table class='progressbartable' width='540'><tr><td>";
	if (label != null) {
		html += label + ": ";
	}
	if (denominator != 0) {
		html += numerator + "/" + denominator;
		html += "</td><td align='right'>";
		html += Math.round(barwidth/5.4) + "%";
		html += "</td></tr></table>";
	}
	else {
		html += numerator + "/ (Unknown)";
		html += "</td></tr></table>";
	}

	return html;
}

function updateConsoleIcon(id) {
	request = $j.ajax({
		url: '/rosita/rositaJob/getConsoleIcon',
		type: 'POST',
		data: {jobId:JOB_ID, stepId: id}
	});
	
	request.fail(function(jqXHR, textStatus) {
//		if (jqXHR.responseText) {
//			alert(jqXHR.responseText);
//		}
	});
	
	request.done(function(msg) {
		$j('#stepconsole' + id).html(msg);
	});
}

function getErrorDisplay(id, wfStep, params) {
	request = $j.ajax({
		url: '/rosita/workflowStep/error',
		type: 'POST',
		data: {jobId:JOB_ID, wfStep: wfStep, params: params}
	});
	
	request.fail(function(jqXHR, textStatus) {
	});
	
	request.done(function(msg) {
		$j('#stepmessage' + wfStep).html(msg);
	});
}

function getStatusDisplay(id, wfStep, params) {
	request = $j.ajax({
		url: '/rosita/workflowStep/status',
		type: 'POST',
		data: {jobId:JOB_ID, wfStep: wfStep, params: params}
	});
	
	request.fail(function(jqXHR, textStatus) {
	});
	
	request.done(function(msg) {
		$j('#stepmessage' + wfStep).html(msg);
	});
}

function getPausedDisplay(id, wfStep, params) {
	request = $j.ajax({
		url: '/rosita/workflowStep/paused',
		type: 'POST',
		data: {jobId:JOB_ID, wfStep: wfStep, params: params}
	});
	
	request.fail(function(jqXHR, textStatus) {
	});
	
	request.done(function(msg) {
		$j('#stepmessage' + wfStep).html(msg);
	});
}

function showSuccess(id) {
	$j('#stepicon'+id).removeClass();
	$j('#stepicon'+id).addClass('stepicon');
	$j('#stepicon'+id).addClass('success');
	updateConsoleIcon(id);
}

function showRunning(id) {
	$j('#stepicon'+id).removeClass();
	$j('#stepicon'+id).addClass('stepicon');
	$j('#stepicon'+id).addClass('running');
	updateConsoleIcon(id);
}

function showFailure(id) {
	$j('#stepicon'+id).removeClass();
	$j('#stepicon'+id).addClass('stepicon');
	$j('#stepicon'+id).addClass('failed');
	updateConsoleIcon(id);
}

function showPaused(id) {
	$j('#stepicon'+id).removeClass();
	$j('#stepicon'+id).addClass('stepicon');
	$j('#stepicon'+id).addClass('paused');
}

function showSkipped(id) {
	$j('#stepicon'+id).removeClass();
	$j('#stepicon'+id).addClass('stepicon');
	$j('#stepicon'+id).addClass('skipped');
}

// -------------

function transferFilename(path) {
	var filename = $j('#filenamePicker').val()
	if (filename == null || filename == '') {
		$j('#filename').val('');
	}
	else {
		$j('#filename').val(path + filename);
	}
}