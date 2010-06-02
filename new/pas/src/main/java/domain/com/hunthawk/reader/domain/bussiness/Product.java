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
		YESNO.put("否",0);
		YESNO.put("是",1);
	}
	
	//是否动态匹配
	public static Map<String,Integer> getYesNo(){
		return YESNO;
	}
	//* 产品ID
	private String id;
	 //* 产品名称
	private String name;
	//* 归属渠道
	
	
	//渠道ID
	//private Integer channelTypeId;
	private Channel channel;
	 //* 状态（0 已上线 1 待上线 2 暂停 3 下线）
	private Integer status;
	 //* 产品类型(1.WAP，2.客户端，3 手持终端)
	private Integer showType;
	 //* 是否动态适配（WAP1.x 或 WAP2.x 或 3G）标识
	private Integer isadapter;
	 //* 是否自动适配宽/窄版(默认否)
	
	private Integer credit;
	 //* 创建日期
	private Date createTime;
	 //* 创建用户id
	private Integer creator;
	
	 //* 修改日期
	private Date modifyTime;
	 //* 修改人ID
	private Integer modifier;
	/**
	* 用户
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
	
	//产品类型
	@Transient
	public String getShowTypeName(){
		String typeName = "未知类型";
		if(showType != null){
			for(Map.Entry<String,Integer> entry :Constants.getBussinessTypes().entrySet()){
				if(entry.getValue().equals(showType))
					return entry.getKey();
			}
		}
		return typeName;
	}
	
	
	//是否动态匹配(WAP1.x 或 WAP2.x 或 3G)
	@Transient
	public String getIsadapterName(){
		String typeName = "否";
		if(isadapter != null){
			for(Map.Entry<String,Integer> entry :getYesNo().entrySet()){
				if(entry.getValue().equals(isadapter))
					return entry.getKey();
			}
		}
		return typeName;
	}

	
	//是否动态匹配(宽/窄版)
	@Transient
	public String getCreditName(){
		String typeName = "否";
		if(credit != null){
			for(Map.Entry<String,Integer> entry :getYesNo().entrySet()){
				if(entry.getValue().equals(credit))
					return entry.getKey();
			}
		}
		return typeName;
	}
	
	//产品状态
	@Transient
	public String getStatusName(){
		String typeName = "未知状态";
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
//		String typeName = "未知类型";
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
