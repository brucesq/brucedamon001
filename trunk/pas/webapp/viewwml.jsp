<html>
<%@ page 
language="java"
contentType="text/html; charset=GBK"
pageEncoding="GBK"
%>
<%
    
	String param = request.getQueryString().replaceAll("sp=","");

	int a= param.indexOf("/");
    int b = param.indexOf("/",a + 2);
    
    int c = param.indexOf("/",b+1);
    
    String bb = param.substring(0,c + 1);

	String requestURL = request.getRequestURL().toString();

    requestURL = requestURL.substring(0,requestURL.lastIndexOf("/"));

	String cssfile = requestURL + "/css.css";

	
	
	
%>
<head>
<base href="<%=bb%>" />
<link rel="stylesheet" type="text/css" href="<%=cssfile%>">
</head>
<body>

<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" class="preview_mobileBg">

	<tr>
		<td valign="top">
			<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<th height="82" valign="bottom">‘§&nbsp; ¿¿</th>
				</tr>
				<tr>
					<td valign="top" style="padding:15px 55px;"><div style="width:100%;height:100%;overflow:auto;"><%=com.hunthawk.reader.page.util.PageUtil.getURLStream(param,requestURL)%></div></td>
				</tr>
				<tr>
					<td height="52" align="center" valign="top" style="color:#7e0501;"><strong>±ÍÃ‚</strong></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>
