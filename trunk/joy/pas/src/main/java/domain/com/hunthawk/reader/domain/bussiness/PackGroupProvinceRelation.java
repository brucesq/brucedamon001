package com.hunthawk.reader.domain.bussiness;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * 
 * @author liuxh
 *
 */
@Entity
@Table(name="READER_BUSSINESS_PG_AREA")
@IdClass(PackGroupProvinceId.class)
public class PackGroupProvinceRelation  implements java.io.Serializable{
	
	public PackGroupProvinceRelation(){}
	/**
	 * ��Ʒid
	 */
	
	private String pid;
	/**
	 * ʡ��id
	 */
	
	private String aid;
	/**
	 * ҳ����id
	 */
	
	private Integer pgid;
	
	@Id
	@Column(name = "productid",   nullable = false)
	public String getPid() {
		
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	@Id
	@Column(name = "area_id",   nullable = false)
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	
	@Column(name = "pack_group_id", nullable = false)
	public Integer getPgid() {
		return pgid;
	}
	public void setPgid(Integer pgid) {
		this.pgid = pgid;
	}
	
	
}
