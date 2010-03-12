package com.hunthawk.reader.domain.resource;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.hibernate.HibernateEqualsBuilder;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * 
 * @author xianlaichen
 * 
 */
@Entity
@Table(name = "reader_resource_all")
@Inheritance(strategy = InheritanceType.JOINED)
public class ResourceAll extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8014333973729829044L;
	public static final Integer RESOURCE_TYPE_BOOK = 1;
	public static final Integer RESOURCE_TYPE_NEWSPAPER = 2;
	public static final Integer RESOURCE_TYPE_MAGAZINE = 3;
	public static final Integer RESOURCE_TYPE_COMICS = 4;
	public static final Integer RESOURCE_TYPE_RING = 5;
	public static final Integer RESOURCE_TYPE_VIDEO = 6;
	public static final Integer RESOURCE_TYPE_INFO = 7;
	/**
	 * ��ԴID ����ΪYXXXXXXX,��8λ�� Y:��Դ���1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ XXXXXXX: ���кţ���0����λ��
	 * ��ͼ��10000001,��ֽ20000001
	 */

	private String id;

	/**
	 * ��Դ����
	 */
	private String name;
	/**
	 * ����ID,�����|1650|1660|��ʽ
	 */
	// private ResourceAuthor resAuthor;
	private String authorId;
	/**
	 * ��ȨID
	 */
	// private ResourceReferen resReferen;
	private Integer copyrightId;
	/**
	 * ISBN
	 */
	private String isbn;
	/**
	 * ��������
	 */
	private Date publishTime;
	/**
	 * �Ƿ�ȫ��
	 */
	private Integer isFinished;
	/**
	 * ��Դ����ID ��Ϊһ����Դ�п��ܻ����ڶ�����࣬������Դ����Դ����ϵ�и�������ϵ��reader_resource_restype
	 */
	// private ResourceType resourceType;
	/**
	 * CP���е�ID
	 */
	// private Integer cpId;
	// private Provider provider;
	private Integer cpId;
	/**
	 * ��ǰ����Դ״̬(���� 0,��ͣ 1,���� 2,)
	 * ���ڵ���Դ״̬(���� 0,���� 1,���� 2,��ͣ 3, ���� 4 , ��� 5 )
	 */
	private Integer status;
	/**
	 * �������
	 */
	private String cArea;
	/**
	 * ���
	 */
	private String cComment;
	/**
	 * ��������
	 */
	private Integer downnum = 0;
	/**
	 * �Ƽ�ָ��
	 */
	private Integer expNum;

	/**
	 * ͼ�鳤���
	 */
	private String introLon;

	/**
	 * �Ƽ���
	 */
	private String bComment;

	/**
	 * ����ĸ
	 */
	private String initialLetter;

	/**
	 * ������
	 */
	private String publisher;

	/**
	 * ����
	 */
	private String bLanguage;

	/**
	 * �Ƿ��׷�
	 */
	private Integer isFirstpublish;

	/**
	 * �Ƿ�ר��
	 */
	private Integer isUnique;

	/**
	 * �Ƿ����
	 */
	private Integer isOut;

	/**
	 * �ؼ���
	 */
	private String rKeyword;
	/**
	 * ������ID
	 */
	private Integer creatorId;
	/**
	 * ����ʱ��
	 */
	private Date createTime;

	/**
	 * ��һ�ΰ�ȨID
	 */
	private Integer lastCopyrightId;

	/**
	 * �޸�ʱ��
	 */
	private Date modifyTime;
	/**
	 * �޸���
	 */
	private Integer modifierId;
	
	/**
	 * �鲿�����ڿ���
	 */
	private Integer division;
	/**
	 * ������
	 */
	private Integer words;
	/**
	 * ��������
	 */
	private Integer searchNum = 0;
	/**
	 * ������������
	 */
	private Integer searchNumMonth = 0;
	/**
	 * ������������
	 */
	private Integer searchNumWeek = 0;
	/**
	 * ������������
	 */
	private Integer searchNumDate = 0;
	/**
	 * ���µ����
	 */
	private Integer downNumMonth = 0;
	/**
	 * ���ܵ����
	 */
	private Integer downNumWeek = 0;
	/**
	 * ��������
	 */
	private Integer downNumDate = 0;
	/**
	 * �ղ�����
	 */
	private Integer favNum = 0;
	/**
	 * ���ղ�����
	 */
	private Integer favNumMonth = 0;
	/**
	 * ���ղ���
	 */
	private Integer favNumWeek = 0;
	/**
	 * ���Ѳ���
	 */
	private Integer favNumDate = 0;
	/**
	 * ��������
	 */
	private Integer orderNum = 0;
	/**
	 * �¶�����
	 */
	private Integer orderNumMonth = 0;
	/**
	 * �ܶ�����
	 */
	private Integer orderNumWeek = 0;
	/**
	 * �ն�����
	 */
	private Integer orderNumDate = 0;
	/**
	 * ������
	 */
	private Integer msgNum = 0;
	/**
	 * ��������
	 */
	private Integer msgNumMonth = 0;
	/**
	 * ��������
	 */
	private Integer msgNumWeek = 0;
	/**
	 * ��������
	 */
	private Integer msgNumDate = 0;
	
	
	/**
	 * ͶƱ��
	 */
	private Integer voteNum = 0;
	/**
	 * ��ͶƱ��
	 */
	private Integer voteNumMonth = 0;
	/**
	 * ��ͶƱ��
	 */
	private Integer voteNumWeek = 0;
	/**
	 * ��ͶƱ��
	 */
	private Integer voteNumDate = 0;
	
	/**
	 * ͼ��۸�
	 */
	private String bookPrice;
	/**
	 * ͼ���ö� 0�����ö���1���ö���Ϊǰ������ʱ��
	 */
	private Integer searchTop = 0;
	/**
	 * ����ָ�� 1��1�ǣ�2��2;3��3;4:4;5:5
	 */
	private Integer healthNum = 1;
	
	/**
	 * �鲿���ݣ�Ϊǰ����������ʹ��
	 */
	private String divisionContent;
	
	/**
	 *pv�������У�
	 */
	private Integer rankingNum = 0;
	/**
	 * pv�������У�
	 */
	private Integer rankingNumMonth = 0;
	/**
	 * pv�������У�
	 */
	private Integer rankingNumWeek = 0;
	/**
	 *pv�������У�
	 */
	private Integer rankingNumDate = 0;
	
	@Id
	@GeneratedValue(generator = "system-assigned")
	@GenericGenerator(name = "system-assigned", strategy = "assigned")
	@Column(name = "resource_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "res_name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * @ManyToOne @JoinColumn(name="author_id",nullable=false) public
	 * ResourceAuthor getResourceAuthor() { return resAuthor; } public void
	 * setResourceAuthor(ResourceAuthor resAuthor) { this.resAuthor = resAuthor; }
	 * 
	 * 
	 * @ManyToOne @JoinColumn(name="copyright_id",nullable=false) public
	 * ResourceReferen getResourceReferen() { return resReferen; } public void
	 * setResourceReferen(ResourceReferen resReferen) { this.resReferen =
	 * resReferen; }
	 */

	@Column(name = "isbn")
	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	@Column(name = "publish_time")
	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	@Column(name = "is_finished")
	public Integer getIsFinished() {
		return isFinished;
	}

	public void setIsFinished(Integer isFinished) {
		this.isFinished = isFinished;
	}

	/*
	 * @ManyToOne @JoinColumn(name="cp_id",nullable=false) public Provider
	 * getProvider() { return provider; } public void setProvider(Provider
	 * provider) { this.provider = provider; }
	 */

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "c_area")
	public String getCArea() {
		return cArea;
	}

	public void setCArea(String cArea) {
		this.cArea = cArea;
	}

	@Column(name = "r_keyword")
	public String getRKeyword() {
		return rKeyword;
	}

	public void setRKeyword(String rKeyword) {
		this.rKeyword = rKeyword;
	}

	@Column(name = "downnum")
	public Integer getDownnum() {
		return downnum;
	}

	public void setDownnum(Integer downnum) {
		this.downnum = downnum;
	}

	@Column(name = "exp_num")
	public Integer getExpNum() {
		return expNum;
	}

	public void setExpNum(Integer expNum) {
		this.expNum = expNum;
	}

	@Column(name = "intro_lon")
	public String getIntroLon() {
		return introLon;
	}

	public void setIntroLon(String introLon) {
		this.introLon = introLon;
	}

	@Column(name = "b_comment")
	public String getBComment() {
		return bComment;
	}

	public void setBComment(String bComment) {
		this.bComment = bComment;
	}

	@Column(name = "initial_letter")
	public String getInitialLetter() {
		return initialLetter;
	}

	public void setInitialLetter(String initialLetter) {
		this.initialLetter = initialLetter;
	}

	@Column(name = "publisher")
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	@Column(name = "b_language")
	public String getBLanguage() {
		return bLanguage;
	}

	public void setBLanguage(String bLanguage) {
		this.bLanguage = bLanguage;
	}

	@Column(name = "is_firstpublish")
	public Integer getIsFirstpublish() {
		return isFirstpublish;
	}

	public void setIsFirstpublish(Integer isFirstpublish) {
		this.isFirstpublish = isFirstpublish;
	}

	@Column(name = "is_unique")
	public Integer getIsUnique() {
		return isUnique;
	}

	public void setIsUnique(Integer isUnique) {
		this.isUnique = isUnique;
	}

	@Column(name = "is_out")
	public Integer getIsOut() {
		return isOut;
	}

	public void setIsOut(Integer isOut) {
		this.isOut = isOut;
	}

	@Column(name = "c_comment")
	public String getCComment() {
		return cComment;
	}

	public void setCComment(String cComment) {
		this.cComment = cComment;
	}

	@Column(name = "in_date")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "creator_id")
	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	@Column(name = "author_id")
	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	@Transient
	public Integer[] getAuthorIds() {
		if (authorId != null && authorId.startsWith("|")) {
			String ids = authorId.substring(1);
			String[] idStrs = ids.split("\\|");
			Integer[] rs = new Integer[idStrs.length];
			int i = 0;
			for (String str : idStrs) {
				try{
					rs[i++] = Integer.parseInt(str);
				}catch(Exception e){
					rs[i-1] = 0;
				}
				
			}
			return rs;
		} else if (authorId != null) {
			return new Integer[] { Integer.parseInt(authorId) };
		} else {
			return new Integer[0];
		}
	}

	@Column(name = "copyright_id")
	public Integer getCopyrightId() {
		return copyrightId;
	}

	public void setCopyrightId(Integer copyrightId) {
		this.copyrightId = copyrightId;
	}

	@Column(name = "cp_id")
	public Integer getCpId() {
		return cpId;
	}

	public void setCpId(Integer cpId) {
		this.cpId = cpId;
	}

	// ��Ȩ״̬,��ҳ����ʾ�õ�
	@Transient
	public String getBookStatus() {
		String typeName = "δ֪״̬";
		if (status != null) {
			for (Map.Entry<String, Integer> entry : Constants
					.getResourceStatus().entrySet()) {
				if (entry.getValue().equals(status))
					return entry.getKey();
			}
		}
		return typeName;
	}

	@Column(name = "LAST_COPYRIGHT_ID")
	public Integer getLastCopyrightId() {
		return lastCopyrightId;
	}

	public void setLastCopyrightId(Integer lastCopyrightId) {
		this.lastCopyrightId = lastCopyrightId;
	}

	@Column(name = "MODIFY_TIME")
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Column(name = "MODIFY_ID")
	public Integer getModifierId() {
		return modifierId;
	}

	public void setModifierId(Integer modifierId) {
		this.modifierId = modifierId;
	}
	
	
	@Column(name = "division_n")
	public Integer getDivision() {
		return division;
	}

	public void setDivision(Integer division) {
		this.division = division;
	}

	@Override
	public boolean equals(Object o) {
		return HibernateEqualsBuilder.reflectionEquals(this, o, "id");
	}

	@Override
	public int hashCode() {
		if (getId() != null) {
			return 2 * getId().hashCode();
		}
		return super.hashCode();
	}
	
	@Transient
	public String getShowModifyTimeName() {
		if(modifyTime != null){
			return ToolDateUtil.dateToString(modifyTime);
		}else{
			return "";
		}
		
	}

	@Column(name = "words")
	public Integer getWords() {
		return words;
	}

	public void setWords(Integer words) {
		this.words = words;
	}

	@Column(name = "SEARCHNUM")
	public Integer getSearchNum() {
		return searchNum;
	}

	public void setSearchNum(Integer searchNum) {
		this.searchNum = searchNum;
	}
	@Column(name = "SEARCHNUM_M")
	public Integer getSearchNumMonth() {
		return searchNumMonth;
	}

	public void setSearchNumMonth(Integer searchNumMonth) {
		this.searchNumMonth = searchNumMonth;
	}
	@Column(name = "SEARCHNUM_W")
	public Integer getSearchNumWeek() {
		return searchNumWeek;
	}

	public void setSearchNumWeek(Integer searchNumWeek) {
		this.searchNumWeek = searchNumWeek;
	}
	@Column(name = "SEARCHNUM_D")
	public Integer getSearchNumDate() {
		return searchNumDate;
	}

	public void setSearchNumDate(Integer searchNumDate) {
		this.searchNumDate = searchNumDate;
	}
	@Column(name = "DOWNNUM_M")
	public Integer getDownNumMonth() {
		return downNumMonth;
	}

	public void setDownNumMonth(Integer downNumMonth) {
		this.downNumMonth = downNumMonth;
	}
	@Column(name = "DOWNNUM_W")
	public Integer getDownNumWeek() {
		return downNumWeek;
	}

	public void setDownNumWeek(Integer downNumWeek) {
		this.downNumWeek = downNumWeek;
	}
	@Column(name = "DOWNNUM_D")
	public Integer getDownNumDate() {
		return downNumDate;
	}

	public void setDownNumDate(Integer downNumDate) {
		this.downNumDate = downNumDate;
	}
	@Column(name = "FAVNUM")
	public Integer getFavNum() {
		return favNum;
	}

	public void setFavNum(Integer favNum) {
		this.favNum = favNum;
	}
	@Column(name = "FAVNUM_M")
	public Integer getFavNumMonth() {
		return favNumMonth;
	}

	public void setFavNumMonth(Integer favNumMonth) {
		this.favNumMonth = favNumMonth;
	}
	@Column(name = "FAVNUM_W")
	public Integer getFavNumWeek() {
		return favNumWeek;
	}

	public void setFavNumWeek(Integer favNumWeek) {
		this.favNumWeek = favNumWeek;
	}
	@Column(name = "ORDERNUM")
	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
	@Column(name = "ORDERNUM_M")
	public Integer getOrderNumMonth() {
		return orderNumMonth;
	}

	public void setOrderNumMonth(Integer orderNumMonth) {
		this.orderNumMonth = orderNumMonth;
	}
	@Column(name = "ORDERNUM_W")
	public Integer getOrderNumWeek() {
		return orderNumWeek;
	}

	public void setOrderNumWeek(Integer orderNumWeek) {
		this.orderNumWeek = orderNumWeek;
	}
	@Column(name = "MSGNUM")
	public Integer getMsgNum() {
		return msgNum;
	}

	public void setMsgNum(Integer msgNum) {
		this.msgNum = msgNum;
	}
	@Column(name = "MSGNUM_M")
	public Integer getMsgNumMonth() {
		return msgNumMonth;
	}

	public void setMsgNumMonth(Integer msgNumMonth) {
		this.msgNumMonth = msgNumMonth;
	}
	@Column(name = "MSGNUM_W")
	public Integer getMsgNumWeek() {
		return msgNumWeek;
	}

	public void setMsgNumWeek(Integer msgNumWeek) {
		this.msgNumWeek = msgNumWeek;
	}

	
	@Column(name = "FAVNUM_D")
	public Integer getFavNumDate() {
		return favNumDate;
	}

	public void setFavNumDate(Integer favNumDate) {
		this.favNumDate = favNumDate;
	}

	@Column(name = "ORDERNUM_D")
	public Integer getOrderNumDate() {
		return orderNumDate;
	}

	public void setOrderNumDate(Integer orderNumDate) {
		this.orderNumDate = orderNumDate;
	}

	@Column(name = "MSGNUM_D")
	public Integer getMsgNumDate() {
		return msgNumDate;
	}

	public void setMsgNumDate(Integer msgNumDate) {
		this.msgNumDate = msgNumDate;
	}

	@Column(name = "VOTENUM")
	public Integer getVoteNum() {
		return voteNum;
	}

	public void setVoteNum(Integer voteNum) {
		this.voteNum = voteNum;
	}

	@Column(name = "VOTENUM_M")
	public Integer getVoteNumMonth() {
		return voteNumMonth;
	}

	public void setVoteNumMonth(Integer voteNumMonth) {
		this.voteNumMonth = voteNumMonth;
	}

	@Column(name = "VOTENUM_W")
	public Integer getVoteNumWeek() {
		return voteNumWeek;
	}

	public void setVoteNumWeek(Integer voteNumWeek) {
		this.voteNumWeek = voteNumWeek;
	}

	@Column(name = "VOTENUM_D")
	public Integer getVoteNumDate() {
		return voteNumDate;
	}

	public void setVoteNumDate(Integer voteNumDate) {
		this.voteNumDate = voteNumDate;
	}

	@Transient
	public Set<String> getRKeyWords() {
		Set<String> keyWords = new HashSet<String>();
		if(rKeyword!=null){
			String[] arrayKey = rKeyword.split("/");
			for(int i=0;i<arrayKey.length;i++){
				keyWords.add(arrayKey[i]);
			}
		}
		return keyWords;
	}
	@Column(name="SEARCH_TOP")
	public Integer getSearchTop() {
		return searchTop;
	}

	public void setSearchTop(Integer searchTop) {
		this.searchTop = searchTop;
	}
	@Column(name="HEALTH_NUM")
	public Integer getHealthNum() {
		return healthNum;
	}

	public void setHealthNum(Integer healthNum) {
		this.healthNum = healthNum;
	}
	@Column(name="DIVISION_CONTENT")
	public String getDivisionContent() {
		return divisionContent;
	}

	public void setDivisionContent(String divisionContent) {
		this.divisionContent = divisionContent;
	}
	@Column(name="RANKING")
	public Integer getRankingNum() {
		return rankingNum;
	}

	public void setRankingNum(Integer rankingNum) {
		this.rankingNum = rankingNum;
	}
	@Column(name="RANKING_M")
	public Integer getRankingNumMonth() {
		return rankingNumMonth;
	}

	public void setRankingNumMonth(Integer rankingNumMonth) {
		this.rankingNumMonth = rankingNumMonth;
	}
	@Column(name="RANKING_W")
	public Integer getRankingNumWeek() {
		return rankingNumWeek;
	}

	public void setRankingNumWeek(Integer rankingNumWeek) {
		this.rankingNumWeek = rankingNumWeek;
	}
	@Column(name="RANKING_D")
	public Integer getRankingNumDate() {
		return rankingNumDate;
	}

	public void setRankingNumDate(Integer rankingNumDate) {
		this.rankingNumDate = rankingNumDate;
	}
	@Column(name="BOOK_PRICE")
	public String getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(String bookPrice) {
		this.bookPrice = bookPrice;
	}
	
	
}
