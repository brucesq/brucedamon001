/**
 * 
 */
package com.hunthawk.reader.domain.inter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * 连载内容预订数据
 * 
 * @author BruceSun
 * 
 */
@Entity
@Table(name = "reader_inter_reservations")
@IdClass(ReservationPK.class)
public class Reservation extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5112947223008521454L;

	private String mobile;

	//内容ID
	private String contentId;

	private Date createTime;

	// 批价包关联ID
	private Integer packRelationId;

	@Id
	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Id
	@Column(name = "content_id")
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "fee_pack_id")
	public Integer getPackRelationId() {
		return packRelationId;
	}
	public void setPackRelationId(Integer packRelationId) {
		this.packRelationId = packRelationId;
	}

}
