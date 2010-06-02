package com.hunthawk.reader.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * @author xianlaichen
 * 
 */
@Entity
@Table(name = "reader_resource_ebook")
@PrimaryKeyJoinColumn(name = "resource_id")
public class Ebook extends ResourceAll {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7595736986123271500L;

	/**
	 * ͼ����ԴID
	 */
	// private ResourceAll resourceall;
	// private EbookType ebooktype;
	/**
	 * ͼ�����ͼƬ
	 */
	private String bookPic;

	/**
	 * ͼ���ļ����·��
	 */
	private String showUrl;

	/**
	 * ͼ���½���
	 */
	private Integer listCount;

	/**
	 * �Ƿ��������鼮�������������,Ĭ�Ϸ�Ϊ0
	 */
	private Integer isFetch;

	/**
	 * ��ǰ��
	 */
	private String rFirst;

	/**
	 * ����
	 */
	private String rAfter;

	/*
	 * @ManyToOne @JoinColumn(name="content_type_id",nullable=false) public
	 * EbookType getEbookType() { return ebooktype; } public void
	 * setEbookType(EbookType ebooktype) { this.ebooktype = ebooktype; }
	 */
	@Column(name = "book_pic")
	public String getBookPic() {
		return bookPic;
	}

	public void setBookPic(String bookPic) {
		this.bookPic = bookPic;
	}

	@Column(name = "show_url")
	public String getShowUrl() {
		return showUrl;
	}

	public void setShowUrl(String showUrl) {
		this.showUrl = showUrl;
	}

	@Column(name = "list_count")
	public Integer getListCount() {
		return listCount;
	}

	public void setListCount(Integer listCount) {
		this.listCount = listCount;
	}

	@Column(name = "is_fetch")
	public Integer getIsFetch() {
		return isFetch;
	}

	public void setIsFetch(Integer isFetch) {
		this.isFetch = isFetch;
	}

	@Column(name = "r_first")
	public String getRFirst() {
		return rFirst;
	}

	public void setRFirst(String first) {
		rFirst = first;
	}

	@Column(name = "r_after")
	public String getRAfter() {
		return rAfter;
	}

	public void setRAfter(String after) {
		rAfter = after;
	}

}
