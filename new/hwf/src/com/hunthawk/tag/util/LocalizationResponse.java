package com.hunthawk.tag.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

//import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

//import org.apache.log4j.Logger;

public class LocalizationResponse extends HttpServletResponseWrapper {
//	private static Logger log = Logger.getLogger(LocalizationResponse.class);
	private PrintWriter writer = null;
	private String local = null;
	
	
	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public LocalizationResponse(HttpServletResponse arg0) {	
		super(arg0);	
	}
		
	public PrintWriter getWriter() throws IOException {
		if(null == writer)
			writer = new LocalizationWriter(
					new OutputStreamWriter(getOutputStream(), 
							"UTF-8"));
		
		((LocalizationWriter)writer).setLocal(local);
		
		return writer;
	}
}
