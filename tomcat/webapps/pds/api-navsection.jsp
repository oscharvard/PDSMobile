<%@ page language="java" contentType="text/xml; charset=UTF-8"
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
        String cache = (String)request.getAttribute("cache");
        String cacheUrl = (String)request.getAttribute("cacheUrl");
        String scale = (String)request.getAttribute("s");
	String jp2x = (String)request.getAttribute("jp2x");
	String jp2y = (String)request.getAttribute("jp2y");
	String caption = (String)request.getAttribute("caption");
	CitationDiv cite = (CitationDiv)request.getAttribute("cite");
	PageDiv pdiv = (PageDiv)request.getAttribute("pdiv");
        PageDiv ppDiv = (PageDiv)request.getAttribute("pchild");
        request.setAttribute("pchild",null);
	IntermediateDiv intDiv = (IntermediateDiv)request.getAttribute("child");
	int n = Integer.parseInt((String)request.getAttribute("n"));
	int level = Integer.parseInt((String)request.getAttribute("level"));
	String index = (String)request.getAttribute("index");
	Integer id = (Integer)request.getAttribute("id");
	String action = (String)request.getAttribute("action");
        action = "get";
	PdsUserState pdsUser = (PdsUserState)request.getAttribute("pdsUser");
        //thumbnails
        String ids = (String)request.getAttribute("idsUrl");
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

    
           
     //intdiv
     if (intDiv != null )
     {         
	children = intDiv.getChildren();
	numberOfChildren = children.size();
	for(int i = 1; i <= level-1; i++) {
            out.print("\t");
	}
	
	
	
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


        <section label="<%=labelTextNoHtml%>"  pagestart="<%=firstLabel%>" pageend ="<%=lastLabel%>" seqstart="<%=intDiv.getFirstOrder()%>" seqend="<%=intDiv.getLastOrder()%>" labelrange="<%= labelRange %>" seqrange="<%= sequenceText %>" link="<%=pdsUrl%>/<%=action%>/<%=id%>?n=<%=intDiv.getFirstOrder()%>">

	<%
        
    }//end intdiv  
    else if (ppDiv != null)
    {
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
		thumbImgSrc = ids + "/"+ppDiv.getDefaultImageID()+"?width=150&amp;height=150&amp;usethumb=y";
                thumbImg = "<img src=\"" + thumbImgSrc + 
                 "\" alt =\"" + labelTextNoHtml + 
                 "\" title=\"" + labelTextNoHtml + 
                 "\" align=\"middle\" valign=\"top\" border=\"1\" class=\"thumbLinks\">";
                //printThumbnail = true;
        }
        else
        {
            thumbImgSrc = ids + "/"+ppDiv.getDefaultImageID()+"?width=150&amp;height=150&amp;usethumb=y";
            thumbImg = "<img src=\"" + thumbImgSrc + 
                 "\" alt =\"" + labelTextNoHtml + 
                 "\" title=\"" + labelTextNoHtml + 
                 "\" align=\"middle\" valign=\"top\" border=\"1\" class=\"thumbLinks\">";
        }

        if (pageLabel == null )
        {
            pageLabel = "";
        }
        if (plabel == null )
        {
            plabel = "";
        }
        
        //temporarily turn off thumbs in this release
        //printThumbnails = false;
        //printThumbFlag = "no";

        String imgSrc = "";
        /*if ( tiff )
        {
            imgSrc = cacheUrl + ppDiv.getDefaultImageID() + "-" + scale + ".gif";
        }
        else
        {
            imgSrc = ids + "/" + ppDiv.getDefaultImageID();
        }*/


	//show images at large resolution (1200x1200, scale factor set to 0.25, x = -1, y = -1)
	// max res setting is really 2400x2400, scale factor set to 0.5, x = 0, y = 0 ) 
	if(jpg) 
	{ 
	    imgSrc = ids + "/" + ppDiv.getDefaultImageID() + "?xcap=" + caption;
	}
	else if(jp2) 
	{
	   jp2x = "-1";
	   jp2y = "-1";
	   //imgSrc = ids + "/" + ppDiv.getDefaultImageID() + "?s=0.25&amp;rotation=0&amp;width=1200&amp;height=1200&amp;x=" + jp2x + "&amp;y=" + jp2y + "&amp;xcap=" + caption;
	   imgSrc = ids + "/" + ppDiv.getDefaultImageID() + "?width=1200&amp;height=1200&amp;xcap=" + caption; 
    	}
	else if(tiff) 
	{
		imgSrc = cacheUrl + ppDiv.getDefaultImageID() + "-4.gif";
	}

	%>             
<page label="<%=labelTextNoHtml%>" pagelabel="<%=pageLabel%>" sequence="<%= sequence %>" pagenum="<%=plabel%>" thumb="<%=thumbImgSrc%>"  image="<%= imgSrc %>" link="<%=pdsUrl%>/<%=action%>/<%=id%>?n=<%= sequence %>" />
   <%
    } //end pagediv
        
        
	if(pdsUser.isNodeExpanded(id,index)) { 
		for(int i = 0; i < numberOfChildren; i++) {
			if(children.get(i) instanceof IntermediateDiv) {
				IntermediateDiv child = (IntermediateDiv)children.get(i);
				request.setAttribute("child",child);
				request.setAttribute("level",Integer.toString(level+1));
				request.setAttribute("index",index+"."+i);
				request.setAttribute("n",Integer.toString(n));;
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
                                
                                
                        //}    
		}
	}
        //intdiv
     if (intDiv != null )
     {%>
     </section>
     <%}
	%>
