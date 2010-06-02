/**
 * 
 */
package com.hunthawk.reader.domain.bussiness;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * @author BruceSun
 * 
 */
@Entity
// @javax.persistence.SequenceGenerator(
// name = "template",
// sequenceName="READER_BUSSINESS_TMPL_SEQ"
// )
@Table(name = "reader_bussiness_template")
public class Template extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7248823760249446350L;

	private Integer id;

	/**
	 * 模版内容
	 */
	private String content;
	/**
	 * 模版类型
	 */
	private TemplateType templateType;

	/**
	 * 模板目录
	 */
	private TemplateCatalog templateCatalog;
	/**
	 * 用户
	 */
	private String users;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 模版标题
	 */
	private String title;
	/**
	 * 模板类型标识(WAP1.x 或 WAP2.x 或 3G)
	 */
	private Integer signType;
	/**
	 * 输出类型(WAP，客户端，手持终端)
	 */
	private Integer showType;
	/**
	 * 下载页模板
	 */
	private Integer downPageId;
	
	/**
	 * 预览使用
	 */
	private String preContent;
	/**
	 * 上一次的状态
	 */
	private Integer preStatus;

	private Date createTime;
	private Integer createorId;
	private Date modifyTime;
	private Integer motifierId;

	@Id
	// @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="template")
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "template_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "t_content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@ManyToOne
	@JoinColumn(name = "t_attach_id", nullable = false)
	public TemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;
	}

	@ManyToOne
	@JoinColumn(name = "T_CATALOG_ID", nullable = false)
	public TemplateCatalog getTemplateCatalog() {
		return templateCatalog;
	}

	public void setTemplateCatalog(TemplateCatalog templateCatalog) {
		this.templateCatalog = templateCatalog;
	}

	@Column(name = "t_users", length = 255)
	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	@Column(name = "is_use", nullable = false)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "t_name", unique = true, nullable = false, length = 50)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "t_sign_type", nullable = false)
	public Integer getSignType() {
		return signType;
	}

	public void setSignType(Integer signType) {
		this.signType = signType;
	}

	@Column(name = "t_show_type", nullable = false)
	public Integer getShowType() {
		return showType;
	}

	public void setShowType(Integer showType) {
		this.showType = showType;
	}

	@Column(name = "t_createtime")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "channel_creator")
	public Integer getCreateorId() {
		return createorId;
	}

	public void setCreateorId(Integer createorId) {
		this.createorId = createorId;
	}

	@Column(name = "channel_modifytime")
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Column(name = "t_modifier")
	public Integer getMotifierId() {
		return motifierId;
	}

	public void setMotifierId(Integer motifierId) {
		this.motifierId = motifierId;
	}

	@Transient
	public String getShowTypeName() {
		String typeName = "未知类型";
		if (showType != null) {
			for (Map.Entry<String, Integer> entry : Constants
					.getBussinessTypes().entrySet()) {
				if (entry.getValue().equals(showType))
					return entry.getKey();
			}
		}
		return typeName;
	}

	@Transient
	public String getSignTypeName() {
		String typeName = "";
		if (signType != null && showType == 1) {
			for (Map.Entry<String, Integer> entry : Constants.getVersionTypes()
					.entrySet()) {
				if (entry.getValue().equals(signType))
					return entry.getKey();
			}
		}
		return typeName;
	}

	@Column(name = "down_page_id")
	public Integer getDownPageId() {
		return downPageId;
	}

	public void setDownPageId(Integer downPageId) {
		this.downPageId = downPageId;
	}

	@Transient
	public String getShowModifyTimeName() {
		if(modifyTime != null){
			return ToolDateUtil.dateToString(modifyTime);
		}else{
			return "";
		}
		
	}
	@Column(name = "PRE_CONTENT")
	public String getPreContent() {
		return preContent;
	}

	public void setPreContent(String preContent) {
		this.preContent = preContent;
	}
	@Column(name = "PRE_STATUS")
	public Integer getPreStatus() {
		return preStatus;
	}

	public void setPreStatus(Integer preStatus) {
		this.preStatus = preStatus;
	}
	
	
}
