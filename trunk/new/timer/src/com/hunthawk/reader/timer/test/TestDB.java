/**
 * 
 */
package com.hunthawk.reader.timer.test;

import java.util.ArrayList;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.reader.domain.statistics.LogData;
import com.hunthawk.reader.domain.system.Variables;

/**
 * @author BruceSun
 *
 */
public class TestDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XmlBeanFactory ctx = new XmlBeanFactory(new ClassPathResource("com/hunthawk/reader/timer/test/test.xml"));
		HibernateGenericController c = (HibernateGenericController)ctx.getBean("hibernateGenericController");
		HibernateGenericController etl = (HibernateGenericController)ctx.getBean("etlhibernateGenericController");
		System.out.println(c.getResultCount(Variables.class, new ArrayList()));
		System.out.println(etl.getResultCount(LogData.class, new ArrayList()));
		

	}

}
