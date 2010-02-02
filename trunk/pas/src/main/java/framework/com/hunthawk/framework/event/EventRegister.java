/**
 * 
 */
package com.hunthawk.framework.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.hunthawk.framework.AbstractLifecycleBean;
import com.hunthawk.framework.annotation.Observer;
import com.hunthawk.framework.util.ClassUtilities;

/**
 * <p>
 * ÊÂ¼þ×¢²á
 * </p>
 * 
 * @author sunquanzhi
 * 
 */
public class EventRegister extends AbstractLifecycleBean {

	private List<String> observerNames = new ArrayList<String>();
	private EventComponent eventComponent;

	public void setObserverNames(List<String> observerNames) {
		this.observerNames = observerNames;
	}

	public void setEventComponent(EventComponent eventComponent) {
		this.eventComponent = eventComponent;
	}

	private void register() {
		for (String beanName : observerNames) {
			Object object = getApplicationContext().getBean(beanName);
			Method[] methods = object.getClass().getMethods();
			for (Method method : methods) {
				Observer observer = ClassUtilities.getAnnotation(method,
						Observer.class);
				if (observer != null) {
					JavaBehaviour behaviour = new JavaBehaviour(object, method,
							observer.positions());
					eventComponent.register(behaviour, observer.mode(),
							observer.value());
				}
			}
		}
	}

	protected void onBootstrap(ApplicationEvent event) {
		register();
	}

	protected void onShutdown(ApplicationEvent event) {
		observerNames.clear();
	}

}
