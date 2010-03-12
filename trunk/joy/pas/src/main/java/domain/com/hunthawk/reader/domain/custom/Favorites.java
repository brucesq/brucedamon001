/**
 * 
 */
package com.hunthawk.reader.domain.custom;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.hunthawk.framework.domain.PersistentObject;

/**
 * @author BruceSun
 *
 */
@Entity
@Table(name="reader_order_user_house")
@IdClass(UserFavoritesPk.class)
public class Favorites extends PersistentObject{

/**
	 * 
	 */
	private static final long serialVersionUID = 3797610201956737313L;
	//	手机号
	private String mobile;
//	内容ID
	private String contentId;
//	批价包关联ID
	private Integer packRelationId;
//	创建时间
	private Date createTime;
	/**
	 * add by liuxh  09-11-12
	 */
	private String productid;
	
	private Integer columnid;
	
	@Column(name = "PRODUCTID")
	public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}
	@Column(name = "COLUMNID")
	public Integer getColumnid() {
		return columnid;
	}

	public void setColumnid(Integer columnid) {
		this.columnid = columnid;
	}

	/**
	 * end
	 */
	@Id
	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Id
	@Column(name = "contentid")
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	@Column(name = "fee_pack_id")
	public Integer getPackRelationId() {
		return packRelationId;
	}
	public void setPackRelationId(Integer packRelationId) {
		this.packRelationId = packRelationId;
	}
	@Column(name = "in_date")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	
}
