package com.hunthawk.reader.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * @author xianlaichen
 *
 */
@Entity
@Table(name = "reader_resource_restype")
@IdClass(ResourceResourceTypeRelation.class)

public class ResourceResType implements java.io.Serializable{

	public ResourceResType(){}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7595736986123271500L;

	/**
	* 资源ID
	*/

	private String rid;

	/**
	* 资源类别ID
	*/
	private Integer resTypeId;
	
	@Id
	@Column(name = "resource_id",nullable = false)
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	@Id
	@Column(name = "resource_type_id",nullable = false)
	public Integer getResTypeId() {
		return resTypeId;
	}

	public void setResTypeId(Integer resTypeId) {
		this.resTypeId = resTypeId;
	}
	
	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}

}
