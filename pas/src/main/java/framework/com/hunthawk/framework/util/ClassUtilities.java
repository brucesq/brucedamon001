/**
 * 
 */
package com.hunthawk.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author sunquanzhi
 *
 */
public class ClassUtilities
{
    /**
     * <p>判断是否是cglib生成的类</p>
     * @param clasz
     * @return
     */
    public static  Class getNestClass(Class clasz)
    {
        if (clasz.getName().indexOf("$$EnhancerByCGLIB$$") > -1)
        {
            return clasz.getSuperclass();
        }else{
            return clasz;
        }
    }
    
    public static  Method getNestMethod(final Class clasz,final Method method)
    {
        if (clasz.getName().indexOf("$$EnhancerByCGLIB$$") > -1)
        {
            Class targetClasz = clasz.getSuperclass();
            try{
                return targetClasz.getMethod(method.getName(), method.getParameterTypes());
            }catch(Exception e)
            {
                e.printStackTrace();
                return method;
            }
           
        }else{
            return method;
        }
        
    }
    public static boolean isAnnotationPresent(Method method,Class<? extends Annotation> clasz)
    {
        if(method.isAnnotationPresent(clasz))
            return true;
        Class[] faces = method.getDeclaringClass().getInterfaces();
        for(Class c : faces)
        {
        	System.out.println(c.getName());
            try{
                Method m = c.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return m.isAnnotationPresent(clasz);
            }catch(Exception e){
            	e.printStackTrace();
            }
        }
        return false;
    }
    
    public static <T extends Annotation> T getAnnotation(Method method,Class<T> clasz)
    {
        T anno = method.getAnnotation(clasz);  
        if(anno != null)
            return anno;
        Class[] faces = method.getDeclaringClass().getInterfaces();
        for(Class c : faces)
        {
            try{
                Method m = c.getDeclaredMethod(method.getName(), method.getParameterTypes());
                anno = m.getAnnotation(clasz);
                if(anno != null)
                    return anno;
            }catch(Exception e){}
        }
        return null;
    }
}
