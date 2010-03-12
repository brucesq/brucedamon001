/**
 * 
 */
package com.hunthawk.framework.hibernate;

import java.io.Serializable;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * <p>©уеп╤о</p>
 * @author sunquanzhi
 *
 */
@SuppressWarnings("serial")
public class NullExpression implements HibernateExpression, Serializable {

	String propertyName;
	
	public NullExpression(String propertyName)
	{
		this.propertyName = propertyName;
	}
	public Criterion createCriteria() {
		
		return Restrictions.isNull(propertyName);
	}

}
