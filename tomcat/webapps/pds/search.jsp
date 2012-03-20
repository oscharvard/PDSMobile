<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="edu.harvard.hul.ois.pdx.util.CitationDiv,
    		edu.harvard.hul.ois.pdx.util.UnicodeUtils"
    %>

	<%
	String pdsUrl = (String)request.getAttribute("pdsUrl");
	String ftsUrl = (String)request.getAttribute("ftsUrl");
	Integer id = (Integer)request.getAttribute("id");
	CitationDiv cite = (CitationDiv)request.getAttribute("cite");
	request.setAttribute("text","Search Citation");
	request.setAttribute("helpUri","http://nrs.harvard.edu/urn-3:hul.ois:pdsugdp2");
	request.setAttribute("helpText","Searching");
	request.setAttribute("showHelp",Boolean.valueOf(true));
	request.setAttribute("pdsUrl",pdsUrl);
	%>
	<jsp:include page="commonheader.jsp"/>
				
	<table width="92%" align="center">
	<tr><td>
	<div class="stdLinksWrap"><strong>Search within citation: </strong>
	<a href="<%=pdsUrl%>/view/<%=id%>" target="_blank"><%=UnicodeUtils.getBidiSpannedElement(cite.getDisplayLabel(),true)%></a>
	</div>
	</td></tr>
	</table>
	
	<table cellspacing="15" border="0" width="100%">
		<tr>
			<td class="stdLinksWrap" valign="top" width="30%">
				<strong>Search Citation</strong> searches the entire document (or group of documents)
		    	and returns an ordered list of links to pages that contain the search term. 
		    	You can click on the link to view the page. 
	    	</td>
			<td valign="top">
				<table class="searchDiv" width="100%" border="0" cellpadding="6" bgcolor="#FFFFFF">
					<tr>
						<td class="pdsbasic80" align="left">
							<strong>Enter search term(s):</strong>
							<br/>
							<form name="search" method="get" action="<%=ftsUrl%>">
								<input name="Q" type="text" id="Q" size="60"/>
								<input type="hidden" name="G" value="<%=id%>"/>
								<input type="hidden" name="T" value="pds-results.xsl"/>
								<input type="hidden" name="F" value="H"/>
								<input type="hidden" name="R" value="<%=pdsUrl%>/search/<%=id%>"/>
								<br/><br/>
								
								<strong>Order results:</strong>
								<br/>
								<input type="radio" name="O" value="R" checked="checked"/>
								By relevance<br/>
								<input type="radio" name="O" value="A"/>
								Alphabetically<br/>
								<input type="radio" name="O" value="D"/>
								By date<br/><br/>
								
								<input type="submit" value="Search" class="buttonhighlightred"/>
							</form>
						</td>
					</tr>
					<tr>
						<td class="pdsbasic80">
							<strong>Hints</strong>:<br/>
							Use * as a wildcard. Examples: cat*, oper* and Harv*
						</td>
					</tr>
				</table>
				<br/>
				<table border="0" width="100%">
					<tr>
						<td class="pdsbasic80" align="right">
							<input type="button" class="buttonhighlightred" value="  Close  " onclick="window.close()" name="button"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>