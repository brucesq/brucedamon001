<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 <%@ page import="java.io.*" %>   
<%
	String fileDir = request.getParameter("file");
	File file = new File(fileDir);
	
	//HttpServletResponse response = getServletResponse();
	response.reset();
	response.setContentType("application/octet-stream");
	response.addHeader("Content-Disposition", "attachment;filename="
			+ file.getName());
	response.addHeader("Content-Length", "" + file.length());
	FileInputStream in = null;
	try {
		OutputStream outp = response.getOutputStream();
		in = new FileInputStream(fileDir);
	
		byte[] b = new byte[1024];
		int i = 0;
	
		while ((i = in.read(b)) > 0) {
			outp.write(b, 0, i);
		}
		outp.flush();
		out.clear();
		pageContext.pushBody();

	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			in = null;
		}
	}


%>