package com.hunthawk.reader.domain.resource;

import com.hunthawk.framework.domain.PersistentObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author xianlaichen
 *
 */
@Entity
@Table(name = "reader_resource_ebook_tome")
public class EbookTome  extends PersistentObject{


	/**
	 * 
	 */
	private static final long serialVersionUID = 7595736986123271500L;
	
	/**
	* 图书卷ID
	* 规则为YXXXXXXXNN,共10位，
	* Y:资源类别1图书，2报纸，3杂志，4漫画，5铃声，6视频
	* XXXXXXX: 资源序列号，由0补足位数
	* NN:卷序列号，由0补足位数
	*/

	private String id;
	
	/**
	* 资源ID
	*/
	private String resourceId;
	
	/**
	* 卷名称
	*/
	private String name;
	
	/**
	* 卷序号
	*/	
	private Integer tomeIndex;
	
	
	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "tome_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name = "resource_id",nullable = false)
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	
	@Column(name = "tome_name",nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	@Column(name = "tome_index",nullable = false)
	public Integer getTomeIndex() {
		return tomeIndex;
	}

	public void setTomeIndex(Integer tomeIndex) {
		this.tomeIndex = tomeIndex;
	}

}
