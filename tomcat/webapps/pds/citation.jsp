<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="edu.harvard.hul.ois.pdx.util.CitationDiv,
    		edu.harvard.hul.ois.pdx.util.PageDiv,
    		edu.harvard.hul.ois.pdx.util.UnicodeUtils,
    		java.util.*"%>       
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
Integer id = (Integer)request.getAttribute("id");
String pdsUrl = (String)request.getAttribute("pdsUrl");
String ids = (String)request.getAttribute("ids");
String action = (String)request.getAttribute("action");
int maxJP2res = 6000; //default to max res

boolean tiff = false;
boolean jp2  = false;
boolean jpg  = false;
boolean ocrText = false;
System.out.println("IMAGESIZE IS: " + imagesize );
if(pdiv.getDefaultImageMimeType().equals("image/tiff")) {
	tiff = true;
}
else if(pdiv.getDefaultImageMimeType().equals("image/jp2") ||
	   pdiv.getDefaultImageMimeType().equals("image/jpx")) {
	jp2 = true;
        int newJP2res = Integer.parseInt((String)request.getAttribute("maxjp2res"));
        if ( (maxJP2res > 300) && (maxJP2res < 600 ) )
                                {
                                    maxJP2res = 300;
                                    s = 8;
                                }
                                else if ( (maxJP2res > 600 ) && (maxJP2res < 1200) )
                                {
                                    maxJP2res = 600;
                                    s = 6;
                                }
                                else if ( (maxJP2res > 1200 ) && (maxJP2res < 2400) )
                                {
                                    maxJP2res = 1200;
                                    s = 4;
                                }
                                else if (maxJP2res > 2400)
                                {
                                    maxJP2res = 2400;
                                    s = 2;
                                }
        if ( newJP2res > 0)
        {
            maxJP2res = newJP2res;
            if (Integer.parseInt(imagesize) > maxJP2res)
            {
                imagesize = Integer.toString(maxJP2res);
                System.out.println("IMAGESIZE INTERPOLATED TO: " + imagesize );
            }
        }
        System.out.println("MAX JP2 RES IS: " + Integer.toString(maxJP2res) );                       
}
else if(pdiv.getDefaultImageMimeType().equals("image/jpeg")) {
	jpg = true;
}
else if(pdiv.getDefaultImageMimeType().equals("text/plain")) {
	ocrText = true;
}
boolean showocr = action.equalsIgnoreCase("viewtext") || ocrText;

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
String gotoOrderLabel = "";
String titleLabel = "";
String orderDisplayLabel = "";
if(pdiv.getOrderLabel() == null || pdiv.getOrderLabel().equals("")) {
	gotoOrderLabel = "None";
	orderLabel  = "";
	orderDisplayLabel = "No Page Number";
}
else {
	gotoOrderLabel = pdiv.getOrderLabel();
	orderLabel = "Page "+pdiv.getOrderLabel();
	titleLabel = " (Page "+pdiv.getOrderLabel()+")";
	orderDisplayLabel = orderLabel;
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
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="robots" content="noindex,follow" />
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
<title><%=UnicodeUtils.getBidiAttribute("Sequence "+n+ UnicodeUtils.BIDI_DELIM + titleLabel+": " + UnicodeUtils.BIDI_DELIM + cite.getDisplayLabel()+ "," + UnicodeUtils.BIDI_DELIM + "Harvard University Library PDS")%></title>
<script type="text/javascript" language="JavaScript" src="<%=pdsUrl%>/js/util.js"></script>
<script type="text/javascript" language="JavaScript" src="<%=pdsUrl%>/js/wz_jsgraphics.js"></script>
<link href="<%=pdsUrl%>/css/pds.css" rel="stylesheet" type="text/css"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<script type="text/javascript" language="JavaScript">
		function popUp(URL, NAME, height, width) {
		var left = Math.floor( (screen.width - width) / 2);
		var top = Math.floor( (screen.height - height) / 2);
		eval("window = window.open(URL, NAME, 'toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width='+width+',height='+height+',left='+left+',top='+top+'');");
		}
</script>
</head>
<body bgcolor="#DDDDDD" link="#000000" vlink="#000000" onload="loaded();">

<table><tr><td>

	<!--  PDS LOGO AND LABEL -->
	<table width="100%" border="0" cellpadding="3" cellspacing="0">
	<tr>
	<td width="45">
	<span>
	<img src="<%=pdsUrl%>/images/PDSshieldSmall.gif" title="" alt="Harvard University"/>
	</span>
	</td>
	<td width="145" valign="top" nowrap="nowrap">
	<span class="textsmallred"><font color="#000000" size="1">Harvard University Library</font><br/>
	<font size="2"><a href="/" class ="redtextlink" target="_top">Page Delivery Service</a></font>
	</span></td>
	
	<!-- TITLE OF METS FILE -->
	<td valign="top">
	
	<div class="citationDiv">
	<%=UnicodeUtils.getBidiDivedElement(cite.getDisplayLabel())%>
	</div>
	
	</td></tr>
	</table>
	
	<!-- SEARCH, OCR, PRINT, LINKS, HELP, COPYRIGHT -->
	<table border="0" cellpadding="3">
	<tr>
	<%
	ArrayList ocr = (ArrayList)pdiv.getOcrID();
	if(ocr==null || ocr.size()==0 || ((Integer)ocr.get(0)).intValue()==0) {
	%>
		<td width="100" align="center" nowrap="nowrap" class="bannercolorgrey">
		<div class="buttonnormalgrey" title="Display the search window">Search</div></td>
	<%} else { %>
		<td width="100" align="center" nowrap="nowrap" class="bannercolor">
		<a href="<%=pdsUrl%>/search/<%=id%>" target="_blank" class="buttonnormal" title="Display the search window">Search</a></td>
	<%} 
	Boolean viewocr = cite.getviewocrFlag();
	if((viewocr == null || viewocr.booleanValue()) && pdiv.getOcrID().size() != 0) {
		// if the default page image is OCR, print "View Image" grayed out
		if(ocrText) { %>
			<td width="100" align="center" nowrap="nowrap" class="bannercolorgrey"><div class="buttonnormalgrey" title="Display page image">View Image</div></td>
		<% } else if(action.equalsIgnoreCase("viewtext")) { %>
			<td width="100" align="center" nowrap="nowrap" class="bannercolor"><a href="<%=pdsUrl%>/view/<%=id%>?n=<%=n+queryString%>&amp;printThumbnails=<%=printThumbFlag%>" target="_top" class="buttonnormal" onclick='javascript:getFrameSize()' title="Display page image">View Image</a></td>
		<% } else {  %>
			<td width="100" align="center" nowrap="nowrap" class="bannercolor"><a href="<%=pdsUrl%>/viewtext/<%=id%>?n=<%=n+queryString%>&amp;printThumbnails=<%=printThumbFlag%>" target="_top" class="buttonnormal" onclick='javascript:getFrameSize()' title="Display text for image">View Text</a></td>
		<%}
	}
	else { %>
		<td width="100" align="center" nowrap="nowrap" class="bannercolorgrey"><div class="buttonnormalgrey" title="Display text for image">View Text</div></td>
	<% } %>
	<td width="100" align="center" nowrap="nowrap" class="bannercolor"><a href="javascript:popUp('<%=pdsUrl%>/printoptions/<%=id%>?n=<%=n%>','printwindow',395,705)" class="buttonnormal" title="Display printing options">Print</a></td>
	<%
	List identifiers = cite.getIdentifiers ();
	Iterator iter = identifiers.iterator ();
	if (iter.hasNext ()) { %>
		<td width="120" align="center" nowrap="nowrap" class="bannercolor"><a href="javascript:popUp('<%=pdsUrl%>/links/<%=id%>','linkswindow',390,620)" class="buttonnormal" title="Display Related Links">Related Links</a></td>
	<%} else { %>
		<td width="120" align="center" nowrap="nowrap" class="bannercolorgrey"><div class="buttonnormalgrey" title="Display Related Links">Related Links</div></td>
	<%} %>
	<td width="100" align="center" nowrap="nowrap" class="bannercolor"><a href="http://nrs.harvard.edu/urn-3:hul.ois:pdsug" class="buttonnormal" target="_blank" title="Display User Guide">Help</a></td>
	<td width="100" align="center" nowrap="nowrap" class="bannercolor"><a href="javascript:popUp('<%=pdsUrl%>/html/copyright.html','copyrightwindow',270,460)" class="buttonnormal" title="Display the copyright statement">Copyright</a></td>
<td width="140" align="center" nowrap="nowrap" class="bannercolor"><a href="javascript:popUp('<%=pdsUrl%>/fullcitation/<%=id%>?n=<%=n%>','citewindow',450,705)" class="buttonnormal" title="Display the full Resource citation">Cite This Resource</a></td>
	</tr>
	</table>


	<!--  GOTO BOXES -->
	<table border="0" cellpadding="3" cellspacing="3">
	<tr>
	<%	
	Boolean gotoflag = cite.getgotoFlag();
	if(gotoflag == null || cite.getgotoFlag())  { %>
		<td align="right" valign="middle" nowrap="nowrap" class="stdLinks">Page:</td>
		<td valign="bottom" align="left" nowrap="nowrap" class="stdLinks">
		<form name="newPageInput" action="<%=pdsUrl%>/view/<%=id%>" target="_top" method="get" title="Go to page">
		<input type="text" name="n" value="<%=gotoOrderLabel%>" size="3"/>
		<%
		if(tiff||jpg) { %>
			<input type="hidden" name="s" value="<%=s%>"/>
		<% } else if(jp2) { %>
			<input type="hidden" name="jp2Res" value="<%=jp2Res%>"/>
			<input type="hidden" name="imagesize" value="<%=imagesize%>"/>
			<input type="hidden" name="rotation" value="<%=jp2Rotate%>"/>
		<% } %>
		<input type="hidden" name="P" value="p"/>
		<input type="hidden" name="preview" value=""/>
                <input type="hidden" name="printThumbnails" value="<%=printThumbFlag%>"/>
		<input type="submit" value="Go" onclick='javascript:getFrameSize()' class="buttonhighlightred"/>
		</form></td>
	<%} else { %>
		<td align="center" valign="middle" nowrap="nowrap" class="stdLinks">Page Number:</td>
		<td valign="bottom" align="left" nowrap="nowrap" class="stdLinks">
		<form name="newPageInput" action="#" title="Go to page">
                <input type="hidden" name="printThumbnails" value="<%=printThumbFlag%>"/>    
		<input type="text" disabled="disabled" name="n" value="<%=gotoOrderLabel%>" size="6"/>
		<input type="submit" disabled="disabled" value="Go" class="buttonhighlightgrey"/>
		</form></td>
	<% } %>
	<td align="right" valign="middle" nowrap="nowrap" class="stdLinks">Sequence:</td>
	<td valign="bottom" align="left" nowrap="nowrap" class="stdLinks">
	<form name="newSeqInput" action="<%=pdsUrl%>/view/<%=id%>" target="_top" method="get" title="Go to sequence">
	<input type="text" name="n" value="<%=n%>" size="3"/>
	<input type="hidden" name="printThumbnails" value="<%=printThumbFlag%>"/>
        
	<%if(tiff||jpg) { %>
		<input type="hidden" name="s" value="<%=s%>"/>
	<%} else if(jp2) { %>
		<input type="hidden" name="jp2Res" value="<%=jp2Res%>"/>
                <input type="hidden" name="imagesize" value="<%=imagesize%>"/>
				<input type="hidden" name="rotation" value="<%=jp2Rotate%>"/>
	<% } %>
	<input type="submit" value="Go" onclick='javascript:getFrameSize()' class="buttonhighlightred"/>
	</form></td>

	<td></td><td></td>
	
	<!-- PREV NEXT Page Controls -->
	<%
	if (n > 1) { %>
		<td align="center" nowrap="nowrap">
		<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?n=<%=n-1+queryString%>&amp;printThumbnails=<%=printThumbFlag%>"
					onclick='javascript:getFrameSize()'
					target="_top">
		<img border="0" src="<%=pdsUrl%>/images/PDSprev.gif" title="Previous Page" alt="Previous Page"/>
		</a></td>
	<%} else {  %>
		<td align="center" nowrap="nowrap">
		<a href="#">
		<img border="0" src="<%=pdsUrl%>/images/PDSprevInactive.gif" title="Previous Page" alt="Previous Page"/>
		</a></td>
	<%
	}
	if (n < cite.getLastPageNumber()) { %>
		<td align="center" nowrap="nowrap">
		<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?n=<%=n+1+queryString%>&amp;printThumbnails=<%=printThumbFlag%>"
					onclick='javascript:getFrameSize()'
					target="_top">
		<img border="0" src="<%=pdsUrl%>/images/PDSnext.gif" title="Next Page" alt="Next Page"/>
		</a></td>
		<td></td>
	<% }else { %>
		<td align="center" nowrap="nowrap">
		<a href="#">
		<img border="0" src="<%=pdsUrl%>/images/PDSnextInactive.gif" title="Next Page" alt="Next Page"/>
		</a></td>
		<td></td>
	<% } %>
	<td></td>
	
	<!-- IMAGE CONTROLS -->
	<%
	if(tiff && !showocr) { %> 
		<!-- inactive buttons  -->
		<td align="center" nowrap="nowrap"><img border="0" src="<%=pdsUrl%>/images/PDSzoomInInactive.gif" title="No more zoom levels" alt=""/></td>
		<td align="center" nowrap="nowrap"><img border="0" src="<%=pdsUrl%>/images/PDSzoomOutInactive.gif" title="No more Zoom levels" alt=""/></td>
		<td align="center" nowrap="nowrap"><img border="0" src="<%=pdsUrl%>/images/PDSrotatePlusInactive.gif" title="Image is not a JPEG2000. Click on 'Print' to create a PDF and rotate the image in Acrobat." alt=""/></td>
		<td align="center" nowrap="nowrap"><img border="0" src="<%=pdsUrl%>/images/PDSrotateMinusInactive.gif" title="Image is not a JPEG2000. Click on 'Print' to create a PDF and rotate the image in Acrobat." alt=""/></td>
		<td></td>
		<td></td>
		<!-- Image size -->
		<td align="center" nowrap="nowrap">
		<form name="imagesizeForm" action="<%=pdsUrl%>/view/<%=id%>" target="_top">
                <input type="hidden" name="printThumbnails" value="<%=printThumbFlag%>"/>
		<input type="hidden" name="n" value="<%=n%>"/>
		<input type="hidden" name="s"/>
		<font class="stdLinks">Image Size:</font>
		<input  type="image" name="small"
			    id="small" title="Small" src="<%=s==8?pdsUrl+"/images/smallActive.gif":pdsUrl+"/images/smallInactive.gif"%>"
			    onclick="javascript:getFrameSize();document.imagesizeForm.s.value=8;imagesizeForm.submit()"/>
		<input  type="image" name="medium"
			    id="medium" title="Medium" src="<%=s==6?pdsUrl+"/images/mediumActive.gif":pdsUrl+"/images/mediumInactive.gif"%>"
			    onclick="javascript:getFrameSize();document.imagesizeForm.s.value=6;imagesizeForm.submit()"/>
		<input  type="image" name="large"
			    id="large" title="Large" src="<%=s==4?pdsUrl+"/images/largeActive.gif":pdsUrl+"/images/largeInactive.gif"%>"
			    onclick="javascript:getFrameSize();document.imagesizeForm.s.value=4;imagesizeForm.submit()"/>
		<input  type="image" name="maximum"
			    id="maximum" title="Maximum" src="<%=s==2?pdsUrl+"/images/maximumActive.gif":pdsUrl+"/images/maximumInactive.gif"%>"
			    onclick="javascript:getFrameSize();document.imagesizeForm.s.value=2;imagesizeForm.submit()"/>
		</form>
		</td>

		<!-- JP2 CONTROLS -->
	<%} else if(jp2 && !showocr) {
		String thumbImgSrc = ids + "/"+pdiv.getDefaultImageID()+"?width=130&amp;height=130&amp;rotation="+jp2Rotate;
		//jp2 zoom in
		if(Double.valueOf(jp2Res)<1) {
	%>
			<td align="center" nowrap="nowrap">
			<form name="jp2ZoomInForm" target="_top" action="">
                        <input type="hidden" name="printThumbnails" value="<%=printThumbFlag%>"/>    
			<input type="hidden" name="action" value="jp2zoomin"/>
			<input type="hidden" name="imagesize" value="<%=imagesize %>"/>
			<input type="hidden" name="jp2x" value="<%=jp2x %>"/>
			<input type="hidden" name="jp2y" value="<%=jp2y %>" />
			<input type="hidden" name="jp2Res" value="<%=Double.valueOf(jp2Res)*2%>"/>
			<input type="hidden" name="rotation" value="<%=jp2Rotate %>"/>
			<input type="hidden" name="n" value="<%=n %>"/>
			<input type="hidden" name="op" value='j'/>			
			<input type="hidden" name="bbx1" value="<%=bbx1 %>"/>
			<input type="hidden" name="bby1" value="<%=bby1 %>"/>
			<input type="hidden" name="bbx2" value="<%=bbx2 %>"/>
			<input type="hidden" name="bby2" value="<%=bby2 %>"/>				
			<input  type="image" name="zoomin" id="zoomin" title="Zoom In" src="<%=pdsUrl %>/images/PDSzoomIn.gif"
				onclick="getFrameSize()"/>
			</form></td>
		<%} else { %>
			<td align="center" nowrap="nowrap"><img border="0" src="<%=pdsUrl%>/images/PDSzoomInInactive.gif" title="No more zoom levels" alt=""/></td>
		<%} 
		if(Double.valueOf(jp2Res)>0.03125) {
		%>
			<!-- jp2 zoom out -->
			<td align="center" nowrap="nowrap">
			<form name="jp2ZoomOutForm" target="_top" action="">
                        <input type="hidden" name="printThumbnails" value="<%=printThumbFlag%>"/>    
			<input type="hidden" name="action" value="jp2zoomout"/>
			<input type="hidden" name="imagesize" value="<%=imagesize %>"/>
			<input type="hidden" name="jp2x" value="<%=jp2x %>"/>
			<input type="hidden" name="jp2y" value="<%=jp2y %>" />
			<input type="hidden" name="jp2Res" value="<%=Double.valueOf(jp2Res)/2%>"/>
			<input type="hidden" name="rotation" value="<%=jp2Rotate %>"/>
			<input type="hidden" name="n" value="<%=n %>"/>
			<input type="hidden" name="op" value='j'/>			
			<input type="hidden" name="bbx1" value="<%=bbx1 %>"/>
			<input type="hidden" name="bby1" value="<%=bby1 %>"/>
			<input type="hidden" name="bbx2" value="<%=bbx2 %>"/>
			<input type="hidden" name="bby2" value="<%=bby2 %>"/>	
			<input  type="image" name="zoomout" id="zoomout" title="Zoom Out" src="<%=pdsUrl%>/images/PDSzoomOut.gif"
				onclick="getFrameSize()"/>
			</form></td>
		<%} else {%>
			<td align="center" nowrap="nowrap"><img border="0" src="<%=pdsUrl%>/images/PDSzoomOutInactive.gif" title="No more Zoom levels" alt=""/></td>
		<%} %>
		<!-- rotate plus button -->
		<td align="center" nowrap="nowrap">
		<form name="jp2rotatePlusForm" target="_top" action="">
                <input type="hidden" name="printThumbnails" value="<%=printThumbFlag%>"/>    
		<input type="hidden" name="action" value="jp2rotate"/>
		<input type="hidden" name="rotdir" value="plus"/>
		<input type="hidden" name="imagesize" value="<%=imagesize%>"/>
        <input type="hidden" name="pvHeight" value="<%=imagesize%>"/>
        <input type="hidden" name="pvWidth" value="<%=imagesize%>"/>
		<input type="hidden" name="jp2x" value="<%=jp2x%>"/>
		<input type="hidden" name="jp2y" value="<%=jp2x%>"/>
		<input type="hidden" name="jp2Res" value="<%=jp2Res%>"/>
		<input type="hidden" name="rotation" value="<%=(Integer.valueOf(jp2Rotate).intValue()-90)%>"/>
		<input type="hidden" name="n" value="<%=n%>"/>
		<input type="hidden" name="op" value="j"/>
		<input type="hidden" name="bbx1" value="<%=bbx1 %>"/>
		<input type="hidden" name="bby1" value="<%=bby1 %>"/>
		<input type="hidden" name="bbx2" value="<%=bbx2 %>"/>
		<input type="hidden" name="bby2" value="<%=bby2 %>"/>
		<input  type="image" name="rotateplus" id="rotateplus" title="Rotate +90" src ="<%=pdsUrl%>/images/PDSrotatePlus.gif"
						onclick="javascript:img=new Image();img.src='<%=thumbImgSrc%>';document.jp2rotatePlusForm.thumbwidth.value=img.width;document.jp2rotatePlusForm.thumbheight.value=img.height;getFrameSize()"/>
		</form></td>
		<!-- rotate minus button -->
		<td align="center" nowrap="nowrap">
		<form name="jp2rotateMinusForm" target="_top" action="">
                <input type="hidden" name="printThumbnails" value="<%=printThumbFlag%>"/>    
		<input type="hidden" name="action" value="jp2rotate"/>
		<input type="hidden" name="rotdir" value="plus"/>
		<input type="hidden" name="imagesize" value="<%=imagesize%>"/>
        <input type="hidden" name="pvHeight" value="<%=imagesize%>"/>
        <input type="hidden" name="pvWidth" value="<%=imagesize%>"/>
		<input type="hidden" name="jp2x" value="<%=jp2x%>"/>
		<input type="hidden" name="jp2y" value="<%=jp2x%>"/>
		<input type="hidden" name="jp2Res" value="<%=jp2Res%>"/>
		<input type="hidden" name="rotation" value="<%=(Integer.valueOf(jp2Rotate).intValue()+90)%>"/>
		<input type="hidden" name="n" value="<%=n%>"/>
		<input type="hidden" name="op" value="j"/>
		<input type="hidden" name="bbx1" value="<%=bbx1 %>"/>
		<input type="hidden" name="bby1" value="<%=bby1 %>"/>
		<input type="hidden" name="bbx2" value="<%=bbx2 %>"/>
		<input type="hidden" name="bby2" value="<%=bby2 %>"/>
		<input  type="image" name="rotateminus" id="rotateminus" title="Rotate -90" src ="<%=pdsUrl%>/images/PDSrotateMinus.gif"
						onclick="getFrameSize()"/>
		</form></td>
		<!-- Image size boxes -->
		<td></td>
		<td></td>
		<td align="center" nowrap="nowrap">
		<form name="imagesizeForm" action="<%=pdsUrl%>/view/<%=id %>" target="_top">
                <input type="hidden" name="printThumbnails" value="<%=printThumbFlag%>"/>
		<input type="hidden" name="action" value="jp2resize"/>
		<input type="hidden" name="op" value="j"/>
		<input type="hidden" name="imagesize"/>
        <input type="hidden" name="pvHeight" value="<%=imagesize%>"/>
        <input type="hidden" name="pvWidth" value="<%=imagesize%>"/>
		<input type='hidden' name="n" value="<%=n%>"/>
		<input type='hidden' name="rotation" value="<%=jp2Rotate%>"/>
		<input type="hidden" name="bbx1" value="<%=bbx1 %>"/>
		<input type="hidden" name="bby1" value="<%=bby1 %>"/>
		<input type="hidden" name="bbx2" value="<%=bbx2 %>"/>
		<input type="hidden" name="bby2" value="<%=bby2 %>"/>
		<%
		int iImageSize = Integer.parseInt(imagesize);
		double dRes = Double.valueOf(jp2Res);

		double small=0;
		double medium=0;
		double large=0;
		double maximum=0;
		
		if(iImageSize==300) {
		    small = dRes;
		    medium = dRes*2;
		    large = dRes*4;
		    maximum = dRes*8;
		}
		else if(iImageSize==600) {
		    small = dRes/2;
		    medium = dRes;
		    large = dRes*2;
		    maximum = dRes*4;
		}
		else if(iImageSize==1200) {
		    small = dRes/4;
		    medium = dRes/2;
		    large = dRes;
		    maximum = dRes*2;
		}
		else if(iImageSize==2400) {
		    small = dRes/8;
		    medium = dRes/4;
		    large = dRes/2;
		    maximum = dRes;
		}
		if(small>1)
		    small=1;
		if(medium>1)
		    medium=1;
		if(large>1)
		    large=1;
		if(maximum>1)
		    maximum=1;
		
		if(small<0.03125)
		    small=0.03125;
		if(medium<0.03125)
		    medium=0.03125;
		if(large<0.03125)
		    large=0.03125;
		if(maximum<0.03125)
		    maximum=0.03125;
		%>
		<input type="hidden" name="jp2Res"/>
		<input type="hidden" name="pres" value="<%=jp2Res%>"/>
		<input type="hidden" name="jp2x" value="<%=jp2x%>"/>
		<input type="hidden" name="jp2y" value="<%=jp2y%>"/>
		<font class="stdLinks">Image Size:</font>
                
                <% if ( 300 <= maxJP2res ) { %>                     
		<input  type="image" name="small"
			    id="small" title="Small" src="<%=imagesize.equals("300")?pdsUrl+"/images/smallActive.gif":pdsUrl+"/images/smallInactive.gif"%>"
			    onclick="javascript:getFrameSize();document.imagesizeForm.imagesize.value=300;document.imagesizeForm.jp2Res.value=<%=small%>;imagesizeForm.submit()"/>
                <% } else { %>
                <!-- img src="<%=pdsUrl%>/images/smallInactive.gif" alt=""/ -->
                <% } %>
                
                <% if ( 600 <= maxJP2res ) { %>   
                <input  type="image" name="medium"
			    id="medium" title="Medium" src="<%=imagesize.equals("600")?pdsUrl+"/images/mediumActive.gif":pdsUrl+"/images/mediumInactive.gif"%>"
			    onclick="javascript:getFrameSize();document.imagesizeForm.imagesize.value=600;document.imagesizeForm.jp2Res.value=<%=medium%>;imagesizeForm.submit()"/>
               <% } else { %>
               <!-- img src="<%=pdsUrl%>/images/mediumInactive.gif" alt=""/  -->
               <% } %>
                
                <% if ( 1200 <= maxJP2res ) { %>   
                <input  type="image" name="large"
			    id="large" title="Large" src="<%=imagesize.equals("1200")?pdsUrl+"/images/largeActive.gif":pdsUrl+"/images/largeInactive.gif"%>"
			    onclick="javascript:getFrameSize();document.imagesizeForm.imagesize.value=1200;document.imagesizeForm.jp2Res.value=<%=large%>;imagesizeForm.submit()"/>
                <% } else { %>
                <!-- img src="<%=pdsUrl%>/images/largeInactive.gif" alt=""/ -->
                <% } %>
                
                <% if ( 2400 <= maxJP2res ) { %>   
                <input  type="image" name="maximum"
			    id="maximum" title="Maximum" src="<%=imagesize.equals("2400")?pdsUrl+"/images/maximumActive.gif":pdsUrl+"/images/maximumInactive.gif"%>"
			    onclick="javascript:getFrameSize();document.imagesizeForm.imagesize.value=2400;document.imagesizeForm.jp2Res.value=<%=maximum%>;imagesizeForm.submit()"/>
                <% } else { %>
                <!-- img src="<%=pdsUrl%>/images/maximumInactive.gif" alt=""/ -->                
                <% } %>


                </form>
		</td>		
		

	<%}
	else {
		//show greyed out versions of the image control buttons
	%>
		<td align="center" nowrap="nowrap"><img border="0" src="<%=pdsUrl%>/images/PDSzoomInInactive.gif" title="No more zoom levels" alt=""/></td>
		<td align="center" nowrap="nowrap"><img border="0" src="<%=pdsUrl%>/images/PDSzoomOutInactive.gif" title="No more Zoom levels" alt=""/></td>
		<td align="center" nowrap="nowrap"><img border="0" src="<%=pdsUrl%>/images/PDSrotatePlusInactive.gif" title="Image is not a JPEG2000. Click on 'Print' to create a PDF and rotate the image in Acrobat." alt=""/></td>
		<td align="center" nowrap="nowrap"><img border="0" src="<%=pdsUrl%>/images/PDSrotateMinusInactive.gif" title="Image is not a JPEG2000. Click on 'Print' to create a PDF and rotate the image in Acrobat." alt=""/></td>
		<td></td>
		<td></td>
		<td align="center" nowrap="nowrap"><font class="stdLinks">Image Size:</font>
                <img src="<%=pdsUrl%>/images/smallInactive.gif" alt=""/>
                <img src="<%=pdsUrl%>/images/mediumInactive.gif" alt=""/>
                <img src="<%=pdsUrl%>/images/largeInactive.gif" alt=""/>
                <img src="<%=pdsUrl%>/images/maximumInactive.gif" alt=""/>
		</td>
	<% } %>
	
</tr></table>

</td><td>
<%if(jp2 && !showocr) {
		String thumbImgSrc = ids + "/"+pdiv.getDefaultImageID()+"?width=130&amp;height=130&amp;rotation="+jp2Rotate;
%>
		<!-- JP2 Thumbnail -->			
		<div id="canvas" style="position:relative;">	
		<form name="jp2clickForm" target="_top" action="" method="get">
                <input type="hidden" name="printThumbnails" value="<%=printThumbFlag%>"/>    
		<input type="hidden" name="action" value="jp2pan"/>
		<input type="hidden" name="imagesize" value="<%=imagesize%>"/>
		<input type="hidden" name="x" value="<%=jp2x%>"/>
		<input type="hidden" name="y" value="<%=jp2y%>" />
		<input type="hidden" name="jp2Res" value="<%=jp2Res%>"/>
		<input type="hidden" name="rotation" value="<%=jp2Rotate%>"/>
		<input type="hidden" name="n" value="<%=n%>"/>
		<input type="hidden" name="op" value="j"/>
		<input  type="image" name="thumbnail" id="thumbnail"
			    src="<%=thumbImgSrc%>" 
			    onclick="getFrameSize();document.jp2clickForm.submit();"
			    alt ="Thumbnail for sequence <%=n%>"
			    title="Thumbnail for sequence <%=n%>"/>
		</form></div>
		
		<script type="text/javascript">
		<!--
		var jg = new jsGraphics("canvas");
		jg.setColor("#ff0000"); // red	
		<%
		Integer bbxdim = Integer.valueOf(bbx2)-Integer.valueOf(bbx1);
		Integer bbydim = Integer.valueOf(bby2)-Integer.valueOf(bby1);
		%>
		jg.drawRect(<%=bbx1%>,<%=bby1%>,<%=bbxdim.intValue()%>,<%=bbydim.intValue()%>);
		jg.paint();
		//-->
		</script>
<%} %>
</td></tr></table>
<!--  PDS LOGO AND LABEL -->
<div id="cit" class="noframediv">
	<table width="100%" border="0" cellpadding="3" cellspacing="0">
	<tr><td colspan="3" align="center"><%="Sequence "+pdiv.getOrder()+" of "+cite.getLastPageNumber()+ " ("+orderDisplayLabel+")"%></td></tr>
	<tr><td colspan="3" align="center"><a href="<%=pdsUrl%>/view/<%=id%>?n=<%=n%>&amp;printThumbnails=<%=printThumbFlag%>">View framed version of this document</a></td></tr>
	</table>
</div>


</body>
</html>