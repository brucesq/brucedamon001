package com.hunthawk.reader.domain.bussiness;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
/**
 * @author liuxh
 *
 */
@Entity
@Table(name = "reader_bussiness_product")
public class Product extends PersistentObject{
	 
private static final long serialVersionUID = 7699798883166815771L;
	
	private static Map<String,Integer> YESNO=new HashMap<String,Integer>();
	static{
		YESNO.put("��",0);
		YESNO.put("��",1);
	}
	
	//�Ƿ�̬ƥ��
	public static Map<String,Integer> getYesNo(){
		return YESNO;
	}
	//* ��ƷID
	private String id;
	 //* ��Ʒ����
	private String name;
	//* ��������
	
	
	//����ID
	//private Integer channelTypeId;
	private Channel channel;
	 //* ״̬��0 ������ 1 ������ 2 ��ͣ 3 ���ߣ�
	private Integer status;
	 //* ��Ʒ����(1.WAP��2.�ͻ��ˣ�3 �ֳ��ն�)
	private Integer showType;
	 //* �Ƿ�̬���䣨WAP1.x �� WAP2.x �� 3G����ʶ
	private Integer isadapter;
	 //* �Ƿ��Զ������/խ��(Ĭ�Ϸ�)
	
	private Integer credit;
	 //* ��������
	private Date createTime;
	 //* �����û�id
	private Integer creator;
	
	 //* �޸�����
	private Date modifyTime;
	 //* �޸���ID
	private Integer modifier;
	/**
	* �û�
	*/
	private String users;
	
	
//	private UserImpl user;
//	
//	public UserImpl getUser() {
//		return user;
//	}
//	public void setUser(UserImpl user) {
//		this.user = user;
//	}
	
	@Id
	@Column(name = "productid",  nullable = false)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Column(name = "s_name", nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@ManyToOne
	@JoinColumn(name="channel_id",nullable=false)
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	@Column(name = "status",nullable = false)
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "s_type", nullable = false)
	public Integer getShowType() {
		return showType;
	}
	public void setShowType(Integer showType) {
		this.showType = showType;
	}
	
	@Column(name = "is_adapter",  nullable = false)
	public Integer getIsadapter() {
		return isadapter;
	}
	public void setIsadapter(Integer isadapter) {
		this.isadapter = isadapter;
	}
	@Temporal(TemporalType.TIMESTAMP)   
    @Column(name="in_date" ,updatable = false, length = 20)   
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "creator_id",  nullable = false)
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
	@Column(name = "is_narrow",  nullable = false)
	public Integer getCredit() {
		return credit;
	}
	public void setCredit(Integer credit) {
		this.credit = credit;
	}
	
	@Column(name = "p_users",length=255)
	public String getUsers() {
		return users;
	}
	public void setUsers(String users) {
		this.users = users;
	}
	
	//��Ʒ����
	@Transient
	public String getShowTypeName(){
		String typeName = "δ֪����";
		if(showType != null){
			for(Map.Entry<String,Integer> entry :Constants.getBussinessTypes().entrySet()){
				if(entry.getValue().equals(showType))
					return entry.getKey();
			}
		}
		return typeName;
	}
	
	
	//�Ƿ�̬ƥ��(WAP1.x �� WAP2.x �� 3G)
	@Transient
	public String getIsadapterName(){
		String typeName = "��";
		if(isadapter != null){
			for(Map.Entry<String,Integer> entry :getYesNo().entrySet()){
				if(entry.getValue().equals(isadapter))
					return entry.getKey();
			}
		}
		return typeName;
	}

	
	//�Ƿ�̬ƥ��(��/խ��)
	@Transient
	public String getCreditName(){
		String typeName = "��";
		if(credit != null){
			for(Map.Entry<String,Integer> entry :getYesNo().entrySet()){
				if(entry.getValue().equals(credit))
					return entry.getKey();
			}
		}
		return typeName;
	}
	
	//��Ʒ״̬
	@Transient
	public String getStatusName(){
		String typeName = "δ֪״̬";
		if(status != null){
			for(Map.Entry<String,Integer> entry :Constants.getProductStatus().entrySet()){
				if(entry.getValue().equals(status))
					return entry.getKey();
			}
		}
		return typeName;
	}

//	@Transient
//	public String getSignTypeName(){
//		String typeName = "δ֪����";
//		if(signType != null){
//			for(Map.Entry<String,Integer> entry :Constants.getVersionTypes().entrySet()){
//				if(entry.getValue().equals(signType))
//					return entry.getKey();
//			}
//		}
//		return typeName;
//	}
	
	@Transient
	public String getShowModifyTimeName() {
		if(modifyTime != null){
			return ToolDateUtil.dateToString(modifyTime);
		}else{
			return "";
		}
		
	}
}
