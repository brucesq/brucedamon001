/**
 * 
 */
package com.hunthawk.reader.domain.resource;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * @author BruceSun
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "pack",
//		sequenceName="READER_RESOURCE_PACK_FEE_SEQ"
//)
@Table(name = "READER_RESOURCE_PACK_FEE")
public class ResourcePack extends PersistentObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6958806023516251973L;
	//���۰�ID
	private Integer id;
	//���۰�����
	private String name;
	//���۰�����.�ƴ�1����Ŀ���£�VIP��2����Ŀ���£����ݿ��ƣ�3����Ŀ���£����棩4�����5
	private Integer type;
	//���۰��۸�
	private String code;
	//ѡ�񼸸�����20ѡ3��(������µ�)
	private Integer choice;
	
	private String spid;
	//�Ʒ�ID
	private String feeId;
	
	private Integer creator;
	
	private Date createTime;

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="pack")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "fee_pack_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "FEE_PACK_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "FEE_TYPE")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	@Column(name = "FEE_CODE")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	@Column(name = "R_CHOICE")
	public Integer getChoice() {
		return choice;
	}

	public void setChoice(Integer choice) {
		this.choice = choice;
	}
	@Column(name = "SPID")
	public String getSpid() {
		return spid;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}
	@Column(name = "FEEID")
	public String getFeeId() {
		return feeId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	@Column(name = "CREATOR_ID")
	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}
	@Column(name = "IN_DATE")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	//��Ʒ����
	@Transient
	public String getShowTypeName(){
		String typeName = "δ֪����";
		if(type != null){
			for(Map.Entry<String,Integer> entry :Constants.getFeeTypeMap().entrySet()){
				if(entry.getValue().equals(type))
					return entry.getKey();
			}
		}
		return typeName;
	}
	
	@Transient
	public String getShowModifyTimeName() {
		if(createTime != null){
			return ToolDateUtil.dateToString(createTime);
		}else{
			return "";
		}
		
	}
}
