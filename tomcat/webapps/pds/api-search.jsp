<%@ page language="java" contentType="text/xml; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.io.BufferedInputStream,
            java.io.InputStream,java.io.InputStreamReader,
            java.io.Reader,java.net.URL,java.util.*"%>
<%
    String searchUrl = (String)request.getAttribute("searchurl");
    System.out.println(searchUrl);
    URL u = new URL(searchUrl);
    
    InputStream in = u.openStream();

    in = new BufferedInputStream(in);
    Reader r = new InputStreamReader(in);
    r.skip(39); // skip "<?xml version="1.0" encoding="UTF-8"?>" string
    int c;
    while ((c = r.read()) != -1) {
      out.print((char) c);
    }

  

%>
