<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="edu.harvard.hul.ois.pdx.util.CitationDiv,
    		edu.harvard.hul.ois.pdx.util.PageDiv,
			edu.harvard.hul.ois.pdx.util.IntermediateDiv,
			edu.harvard.hul.ois.pds.user.PdsUserState,
    		edu.harvard.hul.ois.pdx.util.UnicodeUtils,
			java.io.IOException,
			java.io.PrintWriter,
    		java.util.*"%>       
<%
CitationDiv cite = (CitationDiv)request.getAttribute("cite");
PageDiv pdiv = (PageDiv)request.getAttribute("pdiv");
int n = Integer.parseInt((String)request.getAttribute("n"));
int s = Integer.parseInt((String)request.getAttribute("s"));
String jp2Rotate = (String)request.getAttribute("jp2Rotate");
String jp2Res = (String)request.getAttribute("jp2Res");
String imagesize = (String)request.getAttribute("imagesize");
Integer id = (Integer)request.getAttribute("id");
String pdsUrl = (String)request.getAttribute("pdsUrl");
String action = (String)request.getAttribute("action");
PdsUserState pdsUser = (PdsUserState)request.getAttribute("pdsUser");
List children = cite.getChildren();
int numberOfChildren = children.size();
int index = -1;
int level = 0;

//set print thumbnail flags
String printThumbFlag = (String)request.getAttribute("printThumbnails");
boolean printThumbnails = false;
if ( printThumbFlag != null )
{
    if ("true".equalsIgnoreCase(printThumbFlag))
    {
        printThumbnails = true;
    }
}
else
{
    printThumbFlag = "no";
}
//disable thumbs if and only if citation pages are over a certain limit
boolean DISABLE_THUMBNAILS = false; 
int MAX_THUMBNAILS = Integer.parseInt(((String)request.getAttribute("maxThumbnails")));
int pages_in_citation = cite.getLastPageNumber();
if ( pages_in_citation > MAX_THUMBNAILS)
{
    printThumbFlag = "no";
    printThumbnails = false;
    DISABLE_THUMBNAILS = true;
}

boolean tiff = false;
boolean jp2  = false;
boolean jpg  = false;

if(pdiv.getDefaultImageMimeType().equals("image/tiff")) {
	tiff = true;
}
else if(pdiv.getDefaultImageMimeType().equals("image/jp2") ||
	   pdiv.getDefaultImageMimeType().equals("image/jpx")) {
	jp2 = true;
}
else if(pdiv.getDefaultImageMimeType().equals("image/jpeg")) {
	jpg = true;
}

String queryString = new String();
if(tiff||jpg) {
	queryString = "&amp;s="+s;
}
else if(jp2) {
	queryString = "&amp;imagesize="+imagesize+"&amp;jp2Res="+jp2Res;
	if(!jp2Rotate.equals("0")) {
		queryString = queryString + "&amp;rotation="+jp2Rotate;
	}
}
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
<script type="text/javascript" language="JavaScript" src="<%=pdsUrl%>/js/util.js" ></script>
<script type="text/javascript" language="JavaScript" src="<%=pdsUrl%>/js/deepweb.js" ></script>
<script type="text/javascript" language="JavaScript"><!--
function loaded(){
	if (self != top) {
		hideElement('cit', true);
	}
	else {
		hideElement('cit', false);
	}

}
//--></script>

<link href="<%=pdsUrl%>/css/pds.css" rel="stylesheet" type="text/css"/>
<style type="text/css"><!--
	a:link, a:visited {text-decoration: none;}
--></style>
<title><%=UnicodeUtils.getBidiAttribute("Sequence "+n+ UnicodeUtils.BIDI_DELIM + titleLabel+": "+ UnicodeUtils.BIDI_DELIM + cite.getDisplayLabel()+ "," + UnicodeUtils.BIDI_DELIM + "Harvard University Library PDS")%></title>
</head>
<body bgcolor="#DDDDDD" onload="loaded();javascript:window.scrollTo(0,readCookie('navscroll'));">

