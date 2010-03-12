/**
 * 
 */
package com.hunthawk.framework.tapestry;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.lib.impl.SpringBeanFactoryHolderImpl;
import org.apache.tapestry.web.WebContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * 
 * @author sunquanzhi
 *
 */


public class TapestrySpringBeanFactoryHolderImpl extends
		SpringBeanFactoryHolderImpl implements RegistryShutdownListener {

	private WebContext context;

	public BeanFactory getBeanFactory() {
		if(super.getBeanFactory()==null) {
			super.setBeanFactory(getWebApplicationContext(this.getContext()));
		}
		return super.getBeanFactory();
	}

	public void registryDidShutdown() {
		((ConfigurableApplicationContext) super.getBeanFactory()).close();
	}

	private static WebApplicationContext getWebApplicationContext(WebContext wc) {
		Object obj = wc
				.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (obj == null) {
			return null;
		}
		if (obj instanceof RuntimeException) {
			throw (RuntimeException) obj;
		}
		if (obj instanceof Error) {
			throw (Error) obj;
		}
		if (!(obj instanceof WebApplicationContext)) {
			throw new IllegalStateException("获取不到WebApplicationContext对象："
					+ obj);
		}
		return (WebApplicationContext) obj;
	}

	public WebContext getContext() {
		return context;
	}

	public void setContext(WebContext context) {
		this.context = context;
	}

}
