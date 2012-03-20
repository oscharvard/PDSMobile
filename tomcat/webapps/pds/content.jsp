<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="edu.harvard.hul.ois.pdx.util.PageDiv,
                 edu.harvard.hul.ois.pdx.util.CitationDiv,
    			edu.harvard.hul.ois.pdx.util.UnicodeUtils,
                 java.util.ArrayList,
	             java.util.List,
                 java.io.FileInputStream,
				 java.io.BufferedReader,
				 java.io.InputStreamReader,
				 java.io.File,
				 edu.harvard.hul.ois.pds.Utils;" %>            
<%
CitationDiv cite = (CitationDiv)request.getAttribute("cite");
PageDiv pdiv = (PageDiv)request.getAttribute("pdiv");
String scale = (String)request.getAttribute("s");
int n = Integer.parseInt((String)request.getAttribute("n"));
String imageId = (String)request.getAttribute("imageId");
String mime = (String)request.getAttribute("mime");
String filepath = (String)request.getAttribute("filepath");
String lastPage = (String)request.getAttribute("lastPage");
String idsUrl = (String)request.getAttribute("idsUrl");
String cache = (String)request.getAttribute("cache");
String cacheUrl = (String)request.getAttribute("cacheUrl");
String jp2Rotate = (String)request.getAttribute("jp2Rotate");
//String jp2Res = (String)request.getAttribute("jp2Res");
String jp2x = (String)request.getAttribute("jp2x");
String jp2y = (String)request.getAttribute("jp2y");
String imagesize = (String)request.getAttribute("imagesize");
ArrayList ocrList = (ArrayList)request.getAttribute("ocrList");
//String viewOcr = (String)request.getAttribute("viewOcr");
String action = (String)request.getAttribute("action");
String jp2Res = (String)request.getAttribute("jp2Res");
String pdsUrl = (String)request.getAttribute("pdsUrl");
Integer id = (Integer)request.getAttribute("id");
String caption = (String)request.getAttribute("caption");

