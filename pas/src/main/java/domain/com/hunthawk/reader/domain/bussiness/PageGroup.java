package com.hunthawk.reader.domain.bussiness;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
// @javax.persistence.SequenceGenerator(name = "packgroup", sequenceName =
// "reader_bussiness_packgroup_seq")
@Table(name = "reader_bussiness_packgroup")
public class PageGroup extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1922521525546918382L;

	private Integer id;

	private String pkName;
	// * 页面组描述
	private String pkComment;

	// * 页面组使用状态
	private Integer pkStatus;

	// * 页面组表示(WAP，客户端，手持终端)
	private Integer showType;

	// * 首页模板id(选择按产品或按页面组)wap1x
	private Integer pkOneTempId;

	// 首页模板id(选择按产品或按页面组)wap2.0
	private Integer pkSecondTempId;
	// 首页模板id(选择按产品或按页面组)3g
	private Integer pkThirdTempId;
	// * 删除状态(使用，隐藏)
	private Integer deleteStatus;
	// * 默认资源模板id(全局的)
	private Integer resOneTempId;
	// * 资源模板id(全局的) wap2.0

	private Integer resSecondTempId;
	// * 资源模板id(全局的) 3g

	private Integer resThirdTempId;

	private Date createTime;

	private Integer creator;

	// 用户
	private String users;
	
	 //* 修改日期
	private Date modifyTime;
	 //* 修改人ID
	private Integer modifier;

	// @OneToMany(mappedBy="packGroupId",cascade=CascadeType.ALL)
	// private Set<Columns> columns = new HashSet<Columns>();
	// public Set<Columns> getColumns() {
	// return columns;
	// }
	// public void setColumns(Set<Columns> columns) {
	// this.columns = columns;
	// }
	// public void addColumns(Columns column)
	// {
	// for(Columns col : columns)
	// {
	// if(col.equals(column))
	// return;
	// }
	//		
	// columns.add(column);
	//		
	// }

	@Id
	// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	// "packgroup")
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "pack_group_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "pk_name", nullable = false)
	public String getPkName() {
		return pkName;
	}

	public void setPkName(String pkName) {
		this.pkName = pkName;
	}

	@Column(name = "pk_comment", nullable = true)
	public String getPkComment() {
		return pkComment;
	}

	public void setPkComment(String pkComment) {
		this.pkComment = pkComment;
	}

	@Column(name = "pk_user_status", nullable = false)
	public Integer getPkStatus() {
		return pkStatus;
	}

	public void setPkStatus(Integer pkStatus) {
		this.pkStatus = pkStatus;
	}

	@Column(name = "pk_show_type", nullable = false)
	public Integer getShowType() {
		return showType;
	}

	public void setShowType(Integer showType) {
		this.showType = showType;
	}

	@Column(name = "pk_first_tem_id", nullable = false)
	public Integer getPkOneTempId() {
		return pkOneTempId;
	}

	public void setPkOneTempId(Integer pkOneTempId) {
		this.pkOneTempId = pkOneTempId;
	}

	@Column(name = "pk_second_tem_id")
	public Integer getPkSecondTempId() {
		return pkSecondTempId;
	}

	public void setPkSecondTempId(Integer pkSecondTempId) {
		this.pkSecondTempId = pkSecondTempId;
	}

	@Column(name = "pk_g_tem_id")
	public Integer getPkThirdTempId() {
		return pkThirdTempId;
	}

	public void setPkThirdTempId(Integer pkThirdTempId) {
		this.pkThirdTempId = pkThirdTempId;
	}

	@Column(name = "pk_delete_status", nullable = false)
	public Integer getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	@Column(name = "pk_res_tem_id")
	public Integer getResOneTempId() {
		return resOneTempId;
	}

	public void setResOneTempId(Integer resOneTempId) {
		this.resOneTempId = resOneTempId;
	}

	@Column(name = "pk_res_sencond_tem_id")
	public Integer getResSecondTempId() {
		return resSecondTempId;
	}

	public void setResSecondTempId(Integer resSecondTempId) {
		this.resSecondTempId = resSecondTempId;
	}

	@Column(name = "pk_res_g_tem_id")
	public Integer getResThirdTempId() {
		return resThirdTempId;
	}

	public void setResThirdTempId(Integer resThirdTempId) {
		this.resThirdTempId = resThirdTempId;
	}

	@Column(name = "in_date", nullable = false)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "creator_id", nullable = false)
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

	// 页面组状态
	@Transient
	public String getPkStatusName() {
		String typeName = "未知状态";
		if (pkStatus != null) {
			for (Map.Entry<String, Integer> entry : Constants
					.getProductStatus().entrySet()) {
				if (entry.getValue().equals(pkStatus))
					return entry.getKey();
			}
		}
		return typeName;
	}

	public boolean equals(Object o) {
		boolean bEquals = HibernateEqualsBuilder
				.reflectionEquals(this, o, "id");
		return bEquals;
	}

	@Column(name = "p_users", length = 255)
	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	@Transient
	public String getShowTypeName() {
		String typeName = "未知类型";
		if (showType != null) {
//			for (Map.Entry<String, Integer> entry : Constants
//					.getBussinessTypes().entrySet()) {
//				if (entry.getValue().equals(showType))
//					return entry.getKey();
//			}
			return Constants.getBussinessTypeName(showType);
		}
		return typeName;
	}
	@Transient
	public String getShowModifyTimeName() {
		if(modifyTime != null){
			return ToolDateUtil.dateToString(modifyTime);
		}else{
			return "";
		}
		
	}
}
