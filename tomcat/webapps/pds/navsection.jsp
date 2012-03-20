<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="edu.harvard.hul.ois.pdx.util.CitationDiv,
    		edu.harvard.hul.ois.pdx.util.PageDiv,
			edu.harvard.hul.ois.pdx.util.IntermediateDiv,
			edu.harvard.hul.ois.pds.user.PdsUserState,
    		java.util.*,
    		edu.harvard.hul.ois.pds.Utils;"%>   
<%
        //now handles pages
	String pdsUrl = (String)request.getAttribute("pdsUrl");
	CitationDiv cite = (CitationDiv)request.getAttribute("cite");
	PageDiv pdiv = (PageDiv)request.getAttribute("pdiv");
        PageDiv ppDiv = (PageDiv)request.getAttribute("pchild");
        request.setAttribute("pchild",null);
	IntermediateDiv intDiv = (IntermediateDiv)request.getAttribute("child");
	int n = Integer.parseInt((String)request.getAttribute("n"));
	int s = Integer.parseInt((String)request.getAttribute("s"));
	int level = Integer.parseInt((String)request.getAttribute("level"));
	String index = (String)request.getAttribute("index");
	String jp2Rotate = (String)request.getAttribute("jp2Rotate");
	String jp2Res = (String)request.getAttribute("jp2Res");
	String imagesize = (String)request.getAttribute("imagesize");
	Integer id = (Integer)request.getAttribute("id");
	String action = (String)request.getAttribute("action");
	PdsUserState pdsUser = (PdsUserState)request.getAttribute("pdsUser");
        //thumbnails
        String ids = (String)request.getAttribute("ids");
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

     List children = null;
     int numberOfChildren = 0;

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
           
     //intdiv
     if (intDiv != null )
     {         
	children = intDiv.getChildren();
	numberOfChildren = children.size();
	
	%>
	<div style="white-space:nowrap;">
	<%
	for(int i = 1; i <= level-1; i++) {
		%><img alt="" src="<%=pdsUrl%>/images/blank.gif"/><%
	}
	//if(!intDiv.anyInterChildren()) { //There are no children that are intermediate divs
	//	%><img alt="" src="<%=pdsUrl%>/images/blank.gif"/><%
	//}
	//else {
		//If expanded, or if the page is between the Order range, display minus.gif
		//|| (n>=this.getFirstOrder()&&n<=this.getLastOrder())
		if(pdsUser.isNodeExpanded(id,index)) {
			%><a href="<%=pdsUrl%>/<%=action%>/<%=id%>?op=n&amp;n=<%=n%>&amp;index=<%=index+queryString%>&amp;printThumbnails=<%=printThumbFlag%>"
				onclick="javascript:getFrameSize()">
			<img border="0" alt="" src="<%=pdsUrl%>/images/minus.gif"/></a><%
		}
		else {
			%><a href="<%=pdsUrl%>/<%=action%>/<%=id%>?op=n&amp;n=<%=n%>&amp;index=<%=index+queryString%>&amp;printThumbnails=<%=printThumbFlag%>" 
				onclick="javascript:getFrameSize()">
			<img border="0" alt="" src="<%=pdsUrl%>/images/plus.gif"/></a><%
		}
	//}
	
	
	//Calculate page range label
	String labelText = new String();
	String labelTextNoHtml = new String();
	String firstLabel = null;

        boolean skipLabelRange = false;
        boolean skipDivLabel = false;
                   
	if(intDiv.getFirstLabel()!=null) {
		firstLabel = intDiv.getFirstLabel();
	}
	else {
		firstLabel = "unnumbered page";
                skipLabelRange = true;
	}	
	String lastLabel  = null;
	if(intDiv.getLastLabel()!=null) {
		lastLabel = intDiv.getLastLabel();
	}
	else {
		lastLabel = "unnumbered page";
                skipLabelRange = true;
	}
	//out.println(intDiv.getLabel() + "</a>
	String divLabel = intDiv.getLabel();
        if ( divLabel == null || divLabel.matches("") )
         {
            skipDivLabel = true;
         }
        
	//divLabel = Utils.escapeHtml(divLabel);
        // labelText = labelText + divLabel + "</a>";
	// labelTextNoHtml = labelTextNoHtml + divLabel;
	
	boolean sameOrder = intDiv.getFirstOrder()==intDiv.getLastOrder();
	boolean sameLabel = firstLabel.equals(lastLabel);
        String sequenceText = new String();
        String labelRange = new String();
        
        //set sequence text
        if(sameOrder) {
		//<i><font size=\"1\" face=\"Verdana\"> [pp. " + firstLabel);
		// (seq. " + intDiv.getFirstOrder() +")]</font></i>
		sequenceText = sequenceText + " (seq. " + intDiv.getFirstOrder() +")";
	}
	else 
        {
            sequenceText = sequenceText + " (seq. " + intDiv.getFirstOrder() + 
                    "-" + intDiv.getLastOrder() + ")";
        }
        if(sameLabel) 
        {
            //out.println(firstLabel);
	    labelRange = labelRange + "p. " + firstLabel;
        }
	else 
        {
            //out.println(firstLabel+ "-" + lastLabel);
	    labelRange = labelRange + "pp. " + firstLabel+ "-" + lastLabel;
	}
        
        
        if ( skipLabelRange && skipDivLabel )  //just print sequences
        {
            labelText = "<font size=\"1\" face=\"Verdana\"><i>" + sequenceText + "</i></font></a>";
            labelTextNoHtml = sequenceText;
        }
        else if ( !skipLabelRange && skipDivLabel) //no div labels
        {
            labelText = labelText + "<i><font size=\"1\" face=\"Verdana\">" + 
                    labelRange + "</a> " + sequenceText + "</i></font>";
            labelTextNoHtml = labelTextNoHtml + labelRange + " " + sequenceText;
        }
        else if ( skipLabelRange && !skipDivLabel ) //skip page labels
        {
            labelText = labelText + divLabel +  "</a><font size=\"1\" face=\"Verdana\"><i> " 
                    + sequenceText + "</i></font>";
            labelTextNoHtml = labelTextNoHtml + divLabel + " " + sequenceText;
           
        }
        else //print all label info
        {   
            labelText = labelText + divLabel + 
                    "</a>, <font size=\"1\" face=\"Verdana\"><i> " + 
                    labelRange + " " + sequenceText + "</i></font>";
            labelTextNoHtml = labelTextNoHtml + divLabel + ", " + labelRange + " " + sequenceText;
        }
        
        
        //orig code
	/*if(sameOrder) {
		//<i><font size=\"1\" face=\"Verdana\"> [pp. " + firstLabel);
		// (seq. " + intDiv.getFirstOrder() +")]</font></i>
		labelText = labelText+"<i><font size=\"1\" face=\"Verdana\"> pp. " + firstLabel;
		labelText = labelText +  "(seq. " + intDiv.getFirstOrder() +")</font></i>";
		labelTextNoHtml = labelTextNoHtml +",  " + firstLabel;
		labelTextNoHtml = labelTextNoHtml +  " (seq. " + intDiv.getFirstOrder() +"]";
                System.out.println("same order");
	}
	else {
		//<i><font size=\"1\" face=\"Verdana\"> [pp. 
		labelText = labelText + ", <i><font size=\"1\" face=\"Verdana\">  ";
		//labelTextNoHtml = labelTextNoHtml + " pp. ";
		if(sameLabel && firstLabel.equals("unnumbered page")) {
			//firstLabel = "unnumbered pages";
			//out.println(firstLabel);
			//labelText = labelText + firstLabel ;
			//labelTextNoHtml = labelTextNoHtml + firstLabel ;
		}
		else if(sameLabel) {
			//out.println(firstLabel);
			labelText = labelText + firstLabel;
			labelTextNoHtml = labelTextNoHtml + " " + firstLabel;
		}
		else {
			//out.println(firstLabel+ "-" + lastLabel);
			labelText = labelText + firstLabel+ "-" + lastLabel;
			labelTextNoHtml = labelTextNoHtml + firstLabel+ "-" + lastLabel;
		}
		// (seq. " + intDiv.getFirstOrder() + "-" +
		//		intDiv.getLastOrder() + ")]</font></i>
		labelText = labelText + " (seq. " + intDiv.getFirstOrder() + "-" +
										intDiv.getLastOrder() + ")</font></i>";
		labelTextNoHtml = labelTextNoHtml + " (seq. " + intDiv.getFirstOrder() + "-" +
										intDiv.getLastOrder() + ")";
		
	}*/
	//end of page range label
	%>

	<%
	if(n>=intDiv.getFirstOrder()&&n<=intDiv.getLastOrder())	{
	%>
		<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?n=<%=intDiv.getFirstOrder()+queryString%>&printThumbnails=<%=printThumbFlag%>"
		onclick="javascript:getFrameSize()" 
		target="_top"
		title="<%=labelTextNoHtml%>"
		class="buttonhighlight2">
		<%=labelText%>
	<%}
	//To always expand the section where the current page is.
	//this._expanded=true;
	else { %>
		<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?n=<%=intDiv.getFirstOrder()+queryString%>&printThumbnails=<%=printThumbFlag%>"
		onclick="javascript:getFrameSize()" 
		target="_top"
		title="<%=labelTextNoHtml%>"
		class="stdLinks">
		<%=labelText%>
	<% } %>
	</div>
	<%
        
    }//end intdiv  
    else if (ppDiv != null)
    {
        %>
	<div style="white-space:nowrap;">
	<%
	for(int i = 1; i <= level; i++) 
        {
		%><img alt="" src="<%=pdsUrl%>/images/blank.gif"/><%
	}
        children = ppDiv.getChildren();
	numberOfChildren = children.size();
        String plabel = ppDiv.getLabel();
        String pageLabel = ppDiv.getOrderLabel();
        String seqLabel = ppDiv.getStringOrder();
        String sequence = Integer.toString(ppDiv.getOrder());
        
        //thumbnail prep
        String thumbImgSrc = new String();
        String thumbImg = new String();
        //boolean printThumbnail = false;
        jp2= false;
        jpg = false;
        tiff= false;
        String ppMimeType = ppDiv.getDefaultImageMimeType();
        
        if (ppMimeType != null)
        {
            if(ppMimeType.equals("image/tiff")) {
            	tiff = true;
            }
            else if(ppMimeType.equals("image/jp2") ||
            	   ppMimeType.equals("image/jpx")) {
            	jp2 = true;
            }
            else if(ppMimeType.equals("image/jpeg")) {
            	jpg = true;
            }
        }

        //plabel = plabel + " <i>(seq. " + ppDiv.getOrder() + ")</i>";
        
        String labelText = new String();
	String labelTextNoHtml = new String(); 
           
       labelText = labelText + " <i>(seq. " + sequence +")</i></font>";
       if( (pageLabel!=null ) && (plabel != null) )
        {
            if ( (pageLabel.equalsIgnoreCase(plabel)) || plabel.matches("[pP]age [0-9]+")||
                  plabel.matches("[pP]. [0-9]+") || 
                  plabel.matches("[pP]age \\[[0-9]\\]") ||
                  plabel.matches("[pP]. \\[[0-9]\\]") || 
                  plabel.matches("[Pp]age \\[" + pageLabel + "\\]") || 
                  plabel.matches("[Pp]. \\[" + pageLabel + "\\]")   ||
                  plabel.matches("[Pp]age " + pageLabel) || 
                  plabel.matches("[Pp]. " + pageLabel) ||
                  (plabel.trim().length() == 0 ) ||
                  (plabel.length () == 0 ) ||
                  (plabel.lastIndexOf(pageLabel) > -1) ) //page label == page number
            {
                //System.out.println("pageLabel: " + pageLabel + " plabel: " + plabel);
                labelText = "<font size=\"1\" face=\"Verdana\">" + plabel + "</a> " + labelText;
            }
            else if ( (plabel.trim().length() == 0 ) || (plabel.length () == 0 ) )
            {
                labelText = "<font size=\"1\" face=\"Verdana\">p. " + pageLabel + "</a> " + labelText;
            }
            else 
            {
                labelText = "p. " + pageLabel + " " + labelText;
                plabel = "<font size=\"1\" face=\"Verdana\">" + plabel + "</a>";
                labelText = plabel + ", " + labelText;
            }
        }
        else if ( (plabel==null) && (pageLabel!=null) )
        {    
            labelText = "<font size=\"1\" face=\"Verdana\">p. " + pageLabel + " " + 
                    labelText + "</a>";
        } 
        else if ( (plabel!=null) && (pageLabel==null) )
        {    
            labelText = "<font size=\"1\" face=\"Verdana\">" + plabel + "</a> " + labelText;
        }
        else // if ( plabel==null && pageLabel==null )
        {
           labelText = 
             "<font size=\"1\" face=\"Verdana\"><i>(seq. " + sequence +
             ")</i></font></a>";
        } 
        
        labelTextNoHtml = labelText.replaceAll("\\<.*?>","");
        
        //labelTextNoHtml = labelTextNoHtml + labelText;

		//<i><font size=\"1\" face=\"Verdana\"> [pp. " + firstLabel);
		// (seq. " + intDiv.getFirstOrder() +")]</font></i>
		//labelText = labelText+ " - Page " + pageLabel;
		//labelText = labelText +  "<i> (seq. " + sequence +")</font></i>";
		//labelTextNoHtml = labelTextNoHtml + " - Page " + pageLabel + " (seq. " + sequence +")";
        if(jp2) {
		thumbImgSrc = ids + "/"+ppDiv.getDefaultImageID()+"?width=150&height=150&usethumb=y";
                thumbImg = "<img src=\"" + thumbImgSrc + 
                 "\" alt =\"" + labelTextNoHtml + 
                 "\" title=\"" + labelTextNoHtml + 
                 "\" align=\"middle\" valign=\"top\" border=\"1\" class=\"thumbLinks\">";
                //printThumbnail = true;
        }
        else
        {
            thumbImgSrc = ids + "/"+ppDiv.getDefaultImageID()+"?width=150&height=150&usethumb=y";
            thumbImg = "<img src=\"" + thumbImgSrc + 
                 "\" alt =\"" + labelTextNoHtml + 
                 "\" title=\"" + labelTextNoHtml + 
                 "\" align=\"middle\" valign=\"top\" border=\"1\" class=\"thumbLinks\">";
        }
        
        //temporarily turn off thumbs in this release
        //printThumbnails = false;
        //printThumbFlag = "no";
	
	%>
	
                <% if (printThumbnails)
                {
                %>
                <center><a href="<%=pdsUrl%>/<%=action%>/<%=id%>?n=<%=sequence%>&printThumbnails=<%=printThumbFlag%>"
		onclick="javascript:getFrameSize()" 
		target="_top"
		title="<%=labelTextNoHtml%>"
                class="thumbLinks"><%=thumbImg%></a></center>
 </div>
 <div style="white-space:nowrap;">

            <%
                    for(int i = 1; i <= level; i++) 
                    {
	%><img alt="" src="<%=pdsUrl%>/images/blank.gif"/><%
                    }                    
                 }
                %>
                
		<a href="<%=pdsUrl%>/<%=action%>/<%=id%>?n=<%=sequence%>&printThumbnails=<%=printThumbFlag%>"
		onclick="javascript:getFrameSize()" 
		target="_top"
		title="<%=labelTextNoHtml%>"
		class="stdLinks">
		<%=labelText%>            
        
           
   
	</div>
	<%
    } //end pagediv
        
        
	if(pdsUser.isNodeExpanded(id,index)) { 
		for(int i = 0; i < numberOfChildren; i++) {
			if(children.get(i) instanceof IntermediateDiv) {
				IntermediateDiv child = (IntermediateDiv)children.get(i);
				request.setAttribute("child",child);
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
                                
                                
                        //}    
		}
	}
	%>