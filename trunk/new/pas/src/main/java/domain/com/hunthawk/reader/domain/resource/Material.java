/**
 * 
 */
package com.hunthawk.reader.domain.resource;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * 素材
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_resource_material")
public class Material  extends PersistentObject{

	
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4365850399032582225L;

	private Integer id;

    /**
     * 名称
     *  */
    private String name;
    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件大小
     */
    private Integer size;

    /**
     * 描述
     */
    private String description;
    
    
    /**
     * 扩展名
     */
    private String extName;
    /**
     * 目录ID
     */
    private Integer catalogId;
    
    private Integer creator;
    
    private Date createTime;
    
    private Integer modifier;
    
    private Date modifyTime;

    @Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "mater_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "mater_name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "mater_filename")
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	@Column(name = "file_size")
	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	@Column(name = "mater_desc")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	@Column(name = "mater_ext")
	public String getExtName() {
		return extName;
	}

	public void setExtName(String extName) {
		this.extName = extName;
	}
	@Column(name = "creator_id")
	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}
	@Column(name = "creator_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "modifier_id")
	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}
	@Column(name = "modify_time")
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	@Column(name = "catalog_id")
	public Integer getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(Integer catalogId) {
		this.catalogId = catalogId;
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
