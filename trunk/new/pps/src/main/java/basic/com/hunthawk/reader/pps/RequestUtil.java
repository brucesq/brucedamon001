/**
 * 
 */
package com.hunthawk.reader.pps;

import java.util.List;

import cn.joy.ggg.commons.JoyConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.device.PersonInfo;
import com.hunthawk.reader.domain.device.UAInfo;

/**
 * @author BruceSun
 *
 */
public class RequestUtil {

	private static ThreadLocal<HwfRequestInfo> contain = new ThreadLocal<HwfRequestInfo>();
	/**
	 * ��ȡ�û��ֻ�����
	 * @return
	 */
	public static String getMobile(){
		HwfRequestInfo info = contain.get();
		return info.getMobile();
	}
	/**
	 * �Ʒѿ���,����Ƿ���true����Ҫ�Ʒ�,false�����߼�
	 * @return
	 */
	public static boolean isFeeDisabled(){
		return false;
	}
	/**
	 * ��ȡ�û�UA��Ϣ
	 * @return
	 */
	public static String getUa(){
		HwfRequestInfo info = contain.get();
		return info.getUa();
	}
	/**
	 * ��ȡ�û�����������
	 * @return
	 */
	public static String getAreaId(){
		HwfRequestInfo info = contain.get();
		return info.getAreaId();
	}
	/**
	 * ��ȡUAInfo��Ϣ
	 * @return
	 */
	public static UAInfo getUAInfo(){
		HwfRequestInfo info = contain.get();
		return info.getUAInfo();
	}
	/**
	 * ��ȡ����Ҫ��WAPģ������
	 * @return
	 */
	public static int getNeedWapType(){
		HwfRequestInfo info = contain.get();
//		Product product = info.getProduct();
//		if(product != null){
//			if(product.getIsadapter() != 0){//��Ҫ���䣬ʹ���û��ն˵�֧�ֵ�����
				return info.getUAInfo().getWapType();
//			}
//		}
//		return 1;
	}
	/**
	 * ��ȡ����Ҫ�Ŀ�խ������
	 * @return 1խ��2����
	 */
	public static int getNeedScreenType(){
		HwfRequestInfo info = contain.get();
//		Product product = info.getProduct();
//		if(product != null){
//			if(product.getCredit() != 0){//��Ҫ���䣬ʹ���û��ն˵Ŀ�խƽ����
				return info.getUAInfo().getScreenType();
//			}
//		}
//		return 1;
	}
	/**
	 * ���Cookieֵ
	 * @param name
	 * @param value
	 */
	public static void addCookie(String name,String value){
		HwfRequestInfo info = contain.get();
		info.addCookie(name, value);
	}
	
	public static void addJoyCookie(String name, String value) {
	  HwfRequestInfo info = contain.get();
	  JoyConstants.setCookieVal(info.getResponse(), name, value, JoyConstants.COOKIE_DOMAIN, 315360000);
	}
	/**
	 * ��ȡ��Ʒ��Ϣ
	 * @return
	 */
//	public static Product getProduct(){
//		HwfRequestInfo info = contain.get();
//		return info.getProduct();
//	}
	/**
	 * ��ȡ�û�����,1����ͨ�����û���2����ͨ�û�����ȡ�����ֻ����룬3����ͨ3G�û���6�������û���7���ƶ��û�
	 * @return
	 */
	public static int getMobileType(){
		HwfRequestInfo info = contain.get();
		return info.getMobileType();
	}
	
	/**
	 * ��ȡ�û���������Ϣ
	 * @return
	 */
	public static List<PersonInfo> getPersonInfo(){
		HwfRequestInfo info = contain.get();
		return info.getPersonInfo();
	}
	
	public static void setRequest(HttpServletRequest request,HttpServletResponse response){
		contain.set(new HwfRequestInfo(request,response));
	}
	
	public static void clear(){
		contain.remove();
	}
}
