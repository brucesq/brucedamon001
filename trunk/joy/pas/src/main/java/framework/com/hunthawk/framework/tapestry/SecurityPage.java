/**
 * 
 */
package com.hunthawk.framework.tapestry;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.log4j.Logger;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IRender;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.binding.ListenerMethodBinding;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageValidateListener;
import org.apache.tapestry.form.AbstractFormComponent;
import org.apache.tapestry.form.Form;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.services.ServiceConstants;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.security.SecurityComponent;
import com.hunthawk.framework.security.User;
import com.hunthawk.framework.security.Visit;
import com.hunthawk.reader.page.util.PageUtil;

/**
 * @author sunquanzhi
 * 
 */
public abstract class SecurityPage extends BasePage implements
		PageValidateListener {

	protected static Logger logger = Logger
			.getLogger("com.aspire.pams.framework.page");

	public static final String LoginPage = "LoginPage";
	public static final String HomePage = "Home";
	public static final String AccessDeniedPage = "AccessDeniedPage";

	@InjectObject("spring:securityComponent")
	public abstract SecurityComponent getSecurityComponent();

	@InjectObject("spring:hibernateGenericController")
	public abstract HibernateGenericController getHibernateGenericController();

	@InjectState("visit")
	public abstract Visit getVisit();

	@Bean
	public abstract ValidationDelegate getDelegate();

	public boolean hasRole(String... roles) {
		return getSecurityComponent().hasRole(roles);
	}

	public boolean hasPermission(String name, String action, Object... args) {

		return getSecurityComponent().hasPermission(name, action, args);
	}

	public User getUser() {
		return getVisit().getUser();
	}

	public SecurityPage() {
		super();

	}

	@InjectObject("service:tapestry.globals.HttpServletRequest")
	public abstract HttpServletRequest getServletRequest();

	/**
	 * <p>
	 * 页面验证
	 * </p>
	 */
	public void pageValidate(PageEvent event) {

		Restrict restrict = this.getClass().getSuperclass().getAnnotation(
				Restrict.class);

		String contextPath = getServletRequest().getContextPath();
		String accessDeniedPage = "http://"
				+ PageUtil.getDomainName(getServletRequest().getRequestURL()
						.toString()) + contextPath + "/" + AccessDeniedPage
				+ ".page;jsessionid="
				+ getServletRequest().getSession().getId();

		String loginPage = "http://"
				+ PageUtil.getDomainName(getServletRequest().getRequestURL()
						.toString()) + contextPath + "/" + LoginPage + ".page";

		if (restrict != null) {

			if (getUser() == null) {

				throw new RedirectException(loginPage);
			}

			if (!getSecurityComponent().hasPermission(restrict, this)) {
				throw new RedirectException(accessDeniedPage);
			}
		}
		if (!methodValidate()) {
			throw new RedirectException(accessDeniedPage);
		}
	}

	private boolean methodValidate() {
		String componentName = this.getRequestCycle().getParameter(
				ServiceConstants.COMPONENT);
		if (componentName == null || "".equals(componentName))
			return true;
		String methodName = null;
		if (componentName != null
				&& this.getRequestCycle().getAttribute("isChecked") == null) {
			try {
				ListenerMethodBinding lmb = (ListenerMethodBinding) this
						.getComponent(componentName).getBinding("listener");
				if (lmb != null)
					methodName = this.getMethodName(lmb.toString());
				else
					methodName = getMethodName(getBindingString(componentName));
				this.getRequestCycle().setAttribute("isChecked",
						new StringBuffer("T"));
			} catch (ApplicationRuntimeException e) {

			}
		}

		if (methodName != null) {
			Method[] ms = this.getClass().getMethods();
			Method method = null;
			for (int i = 0; i < ms.length; i++) {
				if (ms[i].getName().equals(methodName)) {
					method = ms[i];
					break;
				}
			}
			if (method != null) {
				Restrict restrict = method.getAnnotation(Restrict.class);
				// 得到方法相应的配置数据
				if (restrict != null) {
					return getSecurityComponent().hasPermission(restrict,
							method, new Object[0], this);
				}
			} else {
				logger.warn("Method:" + methodName + " not found!");
			}
		}

		return true;
	}

	private String getBindingString(String formid) {
		if (formid == null)
			return null;
		IComponent ic = this.getComponent(formid);
		if (!(ic instanceof Form))
			return null;
		Form ff = (Form) ic;
		IRender[] ir = ff.getBody();
		if (ir == null)
			return null;
		ListenerMethodBinding lmb = null;
		for (int i = ir.length - 1; i >= 0; i--) {
			if (ir[i] instanceof AbstractFormComponent) {
				AbstractFormComponent afc = (AbstractFormComponent) ir[i];
				lmb = (ListenerMethodBinding) afc.getBinding("listener");
				if (lmb != null)
					break;
			}
		}
		// <form success="listener:foo" jwcid="@Form" />等情况
		if (lmb == null)
			return ic.getBindings().toString();
		return lmb.toString();
	}

	private String getMethodName(String methodSource) {
		if (methodSource == null)
			return null;
		String methodName = methodSource.toString();
		int start = methodName.indexOf("methodName=");
		if (start == -1)
			return null;
		start += 11;
		methodName = methodName.substring(start);
		methodName = methodName.substring(0, methodName.indexOf(","));
		// methodName =
		// this.getPage().getSpecification().getComponentClassName() + "." +
		// methodName;
		// logger.info("methodName=" + methodName);
		return methodName;
	}

	public Integer getTablePageSize(){
		return 50;
	}
}
