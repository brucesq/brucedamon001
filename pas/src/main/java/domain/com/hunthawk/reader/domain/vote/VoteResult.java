package com.hunthawk.reader.domain.vote;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * @author penglei
 * 
 */
@Entity
//@javax.persistence.SequenceGenerator(name = "statdata", sequenceName = "READER_STATISTICS_DATA_SEQ")
@Table(name = "READER_VOTERESULT_DATA")
public class VoteResult extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -428091167912272664L;

	private Integer id;
	// 产品ID
	private String productId;
	// 栏目ID
	private Integer columnId;
	// 内容ID
	private String contentId;
	// 自定义ID
	private String customId;

	private Integer itemId;

	private String mobile;

	private Date createTime;

	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statdata")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "PRODUCT_ID")
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name = "COLUMN_ID")
	public Integer getColumnId() {
		return columnId;
	}

	public void setColumnId(Integer columnId) {
		this.columnId = columnId;
	}

	@Column(name = "CONTENT_ID")
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	@Column(name = "CUSTOM_ID")
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	@Column(name = "ITEM_ID")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Column(name = "MOBILE")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "CREATE_TIME")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean equals(Object o) {
		boolean bEquals = HibernateEqualsBuilder
				.reflectionEquals(this, o, "id");
		return bEquals;
	}

}