<!--  PDS LOGO AND LABEL -->
<div id="cit" class="noframediv">
	<table width="100%" border="0" cellpadding="3" cellspacing="0">
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

	<!-- TITLE OF METS FILE -->
	<td valign="top">
	
	<div class="citationDiv">
	<%=UnicodeUtils.getBidiDivedElement(cite.getDisplayLabel())%>
	</div>
	
	</td></tr>
	<tr><td colspan="3" align="center" class="noframediv2">
	<a href="<%=pdsUrl%>/view/<%=id%>?n=<%=n+queryString%>&amp;printThumbnails=<%=printThumbFlag%>">View framed version of this document</a>
	</td></tr>
	</table>
</div>

<!-- set div for floating nav bar -->
<div id="fixeddiv" style="
    position:absolute;
    width:150px;
    height:10px;
    left:0px;
    top:0px;
    padding:2px;
    background:#DDDDDD;
    z-index:100">
              
        <!-- original nav table-->
	<script type="text/javascript">setFrameSize();</script>
	<center>
	<table border="0" cellpadding="3">
	<tr>
	<%
	if(cite.firstIntermediate()!=null) {
	%>
		<td align="center" width="100" nowrap="nowrap" class="bannercolor">
		<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?op=n&amp;n=<%=n+queryString%>&amp;treeaction=expand&amp;printThumbnails=<%=printThumbFlag%>"
			class="buttonnormal"
			onclick="javascript:getFrameSize()">Expand All</a>
		</td>
		<td align="center" width="100" nowrap="nowrap" class="bannercolor">
		<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?op=n&amp;n=<%=n+queryString%>&amp;treeaction=collapse&amp;printThumbnails=<%=printThumbFlag%>"
			class="buttonnormal"
			onclick="javascript:getFrameSize()">Collapse All</a>
		</td>
	<%} else { %>
		<td align="center" width="100" nowrap="nowrap" class="bannercolorgrey">
		<div class="buttonnormalgrey">Expand All</div>
		</td>
		<td align="center" width="100" nowrap="nowrap" class="bannercolorgrey">
		<div class="buttonnormalgrey">Collapse All</div>
		</td>
	<% } %>
    </tr>
    <tr>
        <%
       if (!DISABLE_THUMBNAILS)
       {       
        //print thumbnails
	if( printThumbnails ) {
	%>
		<td align="center" width="200" nowrap="nowrap" class="bannercolor" colspan="2" >
		<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?n=<%=n+queryString%>&amp;printThumbnails=no"
			class="buttonnormal"
                        target="_top"
			onclick="javascript:getFrameSize()">Hide Thumbnails</a>
		</td>
	<%} else { %>      
		<td align="center" width="200" nowrap="nowrap" class="bannercolor" colspan="2">
		<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?n=<%=n+queryString%>&amp;printThumbnails=true"
			class="buttonnormal"
                        target="_top"
			onclick="javascript:getFrameSize()">Show Thumbnails</a>
		</td>         
	<% }
       } else { %>
       
       <td align="center" width="200" nowrap="nowrap" class="buttonnormalgrey" colspan="2" title="Currently thumbnail display is not supported for documents over 1,000 pages.">
          Show Thumbnails
       </td>
      
     <% } %>
    </tr></table></center>
    <!-- end orig nav table-->
    
</div>
<script type="text/javascript" src="<%=pdsUrl%>/js/fixedmenu.js"></script>
<!-- end floating div-->

	<div style="white-space:nowrap;">
            
