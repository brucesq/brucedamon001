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
 * 新闻数据表
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_resource_paper")
@PrimaryKeyJoinColumn(name="resource_id")
public class NewsPapers extends ResourceAll{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2036835593973576341L;
	/**
	 * 封面图片
	 */
	private String image;
	/**
	 * 栏目数量
	 */
	private Integer listCount;
	
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
	
	
	
	
	
}
