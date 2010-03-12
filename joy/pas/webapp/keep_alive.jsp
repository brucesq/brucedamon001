<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
request.getSession(true);
String url = request.getContextPath()+"/keep_alive.jsp?r="+System.currentTimeMillis();
url = response.encodeURL(url);

%>
<html>
<head>
<meta http-equiv="refresh" content="240;url=<%=url%>">
</head>
<body>
</body>
</html>
