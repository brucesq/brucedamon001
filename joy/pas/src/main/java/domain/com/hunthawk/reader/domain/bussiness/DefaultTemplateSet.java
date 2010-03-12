/**
 * 
 */
package com.hunthawk.reader.domain.bussiness;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * @author Administrator
 * 
 */
@Entity
@Table(name = "READER_BUSSINESS_TEM_DEFAULT")
@IdClass(DefaultTemplateSetPK.class)
public class DefaultTemplateSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 134234133434L;
	/**
	 * 栏目、资源、详情、资费
	 */
	private Integer pageType;
	/**
	 * 1.x 2.0 3.0
	 */
	private Integer wapType;

	private Integer templateId;

	@Id
	@Column(name = "PAGE_TYPE")
	public Integer getPageType() {
		return pageType;
	}

	public void setPageType(Integer pageType) {
		this.pageType = pageType;
	}

	@Id
	@Column(name = "WAP_TYPE")
	public Integer getWapType() {
		return wapType;
	}

	public void setWapType(Integer wapType) {
		this.wapType = wapType;
	}

	@Column(name = "TEMPLATE_ID")
	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

}
