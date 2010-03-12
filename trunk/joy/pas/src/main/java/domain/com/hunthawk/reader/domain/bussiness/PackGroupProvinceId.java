package com.hunthawk.reader.domain.bussiness;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author liuxh
 * 
 */
@Embeddable
public class PackGroupProvinceId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4049556817571156607L;

	private String pid;

	// private Integer pgid;
	private String aid;

	public PackGroupProvinceId() {
	}

	@Column(name = "productid", nullable = false)
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Column(name = "area_id", nullable = false)
	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	// @Column(name = "pack_group_id",unique = true, nullable = false)
	// public Integer getPgid() {
	// return pgid;
	// }
	// public void setPgid(Integer pgid) {
	// this.pgid = pgid;
	// }
	public boolean equals(Object o) {
		// return HibernateEqualsBuilder.reflectionEquals(this, o,"pid");
		if (this == o)
			return true;
		if (!(o instanceof PackGroupProvinceRelation))
			return false;
		final PackGroupProvinceRelation other = (PackGroupProvinceRelation) o;
		if (this.getPid().equals(other.getPid())
				&& this.getAid().equals(other.getPgid()))
			return true;
		else
			return false;
	}

	public int hashCode() {
		int result;
		result = (pid == null ? 0 : pid.hashCode());
		result = 29 * result + (aid == null ? 0 : aid.hashCode());
		return result;
	}
}
