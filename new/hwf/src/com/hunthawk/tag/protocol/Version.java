/**
 * 
 */
package com.hunthawk.tag.protocol;

/**
 * <p>支持的协议信息</p>
 * @author sunquanzhi
 *
 */
public class Version {
	
	public static final String WAP_VERSION_1= "wap1.x";
	public static final String WAP_VERSION_2= "wap2.0";
	/**手机报客户端**/
	public static final String WAP_VERSION_ASPIRE = "aspire";
	/**掌上精彩客户端**/
	public static final String WAP_VERSION_JSJC = "jsjc";
	
	public static final String WAP_VERSION_JCLIENT = "jclient";
	
	public static final String CONTENT_TYPE_1 = "text/vnd.wap.wml;charset=utf-8";
	public static final String CONTENT_TYPE_2 = "text/html;charset=utf-8";
	public static final String CONTENT_TYPE_NORMAL = "text/xml;charset=utf-8";
	public static final String CONTENT_TYPE_JSJC = "text/vnd.wap.wml;charset=utf-8";
	public static final String CONTENT_TYPE_JCLIENT = "text/vnd.wap.wml;charset=utf-8";
	
	public static final String SUFFIX_1 = ".wml";
	public static final String SUFFIX_2 = ".xhtml";
	public static final String SUFFIX_NORMAL = ".xhtml";
	public static final String SUFFIX_JSJC = ".jsjc";
	public static final String SUFFIX_JCLIENT = ".jclient";
	
	/**响应头信息,如：text/vnd.wap.wml;charset=utf-8**/
	private String contentType ;
	/**内容协议，如：wap1.x**/
	private String protocol ;
	/**扩展文件符，如.wml**/
	private String suffix;
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
