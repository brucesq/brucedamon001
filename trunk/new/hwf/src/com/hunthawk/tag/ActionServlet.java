/**
 * 
 */
package com.hunthawk.tag;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hunthawk.tag.process.Redirect;
import com.hunthawk.tag.process.RedirectUtil;
import com.hunthawk.tag.process.Refresh;
import com.hunthawk.tag.process.RefreshUtil;
import com.hunthawk.tag.process.TagProcess;
import com.hunthawk.tag.protocol.Version;
import com.hunthawk.tag.protocol.VersionHolder;
import com.hunthawk.tag.template.DefaultTemplateFactory;
import com.hunthawk.tag.util.ContentFilter;

/**
 * @author sunquanzhi
 * @since 1.0
 */
@SuppressWarnings("serial")
public class ActionServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(ActionServlet.class);

	private TagFactory factory;

	public void init() {
		factory = TagFactory.getInstance("hwf-config*.xml");
	}

	public void destroy() {
		factory = null;
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		process(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		process(request, response);
	}

	/**
	 * <p>
	 * Process the user request
	 * </p>
	 * 
	 * @since 1.0
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void process(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// long startTime = System.currentTimeMillis();

		request.setCharacterEncoding("UTF-8");
		String content = "";
		String template = TemplateFactory.getInstance(
				factory.getTemplateClass()).getTemplate(request);
		if (template != null) {
			VersionHolder.setVersion(template);

			logger.info("Process: begin TagProcess");
			content = TagProcess.process(template, factory, request);
			logger.info("Process: end TagProcess");

			Redirect redirect = RedirectUtil.getRedirect();
			if (redirect != null) {
				if (redirect.getMode() == 1) {
					response.sendRedirect(redirect.getGotourl());
					VersionHolder.clear();
					RedirectUtil.clear();
					return;
				}
				content = processRedirect(redirect);
				RedirectUtil.clear();

			}
			Refresh refresh = RefreshUtil.getRefresh();
			if (refresh != null) {
				content = RefreshUtil.process(content, refresh, VersionHolder
						.getVersion());
				RefreshUtil.clear();
			}
			content = ContentFilter.filter(content);
		}

		Version version = VersionHolder.getVersion();
		if (version == null) {
			version = VersionHolder.setVersion(content);
		}
		response.setContentType(version.getContentType());
		VersionHolder.clear();

		PrintWriter out = response.getWriter();
		out.println(content);
		// long endTime = System.currentTimeMillis();
		// logger.info((endTime - startTime)+" ms");
	}

	private static DefaultTemplateFactory defaultTemplateFactory = new DefaultTemplateFactory();

	/**
	 * <p>
	 * Process redirect
	 * </p>
	 * 
	 * @since 1.1
	 * @param redirect
	 * @return
	 */
	protected String processRedirect(Redirect redirect) {
		System.out.println(redirect.getMode()+":asasasaasas");
		if (redirect.getMode() == 0) {
			String template = defaultTemplateFactory
					.getFileTemplate(TagConstants.REDIRECT_TEMPLATE);
			try {
				template = template.replaceAll("\\$#title#", redirect
						.getTitle());
				template = template.replaceAll("\\$#time#", ""
						+ redirect.getTime());
				template = template.replaceAll("\\$#gotourl#", redirect
						.getGotourl());
				template = template.replaceAll("\\$#gototext#", redirect
						.getGototext());
			} catch (Exception e) {
				logger.error("跳转参数不全", e);
			}
			return template;
		} else {
			String template = defaultTemplateFactory
					.getFileTemplate(TagConstants.FORWARD_TEMPLATE);
			try {
				template = template.replaceAll("\\$#title#", redirect
						.getTitle());
				template = template.replaceAll("\\$#gotourl#", redirect
						.getGotourl());
				template = template.replaceAll("\\$#gototext#", redirect
						.getGototext());
			} catch (Exception e) {
				logger.error("跳转参数不全", e);
			}
			return template;
		}

	}
}
