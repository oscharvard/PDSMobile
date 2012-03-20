<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="edu.harvard.hul.ois.pdx.util.CitationDiv,
    		edu.harvard.hul.ois.pdx.util.PageDiv,
    		edu.harvard.hul.ois.pdx.util.UnicodeUtils"%>
    
<%
String helpText = (String)request.getAttribute("helpText");
String text = (String)request.getAttribute("text");
String helpUri = (String)request.getAttribute("helpUri");
Boolean showHelp = (Boolean)request.getAttribute("showHelp");
String pdsUrl = (String)request.getAttribute("pdsUrl");
CitationDiv cite = (CitationDiv)request.getAttribute("cite");
PageDiv pdiv = (PageDiv)request.getAttribute("pdiv");

String orderLabel = null;
String titleLabel = "";
if(pdiv.getOrderLabel() == null || pdiv.getOrderLabel().equals("")) {
	orderLabel  = "";
}
else {
	orderLabel = "Page "+pdiv.getOrderLabel();
	titleLabel = " (Page "+pdiv.getOrderLabel()+")";
}
%>
   
 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="robots" content="noindex,nofollow" />
<link href="<%=pdsUrl%>/css/pds.css" rel="stylesheet" type="text/css"/>
<title><%=UnicodeUtils.getBidiAttribute("Sequence "+pdiv.getOrder()+ UnicodeUtils.BIDI_DELIM + titleLabel+": "+UnicodeUtils.BIDI_DELIM+ cite.getDisplayLabel()+ "," + UnicodeUtils.BIDI_DELIM + "Harvard University Library PDS")%></title>
</head>
<body bgcolor="#DDDDDD" link="#000000" vlink="#000000" onload="self.focus();">
	<table bgcolor="#DDDDDD" width="100%" border="0" cellpadding="3" cellspacing="0">
	<tr>
	<td width="45">
	<span>
	<img src="<%=pdsUrl%>/images/PDSshieldSmall.gif" title="" alt="Harvard University" width="45" height="53"/>
	</span>
	</td>
	<td width="145" valign="top" nowrap="nowrap">
	<span class="textsmallred"><font color="#000000" size="1">Harvard University Library</font><br/>
	<font size="2"><a href="/" class="redtextlink" target="_top">Page Delivery Service</a></font>
	</span></td>
	
	<td align="center" valign="middle"><font size="5" face="Verdana, Arial, Helvetica, sans-serif"><%=text%></font>
	</td>
	<%
	if(showHelp) {
	%>
		<td valign="bottom" align="right" class="pdsbasic80" nowrap="nowrap"><img src="<%=pdsUrl%>/images/help.gif" alt="Help" title="Tips" width="16" height="18" align="bottom"/>
		Tips on <a href="<%=helpUri%>" target="_blank" ><%=helpText%></a>&nbsp;&nbsp;&nbsp;&nbsp;
		</td>
	<% } %>
	</tr></table><hr/>
