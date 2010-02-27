/**
 * 
 */
package com.hunthawk.reader.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_resource_application")
@PrimaryKeyJoinColumn(name="resource_id")
public class Application extends ResourceAll{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8055485685153724944L;
	
	//封面图
	private String image;
	

	/**
     * 单机/联网
     */
    private Integer isOnline = 0;
    
    /**
     * 版本
     */
    private String applicationEdition;
    
	
	@Column(name = "pm_pic")
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	@Column(name = "is_online")
	public Integer getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(Integer isOnline) {
		this.isOnline = isOnline;
	}
	
	@Column(name = "app_edition")
	public String getApplicationEdition() {
		return applicationEdition;
	}
	public void setApplicationEdition(String applicationEdition) {
		this.applicationEdition = applicationEdition;
	}
	
}
