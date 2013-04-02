<div class="progressbarborder" style="border-color: ${color}">
	<g:if test="${!numerator}"><g:set var="numerator" value="${0}"/></g:if>
	<g:if test="${!denominator}"><g:set var="denominator" value="${0}"/></g:if>
	<g:if test="${!(numerator instanceof Long || numerator instanceof Integer)}">
		<g:set var="numerator" value="${Long.parseLong(numerator)}"/>
	</g:if>
	<g:if test="${!(denominator instanceof Long || denominator instanceof Integer)}">
		<g:set var="denominator" value="${Long.parseLong(denominator)}"/>
	</g:if>
	<g:set var="barwidth" value="${Math.round((numerator*540)/denominator)}"/>
	<g:if test="${barwidth > 540}">
		<g:set var="barwidth" value="${540}"/>
	</g:if>
	<g:if test="${denominator == 0}">
		<g:set var="barwidth" value="${0}"/>
	</g:if>
	<div class="progressbar" style="background-color: ${color}; width: ${barwidth}px">&nbsp;</div>
</div>

<table class="progressbartable" width="540">
	<tr>
		<td>
			<g:if test="${label}">
				${label}: 
			</g:if>
			<g:if test="${denominator != 0}">
				${numerator}/${denominator}
			</g:if>
			<g:else>
				${numerator}
			</g:else>
		</td>
		
		<td align="right">
			<g:if test="${barwidth}">
			${Math.round(barwidth/5.4)}%
			</g:if>
		</td>
	</tr>
</table>