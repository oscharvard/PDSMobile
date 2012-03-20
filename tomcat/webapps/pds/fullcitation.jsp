<%@ page language="java" contentType="text/html; charset=UTF-8"
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

String desc = UnicodeUtils.getBidiDivedElement(cite.getDisplayLabel());
String pageDisp = "";

if(pdiv.getLabel() == null || pdiv.getLabel().equals("")) {
	pageDisp = "";
}
else {
	pageDisp = pdiv.getLabel();
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="robots" content="noindex,nofollow" />

<title>Cite This Resource</title>
<link href="<%=pdsUrl%>/css/pds.css" rel="stylesheet" type="text/css"/>
<style type="text/css">
h1 {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 90%;
	font-weight: bold;
	margin-left: 0.4em;
}
h2 {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 80%;
	color: #990000;
	margin-top: 1.5em;
	margin-bottom: -1em;
	margin-left: 0.4em;
}
dl {
    padding: 0.5em;
    font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 75%;
	line-height: 150%;
  }
  dl dt {
    float: left;
    clear: left;
    text-align: left;
    font-weight: bold;
	padding-right: .8em;
	margin-left: 1.3em;
  }
 
  dl dd {
    
  }
 .note {    
 	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 75%;
	line-height: 150%;
	margin-left: 0.4em;
	margin-right: .4em;
}
 

</style>

</head>
<!-- label |<%=pdiv.getLabel()%>| order: |<%=pdiv.getStringOrder()%>| -->
<body bgcolor="#DDDDDD" link="#000000" vlink="#000000">
<h1>Citation Information</h1>
<div class="note">Please note that a complete citation may require additional information (document date, author's name, range of pages/sequence numbers, etc.)</div>


<h2>Resource:</h2>
<dl>
<dt>Persistent Link:</dt> <dd class="resUrn"><%=mainUrn%></dd>
<dt>Description:</dt> <dd class="resDes"> <%=desc %></dd>
<dt class="req">Repository:</dt> <dd class="resRep"> <%=repos %></dd>
<dt>Institution:</dt> <dd class="resInst"> Harvard University</dd>
<dt class="req">Accessed:</dt> <dd class="accDate"> <%=accDate %></dd>
</dl>


<h2>Page:</h2>
<dl>
<dt>Persistent Link:</dt> <dd class="resUrn"> <%=pageUrn%></dd>
<dt>Description:</dt> <dd class="resDes"><%=desc %><%= (!desc.endsWith(".")?".":"") %> <%=sectLabels %></dd>
<dt>Page:</dt> <dd class="resPg"><%=pageDisp %> (seq. <%=nStr %>)</dd>
<dt>Repository:</dt> <dd class="resRep">  <%=repos %></dd>
<dt>Institution:</dt> <dd class="resInst"> Harvard University</dd>
<dt>Accessed:</dt> <dd class="accDate"> <%=accDate %></dd>
</dl>


<div class="pdsbasic80" align="center">
	<input class="buttonhighlightred" value="  Close  " onclick="window.close()" name="button" type="button"/>
	</div>

</body>
</html>