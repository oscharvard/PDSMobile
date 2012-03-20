<%@ page language="java" contentType="text/xml; charset=UTF-8"
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

//hardcode jp2 params for now
jp2x = "-1";
jp2y= "-1";
jp2Res = "";
jp2Rotate = "0";

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
boolean showocr = false;//= action.equalsIgnoreCase("getocr") || text;
String orderLabel = null;
String titleLabel = "";
String orderDisplayLabel = "";
if(pdiv.getOrderLabel() == null || pdiv.getOrderLabel().equals("")) {
	orderLabel  = "";
	orderDisplayLabel = ""; //just return nothing if no label exists
}
else {
	orderLabel = "Page "+pdiv.getOrderLabel();
	titleLabel = " (Page "+pdiv.getOrderLabel()+")";
	orderDisplayLabel = orderLabel;
}
%>
<page>
    <fulltitle><%="Sequence "+n+ " " + titleLabel+": " + cite.getDisplayLabel() %></fulltitle>
    <displaylabel><%=cite.getDisplayLabel()%></displaylabel>
    <sequence><%=pdiv.getOrder()%></sequence>
    <lastpage><%=cite.getLastPageNumber()%></lastpage>
    <orderlabel><%=orderDisplayLabel%></orderlabel>
<% 
	//print the ocr if available
	List<Object> printedOCR = new ArrayList<Object>();
        if (ocrList.size() > 0 )
        {
            showocr = true;
        }
 if (showocr)
 { %>
    <text>
<%	for(int i=0; i<ocrList.size();i++) {
	    if(!printedOCR.contains(ocrList.get(i))) {
			File src = new File((String)ocrList.get(i));
			FileInputStream fstream = new FileInputStream(src);
			BufferedReader in = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
			while(in.ready()) {
				String line = in.readLine();
				line = Utils.escapeHtml(line);
				out.println (line);
			}
			in.close();			
			printedOCR.add(ocrList.get(i));
	    }    
	} %>
    </text>
 <%}
if(jpg) { %>
    <image><%=idsUrl+"/"%><%=imageId%>?xcap=<%=caption%></image>
    <img_mimetype>jpg</img_mimetype>
<%
}
else if(jp2) {
%>
    <image><%=idsUrl+"/"%><%=imageId%>?s=<%=jp2Res%>&amp;rotation=<%=jp2Rotate%>&amp;width=<%=imagesize%>&amp;height=<%=imagesize%>&amp;x=<%=jp2x%>&amp;y=<%=jp2y%>&amp;xcap=<%=caption%></image>
    <img_mimetype>jp2</img_mimetype>
    <small><%=idsUrl+"/"%><%=imageId%>?s=0.125&amp;rotation=<%=jp2Rotate%>&amp;width=300&amp;height=300&amp;x=<%=jp2x%>&amp;y=<%=jp2y%>&amp;xcap=<%=caption%></small>
    <medium><%=idsUrl+"/"%><%=imageId%>?s=0.25&amp;rotation=<%=jp2Rotate%>&amp;width=600&amp;height=600&amp;x=<%=jp2x%>&amp;y=<%=jp2y%>&amp;xcap=<%=caption%></medium>
    <large><%=idsUrl+"/"%><%=imageId%>?s=0.5&amp;rotation=<%=jp2Rotate%>&amp;width=1200&amp;height=1200&amp;x=<%=jp2x%>&amp;y=<%=jp2y%>&amp;xcap=<%=caption%></large>
    <max><%=idsUrl+"/"%><%=imageId%>?s=1&amp;rotation=<%=jp2Rotate%>&amp;width=2400&amp;height=2400&amp;x=<%=jp2x%>&amp;y=<%=jp2y%>&amp;xcap=<%=caption%></max>
    <%}
else if(tiff) {
%>
    <image><%=cacheUrl%><%=imageId%>-<%=scale%>.gif</image>
    <img_mimetype>tiff</img_mimetype>
    <small><%=cacheUrl%><%=imageId%>-8.gif</small>
    <medium><%=cacheUrl%><%=imageId%>-6.gif</medium>
    <large><%=cacheUrl%><%=imageId%>-4.gif</large>
    <max><%=cacheUrl%><%=imageId%>-2.gif</max>
<%
}
%>
    <thumb><%=idsUrl+"/"%><%=imageId%>?width=150&amp;height=150&amp;usethumb=y</thumb>
<%
		if (n > 1) { %>
    <prevlink><%=pdsUrl%>/get/<%=id%>?n=<%=n-1%>&amp;s=<%=scale%></prevlink>
<%} if (n < cite.getLastPageNumber()) { %>
    <nextlink><%=pdsUrl%>/get/<%=id%>?n=<%=n+1%>&amp;s=<%=scale%></nextlink>
	<% } %>
</page>