boolean tiff = false;
boolean jp2  = false;
boolean jpg  = false;
boolean text = false;
if(mime.equals("image/tiff")) {
	tiff = true;
}
else if(mime.equals("image/jp2") ||
		mime.equals("image/jpx")) {
	jp2 = true;
}
else if(mime.equals("image/jpeg") ||
		mime.equals("image/gif")) {
	jpg = true;
}
else if(mime.equals("text/plain")) {
	text = true;
}
boolean showocr = action.equalsIgnoreCase("viewtext") || text;
String orderLabel = null;
String titleLabel = "";
String orderDisplayLabel = "";
if(pdiv.getOrderLabel() == null || pdiv.getOrderLabel().equals("")) {
	orderLabel  = "";
	orderDisplayLabel = "No Page Number";
}
else {
	orderLabel = "Page "+pdiv.getOrderLabel();
	titleLabel = " (Page "+pdiv.getOrderLabel()+")";
	orderDisplayLabel = orderLabel;
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="robots" content="index,follow" />
<script type="text/javascript" language="JavaScript" src="<%=pdsUrl%>/js/deepweb.js" ></script>
<script type="text/javascript" language="JavaScript"><!--
function loaded(){
	if (self != top) {
		hideElement('cit', true);
		hideElement('nav', true);
	}
	else {
		hideElement('cit', false);
		hideElement('nav', false);
	}

}
//--></script>
<link href="<%=pdsUrl%>/css/pds.css" rel="stylesheet" type="text/css"/>
<title><%=UnicodeUtils.getBidiAttribute("Sequence "+n+UnicodeUtils.BIDI_DELIM + titleLabel+": "+ UnicodeUtils.BIDI_DELIM + cite.getDisplayLabel()+ "," + UnicodeUtils.BIDI_DELIM + "Harvard University Library PDS")%></title>
</head>
<%

if (showocr) {%><body onload="loaded();"><%} else {%><body bgcolor="#DDDDDD" onload="loaded();"><% } %>

<!--  PDS LOGO AND LABEL -->
<div id="cit" class="noframediv">
	<table width="100%" border="0" cellpadding="3" cellspacing="0">
	<tr>
	<td width="45">
	<span>
	<img src="<%=pdsUrl%>/images/PDSshieldSmall.gif" alt="Veritas" width="45" height="53"/>
	</span>
	</td>
	<td width="145" valign="top" nowrap="nowrap">
	<span class="textsmallred"><font color="#000000" size="1">Harvard University Library</font><br/>
	<font size="2"><a href="/" class="redtextlink" target="_top">Page Delivery Service</a></font>
	</span></td>

	<!-- TITLE OF METS FILE -->
	<td valign="top">
	
	<div class="citationDiv">
	<%=UnicodeUtils.getBidiDivedElement(cite.getDisplayLabel())%>
	</div>
	
	</td></tr>
	<tr><td colspan="3" align="center"><%="Sequence "+pdiv.getOrder()+" of "+cite.getLastPageNumber()+ " ("+orderDisplayLabel+")"%></td></tr>
	<tr><td colspan="3" align="center" class="noframediv2"><a href="<%=pdsUrl%>/view/<%=id%>?n=<%=n%>">View framed version of this document</a></td></tr>
	</table>
</div>

<%
if (showocr) {
	//print the ocr
	List<Object> printedOCR = new ArrayList<Object>();
	for(int i=0; i<ocrList.size();i++) {
	    if(!printedOCR.contains(ocrList.get(i))) {
			File src = new File((String)ocrList.get(i));
			FileInputStream fstream = new FileInputStream(src);
			BufferedReader in = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
			while(in.ready()) {
				String line = in.readLine();
				line = Utils.escapeHtml(line);
				out.println (line);
				out.println("<br/>");
			}
			in.close();			
			printedOCR.add(ocrList.get(i));
	    }
	}
}
	
else if(jpg) {
%>
	<img src="<%=idsUrl+"/"%><%=imageId%>?xcap=<%=caption%>" alt="Sequence <%=n%> of <%=lastPage%>" title="Sequence <%=n%> of <%=lastPage%>"/>
<%
}
else if(jp2) {
%>
	<img src="<%=idsUrl+"/"%><%=imageId%>?s=<%=jp2Res%>&amp;rotation=<%=jp2Rotate%>&amp;width=<%=imagesize%>&amp;height=<%=imagesize%>&amp;x=<%=jp2x%>&amp;y=<%=jp2y%>&amp;xcap=<%=caption%>" title="Sequence <%=n%> of <%=lastPage%>" alt="Sequence <%=n%> of <%=lastPage%>"/>
<%
}
else if(tiff) {
%>
	<img src="<%=cacheUrl%><%=imageId%>-<%=scale%>.gif" alt="Sequence <%=n%> of <%=lastPage%>" title="Sequence <%=n%> of <%=lastPage%>"/>
<%
}
%>

<div id="nav" class="noframediv">
	<table width="100%" border="0" cellpadding="3" cellspacing="0" class="noframediv2">
	<tr>
	<td align="left" nowrap="nowrap">
		<!-- Previous Page Link -->
		<%
		if (n > 1) { %>
			<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?op=t&amp;n=<%=n-1%>&amp;s=<%=scale%>" onclick='javascript:getFrameSize()' target="_top">
			<img border="0" src="<%=pdsUrl%>/images/PDSprev.gif" title="Go to sequence <%=n-1%>" alt="Go to sequence <%=n-1%>"/>
			</a>
		<%} else {  %>
			<a href="#">
			<img border="0" src="<%=pdsUrl%>/images/PDSprevInactive.gif" title="No Previous Sequence" alt="No Previous Sequence"/>
			</a>
		<%
		}
		%>
	</td>
	<td align="center"><a href="<%=pdsUrl%>/<%=action%>/<%=id%>?op=n&amp;n=<%=n%>&amp;treeaction=expand" title="Table of Contents">Table of Contents</a></td>
	<td align="right" nowrap="nowrap">
	<!-- Next Page Link -->
	<%
	if (n < cite.getLastPageNumber()) { %>
		<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?op=t&amp;n=<%=n+1%>&amp;s=<%=scale%>" onclick='javascript:getFrameSize()' target="_top">
		<img border="0" src="<%=pdsUrl%>/images/PDSnext.gif" title="Go to sequence <%=n+1%>" alt="Go to sequence <%=n+1%>"/>
		</a>
	<% }else { %>
		<a href="#">
		<img border="0" src="<%=pdsUrl%>/images/PDSnextInactive.gif" title="No Next Sequence" alt="No Next Sequence"/>
		</a>
	<% } %>

	</td>
	</tr>
	<tr>
	<td colspan="3" align="center">
	<%
	Boolean viewocr = cite.getviewocrFlag();
	if((viewocr == null || viewocr.booleanValue()) && pdiv.getOcrID().size() != 0) {
		// if the default page image is OCR, print "View Image" grayed out
		if(action.equalsIgnoreCase("viewtext")) { %>
			<a href="<%=pdsUrl%>/view/<%=id%>?op=t&amp;n=<%=n%>&amp;s=<%=scale%>" title="Display page image">View Image</a>
		<% } else {  %>
			<a href="<%=pdsUrl%>/viewtext/<%=id%>?op=t&amp;n=<%=n%>" title="Display text for image">View Text</a>
		<%}
	}
	else { %>
		No text for this page
	<% } %>
	</td>
	</tr>
	</table>
</div>

</body>
</html>
