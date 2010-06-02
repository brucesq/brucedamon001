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
 * ��������
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

	//����������
	public static final int TYPE_CONTENT = 1;
	//����Ŀ����
	public static final int TYPE_COLUMN = 2;
	//�Բ�Ʒ����
	public static final int TYPE_PRODUCT = 3;
	//�û���������
	public static final int TYPE_CUSTOM = 4;
	
	// ���Դ���
	public static final int MSG_STATUS_WAIT_ADUIT = 0;

	// ���Է���δ��
	public static final int MSG_STATUS_PUB_NOADUIT = 1;

	// ���Է�������
	public static final int MSG_STATUS_PUB_ADUIT = 2;

	// ����������
	public static final int MSG_STATUS_HIDED = 3;

	// ������ɾ��
	public static final int MSG_STATUS_DELETED = 4;
	/**
	 * 
	 */
	private static final long serialVersionUID = 6937609440490562962L;
	
	private Integer id;
	//������
	private MsgRecord parent;
	//��������
	private String content;
	//����״̬��0������1������δ��2����������3�������Σ�4���߼�ɾ��
	private Integer status;
	private String mobile;
	//����ԭ��0��δ���Σ�1����������2���ظ����ԣ�4�����д�
	private Integer reason;
	//��Ӧ�İ��ID
	private Integer boardId;
	//��ƷID
	private String productId;
	//��ĿID
	private Integer columnId;
	//����ID
	private String contentId;
	//Ŀ�����ƣ�����Ƕ���Ŀ���Ծ�����Ŀ���ơ�����Ƕ���Դ���Ծ�����Դ���ơ�����ǶԲ�Ʒ���Ծ��ǲ�Ʒ����
	private String name;
	//�Զ���ID
	private String customId;
	//�Զ�������
	private String customName;
	//����������
	private Integer subCount;
	//��������
	private Integer goodCount;
	//��������
	private Integer poorCount;
	//��������
	private Integer neutralCount;
	//ʡ��
	private String prov;
	//����
	private String city;
	//��������
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
