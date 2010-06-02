package com.hunthawk.reader.domain.resource;

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
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * @author xianlaichen
 * 
 */
@Entity
@Table(name = "reader_resource_type")
public class ResourceType extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7595736986123271500L;

	public static final Integer TYPE_BOOK = 1;
	public static final Integer TYPE_NEWSPAPERS = 2;
	public static final Integer TYPE_MAGAZINE = 3;
	public static final Integer TYPE_COMICS = 4;
	public static final Integer TYPE_SOUND = 5;
	public static final Integer TYPE_VIDEO = 6;
	public static final Integer TYPE_INFO = 7;

	private Integer id;

	/**
	 * 资源类别名称
	 */
	private String name;
	/**
	 * 资源类型归属者(1：图书，2：图片，3：铃声，4：视频,show_type是来区分),此信息也是写在页面中
	 */
	private Integer showType;
	
	private ResourceType parent;

	private Date createTime;

	private Integer creatorId;

	@Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "resource_type_id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "r_type_name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "show_type", nullable = false)
	public Integer getShowType() {
		return showType;
	}

	public void setShowType(Integer showType) {
		this.showType = showType;
	}
	@ManyToOne
	@JoinColumn(name="parent_id")
	public ResourceType getParent() {
		return parent;
	}

	public void setParent(ResourceType parentId) {
		this.parent = parentId;
	}

	@Column(name = "in_date")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "creator_id")
	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	@SuppressWarnings("unused")
	private String sType;

	// 资源归属类别
	@Transient
	public String getSType() {
		String typeName = "未知类别";
		if (showType != null) {
			for (Map.Entry<String, Integer> entry : Constants.getResourceType()
					.entrySet()) {
				if (entry.getValue().equals(showType))
					return entry.getKey();
			}
		}
		return typeName;
	}

	public boolean equals(Object o) {
		return HibernateEqualsBuilder.reflectionEquals(this, o, "id");
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
