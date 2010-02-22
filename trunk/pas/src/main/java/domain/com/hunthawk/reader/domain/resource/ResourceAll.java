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
	 * 资源ID 规则为YXXXXXXX,共8位， Y:资源类别1图书，2报纸，3杂志，4漫画，5铃声，6视频 XXXXXXX: 序列号，由0补足位数
	 * 如图书10000001,报纸20000001
	 */

	private String id;

	/**
	 * 资源名称
	 */
	private String name;
	/**
	 * 作者ID,存放是|1650|1660|形式
	 */
	// private ResourceAuthor resAuthor;
	private String authorId;
	/**
	 * 版权ID
	 */
	// private ResourceReferen resReferen;
	private Integer copyrightId;
	/**
	 * ISBN
	 */
	private String isbn;
	/**
	 * 出版日期
	 */
	private Date publishTime;
	/**
	 * 是否全本
	 */
	private Integer isFinished;
	/**
	 * 资源分类ID 因为一个资源有可能会属于多个分类，所以资源跟资源类别关系有个从属关系表reader_resource_restype
	 */
	// private ResourceType resourceType;
	/**
	 * CP表中的ID
	 */
	// private Integer cpId;
	// private Provider provider;
	private Integer cpId;
	/**
	 * 以前的资源状态(商用 0,暂停 1,隐藏 2,)
	 * 现在的资源状态(商用 0,待审 1,隐藏 2,暂停 3, 复审 4 , 否决 5 )
	 */
	private Integer status;
	/**
	 * 出版地区
	 */
	private String cArea;
	/**
	 * 简介
	 */
	private String cComment;
	/**
	 * 下载数据
	 */
	private Integer downnum = 0;
	/**
	 * 推荐指数
	 */
	private Integer expNum;

	/**
	 * 图书长简介
	 */
	private String introLon;

	/**
	 * 推荐语
	 */
	private String bComment;

	/**
	 * 首字母
	 */
	private String initialLetter;

	/**
	 * 出版社
	 */
	private String publisher;

	/**
	 * 语言
	 */
	private String bLanguage;

	/**
	 * 是否首发
	 */
	private Integer isFirstpublish;

	/**
	 * 是否专有
	 */
	private Integer isUnique;

	/**
	 * 是否出版
	 */
	private Integer isOut;

	/**
	 * 关键字
	 */
	private String rKeyword;
	/**
	 * 创建人ID
	 */
	private Integer creatorId;
	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 上一次版权ID
	 */
	private Integer lastCopyrightId;

	/**
	 * 修改时间
	 */
	private Date modifyTime;
	/**
	 * 修改人
	 */
	private Integer modifierId;
	
	/**
	 * 书部或者期刊号
	 */
	private Integer division;
	/**
	 * 总字数
	 */
	private Integer words;
	/**
	 * 搜索总数
	 */
	private Integer searchNum = 0;
	/**
	 * 上月月搜索数
	 */
	private Integer searchNumMonth = 0;
	/**
	 * 上周周搜索数
	 */
	private Integer searchNumWeek = 0;
	/**
	 * 昨天天搜索数
	 */
	private Integer searchNumDate = 0;
	/**
	 * 上月点击数
	 */
	private Integer downNumMonth = 0;
	/**
	 * 上周点击数
	 */
	private Integer downNumWeek = 0;
	/**
	 * 昨天点击数
	 */
	private Integer downNumDate = 0;
	/**
	 * 收藏总数
	 */
	private Integer favNum = 0;
	/**
	 * 月收藏总数
	 */
	private Integer favNumMonth = 0;
	/**
	 * 周收藏数
	 */
	private Integer favNumWeek = 0;
	/**
	 * 日搜藏数
	 */
	private Integer favNumDate = 0;
	/**
	 * 订购总数
	 */
	private Integer orderNum = 0;
	/**
	 * 月订购数
	 */
	private Integer orderNumMonth = 0;
	/**
	 * 周订购数
	 */
	private Integer orderNumWeek = 0;
	/**
	 * 日订购数
	 */
	private Integer orderNumDate = 0;
	/**
	 * 留言数
	 */
	private Integer msgNum = 0;
	/**
	 * 月留言数
	 */
	private Integer msgNumMonth = 0;
	/**
	 * 周留言数
	 */
	private Integer msgNumWeek = 0;
	/**
	 * 日留言数
	 */
	private Integer msgNumDate = 0;
	
	
	/**
	 * 投票数
	 */
	private Integer voteNum = 0;
	/**
	 * 月投票数
	 */
	private Integer voteNumMonth = 0;
	/**
	 * 周投票数
	 */
	private Integer voteNumWeek = 0;
	/**
	 * 日投票数
	 */
	private Integer voteNumDate = 0;
	
	/**
	 * 图书价格
	 */
	private String bookPrice;
	/**
	 * 图书置顶 0：不置顶；1：置顶（为前端搜索时候）
	 */
	private Integer searchTop = 0;
	/**
	 * 健康指数 1：1星，2：2;3：3;4:4;5:5
	 */
	private Integer healthNum = 1;
	
	/**
	 * 书部内容，为前端搜索访问使用
	 */
	private String divisionContent;
	
	/**
	 *pv（总排行）
	 */
	private Integer rankingNum = 0;
	/**
	 * pv（月排行）
	 */
	private Integer rankingNumMonth = 0;
	/**
	 * pv（周排行）
	 */
	private Integer rankingNumWeek = 0;
	/**
	 *pv（日排行）
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

	// 版权状态,给页面显示用的
	@Transient
	public String getBookStatus() {
		String typeName = "未知状态";
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
