/**
 * 
 */
package com.hunthawk.framework.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hunthawk.framework.annotation.RaiseEvent;
import com.hunthawk.framework.enhance.PolicyComponent;
import com.hunthawk.framework.event.EventListener.Mode;
import com.hunthawk.framework.util.ClassUtilities;

/**
 * @author sunquanzhi
 * 
 */
public class EventComponentImpl implements EventComponent {

	private Map<String, List<JavaBehaviour>> beforeNotify = new HashMap<String, List<JavaBehaviour>>();
	private Map<String, List<JavaBehaviour>> afterNotify = new HashMap<String, List<JavaBehaviour>>();
	PolicyComponent policyComponent;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void init() {
		policyComponent.registerAfter(this);
		policyComponent.registerBefore(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.event.EventComponent#register(com.aspire.pams.framework.event.EventListener,
	 *      com.aspire.pams.framework.event.EventListener.Mode)
	 */
	public void register(JavaBehaviour listener, Mode mode, String eventName) {
		switch (mode) {
		case Before:
			List<JavaBehaviour> behaviours = beforeNotify.get(eventName);
			if (behaviours == null) {
				behaviours = new ArrayList<JavaBehaviour>();
				beforeNotify.put(eventName, behaviours);
			}
			behaviours.add(listener);
			break;
		case After:
			List<JavaBehaviour> afterbehaviours = afterNotify.get(eventName);
			if (afterbehaviours == null) {
				afterbehaviours = new ArrayList<JavaBehaviour>();
				afterNotify.put(eventName, afterbehaviours);
			}
			afterbehaviours.add(listener);
			break;
		default:
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.enhance.BeforeEnhance#before(java.lang.reflect.Method,
	 *      java.lang.Object[], java.lang.Object)
	 */
	public void before(Method method, Object[] args, Object owner)
			throws Throwable {

		if (ClassUtilities.isAnnotationPresent(method, RaiseEvent.class)) {
			RaiseEvent event = ClassUtilities.getAnnotation(method,
					RaiseEvent.class);

			String eventName = event.value();

			if (eventName.equals("")) {
				eventName = owner.getClass().getName() + "." + method.getName();
			}
			List<JavaBehaviour> behaviours = beforeNotify.get(eventName);
			if (behaviours != null) {
				for (JavaBehaviour listener : behaviours) {
					listener.invoke(args, null);
				}
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.enhance.AfterEnhance#after(java.lang.reflect.Method,
	 *      java.lang.Object[], java.lang.Object, java.lang.Object)
	 */
	public void after(Method method, Object[] args, Object owner,
			Object return_obj) throws Throwable {

		if (ClassUtilities.isAnnotationPresent(method, RaiseEvent.class)) {
			RaiseEvent event = ClassUtilities.getAnnotation(method,
					RaiseEvent.class);
			;
			String eventName = event.value();
			if (eventName.equals("")) {
				eventName = owner.getClass().getName() + "." + method.getName();
			}
			List<JavaBehaviour> behaviours = afterNotify.get(eventName);
			if (behaviours != null) {
				for (JavaBehaviour listener : behaviours) {
					listener.invoke(args, return_obj);
				}
			}

		}

	}

}
