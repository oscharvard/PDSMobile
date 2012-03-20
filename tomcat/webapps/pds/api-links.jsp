<%@ page language="java" contentType="text/xml; charset=UTF-8"
    pageEncoding="UTF-8"
    import="edu.harvard.hul.ois.pdx.util.CitationDiv,
   			edu.harvard.hul.ois.pdx.util.Identifier,
	    	java.util.*,
	    	edu.harvard.hul.ois.pdx.util.PageDiv,
    		edu.harvard.hul.ois.pdx.util.UnicodeUtils;"
    %>
	<%
	String pdsUrl = (String)request.getAttribute("pdsUrl");
	Integer id = (Integer)request.getAttribute("id");
	CitationDiv cite = (CitationDiv)request.getAttribute("cite");
	request.setAttribute("text","Related Links");
	request.setAttribute("helpUri","#");
	request.setAttribute("helpText","");
	request.setAttribute("showHelp",Boolean.valueOf(false));
	request.setAttribute("pdsUrl",pdsUrl);
	List identifiers = cite.getIdentifiers ();
	Iterator iter = identifiers.iterator ();
	String text = (String)request.getAttribute("text");
	PageDiv pdiv = (PageDiv)request.getAttribute("pdiv");
	String orderLabel = null;
	String titleLabel = "";
	if(pdiv.getOrderLabel() == null || pdiv.getOrderLabel().equals("")) {
		orderLabel  = "";
	}
	else {
		orderLabel = "Page "+pdiv.getOrderLabel();
		titleLabel = " (Page "+pdiv.getOrderLabel()+")";
	}
	%>
<related>
	<%while (iter.hasNext ()) {%>

		<%
		Identifier identifier = (Identifier) iter.next ();
		if(identifier.getType().compareToIgnoreCase("hollis") == 0) {
			if(identifier.getDisplayLabel()!=null && identifier.getDisplayLabel().length()>0) {
		%>
       <link>
            <identifier><%=identifier.getValue()%></identifier>
            <type>hollis</type>
            <description><%=identifier.getDisplayLabel()%></description>
            <value>http://hollisclassic.harvard.edu/F?func=find-c&amp;CCL_TERM=sys=<%=identifier.getValue()%></value>
       </link>
		<%} else { %>
       <link>
            <identifier><%=identifier.getValue()%></identifier>
            <type>hollis</type>
            <value>http://hollisclassic.harvard.edu/F?func=find-c&amp;CCL_TERM=sys=<%=identifier.getValue()%></value>
       </link>
		<%	}
		}
		else if(identifier.getType().compareToIgnoreCase("oldhollis") == 0) {
			if(identifier.getDisplayLabel()!=null && identifier.getDisplayLabel().length()>0) {
		%>
       <link>
            <identifier><%=identifier.getValue()%></identifier>
            <type>oldhollis</type>
            <description><%=identifier.getDisplayLabel()%></description>
            <value>http://hollisclassic.harvard.edu/F?func=find-c&amp;CCL_TERM=KHA=<%=identifier.getValue()%></value>
       </link>
                                        <%} else {%>
       <link>
            <identifier><%=identifier.getValue()%></identifier>
            <type>oldhollis</type>
            <value>http://hollisclassic.harvard.edu/F?func=find-c&amp;CCL_TERM=KHA=<%=identifier.getValue()%></value>
       </link>
                                        <%	}
		}
		else if (identifier.getType().compareToIgnoreCase("uri") == 0) {
			if(identifier.getDisplayLabel()!=null && identifier.getDisplayLabel().length()>0) {
		%>
       <link>
            <identifier><%=identifier.getValue()%></identifier>
            <type>uri</type>
            <description><%=identifier.getDisplayLabel()%></description>
            <value><%=identifier.getValue()%></value>
       </link>
                         <%} else {%>
				<a href="<%=identifier.getValue()%>"
					target="_blank" class="citLinksLine">URL <%=identifier.getValue()%></a><br/>
       <link>
            <identifier><%=identifier.getValue()%></identifier>
            <type>uri</type>
            <value><%=identifier.getValue()%></value>
       </link>
		<%	}
		}
		else {
			if(identifier.getDisplayLabel()!=null && identifier.getDisplayLabel().length()>0) {
		%>
				<div class="citLinksLine"><%=identifier.getDisplayLabel()%> <%=identifier.getValue()%></div>
       <link>
            <identifier><%=identifier.getValue()%></identifier>
            <type><%=identifier.getType()%></type>
            <description><%=identifier.getDisplayLabel()%></description>
            <value><%=identifier.getValue()%></value>
       </link>
                                <%}else {%>
				<div class="citLinksLine"><%=identifier.getType()%> <%=identifier.getValue()%></div>
       <link>
            <identifier><%=identifier.getValue()%></identifier>
            <type><%=identifier.getType()%></type>
            <value><%=identifier.getValue()%></value>
       </link>
                                <%	}
		}
	   } %>
</related>