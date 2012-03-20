<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String pdsUrl = (String)request.getAttribute("pdsUrl");
String message = (String)request.getAttribute("message");
Throwable exception = (Throwable)request.getAttribute("exception");
HttpServletRequest req = (HttpServletRequest)request.getAttribute("req");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Harvard University Library PDS - ERROR</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="<%=pdsUrl%>/css/pds.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#DDDDDD" link="#000000" vlink="#000000" onload="self.focus();">
	<table bgcolor="#DDDDDD" width="100%" border="0" cellpadding="3" cellspacing="0">
	<tr>
	<td width="45">
	<span>
	<img src="<%=pdsUrl%>/images/PDSshieldSmall.gif" alt="Veritas" width="45" height="53">
	</span>
	</td>
	<td width="145" align="left" valign="top" nowrap>
	<span class="textsmallred"><font color="#000000" size="1"><nobr>Harvard University Library</nobr></font><br>
	<font size="2"><nobr><a href="/" class="redtextlink" target="_top">Page Delivery Service</a></nobr></font><br></span>
	</td><td align="center" valign="middle"><font color="#990000" size="5" face="Verdana, Arial, Helvetica, sans-serif">
	<%=message%>
	</font>
	</td>
	</tr></table><hr>
	<center>
	<table class="searchDiv" width="92%" border="0" cellpadding="6" bgcolor="#FFFFFF">
	<tr class="pdsbasic80">
	<td>
	<br>
	Click the back button in your browser to return to the previous page.
	<!-- 
	<a href="javascript:history.go(-1)">Click here to go back</a></strong>
	-->
	<br>
	<%
	if(exception!=null) {
	%>
		<p><strong>Error</strong> : 
		<%=exception.getMessage()%>
		</p>
	<%} %>
	</td></tr>
	</table>
	</center>
</body>
</html>