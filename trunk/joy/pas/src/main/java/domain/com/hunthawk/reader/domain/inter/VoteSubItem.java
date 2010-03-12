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
 * ����ͶƱ������
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
	
	// ������ͶƱ
	public static final int TYPE_CONTENT = 1;
	// ����ĿͶƱ
	public static final int TYPE_COLUMN = 2;
	// �Բ�ƷͶƱ
	public static final int TYPE_PRODUCT = 3;
	// �Թ̶�����
	public static final int TYPE_CUSTOM = 4;

	private String id;
	// ��ĿID
	private Integer itemId;
	// ͶƱ��
	private Integer voteValue;
	// ͶƱ����
	private Integer voteType;

	// ��ƷID
	private String productId;
	// ��ĿID
	private Integer columnId;
	// ����ID
	private String contentId;
	// �Զ���ID
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
