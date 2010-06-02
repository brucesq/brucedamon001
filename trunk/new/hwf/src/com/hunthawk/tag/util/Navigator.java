/**
 * 
 */
package com.hunthawk.tag.util;



import java.util.ArrayList;
import java.util.List;

/**
 * <p>导航工具类</p>
 * @author sunquanzhi
 *
 */
public class Navigator {

	private List list ;
	private int currentPage;
	private int pagesize;
	private int navinum;
	private int maxIndex;
	private int minIndex;
	private int pageCount;
	private int minRowIndex;
	private int maxRowIndex;
	/**
	 * 
	 * @param list  数据集合
	 * @param currentPage 当前页面
	 * @param pagesize 每页记录数目
	 * @param navinum 导航中显示的数量
	 */
	public Navigator(List list,int currentPage,int pagesize,int navinum) {
		this.list = list;
		this.currentPage = currentPage;
		this.pagesize = pagesize;
		this.navinum = navinum;
		init();
	}
	public Navigator(String[] arr,int currentPage,int pagesize,int navinum) {
		list = new ArrayList();
		for(int i=0;i<arr.length;i++)
		{
			list.add(arr[i]);
		}
		this.currentPage = currentPage;
		this.pagesize = pagesize;
		this.navinum = navinum;
		init();
	}
	private void init()
	{
		if(list == null || list.size() == 0)
		{
			maxIndex = 0;
			minIndex = 0;
			pageCount = 0;
			minRowIndex = 0;
			maxRowIndex = 0;
		}else{
			pageCount = (int)Math.ceil((double)list.size()/(double)pagesize);
			minRowIndex = currentPage*pagesize; 
			maxRowIndex = currentPage*pagesize+pagesize -1;
			if(maxRowIndex >= list.size() )
			{
				maxRowIndex = list.size() - 1;
			}
			minIndex = currentPage - navinum/2;
			maxIndex = minIndex + navinum;
			if(maxIndex >= pageCount)
			{
				maxIndex = pageCount -1;
				minIndex = maxIndex - navinum;
			}
			if(minIndex < 0)
			{
				minIndex = 0;
				maxIndex = minIndex + navinum;
			}
			if(maxIndex >= pageCount)
			{
				maxIndex = pageCount - 1;
			}
		}
	}

	/**
	 * <p>获得当前页面记录的结束位置</p>
	 * @return
	 */
	public int getMaxrowindex()
	{
		return this.maxRowIndex;
	}
	/**
	 * <p>获得当前页面记录的初始位置</p>
	 * @return
	 */
	public int getMinrowindex()
	{
		return this.minRowIndex;
	}
	/**
	 * <p>获得导航最大的页面值</p>
	 * @return
	 */
	public int getMaxindex()
	{
		return this.maxIndex;
	}
	/**
	 * <p>获得导航最小的页面值</p>
	 * @return
	 */
	public int getMinindex()
	{
		return this.minIndex;
	}
	/**
	 * <p>获得记录集</p>
	 * @return
	 */
	public List getList()
	{
		return this.list;
	}
	/**
	 * <p>获得当前页数</p>
	 * @return
	 */
	public int getCurrentpage()
	{
		return this.currentPage;
	}
	/**
	 * <p>获得页数</p>
	 * @return
	 */
	public int getPagecount()
	{
		return this.pageCount;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
