/**
 * 
 */
package com.hunthawk.reader.domain.inter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;

/**
 * 留言内容
 * @author BruceSun
 *
 */
@Entity
//@javax.persistence.SequenceGenerator(
//		name = "msgrecord",
//		sequenceName="reader_inter_msgrecord_seq"
//)
@Table(name = "reader_inter_msgrecord")
public class MsgRecord extends PersistentObject{

	//对内容留言
	public static final int TYPE_CONTENT = 1;
	//对栏目留言
	public static final int TYPE_COLUMN = 2;
	//对产品留言
	public static final int TYPE_PRODUCT = 3;
	//用户定制留言
	public static final int TYPE_CUSTOM = 4;
	
	// 留言待审
	public static final int MSG_STATUS_WAIT_ADUIT = 0;

	// 留言发布未审
	public static final int MSG_STATUS_PUB_NOADUIT = 1;

	// 留言发布已审
	public static final int MSG_STATUS_PUB_ADUIT = 2;

	// 留言已屏蔽
	public static final int MSG_STATUS_HIDED = 3;

	// 留言已删除
	public static final int MSG_STATUS_DELETED = 4;
	/**
	 * 
	 */
	private static final long serialVersionUID = 6937609440490562962L;
	
	private Integer id;
	//父留言
	private MsgRecord parent;
	//留言内容
	private String content;
	//留言状态，0：待审；1：发布未审；2：发布已审；3：已屏蔽；4：逻辑删除
	private Integer status;
	private String mobile;
	//屏蔽原因，0：未屏蔽；1：黑名单；2：重复留言；4：敏感词
	private Integer reason;
	//对应的板块ID
	private Integer boardId;
	//产品ID
	private String productId;
	//栏目ID
	private Integer columnId;
	//内容ID
	private String contentId;
	//目标名称，如果是对栏目留言就是栏目名称、如果是对资源留言就是资源名称、如果是对产品留言就是产品名称
	private String name;
	//自定义ID
	private String customId;
	//自定义名称
	private String customName;
	//子留言数量
	private Integer subCount;
	//好评数量
	private Integer goodCount;
	//差评数量
	private Integer poorCount;
	//中立数量
	private Integer neutralCount;
	//省份
	private String prov;
	//城市
	private String city;
	//留言类型
	private Integer msgType;
	
	private Date createTime;
	
	private Date modifyTime;

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="msgrecord")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@ManyToOne
	@JoinColumn(name="parent_id")
	public MsgRecord getParent() {
		return parent;
	}

	public void setParent(MsgRecord parent) {
		this.parent = parent;
	}
	@Column(name = "content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Column(name = "reason")
	public Integer getReason() {
		return reason;
	}

	public void setReason(Integer reason) {
		this.reason = reason;
	}
	@Column(name = "board_id")
	public Integer getBoardId() {
		return boardId;
	}

	public void setBoardId(Integer boardId) {
		this.boardId = boardId;
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
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "custom_id")
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}
	@Column(name = "custom_name")
	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}
	@Column(name = "sub_count")
	public Integer getSubCount() {
		return subCount;
	}

	public void setSubCount(Integer subCount) {
		this.subCount = subCount;
	}
	@Column(name = "good_count")
	public Integer getGoodCount() {
		return goodCount;
	}

	public void setGoodCount(Integer goodCount) {
		this.goodCount = goodCount;
	}
	@Column(name = "poor_count")
	public Integer getPoorCount() {
		return poorCount;
	}

	public void setPoorCount(Integer poorCount) {
		this.poorCount = poorCount;
	}

	@Column(name = "neutral_count")
	public Integer getNeutralCount() {
		return neutralCount;
	}

	public void setNeutralCount(Integer neutralCount) {
		this.neutralCount = neutralCount;
	}
	@Column(name = "prov")
	public String getProv() {
		return prov;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}
	@Column(name = "city")
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "modify_time")
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	
	@Column(name = "msg_type")
	public Integer getMsgType() {
		return msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	public boolean equals(Object o) {
	      return HibernateEqualsBuilder.reflectionEquals(this, o,"id");
	}
	
}
