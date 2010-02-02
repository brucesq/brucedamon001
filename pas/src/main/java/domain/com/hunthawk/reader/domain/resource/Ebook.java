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
	 * 图书资源ID
	 */
	// private ResourceAll resourceall;
	// private EbookType ebooktype;
	/**
	 * 图书封面图片
	 */
	private String bookPic;

	/**
	 * 图书文件存放路径
	 */
	private String showUrl;

	/**
	 * 图书章节数
	 */
	private Integer listCount;

	/**
	 * 是否是引进书籍（翻译的姓名）,默认否为0
	 */
	private Integer isFetch;

	/**
	 * 序，前言
	 */
	private String rFirst;

	/**
	 * 后续
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
