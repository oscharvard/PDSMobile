<?xml version="1.0" encoding="utf-8"?>
<%@ page language="java" contentType="text/xml; charset=UTF-8"
    pageEncoding="UTF-8"
    import="edu.harvard.hul.ois.pdx.util.CitationDiv,
    		edu.harvard.hul.ois.pdx.util.PageDiv,
    		edu.harvard.hul.ois.pdx.util.UnicodeUtils,
    		java.util.*"%>
<%
CitationDiv cite = (CitationDiv)request.getAttribute("cite");
PageDiv pdiv = (PageDiv)request.getAttribute("pdiv");
String nStr = (String)request.getAttribute("nStr");

if (nStr == null || nStr.equals("")) {
nStr = "1";
}

Integer id = (Integer)request.getAttribute("id");
String pdsUrl = (String)request.getAttribute("pdsUrl");
String repos = (String)request.getAttribute("repos");
String mainUrn = (String)request.getAttribute("mainUrn");
String pageUrn = (String)request.getAttribute("pageUrn");
String sectLabels = (String)request.getAttribute("sectLabels");
String accDate  = (String)request.getAttribute("accDate");

String desc = cite.getDisplayLabel();
String pageDisp = "";

if(pdiv.getLabel() == null || pdiv.getLabel().equals("")) {
	pageDisp = "";
}
else {
	pageDisp = pdiv.getLabel();
}
%>
<citation>
    <resource>
        <urn><%=mainUrn%></urn>
        <description><%=desc %></description>
        <repository><%=repos %></repository>
        <institution>Harvard University</institution>
        <accessed><%=accDate %></accessed>
    </resource>
    <page>
        <urn><%=pageUrn%></urn>
        <description><%=desc %><%= (!desc.endsWith(".")?".":"") %><%=sectLabels %></description>
        <pagelabel><%=pageDisp %> (seq. <%=nStr %>)</pagelabel>
        <repository><%=repos %></repository>
        <institution>Harvard University</institution>
        <accessed><%=accDate %></accessed>
    </page>
</citation>