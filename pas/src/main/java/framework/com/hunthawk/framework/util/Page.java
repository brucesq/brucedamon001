package com.hunthawk.framework.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 多页中的分页,封装了页面索引,页面数据,并提供了一些助手方法
 * <p>
 * 页面索引遵守Java的数组的习惯，从0开始.在展示的时候需要对页面索引进行处理
 * @author penglei
 */
public class Page implements Serializable {

	public static final long serialVersionUID = 1L;

	/**
	 * 默认的页面可装载的数据条目总和
	 */
	public static final int DEFAULT_DATA_SIZE_IN_ONE_PAGE = 10;

	/*
	 * 页面可装载的数据条目
	 */
	private int dataCountInOnePage = DEFAULT_DATA_SIZE_IN_ONE_PAGE;

	/*
	 * 当前页面的索引
	 */
	private int currentPageNo;

	/*
	 * 页面数据
	 */
	private List data;

	/*
	 * 所有的数据条目总数（所有的分页的数据累加）
	 */
	private long totalDataCount;

	/**
	 * 
	 */
	public Page() {
		this(0, 0, DEFAULT_DATA_SIZE_IN_ONE_PAGE, Collections.EMPTY_LIST);
	}

	/**
	 * @param currentPageNo
	 *            当前页面的索引
	 * @param totalCount
	 *            所有的记录数
	 * @param pageSize
	 *            每页的记录数
	 * @param data
	 *            页面数据
	 */
	public Page(int currentPageNo, long totalCount, int pageSize, List data) {
		if (0 > pageSize)
			throw new IllegalStateException("页面的数据大小不能为0！");
		this.dataCountInOnePage = pageSize;
		this.currentPageNo = currentPageNo;
		this.totalDataCount = totalCount;
		this.data = data;
		if (this.currentPageNo >= this.getTotalPageCount()) {
			this.currentPageNo = (int) (this.getTotalPageCount());
		} else {
			this.currentPageNo = currentPageNo;
		}
	}

	/**
	 * 所有页面的数据总数
	 */
	public long getTotalDataCount() {
		return this.totalDataCount;
	}

	/**
	 * 页面总数
	 */
	public long getTotalPageCount() {
		if (totalDataCount % dataCountInOnePage == 0)
			return totalDataCount / dataCountInOnePage;
		else
			return totalDataCount / dataCountInOnePage + 1;
	}

	/**
	 * 页面数据数量
	 */
	public int getDataCountInOnePage() {
		return dataCountInOnePage;
	}

	/**
	 * 页面数据
	 */
	public List getData() {
		return data;
	}

	/**
	 * 当前页
	 */
	public int getCurrentPageNo() {
		return currentPageNo;
	}

	/**
	 * 当前页是否有下一页
	 */
	public boolean hasNextPage() {
		return this.getCurrentPageNo() < this.getTotalPageCount() - 1;
	}

	/**
	 * 当前页是否有前一页
	 */
	public boolean hasPreviousPage() {
		return this.getCurrentPageNo() > 0;
	}

	/**
	 * 获取页面的第一条数据在所有数据中的索引 默认每页可装载的数据最大为20，第5页的第一条数据在所有数据中的索引为100
	 */
	public static int getStartOfPage(int pageNo) {
		return getStartOfPage(pageNo, DEFAULT_DATA_SIZE_IN_ONE_PAGE);
	}

	/**
	 * 获取页面的第一条数据在所有数据中的索引 例如，每页可装载的数据最大数为30，第3页的第一条数据在所有数据中的索引为90
	 */
	public static int getStartOfPage(int pageNo, int pageSize) {
		if (0 > pageNo)
			throw new IllegalArgumentException("页面索引不能小于0!");
		return pageNo * pageSize;
	}
}
