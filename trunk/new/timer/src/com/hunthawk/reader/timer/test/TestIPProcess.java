/**
 * 
 */
package com.hunthawk.reader.timer.test;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.reader.domain.system.IpInfo;
import com.hunthawk.reader.timer.job.StatisticsAccessLogJob;

/**
 * @author sunquanzhi
 *
 */
public class TestIPProcess {
	public static void main(String[] args) {
//		XmlBeanFactory ctx = new XmlBeanFactory(new ClassPathResource("com/hunthawk/reader/timer/test/ip.xml"));
//		HibernateGenericController c = (HibernateGenericController)ctx.getBean("hibernateGenericController");
//		long startTime = System.currentTimeMillis();
//		for(int i=0;i<1000000;i++){
//			IpInfo info = StatisticsAccessLogJob.getIpOperator("22.76.124.0", c);
//			System.out.println(info.getOperators()+":"+info.getProvinceId());
//		}
//		long endTime = System.currentTimeMillis();
//
//		System.out.println((endTime-startTime));
		long startTime = System.currentTimeMillis();
		for(int i=0;i<1000000;i++){
		   StatisticsAccessLogJob.processIp("10.171.11.0, 211.143.145.209");
		}
		long endTime = System.currentTimeMillis();
		System.out.println((endTime-startTime));
	}
	
}
