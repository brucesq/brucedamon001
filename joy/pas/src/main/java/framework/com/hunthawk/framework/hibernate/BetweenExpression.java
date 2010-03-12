/**
 * 
 */
package com.hunthawk.framework.hibernate;

import java.io.Serializable;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * <p>
 * 属性区间内
 * </p>
 * 
 * @author sunquanzhi
 * 
 */
@SuppressWarnings("serial")
public class BetweenExpression implements HibernateExpression, Serializable {

	String propertyName;
	Object lo;
	Object hi;

	/**
	 * 
	 * @param propertyName
	 * @param lo
	 * @param hi
	 */
	public BetweenExpression(String propertyName, Object lo, Object hi) {
		this.propertyName = propertyName;
		this.lo = lo;
		this.hi = hi;
	}

	public Criterion createCriteria() {
		return Restrictions.between(propertyName, lo, hi);
	}

}
