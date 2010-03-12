/**
 * 
 */
package com.hunthawk.reader.domain.bussiness;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author BruceSun
 *
 */
@Embeddable
public class DefaultTemplateSetPK  implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4116760892035367776L;
	/**
	 * ��Ŀ����Դ�����顢�ʷ�
	 */
	private Integer pageType;
	/**
	 * 1.x 2.0 3.0
	 */
	private Integer wapType;
	
	@Column(name = "PAGE_TYPE")
	public Integer getPageType() {
		return pageType;
	}
	public void setPageType(Integer pageType) {
		this.pageType = pageType;
	}
	@Column(name = "WAP_TYPE")
	public Integer getWapType() {
		return wapType;
	}
	public void setWapType(Integer wapType) {
		this.wapType = wapType;
	}
	
	
}
