/**
 * 
 */
package com.hunthawk.reader.domain.inter;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * 留言板块
 * @author BruceSun
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "msgboard",
//		sequenceName="reader_inter_msgboard_seq"
//)
@Table(name = "reader_inter_msgboard")
public class MsgBoard extends PersistentObject{

	private static Map<String,Integer> MSGBOARDSTATUS = new TreeMap<String,Integer>();
	static{
		MSGBOARDSTATUS.put("上线", 1);
		MSGBOARDSTATUS.put("下线", 0);
		
	}
	
	public static Map<String,Integer> getBoardStatus(){
		return MSGBOARDSTATUS;
	}
	
	private static Map<String,Integer> MSGBOARDAUDIT = new TreeMap<String,Integer>();
	static{
		MSGBOARDAUDIT.put("先审后发", 1);
		MSGBOARDAUDIT.put("先发后审", 0);
		
	}
	
	public static Map<String,Integer> getBoardAudit(){
		return MSGBOARDAUDIT;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8641201291483197236L;
	
	private Integer id;
	//父板块
	private MsgBoard parent;
	//板块名称
	private String name;
	//状态 1上线 0下线
	private Integer status;
	//是否需要审核, 1:先审后发；0：先发后审
	private Integer auditing;
	
	private Date createTime;
	
	private Date updateTime;
	
	private Integer creator;
	
	private Integer modifier;

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="msgboard")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="parent_id")
	public MsgBoard getParent() {
		return parent;
	}

	public void setParent(MsgBoard parent) {
		this.parent = parent;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "auditing")
	public Integer getAuditing() {
		return auditing;
	}

	public void setAuditing(Integer auditing) {
		this.auditing = auditing;
	}
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "creator_id")
	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}
	@Column(name = "modifier_id")
	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}
	@Transient
	public String getAuditName(){
		if(auditing == null)
			return "";
		switch(auditing){
		case  1:
			return "先审后发";
		case 0:
			return "先发后审";
		
		}
		return "";
	}
	
	@Transient
	public String getStatusName(){
		if(status == null)
			return "";
		switch(status){
		case  1:
			return "上线";
		case 0:
			return "下线";
		
		}
		return "";
	}
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
}
