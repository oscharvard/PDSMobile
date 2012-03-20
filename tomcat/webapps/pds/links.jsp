<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="edu.harvard.hul.ois.pdx.util.CitationDiv,
   			edu.harvard.hul.ois.pdx.util.Identifier,
	    	java.util.*,
	    	edu.harvard.hul.ois.pdx.util.PageDiv,
    		edu.harvard.hul.ois.pdx.util.UnicodeUtils;"
    %>
	<%
	String pdsUrl = (String)request.getAttribute("pdsUrl");
	Integer id = (Integer)request.getAttribute("id");
	CitationDiv cite = (CitationDiv)request.getAttribute("cite");
	request.setAttribute("text","Related Links");
	request.setAttribute("helpUri","#");
	request.setAttribute("helpText","");
	request.setAttribute("showHelp",Boolean.valueOf(false));
	request.setAttribute("pdsUrl",pdsUrl);
	List identifiers = cite.getIdentifiers ();
	Iterator iter = identifiers.iterator ();
	String text = (String)request.getAttribute("text");
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
<meta name="robots" content="index,follow" />
<link href="<%=pdsUrl%>/css/pds.css" rel="stylesheet" type="text/css"/>
<title><%=UnicodeUtils.getBidiAttribute("Sequence "+pdiv.getOrder()+ UnicodeUtils.BIDI_DELIM + titleLabel+": "+ UnicodeUtils.BIDI_DELIM + cite.getDisplayLabel()+ UnicodeUtils.BIDI_DELIM + "Harvard University Library PDS")%></title>
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

	</tr></table><hr/>
				
	<table width="92%" align="center"><tr><td><div class="stdLinksWrap"><strong>Links relating to: 
	</strong>
	<a href="<%=pdsUrl%>/view/<%=id%>" target="_blank"><%=UnicodeUtils.getBidiDivedElement(cite.getDisplayLabel())%></a>
	</div></td></tr></table>
	<table cellspacing="15" border="0" width="100%"><tr>
		<td class="stdLinksWrap" valign="top" width="30%">
		<p><strong>Related Links</strong> take you to information outside of PDS that relates to 
		the displayed document. E.g.,  a link to a description of the document in HOLLIS,  to a finding aid
		in OASIS, or to a specialized web site with additional information.</p>
  		<p>Click on the link and the information will display in a new browser window. </p></td>
					
	<td valign="top"><table width="100%" class="searchDiv" border="0" cellpadding="6" bgcolor="#FFFFFF">
	<%while (iter.hasNext ()) {%>
		<tr><td>
		<%
		Identifier identifier = (Identifier) iter.next ();
		if(identifier.getType().compareToIgnoreCase("hollis") == 0) {
			if(identifier.getDisplayLabel()!=null && identifier.getDisplayLabel().length()>0) {
		%>
				<a href="http://hollisclassic.harvard.edu/F?func=find-c&amp;CCL_TERM=sys=<%=identifier.getValue()%>"
					target="_blank" class="citLinksLine"><%=UnicodeUtils.getBidiSpannedElement(identifier.getDisplayLabel(),true)%></a><br/>
		<%} else { %>
				<a href="http://hollisclassic.harvard.edu/F?func=find-c&amp;CCL_TERM=sys=<%=identifier.getValue()%>"
					target="_blank" class="citLinksLine">HOLLIS <%=identifier.getValue()%></a><br/>
		<%	}
		}
		else if(identifier.getType().compareToIgnoreCase("oldhollis") == 0) {
			if(identifier.getDisplayLabel()!=null && identifier.getDisplayLabel().length()>0) {
		%>
				<a href="http://hollisclassic.harvard.edu/F?func=find-c&amp;CCL_TERM=KHA=<%=identifier.getValue()%>" 
					target="_blank" class="citLinksLine"><%=UnicodeUtils.getBidiSpannedElement(identifier.getDisplayLabel(),true)%></a><br/>
			<%} else {%>
				<a href="http://hollisclassic.harvard.edu/F?func=find-c&amp;CCL_TERM=KHA=<%=identifier.getValue()%>"
					target="_blank" class="citLinksLine">HOLLIS "<%=identifier.getValue()%></a><br/>
		<%	}					
		}
		else if (identifier.getType().compareToIgnoreCase("uri") == 0) {
			if(identifier.getDisplayLabel()!=null && identifier.getDisplayLabel().length()>0) {
		%>
				<a href="<%=identifier.getValue()%>"
					target="_blank" class="citLinksLine"><%=UnicodeUtils.getBidiSpannedElement(identifier.getDisplayLabel(),true)%></a><br/>
			<%} else {%>
				<a href="<%=identifier.getValue()%>"
					target="_blank" class="citLinksLine">URL <%=identifier.getValue()%></a><br/>
		<%	}
		}
		else {
			if(identifier.getDisplayLabel()!=null && identifier.getDisplayLabel().length()>0) {
		%>
				<div class="citLinksLine"><%=UnicodeUtils.getBidiSpannedElement(identifier.getDisplayLabel(),true)%> <%=identifier.getValue()%></div>
			<%}else {%>
				<div class="citLinksLine"><%=identifier.getType()%> <%=identifier.getValue()%></div>
		<%	}
		}%>
		</td></tr>
	 <% } %>
	 </table><br/>
	 <table width="100%"><tr><td class="pdsbasic80" align="right"><input type="button" class="buttonhighlightred" value="  Close  " onclick="window.close()" name="button"/></td></tr></table>
	 </td></tr></table><br/>
</body>
</html>