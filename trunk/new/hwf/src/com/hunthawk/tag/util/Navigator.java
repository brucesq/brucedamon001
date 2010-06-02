/**
 * 
 */
package com.hunthawk.tag.util;



import java.util.ArrayList;
import java.util.List;

/**
 * <p>����������</p>
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
	 * @param list  ���ݼ���
	 * @param currentPage ��ǰҳ��
	 * @param pagesize ÿҳ��¼��Ŀ
	 * @param navinum ��������ʾ������
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
	 * <p>��õ�ǰҳ���¼�Ľ���λ��</p>
	 * @return
	 */
	public int getMaxrowindex()
	{
		return this.maxRowIndex;
	}
	/**
	 * <p>��õ�ǰҳ���¼�ĳ�ʼλ��</p>
	 * @return
	 */
	public int getMinrowindex()
	{
		return this.minRowIndex;
	}
	/**
	 * <p>��õ�������ҳ��ֵ</p>
	 * @return
	 */
	public int getMaxindex()
	{
		return this.maxIndex;
	}
	/**
	 * <p>��õ�����С��ҳ��ֵ</p>
	 * @return
	 */
	public int getMinindex()
	{
		return this.minIndex;
	}
	/**
	 * <p>��ü�¼��</p>
	 * @return
	 */
	public List getList()
	{
		return this.list;
	}
	/**
	 * <p>��õ�ǰҳ��</p>
	 * @return
	 */
	public int getCurrentpage()
	{
		return this.currentPage;
	}
	/**
	 * <p>���ҳ��</p>
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
