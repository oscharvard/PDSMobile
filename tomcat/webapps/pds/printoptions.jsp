<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

	<%
	String pdsUrl = (String)request.getAttribute("pdsUrl");
	String ftsUrl = (String)request.getAttribute("ftsUrl");
	Integer id = (Integer)request.getAttribute("id");
	int n = Integer.parseInt((String)request.getAttribute("n"));
	request.setAttribute("text","Convert to PDF for Printing");
	request.setAttribute("helpUri","http://nrs.harvard.edu/urn-3:hul.ois:pdsugdp1");
	request.setAttribute("helpText","Converting to PDF for printing");
	request.setAttribute("showHelp",Boolean.valueOf(true));
	request.setAttribute("pdsUrl",pdsUrl);
	String err = (String)request.getAttribute("err");
	String errmsg = ""; 
        String email = ((String)request.getAttribute("asyncEmail") == null)? "" : (String)request.getAttribute("asyncEmail"); 
        String start = ((String)request.getAttribute("start") == null)? "" : (String)request.getAttribute("start");
        String end = ((String)request.getAttribute("end")==null)? "" : (String)request.getAttribute("end");
        String printOpt = ((String)request.getAttribute("printOpt")==null)? "current" : (String)request.getAttribute("printOpt");
        String printOpt0 = "";
        String printOpt1 = "";
        String printOpt2 = "";
        //make printOpt sticky
        if ( printOpt.equals("all") )
        {
            printOpt2 = "checked";
        }
        else if ( printOpt.equals("range") )
        {
            printOpt1 = "checked";
        }
        else
        {
            printOpt0 = "checked";
        }
        
        if (err != null && !(err.trim().equals(""))) {
		if (err.equals("toobig")) {
			errmsg = "Please limit your page sequence range to a maximum of 10 pages for instant printing or enter your email address to have your larger selection sent to you.";
		}
		else if (err.equals("range")) {
			errmsg = "Invalid page range specified.";
		}
                else if ((err.equals("async")) && ((email != null && !(email.trim().equals("")))) )
                {
                    errmsg = "Your PDF is being generated. An email will be sent to " + email + " when it is ready for pickup. Requests larger than 500 pages can take an hour or more to process. The link to your PDF will remain available for 7 days.";
                }
                else if (err.equals("noemail")) {
			errmsg = "Please include a valid email address.";
		}
	}
	%>
<script type="text/javascript" language="JavaScript"><!--
        
    
//
function checkRanges(){
	if ( (document.print.end.value - document.print.start.value) < 10 )
        {
		document.print.email.focus();
	}
	
}
//--></script>

        <jsp:include page="commonheader.jsp"/>  
	<% if (!errmsg.equals("")) { %>
        <div class="textsmallred"><strong><small><%=errmsg%></small></strong></div><hr/>
	<% } %>			
	<table width="100%" border="0" cellspacing="15">
	<tr valign="top">
	<td class="stdLinksWrap" width="30%">
	<strong>Convert to PDF</strong> creates a PDF file for the selected pages.
        You can print the PDF or save it to disk.
        <p>
        This service creates PDFs at 10 pages-per-minute.
        Requests larger than 500 pages can take an hour or more to process.
        <p>
        The link to your PDF will remain active for 7 days.

	</td><td>
	<form name="print" action="<%=pdsUrl%>/print/<%=id%>">
	<input type="hidden" name="n" value="<%=n%>"/>
	<input type="hidden" name="id" value="<%=id%>"/>
	<table width="100%" border="0" class="searchDiv" bgcolor="#FFFFFF" cellpadding="6" cellspacing="0">
	<tr>
	<td align="left">
	<div class="textsmallred"><small>Requests for 10 pages or less will be delivered to your browser immediately. For larger requests, please enter an email address and a link to the PDF will be delivered by email.
                <p>
         <u>Firefox users</u>: To open large PDFs, go to Tools &gt; Options &gt; Applications and set Firefox to open Adobe Documents in Adobe Acrobat (not in "Adobe Acrobat in Firefox").


                <small></div><hr/>
	</td></tr>
	<tr> <td class="stdLinks">
	<input type="radio" name="printOpt" value="current" <%= printOpt0 %> />Convert current page
	</td></tr>
	<tr><td class="stdLinks">
	<input type="radio" name="printOpt" value="range" <%= printOpt1 %> />Convert page sequence
	<input type="text" name="start" size="6" onclick="javascript:document.print.printOpt[1].checked=true;" value="<%= start %>" /> to
	<input type="text" name="end" size="6" onclick="javascript:document.print.printOpt[1].checked=true;" value="<%= end %>"/>
	</td></tr>
	<tr><td class="stdLinks">
	<input type="radio" name="printOpt" value="all" <%= printOpt2 %> />Convert entire document
         </td></tr>
        <tr><td class="stdLinks">
         Email address: <input type="text" name="email" size="32" onclick="" value="<%= email %>"/>
	</td></tr>
	
	</table>
	<br/>
	<table border="0" width="100%" align="center"><tr align="right"><td>
	<div class="pdsbasic80">
	<input type="submit" class="buttonhighlightred" value="   Convert   "  onclick="" name="submit"/>
	<input type="button" class="buttonhighlightred" value="  Close  " onclick="window.close()" name="button"/>
	</div></td></tr></table></form></td></tr></table>
</body>
</html>