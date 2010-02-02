/**
 * 
 */
package com.hunthawk.reader.enhance;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.enhance.AfterEnhance;
import com.hunthawk.framework.enhance.PolicyComponent;
import com.hunthawk.framework.security.SecurityContextHolder;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.framework.util.ClassUtilities;
import com.hunthawk.framework.util.PositionUtil;
import com.hunthawk.reader.domain.system.Log;

/**
 * @author BruceSun
 * 
 */
public class LogableEnhance implements AfterEnhance {

	private PolicyComponent policyComponent;

	private HibernateGenericController hibernateGenericController;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setHibernateGenericController(
			HibernateGenericController hibernateGenericController) {
		this.hibernateGenericController = hibernateGenericController;
	}

	public void init() {
		policyComponent.registerAfter(this);
	}

	public void after(Method method, Object[] args, Object owner,
			Object return_obj) throws Throwable {
		if (ClassUtilities.isAnnotationPresent(method, Logable.class)) {
			try {
				Logable logable = ClassUtilities.getAnnotation(method,
						Logable.class);
				String keys = getKeys(PositionUtil.getArg(
						logable.keyposition(), args), logable.keyproperty());
				String values = "";
				for (int i = 0; i < logable.position().length; i++) {
					values += "{"
							+ getValues(PositionUtil.getArg(
									logable.position()[i], args), logable
									.property()[i]) + "}\n";
				}
				Log log = new Log();
				log.setDetail(values);
				log.setLogTime(new Date());
				log.setName(logable.name());
				if (SecurityContextHolder.getContext().getUser() == null) {
					log.setUserId(-1);
				} else {
					
					log.setUserId(SecurityContextHolder.getContext().getUser()
							.getId());
				}

				log.setKey(keys);
				log.setAction(logable.action());
				LogThread logThread = new LogThread(log,hibernateGenericController);
				Thread thread = new Thread(logThread);
				thread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * <p>
	 * 获取当前操作的内容
	 * </p>
	 * 
	 * @param obj
	 * @param properties
	 * @return
	 */
	private String getValues(Object objs, String properties) {
		String detail = "";
		if (objs instanceof Collection) {
			for (Object obj : (Collection) objs) {
				detail += getValues(obj, properties);
			}
		} else {
			String[] pvs = properties.split(",");
			detail += "[";
			for (String str : pvs) {
				String[] pv = str.split("=");
				String[] propertys = pv[0].split("\\.");
				detail += pv[1] + ":";
				try {
					Object value = objs;
					if (!pv[0].equals("native")) {
						for (int i = 0; i < propertys.length; i++) {
							value = BeanUtils.forceGetProperty(value,
									propertys[i]);

						}
					}
					detail += value;
				} catch (Exception e) {
					detail += "空";
				}
				detail += "; ";
			}
			detail += "]";
		}

		return detail;
	}

	/**
	 * <p>
	 * 获取操作日志主键集合
	 * </p>
	 * 
	 * @param objs
	 * @param properties
	 * @return
	 */
	private String getKeys(Object objs, String properties) {
		String keys = "";
		if (objs instanceof Collection) {
			for (Object o : (Collection) objs) {
				keys += getKeys(o, properties);
			}
		} else {
			try{
				if(properties.equals("native")){
					keys += "K" + objs.toString()+ ",";
				}else{
					keys += "K" + BeanUtils.forceGetProperty(objs, properties) + ",";
				}
			}catch(Exception e){
				
			}
		}
		return keys;
	}

	class LogThread implements Runnable {
		private Log log;
		private HibernateGenericController hibernateGenericController;
		
		public LogThread(Log log,
				HibernateGenericController hibernateGenericController) {
			super();
			this.log = log;
			this.hibernateGenericController = hibernateGenericController;
		}

		public void run() {
			hibernateGenericController.save(log);
		}
	}
}