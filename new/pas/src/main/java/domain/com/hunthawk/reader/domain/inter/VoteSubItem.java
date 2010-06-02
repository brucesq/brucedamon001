/**
 * 
 */
package com.hunthawk.reader.domain.inter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * 具体投票内容项
 * @author BruceSun
 * 
 */
@Entity
@Table(name = "reader_inter_vote_subitem")
public class VoteSubItem extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1442166038724192160L;
	
	// 对内容投票
	public static final int TYPE_CONTENT = 1;
	// 对栏目投票
	public static final int TYPE_COLUMN = 2;
	// 对产品投票
	public static final int TYPE_PRODUCT = 3;
	// 对固定链接
	public static final int TYPE_CUSTOM = 4;

	private String id;
	// 条目ID
	private Integer itemId;
	// 投票数
	private Integer voteValue;
	// 投票类型
	private Integer voteType;

	// 产品ID
	private String productId;
	// 栏目ID
	private Integer columnId;
	// 内容ID
	private String contentId;
	// 自定义ID
	private String customId;

	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	@Column(name = "item_id")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	@Column(name = "vote_value")
	public Integer getVoteValue() {
		return voteValue;
	}

	public void setVoteValue(Integer voteValue) {
		this.voteValue = voteValue;
	}
	@Column(name = "vote_type")
	public Integer getVoteType() {
		return voteType;
	}

	public void setVoteType(Integer voteType) {
		this.voteType = voteType;
	}
	@Column(name = "product_id")
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	@Column(name = "column_id")
	public Integer getColumnId() {
		return columnId;
	}

	public void setColumnId(Integer columnId) {
		this.columnId = columnId;
	}
	@Column(name = "content_id")
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	@Column(name = "custom_id")
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public boolean equals(Object o) {
		boolean bEquals = HibernateEqualsBuilder
				.reflectionEquals(this, o, "id");
		return bEquals;
	}
}
