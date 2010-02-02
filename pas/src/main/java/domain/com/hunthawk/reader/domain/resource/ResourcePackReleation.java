/**
 * 
 */
package com.hunthawk.reader.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * @author BruceSun
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "pack",
//		sequenceName="READER_RESOURCE_P_RES_INFO_SEQ"
//)
@Table(name = "READER_RESOURCE_P_RES_INFO")
public class ResourcePackReleation extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2237591564539991628L;
	//ID
	private Integer id;
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
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="pack")
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	
	@Transient
	public Integer getFirstOrder() {
		if(order != null){
			String strOrder = order+"";
			return Integer.parseInt(strOrder.substring(0, 6));
		}else{
			return 500000;
		}
		
	}
	@Transient
	public Integer getSubOrder() {
		if(order != null){
			String strOrder = order+"";
			return Integer.parseInt(strOrder.substring(6, 9));
		}else{
			return 000;
		}
		
	}
}
