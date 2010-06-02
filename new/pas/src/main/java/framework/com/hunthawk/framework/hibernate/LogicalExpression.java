/**
 * 
 */
package com.hunthawk.framework.hibernate;

import java.io.Serializable;

import org.hibernate.criterion.Criterion;

/**
 * @author sunquanzhi
 *
 */
@SuppressWarnings("serial")
public class LogicalExpression extends  org.hibernate.criterion.LogicalExpression implements HibernateExpression, Serializable {

	
	
	public LogicalExpression(HibernateExpression lhs,HibernateExpression rhs,LogicalType type)
	{
		super(lhs.createCriteria(),rhs.createCriteria(),type.getValue());
	}
	
	public Criterion createCriteria() {
		return  this;
	}

}
