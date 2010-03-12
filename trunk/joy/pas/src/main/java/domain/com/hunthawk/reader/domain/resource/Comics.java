/**
 * 
 */
package com.hunthawk.reader.domain.resource;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * 漫画/动画
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_resource_comics")
@PrimaryKeyJoinColumn(name="resource_id")
public class Comics extends ResourceAll{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2151447846273850074L;

	//制作时间
	private Date productTime;
	
	/**
	 * 封面图片
	 */
	private String image;
	
	/**
	 * 栏目数量
	 */
	private Integer listCount;
	
	/**
	* 序，前言
	*/	
	private String rFirst;

	/**
	* 后续
	*/	
	private String rAfter;

	@Column(name = "product_time")
	public Date getProductTime() {
		return productTime;
	}

	public void setProductTime(Date productTime) {
		this.productTime = productTime;
	}
	@Column(name = "list_count")
	public Integer getListCount() {
		return listCount;
	}

	public void setListCount(Integer listCount) {
		this.listCount = listCount;
	}
	
	@Column(name = "pm_pic")
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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
