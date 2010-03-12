/**
 * 
 */
package com.hunthawk.reader.domain.bussiness;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.hunthawk.reader.domain.resource.ResourcePack;

/**
 * 栏目资源关联
 * @author BruceSun
 *
 */
@Entity
@Table(name="READER_BUSSINESS_COLUMN_RES")
@IdClass(ColumnResourceRelationId.class)
public class ColumnResourceRelation implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -845588340492868014L;
	
	//批价包关联ID
	private Integer relId;
	//栏目ID
	private Integer columnId;
	//批价包
	private ResourcePack pack;
	
	private String resourceId;
	//Cpid(包月的就为空)
	private String cpid;
	//计费id(包月的就为空)
	private String feeId;
	//设置计费点，从第几个章节开始计费(包月的就为空)
	private Integer choice;
	//排序
	private Integer order;
	//状态默认上线状态
	private Integer status;
	
	@Id
	@Column(name = "REL_ID")
	public Integer getRelId() {
		return relId;
	}
	public void setRelId(Integer id) {
		this.relId = id;
	}
	
	
	@Id
	@Column(name = "COLUMN_ID")
	public Integer getColumnId() {
		return columnId;
	}
	public void setColumnId(Integer columnId) {
		this.columnId = columnId;
	}
	
	@ManyToOne
	@JoinColumn(name="FEE_PACK_ID",nullable=false)
	public ResourcePack getPack() {
		return pack;
	}
	public void setPack(ResourcePack pack) {
		this.pack = pack;
	}
	@Column(name = "RESOURCE_ID",nullable=false)
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	@Column(name = "CPID")
	public String getCpid() {
		return cpid;
	}
	public void setCpid(String cpid) {
		this.cpid = cpid;
	}
	@Column(name = "FEEID")
	public String getFeeId() {
		return feeId;
	}
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	@Column(name = "FEE_CHOICE")
	public Integer getChoice() {
		return choice;
	}
	public void setChoice(Integer choice) {
		this.choice = choice;
	}
	@Column(name = "RES_ORDER")
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	@Column(name = "RES_STATUS")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
