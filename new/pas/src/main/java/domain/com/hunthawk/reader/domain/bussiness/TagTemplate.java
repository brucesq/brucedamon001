/**
 * 
 */
package com.hunthawk.reader.domain.bussiness;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name = "READER_TAG_TEMPLATE")
public class TagTemplate  extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3308617538480894404L;
	
	private Integer id;

	private String name;
	
	private String tagName;
	
	private String wmlContent;
	
	private String htmlContent;

	 //* 创建日期
	private Date createTime;
	 //* 创建用户id
	private Integer creator;
	
	 //* 修改日期
	private Date modifyTime;
	 //* 修改人ID
	private Integer modifier;
	
	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "TAG_NAME")
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	@Column(name = "WML_CONTENT")
	public String getWmlContent() {
		return wmlContent;
	}

	public void setWmlContent(String wmlContent) {
		this.wmlContent = wmlContent;
	}
	@Column(name = "HTML_CONTENT")
	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
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


}
