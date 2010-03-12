/**
 * 
 */
package com.hunthawk.reader.enhance;

import java.lang.reflect.Method;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hunthawk.framework.enhance.AfterEnhance;
import com.hunthawk.framework.enhance.PolicyComponent;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.framework.util.ClassUtilities;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.enhance.annotation.Memcached;

/**
 * @author BruceSun
 * 
 */
public class MemcachedEnhance implements AfterEnhance {

	private static final Logger logger = LoggerFactory
			.getLogger(MemcachedEnhance.class);

	private PolicyComponent policyComponent;

	private MemCachedClientWrapper memcached;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public void init() {
		policyComponent.registerAfter(this);
	}

	public void after(Method method, Object[] args, Object owner,
			Object return_obj) throws Throwable {

		if (memcached == null)
			return;

		if (ClassUtilities.isAnnotationPresent(method, Memcached.class)) {
			Memcached memcacheAnno = ClassUtilities.getAnnotation(method,
					Memcached.class);
			Object obj = getArg(memcacheAnno.value(), args);
			if (obj != null) {
				String[] props = memcacheAnno.properties();
				for (String prop : props) {
					clear(prop.split("#"), obj, memcacheAnno);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void clear(String[] props, Object obj, Memcached memcacheAnno) {
		String key = memcacheAnno.targetClass().getName();
		boolean currentDate = false;
		for (String prop : props) {
			if (prop.startsWith("!")) {
				key += Constants.MEMCACHED_SLASH + prop.substring(1);
			}else if(prop.startsWith("@")){//日期处理
				currentDate = true;
				key += Constants.MEMCACHED_SLASH + prop.substring(1);
			}else if(prop.equals("native")){
				key += Constants.MEMCACHED_SLASH + obj;
			}else {
				try {
					String value = getProperty(obj, prop);
					key += Constants.MEMCACHED_SLASH + value;
				} catch (Exception e) {
					logger.error("Memcached Enhance error!", e);
				}
			}

		}
		
		if(currentDate){
			memcached.set(key, new Date(),0);
			return ;
		}
		if (memcacheAnno.type().equals(Memcached.Type.DELETE)) {
			memcached.delete(key);
		} else {
			memcached.set(key, obj,24 * MemCachedClientWrapper.HOUR);
		}

	}
	
	private String getProperty(Object obj,String property){
		String[] propertys = property.split("\\.");
		try{
			Object value = obj;
			for(String str : propertys){
				value = BeanUtils.forceGetProperty(value, str);
			}
			return value.toString();
		}catch(Exception e){
			logger.error("Memcached Enhance error!", e);
			return "";
		}
		
	}

	private Object getArg(Memcached.Position position, Object[] args) {
		Object arg = null;
		switch (position) {
		case ARG_1:
			arg = getArg(0, args);
			break;
		case ARG_2:
			arg = getArg(1, args);
			break;
		case ARG_3:
			arg = getArg(2, args);
			break;
		case ARG_4:
			arg = getArg(3, args);
			break;
		case ARG_5:
			arg = getArg(4, args);
			break;
		case ARG_6:
			arg = getArg(5, args);
			break;
		case ARG_7:
			arg = getArg(6, args);
			break;
		case ARG_8:
			arg = getArg(7, args);
			break;
		case ARG_9:
			arg = getArg(8, args);
			break;
		default:
			break;
		}
		return arg;
	}

	private Object getArg(int pos, Object[] args) {
		if (args.length > pos) {
			return args[pos];
		}
		return null;
	}

	public static void main(String[] args) {
		String s = "url";
		System.out.println(s.split("#").length);
	}
}
