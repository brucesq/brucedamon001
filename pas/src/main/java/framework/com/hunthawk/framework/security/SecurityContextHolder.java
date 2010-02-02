/**
 * 
 */
package com.hunthawk.framework.security;

import org.springframework.util.Assert;

import com.hunthawk.framework.security.simple.MockSecurityContext;

/**
 * @author sunquanzhi
 *
 */
@SuppressWarnings("unchecked")
public class SecurityContextHolder {

	private static ThreadLocal contextHolder = new InheritableThreadLocal();
	
	public static void clearContext() {
        contextHolder.set(null);
    }

    public static SecurityContext getContext() {
        if (contextHolder.get() == null) {
            contextHolder.set(new MockSecurityContext());
        }

        return (SecurityContext) contextHolder.get();
    }

    public static void setContext(SecurityContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder.set(context);
    }

}
