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
	 * 获取用户手机号码
	 * @return
	 */
	public static String getMobile(){
		HwfRequestInfo info = contain.get();
		return info.getMobile();
	}
	/**
	 * 计费开关,如果是返回true不需要计费,false正常逻辑
	 * @return
	 */
	public static boolean isFeeDisabled(){
		return false;
	}
	/**
	 * 获取用户UA信息
	 * @return
	 */
	public static String getUa(){
		HwfRequestInfo info = contain.get();
		return info.getUa();
	}
	/**
	 * 获取用户所属地区号
	 * @return
	 */
	public static String getAreaId(){
		HwfRequestInfo info = contain.get();
		return info.getAreaId();
	}
	/**
	 * 获取UAInfo信息
	 * @return
	 */
	public static UAInfo getUAInfo(){
		HwfRequestInfo info = contain.get();
		return info.getUAInfo();
	}
	/**
	 * 获取所需要的WAP模板类型
	 * @return
	 */
	public static int getNeedWapType(){
		HwfRequestInfo info = contain.get();
//		Product product = info.getProduct();
//		if(product != null){
//			if(product.getIsadapter() != 0){//需要适配，使用用户终端的支持的类型
				return info.getUAInfo().getWapType();
//			}
//		}
//		return 1;
	}
	/**
	 * 获取所需要的宽窄屏类型
	 * @return 1窄屏2宽屏
	 */
	public static int getNeedScreenType(){
		HwfRequestInfo info = contain.get();
//		Product product = info.getProduct();
//		if(product != null){
//			if(product.getCredit() != 0){//需要适配，使用用户终端的宽窄平属性
				return info.getUAInfo().getScreenType();
//			}
//		}
//		return 1;
	}
	/**
	 * 添加Cookie值
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
	 * 获取产品信息
	 * @return
	 */
//	public static Product getProduct(){
//		HwfRequestInfo info = contain.get();
//		return info.getProduct();
//	}
	/**
	 * 获取用户类型,1：联通正常用户，2：联通用户但获取不到手机号码，3：联通3G用户，6：电信用户，7：移动用户
	 * @return
	 */
	public static int getMobileType(){
		HwfRequestInfo info = contain.get();
		return info.getMobileType();
	}
	
	/**
	 * 获取用户白名单信息
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
