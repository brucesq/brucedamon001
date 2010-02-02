/**
 * 
 */
package com.hunthawk.reader.pps.usercener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @author liuxh
 *
 */
public final  class PropertiesUtil {
	private static final long serialVersionUID = 1589137653198362417L;
	//UserLogin:tcp -h 219.234.95.190 -p 10000  对象标识，服务端口号
	private static final String DEF_ADAPTER_SIGN_LOGIN="login";
	private static final String DEF_ADAPTER_SIGN_REGIST="regist";
	private static final String DEF_ADAPTER_SIGN_EDITINFO="editinfo";
	
	private static String point;//服务端口号
	private static String remoteIP;//远程IP
	private static String protocol;//协议
	private static Map<String,String> adapterObjs=new HashMap<String,String>();//适配置对象
	private static String param_login;
	private static String param_regist;
	private static String param_editinfo;
	
	public static String getParam_login() {
		return adapterObjs.get(DEF_ADAPTER_SIGN_LOGIN)+":"+protocol+" -h "+remoteIP+" -p "+point;
	}


	public static String getParam_regist() {
		return  adapterObjs.get(DEF_ADAPTER_SIGN_REGIST)+":"+protocol+" -h "+remoteIP+" -p "+point;
	}


	public static String getParam_editinfo() {
//		System.out.println(adapterObjs.get("editinfo")+":"+protocol+"  -h "+remoteIP+"  -p "+point);
		return adapterObjs.get(DEF_ADAPTER_SIGN_EDITINFO)+":"+protocol+" -h "+remoteIP+" -p "+point;
	}


	static { 
		try {
			Properties props = new Properties();
			InputStream in = PropertiesUtil.class.getResourceAsStream("/usercenter.properties");
			props.load(in);
			point=props.getProperty("ice.client.param.point").trim();
			remoteIP=props.getProperty("ice.client.param.remoteIP").trim();
			protocol=props.getProperty("ice.client.param.protocol").trim();
			
			String[] tmp=props.getProperty("ice.client.param.adapterObjs").trim().split(",");
			adapterObjs.put(DEF_ADAPTER_SIGN_LOGIN, tmp[0]);
			adapterObjs.put(DEF_ADAPTER_SIGN_REGIST,tmp[1]);
			adapterObjs.put(DEF_ADAPTER_SIGN_EDITINFO, tmp[2]);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private static String getPoint() {
		return point;
	}

	private static void setPoint(String point) {
		PropertiesUtil.point = point;
	}

	private static String getRemoteIP() {
		return remoteIP;
	}

	private static void setRemoteIP(String remoteIP) {
		PropertiesUtil.remoteIP = remoteIP;
	}

	private static String getProtocol() {
		return protocol;
	}

	private static void setProtocol(String protocol) {
		PropertiesUtil.protocol = protocol;
	}

	
	public static void main(String [] args){
		System.out.println(PropertiesUtil.getParam_login());
	}
}
