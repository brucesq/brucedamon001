/**
 * In hivemodule.xml:
   <service-point id="HibernateAdapter" interface="org.apache.tapestry.util.io.SqueezeAdaptor">
        <invoke-factory>
            <construct class="com.aspire.pams.framework.tapestry.HibernateSqueezer"/>
		    <set-object property="hibernateGenericController" value="spring:hibernateGenericController" />
        </invoke-factory>
    </service-point>
    <contribution configuration-id="tapestry.data.SqueezeAdaptors">
        <adaptor object="service:HibernateAdapter" />
    </contribution>
 *
 */
package com.hunthawk.framework.tapestry;

import java.io.Serializable;

import org.apache.tapestry.services.DataSqueezer;
import org.apache.tapestry.util.io.SqueezeAdaptor;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.Persistent;

/**
 * <p>
 * Hibernate对象序列化类
 * </p>
 * 
 * @author sunquanzhi
 * 
 */
public class HibernateSqueezer implements SqueezeAdaptor {

	// public HibernateSqueezer(HibernateGenericController controller)
	// {
	// this.controller = controller;
	// }
	private static final String PREFIX = "h";

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public HibernateGenericController getHibernateGenericController() {
		return this.controller;
	}

	public Class getDataClass() {
		return Persistent.class;
	}

	public String getPrefix() {
		return PREFIX;
	}

	public String squeeze(DataSqueezer squeezer, Object data) {
		String entityName = controller.getEntityName(data);
		String id = "";
		try {
			id = squeezer.squeeze(controller.getId(data.getClass(), data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO the object's version should be taken into account here.
		return PREFIX + entityName + ":" + id;
	}

	public Object unsqueeze(DataSqueezer squeezer, String string) {
		string = string.substring(1);

		int pos = string.indexOf(':');
		String entityName = string.substring(0, pos);
		String idString = string.substring(pos + 1);
		Serializable id = (Serializable) squeezer.unsqueeze(idString);
		try {

			Object obj = controller.get(entityName, id);
			if (obj == null) {
				obj = Class.forName(entityName).newInstance();
			}
			return obj;
		} catch (Exception e) {
			try {
				return Class.forName(entityName).newInstance();
			} catch (Exception ex) {
				return null;
			}
		}
		// TODO check for stale data (see squeeze)

	}

}
