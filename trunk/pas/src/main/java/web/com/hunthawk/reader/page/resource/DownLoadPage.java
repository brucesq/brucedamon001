/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.util.ContentType;

/**
 * @author BruceSun
 * 
 */
public abstract class DownLoadPage extends BasePage {

	@InjectObject("service:tapestry.globals.HttpServletResponse")
	public abstract HttpServletResponse getServletResponse();

	private File file;

	public void setFile(File file) {
		this.file = file;
	}

	public ContentType getResponseContentType() {
		return new ContentType("application/octet-stream");
	}

	@Override
	public void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {

		HttpServletResponse response = getServletResponse();
		response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition", "attachment;filename="
				+ file.getName());
		response.addHeader("Content-Length", "" + file.length());
		FileInputStream in = null;
		try {
			OutputStream out = response.getOutputStream();
			in = new FileInputStream(file);

			byte[] b = new byte[1024];
			int i = 0;

			while ((i = in.read(b)) > 0) {
				out.write(b, 0, i);
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApplicationRuntimeException(e);
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

	}
}
