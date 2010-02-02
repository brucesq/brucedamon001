package com.hunthawk.reader.domain.bussiness;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * 
 * @author liuxh
 * 
 */
@Entity
// @javax.persistence.SequenceGenerator(
// name = "column",
// sequenceName="reader_bussiness_column_seq"
// )
@Table(name = "reader_bussiness_column")
public class Columns extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7136297283419076340L;
	private Integer id;
	// * 价格包ID
	private Integer pricepackId;
	// * 栏目名称

	private String name;
	// * 栏目标题

	private String title;
	// * 栏目描述

	private String comment;

	// * 栏目图标

	private String icon;
	// * 栏目状态 (1上线，3隐藏，2下线)
	private Integer status;
	// * 栏目排序
	private Integer order;
	// * 栏目模板ID

	private Integer colOneTempId;
	// Wap2.0

	private Integer colSecondTempId;
	// 3g

	private Integer colThirdTempId;
	// * 资源模板ID

	private Integer resOneTempId;
	// Wap2.0

	private Integer resSecondTempId;
	// 3g

	private Integer resThirdTempId;
	
	
	// * 详情模板ID

	private Integer delOneTempId;
	// Wap2.0

	private Integer delSecondTempId;
	// 3g

	private Integer delThirdTempId;
	
	// * 内容显示条数设定

	private Integer countSet;

	// * 栏目有效期

	private Date expiryDate;
	// * 创建日期

	private Date createTime;
	// * 创建者ID

	private Integer creator;

	/**
	 * 父栏目
	 */

	private Columns parent;

	private PageGroup pagegroup;

	// 用户
	private String users;
	
	 //* 修改日期
	private Date modifyTime;
	 //* 修改人ID
	private Integer modifier;
	/**
	 * 栏目类型 0 普通 1 分类 2 搜索 
	 */
	private Integer columnType;
	/**
	 * 创建类型 0用户创建 1系统创建
	 */
	private Integer createType;
	/**
	 * 分类ID
	 */
	private Integer resourceTypeId;
	/**
	 * 排序类型,0按照排序ID降序、1按照ID降序、2按照点击数降序、5按照排序ID升序、6按照ID升序
	 */
	private Integer orderType;

	@ManyToOne
	@JoinColumn(name = "l_fist_id")
	public Columns getParent() {
		return parent;
	}

	public void setParent(Columns parent) {
		this.parent = parent;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PACK_GROUP_ID")
	public PageGroup getPagegroup() {
		return pagegroup;
	}

	public void setPagegroup(PageGroup pagegroup) {
		this.pagegroup = pagegroup;
	}

	@Id
	// @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="column")
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "list_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "RESOURCE_PACK_ID")
	public Integer getPricepackId() {
		return pricepackId;
	}

	public void setPricepackId(Integer pricepackId) {
		this.pricepackId = pricepackId;
	}

	@Column(name = "l_name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "l_title", nullable = false)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "l_comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "l_icon")
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Column(name = "l_use_status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "list_order")
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Column(name = "template_id")
	public Integer getColOneTempId() {
		return colOneTempId;
	}

	public void setColOneTempId(Integer colOneTempId) {
		this.colOneTempId = colOneTempId;
	}

	@Column(name = "template_id_sec")
	public Integer getColSecondTempId() {
		return colSecondTempId;
	}

	public void setColSecondTempId(Integer colSecondTempId) {
		this.colSecondTempId = colSecondTempId;
	}

	@Column(name = "template_id_g")
	public Integer getColThirdTempId() {
		return colThirdTempId;
	}

	public void setColThirdTempId(Integer colThirdTempId) {
		this.colThirdTempId = colThirdTempId;
	}

	@Column(name = "template_id_res")
	public Integer getResOneTempId() {
		return resOneTempId;
	}

	public void setResOneTempId(Integer resOneTempId) {
		this.resOneTempId = resOneTempId;
	}

	@Column(name = "TEMPLATE_ID_RES_SEC")
	public Integer getResSecondTempId() {
		return resSecondTempId;
	}

	public void setResSecondTempId(Integer resSecondTempId) {
		this.resSecondTempId = resSecondTempId;
	}

	@Column(name = "TEMPLATE_ID_RES_G")
	public Integer getResThirdTempId() {
		return resThirdTempId;
	}

	public void setResThirdTempId(Integer resThirdTempId) {
		this.resThirdTempId = resThirdTempId;
	}
	
	
	
	@Column(name = "TEMPLATE_ID_DEL")
	public Integer getDelOneTempId() {
		return delOneTempId;
	}

	public void setDelOneTempId(Integer delOneTempId) {
		this.delOneTempId = delOneTempId;
	}
	
	@Column(name = "TEMPLATE_ID_DEL_SEC")
	public Integer getDelSecondTempId() {
		return delSecondTempId;
	}

	public void setDelSecondTempId(Integer delSecondTempId) {
		this.delSecondTempId = delSecondTempId;
	}
	
	@Column(name = "TEMPLATE_ID_DEL_G")
	public Integer getDelThirdTempId() {
		return delThirdTempId;
	}

	public void setDelThirdTempId(Integer delThirdTempId) {
		this.delThirdTempId = delThirdTempId;
	}

	@Column(name = "show_number")
	public Integer getCountSet() {
		return countSet;
	}

	public void setCountSet(Integer countSet) {
		this.countSet = countSet;
	}

	@Column(name = "l_life_date")
	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Column(name = "in_date")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "creator_id")
	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}
	
	@Column(name="MODIFY_TIME")
	public Date getModifyTime() {
		return modifyTime;
	}
	
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	@Column(name="MODIFY_ID")
	public Integer getModifier() {
		return modifier;
	}
	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

	public boolean equals(Object o) {
		boolean bEquals = HibernateEqualsBuilder
				.reflectionEquals(this, o, "id");
		return bEquals;
	}
	@Transient
	public String getShowModifyTimeName() {
		if(modifyTime != null){
			return ToolDateUtil.dateToString(modifyTime);
		}else{
			return "";
		}
		
	}

	@Transient
	public String getStatusName() {
		if (status == null)
			return "";
		switch (status) {
		case 1:
			return "上线";
		case 2:
			return "下线";
		case 3:
			return "隐藏";
		}
		return "";
	}

	@Column(name = "p_users", length = 255)
	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	@Column(name="COLUMN_TYPE")
	public Integer getColumnType() {
		return columnType;
	}

	public void setColumnType(Integer columnType) {
		this.columnType = columnType;
	}

	@Column(name="CREATE_TYPE")
	public Integer getCreateType() {
		return createType;
	}

	public void setCreateType(Integer createType) {
		this.createType = createType;
	}
	
	@Column(name="RESOURCE_TYPE_ID")
	public Integer getResourceTypeId() {
		return resourceTypeId;
	}

	public void setResourceTypeId(Integer resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

	@Column(name="ORDER_TYPE")
	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	@Transient
	public String getOrderTypeName() {
		for (Map.Entry<String, Integer> entry : Constants.getOrderTypeMap().entrySet()) {
			if (entry.getValue().equals(orderType)) {
				return entry.getKey();
			}
		}
		return "";
	}
}
