package com.hunthawk.reader.pps;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hunthawk.reader.enhance.util.Des;
import com.hunthawk.tag.TagConstants;
import com.hunthawk.tag.TagFactory;
import com.hunthawk.tag.TemplateFactory;
import com.hunthawk.tag.process.Redirect;
import com.hunthawk.tag.process.RedirectUtil;
import com.hunthawk.tag.process.Refresh;
import com.hunthawk.tag.process.RefreshUtil;
import com.hunthawk.tag.process.TagProcess;
import com.hunthawk.tag.protocol.Version;
import com.hunthawk.tag.protocol.VersionHolder;
import com.hunthawk.tag.template.DefaultTemplateFactory;
import com.hunthawk.tag.util.ContentFilter;

public class ActionServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -498520875520787274L;

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

	
	private String sessionId = "sid";

	private String cookieDomain = "";

	private String cookiePath = "/";
	
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

//		request.setCharacterEncoding("UTF-8");
//		String mobileSec = request.getParameter("mobileno");
//		if(StringUtils.isNotEmpty(mobileSec)){
//			Des des = new Des();
//			String mobileNo = des.decrypt(mobileSec);
//			Cookie mycookies = new Cookie(sessionId, mobileNo);
//			mycookies.setMaxAge(315360000);
//			if (this.cookieDomain != null && this.cookieDomain.length() > 0) {
//				mycookies.setDomain(this.cookieDomain);
//			}
//			mycookies.setPath(this.cookiePath);
//			response.addCookie(mycookies);
//			request.getSession().setAttribute("x-up-calling-line-id", mobileNo);
//		}
//		RequestUtil.setRequest(request,response);
//		FeeProcess.process(request);
		
		String page = request.getParameter(ParameterConstants.PAGE);
		String resourceId = URLUtil.getResourceId(request);
		if(ParameterConstants.PAGE_RESOURCE.equals(page)){
			StatisticsLog.logStat(1, resourceId);
		}else if(ParameterConstants.PAGE_DETAIL.equals(page)){
			StatisticsLog.logStat(1, resourceId);
		}
		
		String content = "";
		String template = TemplateFactory.getInstance(
				factory.getTemplateClass()).getTemplate(request);
		if (template != null) {
			VersionHolder.setVersion(template);
			content = TagProcess.process(template, factory, request);
			Redirect redirect = RedirectUtil.getRedirect();
			if (redirect != null) {
				if (redirect.getMode() == 1) {
					AccessLog.log(request, 0, RequestUtil.getNeedWapType());
					response.sendRedirect(redirect.getGotourl());
					VersionHolder.clear();
					RedirectUtil.clear();
					RequestUtil.clear();
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
			content = content.replaceAll ( "!and!" , "&" ) ;
		}

		Version version = VersionHolder.getVersion();
		if (version == null) {
			version = VersionHolder.setVersion(content);
		}
		response.setContentType(version.getContentType());
		PrintWriter out = response.getWriter();
		out.println(content);
		AccessLog.log(request, content.getBytes().length, RequestUtil.getNeedWapType());
		
		VersionHolder.clear();
		RequestUtil.clear();
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
