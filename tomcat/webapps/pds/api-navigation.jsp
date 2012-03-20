<%@ page language="java" contentType="text/xml; charset=UTF-8"
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
int n = 1;//Integer.parseInt((String)request.getAttribute("n"));
Integer id = (Integer)request.getAttribute("id");
String pdsUrl = (String)request.getAttribute("pdsUrl");
String cache = (String)request.getAttribute("cache");
String cacheUrl = (String)request.getAttribute("cacheUrl");
String scale = (String)request.getAttribute("s");
String action = (String)request.getAttribute("action");
PdsUserState pdsUser = (PdsUserState)request.getAttribute("pdsUser");
String jp2x = (String)request.getAttribute("jp2x");
String jp2y = (String)request.getAttribute("jp2y");
String caption = (String)request.getAttribute("caption");
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

String orderLabel = null;
String titleLabel = "";
String orderDisplayLabel = "";
if(pdiv.getOrderLabel() == null || pdiv.getOrderLabel().equals("")) {
	orderLabel  = "";
	orderDisplayLabel = "";
}
else {
	orderLabel = "Page "+pdiv.getOrderLabel();
	titleLabel = " (Page "+pdiv.getOrderLabel()+")";
	orderDisplayLabel = orderLabel;
}

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
<document  title="<%=cite.getDisplayLabel()%>"  pages="<%= cite.getLastPageNumber() %>" >
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
					request.setAttribute("n",Integer.toString(n));
					request.setAttribute("pdiv",pdiv);
					request.setAttribute("pdsUrl",pdsUrl);
                                        request.setAttribute("printThumbnails",printThumbFlag);
                                        request.setAttribute("s",scale);
                                        request.setAttribute("cacheUrl",cacheUrl);
					request.setAttribute("jp2x",jp2x);
					request.setAttribute("jp2y",jp2y);
					request.setAttribute("caption",caption);
					%>
				    <jsp:include page="api-navsection.jsp"/>
					<%
				}
                                else if(children.get(i) instanceof PageDiv) { 
                                PageDiv pchild = (PageDiv)children.get(i);
                                //IntermediateDiv child = (IntermediateDiv)children.get(i);
				request.setAttribute("pchild",pchild);
                                request.setAttribute("child",null);
				request.setAttribute("level",Integer.toString(level+1));
				request.setAttribute("index",index+"."+i);
				request.setAttribute("n",Integer.toString(n));
				request.setAttribute("pdiv",pdiv);
				request.setAttribute("pdsUrl",pdsUrl);
                                request.setAttribute("printThumbnails",printThumbFlag);
                                request.setAttribute("s",scale);
                                request.setAttribute("cacheUrl",cacheUrl);
				request.setAttribute("jp2x",jp2x);
                                request.setAttribute("jp2y",jp2y);
                                request.setAttribute("caption",caption);
				%>
				<jsp:include page="api-navsection.jsp" />
				<%
                           }
                                
			}
		}
		%>	
</document>
