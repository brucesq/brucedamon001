/**
 * 
 */
package com.hunthawk.reader.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * ��־
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_resource_magazine")
@PrimaryKeyJoinColumn(name="resource_id")
public class Magazine extends ResourceAll{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7598254455077150616L;
	/**
	 * ����ͼƬ
	 */
	private String image;
	/**
	 * ��Ŀ����
	 */
	private Integer listCount;
	
	/**
	* ��ǰ��
	*/	
	private String rFirst;

	/**
	* ����
	*/	
	private String rAfter;

	
	@Column(name = "pm_pic")
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	@Column(name = "list_count")
	public Integer getListCount() {
		return listCount;
	}
	public void setListCount(Integer listCount) {
		this.listCount = listCount;
	}
	
	@Column(name = "r_first")
	public String getRFirst() {
		return rFirst;
	}
	public void setRFirst(String first) {
		rFirst = first;
	}
	@Column(name = "r_after")
	public String getRAfter() {
		return rAfter;
	}
	public void setRAfter(String after) {
		rAfter = after;
	}
	
	
}
