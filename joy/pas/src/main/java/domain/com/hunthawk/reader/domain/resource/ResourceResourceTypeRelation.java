package com.hunthawk.reader.domain.resource;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;

/**
 * 
 * @author xianlaichen
 *
 */
@Embeddable

public class ResourceResourceTypeRelation implements java.io.Serializable{
	
	private String rid;
	Integer resTypeId;
	
	public ResourceResourceTypeRelation(){}
	@Column(name = "resource_id", unique = true,   nullable = false)
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	
	@Column(name = "resource_type_id",unique = true,  nullable = false)
	public Integer getResTypeId() {
		return resTypeId;
	}
	public void setResTypeId(Integer resTypeId) {
		this.resTypeId = resTypeId;
	}
	public boolean equals(Object o) {
	     // return HibernateEqualsBuilder.reflectionEquals(this, o,"pid");
		if(this==o)return true;
		if(!(o instanceof ResourceResType))return false;
				final ResourceResType other=(ResourceResType)o;
			if(this.getRid().equals(other.getRid()) && this.getResTypeId().equals(other.getResTypeId()))
				return true;
			else
				return false;
	}
	public int hashCode(){
		int result;
		result=(rid==null?0:rid.hashCode());
		result=29*result+(resTypeId==null?0:resTypeId.hashCode());
		return result;
	}
}
