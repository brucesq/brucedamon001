/**
 * 
 */
package com.hunthawk.framework.hibernate;

import java.io.Serializable;
import java.util.Collection;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * @author BruceSun
 *
 */
public class NotInExpression implements Serializable, HibernateExpression {

	Object[] values;
	String propertyName;
	public NotInExpression(String propertyName,Object[] values)
	{
		this.propertyName = propertyName;
		this.values = values;
	}
	public NotInExpression(String propertyName,Collection values)
	{
		this.propertyName = propertyName;
		this.values = values.toArray();
	}
	public Criterion createCriteria() {
		
		return Restrictions.not(Restrictions.in(propertyName,values));
	}

}
