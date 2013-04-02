<!DOCTYPE HTML5>
<html>
    <head>
        <title>ROSITA</title>
        <script src="${resource(dir:'js',file:'rosita.js')}"></script>
		<script src="${resource(dir:'js',file:'jquery.js')}"></script>
		<g:javascript>$j = jQuery.noConflict();</g:javascript>
		<link rel="stylesheet" href="${resource(dir:'css',file:'rosita.css')}" />
        <g:layoutHead />
    </head>

	<body style="width: 100%; margin: 0;">
	<center>
		<!-- begin header -->
		<table style="width:100%; padding: 3px; background-color: #FFFFFF;">
			<tr>
				<td style="width:75%;">
					<a href="${createLink([action:'index',controller:'rositaJob'])}"><img src="${resource(dir:'images',file:'banner.png')}"/></a>
				</td>	
				<td style="width:25%; text-align:right; padding-right:10px; color:#eee; white-space:nobr;">
					<span class="header_top_nav" style="color: #eee;">ROSITA</span>
					<g:isLoggedIn>
						<g:link controller="logout" class="top_navlink">Log off</g:link><br/>
						<g:link controller="user" action="edit" class="top_navlink" style="font-size: 8pt">Change login</g:link>
					</g:isLoggedIn>
					<g:isNotLoggedIn>
						<g:link controller="login" class="top_navlink">Log on</g:link>
					</g:isNotLoggedIn>
				</td>
			</tr>
		</table>
		<!-- end header -->	
			
		<div id="content" style="width: 80%;"><g:layoutBody /></div>
	</center>
	</body>
</html>