<br> 
&nbsp;
<br> 
<br>
            
	<%
	if(cite.firstIntermediate()!=null) {
		if(pdsUser.isNodeExpanded(id,"-1")) {
	%>
			<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?op=n&amp;amp;n=<%=n+queryString%>&amp;index=<%=index%>&amp;printThumbnails=<%=printThumbFlag%>"
			   onclick="javascript:getFrameSize()">
			<img border="0" alt="" src="<%=pdsUrl%>/images/minus.gif"/>
			</a>
	<%}	else { %>
		
			<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?op=n&amp;n=<%=n+queryString%>&amp;index=<%=index%>&amp;printThumbnails=<%=printThumbFlag%>"
				onclick="javascript:getFrameSize()">
			<img border="0" alt="" src="<%=pdsUrl%>/images/plus.gif"/>
			</a>
	<% } %>
	<%} else { %>
			<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?op=n&amp;n=<%=n+queryString%>&amp;index=<%=index+"#"%><%=index%>&amp;printThumbnails=<%=printThumbFlag%>" 
				onclick="javascript:getFrameSize()">
			<img border="0" alt="" src="<%=pdsUrl%>/images/blank.gif"/>
			</a>
	<% } %>
	<%
	//Use the label from citation div.  If it is null, use title label.
	String labelText = new String();
	String labelTextNoHtml = new String();
	String label=cite.getLabel();
	if(label != null && !label.equals("")) {
		//out.println(cite.getLabel());
		labelText = labelText + cite.getLabel();
		labelTextNoHtml = labelTextNoHtml + cite.getLabel();
	}
	else {
		//out.println(cite.getDisplayLabel());
		labelText = labelText + cite.getDisplayLabel();
		labelTextNoHtml = labelTextNoHtml + cite.getDisplayLabel();
	}
	// <i>[" + cite.getLastPageNumber() + " pages.]</i><br></nobr>
	labelText = UnicodeUtils.getBidiSpannedElement(labelText + UnicodeUtils.BIDI_DELIM + " [" + cite.getLastPageNumber() + " pages]\n",false);
	labelTextNoHtml = UnicodeUtils.getBidiAttribute(labelTextNoHtml + UnicodeUtils.BIDI_DELIM + " ["+ cite.getLastPageNumber() + " pages]\n");
	%>
	
	<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?n=1<%=queryString%>&amp;printThumbnails=<%=printThumbFlag%>"
	   onclick="javascript:getFrameSize()" target="_top"
	   class="stdLinks"
	   title="<%=labelTextNoHtml%>"><%=labelText%></a>
   <br/>
	</div>

		<%
 
		level++;
		index=0;
		if(pdsUser.isNodeExpanded(id,"-1")) {		
			for(int i = 0; i < numberOfChildren; i++) {
				if(children.get(i) instanceof IntermediateDiv) {
					IntermediateDiv child = (IntermediateDiv)children.get(i);
					request.setAttribute("child",child);
					request.setAttribute("level",Integer.toString(level+1));
					request.setAttribute("index",Integer.toString(index+i));
					request.setAttribute("s",Integer.toString(s));
					request.setAttribute("n",Integer.toString(n));
					request.setAttribute("jp2Res",jp2Res);
					request.setAttribute("jp2Rotate",jp2Rotate);
					request.setAttribute("imagesizes",imagesize);
					request.setAttribute("pdiv",pdiv);
					request.setAttribute("pdsUrl",pdsUrl);
                                        request.setAttribute("printThumbnails",printThumbFlag);
					%>
				    <jsp:include page="navsection.jsp"/>
					<%
				}
                                else if(children.get(i) instanceof PageDiv) { 
                                PageDiv pchild = (PageDiv)children.get(i);
                                //IntermediateDiv child = (IntermediateDiv)children.get(i);
				request.setAttribute("pchild",pchild);
                                request.setAttribute("child",null);
				request.setAttribute("level",Integer.toString(level+1));
				request.setAttribute("index",index+"."+i);
				request.setAttribute("s",Integer.toString(s));
				request.setAttribute("n",Integer.toString(n));
				request.setAttribute("jp2Res",jp2Res);
				request.setAttribute("jp2Rotate",jp2Rotate);
				request.setAttribute("imagesizes",imagesize);
				request.setAttribute("pdiv",pdiv);
				request.setAttribute("pdsUrl",pdsUrl);
                                request.setAttribute("printThumbnails",printThumbFlag);
				%>
				<jsp:include page="navsection.jsp" />
				<%
                           }
                                
			}
		}
		%>
	
</body>
</html>