package com.hunthawk.reader.domain.adapter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * 适配器
 * @author penglei
 *
 */
@Entity
@Table(name = "reader_adapter_adapter")
public class Adapter extends PersistentObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6305366608165825021L;

	/**
	 * ID，唯一主键，自增长
	 */
	private Integer id;
	
	/**
	 * 适配器名称
	 */
	private String name;
	
	/**
	 * 适配类型ID
	 */
	private Integer adapterTypeId;
	
	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * 状态
	 */
	private Integer status;
	
	/**
	 * 创建者ID
	 */
	private Integer creatorId;

	/**
	 * 创建时间
	 */
	private Date createTime;
	
	
	/**
	 * 最后修改者ID
	 */
	private Integer modifierId;
	
	/**
	 * 最后修改时间
	 */
	private Date modifyTime;
	
	
	

	public Adapter() {
		super();
	}

	public Adapter(Integer id, String name, Integer adapterTypeId,
			String description, Integer status, Integer creatorId,
			Date createTime, Integer modifierId, Date modifyTime) {
		super();
		this.id = id;
		this.name = name;
		this.adapterTypeId = adapterTypeId;
		this.description = description;
		this.status = status;
		this.creatorId = creatorId;
		this.createTime = createTime;
		this.modifierId = modifierId;
		this.modifyTime = modifyTime;
	}

	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "adapter_Type_id")
	public Integer getAdapterTypeId() {
		return adapterTypeId;
	}

	public void setAdapterTypeId(Integer adapterTypeId) {
		this.adapterTypeId = adapterTypeId;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "Creator_Id")
	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	@Column(name = "Create_Time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "Modifier_Id")
	public Integer getModifierId() {
		return modifierId;
	}

	public void setModifierId(Integer modifierId) {
		this.modifierId = modifierId;
	}

	@Column(name = "Modify_Time")
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
}
