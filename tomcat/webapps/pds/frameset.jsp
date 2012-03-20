<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="edu.harvard.hul.ois.pdx.util.CitationDiv,
    		edu.harvard.hul.ois.pdx.util.PageDiv,
    		edu.harvard.hul.ois.pdx.util.UnicodeUtils"%>       
<%
CitationDiv cite = (CitationDiv)request.getAttribute("cite");
PageDiv pdiv = (PageDiv)request.getAttribute("pdiv");
int n = Integer.parseInt((String)request.getAttribute("n"));
int s = Integer.parseInt((String)request.getAttribute("s"));
String jp2Rotate = (String)request.getAttribute("jp2Rotate");
String jp2Res = (String)request.getAttribute("jp2Res");
String jp2x = (String)request.getAttribute("jp2x");
String jp2y = (String)request.getAttribute("jp2y");
String imagesize = (String)request.getAttribute("imagesize");
String bbx1 = (String)request.getAttribute("bbx1");
String bby1 = (String)request.getAttribute("bby1");
String bbx2 = (String)request.getAttribute("bbx2");
String bby2 = (String)request.getAttribute("bby2");
String pdsUrl = (String)request.getAttribute("pdsUrl");
Integer id = (Integer)request.getAttribute("id");
String action = (String)request.getAttribute("action");

String orderLabel = null;
String titleLabel = "";
if(pdiv.getOrderLabel() == null || pdiv.getOrderLabel().equals("")) {
	orderLabel  = "";
}
else {
	orderLabel = "Page "+pdiv.getOrderLabel();
	titleLabel = " (Page "+pdiv.getOrderLabel()+")";
}

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
//disable thumbs
//printThumbFlag = "no";

String queryString = "&amp;n="+n+"&amp;s="+s+"&amp;rotation="+jp2Rotate+"&amp;imagesize="+
					 imagesize+"&amp;jp2Res="+jp2Res+"&amp;jp2x="+jp2x+"&amp;jp2y="+jp2y+
					 "&amp;bbx1="+bbx1+"&amp;bby1="+bby1+"&amp;bbx2="+bbx2+"&amp;bby2="+bby2+"&amp;printThumbnails="+printThumbFlag;

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="robots" content="noindex,follow" />
<title><%=UnicodeUtils.getBidiAttribute("Sequence "+n+ UnicodeUtils.BIDI_DELIM + titleLabel+": "+ UnicodeUtils.BIDI_DELIM + cite.getDisplayLabel()+ UnicodeUtils.BIDI_DELIM + "Harvard University Library PDS")%></title>
</head>
<frameset id="fs1" rows="136,*">
<frame name="citation" title="Descriptive metadata" frameborder="1" scrolling="no" src="<%=pdsUrl%>/<%=action%>/<%=id%>?op=c<%=queryString%>"/>
<frameset id="fs2" cols="225,*">
<frame name="navigation" title="Structural metadata and navigation" frameborder="1" scrolling="auto" src="<%=pdsUrl%>/<%=action%>/<%=id%>?op=n<%=queryString%>"/>
<frame name="content" title="Representation of the current page" frameborder="1" scrolling="auto" src="<%=pdsUrl%>/<%=action%>/<%=id%>?op=t<%=queryString%>"/>
</frameset>
     <noframes>
     <body>
	     Harvard University Page Delivery Service:<br/>
	     <%=UnicodeUtils.getBidiDivedElement(cite.getDisplayLabel())%><br/>
	     <%="Sequence "+n+" ("+orderLabel+")"%><br/>
	     <a href="<%=pdsUrl%>/view/<%=id%>?op=n&amp;treeaction=expand<%=queryString%>" title="Table of Contents">Table of Contents</a><br/>     
	     <a href="<%=pdsUrl%>/view/<%=id%>?op=t<%=queryString%>" title="Image of <%="Sequence "+n+" ("+orderLabel+")"%>">Image of <%="Sequence "+n+" ("+orderLabel+")"%></a><br/>
	     <a href="<%=pdsUrl%>/viewtext/<%=id%>?op=t<%=queryString%>" title="Text of <%="Sequence "+n+" ("+orderLabel+")"%>">Text of <%="Sequence "+n+" ("+orderLabel+")"%></a><br/>
	     <a href="<%=pdsUrl%>/links/<%=id%>" title="Related Links">Related Links</a>
	 </body>
     </noframes>
</frameset>
</html>