/**
 * 
 */
package com.hunthawk.reader.enhance;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import com.hunthawk.framework.enhance.BeforeEnhance;
import com.hunthawk.framework.enhance.PolicyComponent;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.LogicalExpression;
import com.hunthawk.framework.hibernate.LogicalType;
import com.hunthawk.framework.security.SecurityContextHolder;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.framework.util.ClassUtilities;
import com.hunthawk.reader.domain.system.Group;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.enhance.annotation.Filter;
import com.hunthawk.reader.security.PowerUtil;

/**
 * @author BruceSun
 *
 */
public class FilterEnhance implements BeforeEnhance {
	
	
	 private static final String USERS = "users";

	PolicyComponent policyComponent;
	public void setPolicyComponent(PolicyComponent policyComponent)
	{
		this.policyComponent = policyComponent;
	}
	public void init()
	{
		policyComponent.registerBefore(this);
	}
	/* (non-Javadoc)
	 * @see com.hunthawk.framework.enhance.BeforeEnhance#before(java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
	 */
	public void before(Method method, Object[] args, Object owner)
			throws Throwable {
		if(ClassUtilities.isAnnotationPresent(method,Filter.class) && SecurityContextHolder.getContext().getUser() != null){
			if(SecurityContextHolder.getContext().getUser().hasRole("notfilter"))
				return;
			Filter filter = ClassUtilities.getAnnotation(method,Filter.class);
			Object  obj = getArg(filter.value(),args);
			if(obj != null){
				UserImpl user = (UserImpl)SecurityContextHolder.getContext().getUser();
				Collection<HibernateExpression> expressions = (Collection<HibernateExpression>)obj;
				expressions.addAll(createUserExpressions(filter.targetClass(),user));
				
			}
		}
	}
	
	private Collection<HibernateExpression> createUserExpressions(Class clazz,UserImpl user){
		 Field field= null;
	     try{
	            field = BeanUtils.getDeclaredField(clazz, USERS);
	     }
        catch(Exception e){}

        Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();

        if (field!=null && user != null && !user.isAdmin()) {
            expressions = new ArrayList<HibernateExpression>();

            CompareExpression ownerExpression=new CompareExpression(USERS,
                                                  PowerUtil.OWNER + ":" +
                                                  user.getId() + ";%",
                                                  CompareType.Like);
            CompareExpression userExpression=new CompareExpression(USERS,
                                                  "%" + PowerUtil.USER +
                                                  user.getId() + ":%",
                                                  CompareType.Like);

            LogicalExpression usersExpression=new LogicalExpression(ownerExpression,userExpression,LogicalType.Or);

           
                for (Group group : user.getGroups()) {
                    CompareExpression groupExpression=new CompareExpression(
                            USERS,
                            "%" + PowerUtil.GROUP +
                            group.getId() + ":%",
                            CompareType.Like);

                    usersExpression = new LogicalExpression(usersExpression,groupExpression,LogicalType.Or);
                }
            

            expressions.add(usersExpression);


        }

        return expressions;
	}

	private Object getArg(Filter.Position position,Object[] args)
	{
		Object arg = null;
		switch(position)
		{
		case ARG_1:
			 arg = getArg(0,args);
			 break;
		case ARG_2:
			 arg = getArg(1,args);
			 break;
		case ARG_3:
			 arg = getArg(2,args);
			 break;
		case ARG_4:
			 arg = getArg(3,args);
			 break;
		case ARG_5:
			 arg = getArg(4,args);
			 break;
		case ARG_6:
			 arg = getArg(5,args);
			 break;
		case ARG_7:
			 arg = getArg(6,args);
			 break;
		case ARG_8:
			 arg = getArg(7,args);
			 break;
		case ARG_9:
			 arg = getArg(8,args);
			 break;
		default:
				 break;
		}
		return arg;
	}
	private Object getArg(int pos,Object[] args)
	{
		if(args.length > pos)
		{
			return args[pos];
		}
		return null;
	}

}
