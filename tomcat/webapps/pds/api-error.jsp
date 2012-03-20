<%@ page language="java" contentType="text/xml; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true" %>
<%
String pdsUrl = (String)request.getAttribute("pdsUrl");
String message = (String)request.getAttribute("message");
HttpServletRequest req = (HttpServletRequest)request.getAttribute("req");
%>
<error>
    <message><%=message%></message>
	<%
	if(exception!=null) {
	%>
                <exception>
		<%=exception.getMessage()%>
                <% exception.printStackTrace(); %>
		</exception>
	<%} %>
 </error>