/**
 * 
 */
package com.hunthawk.framework.hibernate;

import org.hibernate.criterion.Criterion;

/**
 * @author sunquanzhi
 *
 */
public interface HibernateExpression {
	public Criterion createCriteria();
}
