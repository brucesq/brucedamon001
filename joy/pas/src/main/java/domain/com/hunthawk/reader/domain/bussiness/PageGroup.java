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
	// * ҳ��������
	private String pkComment;

	// * ҳ����ʹ��״̬
	private Integer pkStatus;

	// * ҳ�����ʾ(WAP���ͻ��ˣ��ֳ��ն�)
	private Integer showType;

	// * ��ҳģ��id(ѡ�񰴲�Ʒ��ҳ����)wap1x
	private Integer pkOneTempId;

	// ��ҳģ��id(ѡ�񰴲�Ʒ��ҳ����)wap2.0
	private Integer pkSecondTempId;
	// ��ҳģ��id(ѡ�񰴲�Ʒ��ҳ����)3g
	private Integer pkThirdTempId;
	// * ɾ��״̬(ʹ�ã�����)
	private Integer deleteStatus;
	// * Ĭ����Դģ��id(ȫ�ֵ�)
	private Integer resOneTempId;
	// * ��Դģ��id(ȫ�ֵ�) wap2.0

	private Integer resSecondTempId;
	// * ��Դģ��id(ȫ�ֵ�) 3g

	private Integer resThirdTempId;

	private Date createTime;

	private Integer creator;

	// �û�
	private String users;
	
	 //* �޸�����
	private Date modifyTime;
	 //* �޸���ID
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

	// ҳ����״̬
	@Transient
	public String getPkStatusName() {
		String typeName = "δ֪״̬";
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
		String typeName = "δ֪����";
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
