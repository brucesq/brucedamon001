package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.service.resource.ResourceService;
@Restrict(roles = { "resource" }, mode = Restrict.Mode.ROLE)
public abstract class ShowComicsChapterPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("resource/EditComicsChapterPage")
	public abstract EditComicsChapterPage getEditComicsChapterPage();

	@InjectPage("resource/ShowComicsChapterImagePage")
	public abstract ShowComicsChapterImagePage getShowComicsChapterImagePage();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	@Persist("session")
	public abstract ResourceAll getResourceAll();

	public abstract void setResourceAll(ResourceAll resourceAll);

	public abstract String getChapterName();

	public abstract void setChapterName(String name);

	public abstract Integer getChapterId();

	public abstract void setChapterId(Integer id);

	public abstract Integer getChapterIndex();

	public abstract void setChapterIndex(Integer id);

	public IPage onEdit(ComicsChapter chapter, ResourceAll resourceAll) {
		getEditComicsChapterPage().setModel(chapter);
		getEditComicsChapterPage().setResourceAll(resourceAll);
		return getEditComicsChapterPage();
	}

	public IPage editChapter(ResourceAll resourceAll) {
		EditComicsChapterPage page = getEditComicsChapterPage();
		page.setResourceAll(resourceAll);
		return page;

	}

	public IPage onImage(ComicsChapter chapter) {
		getShowComicsChapterImagePage().setModel(chapter);
		getShowComicsChapterImagePage().setComicsChapter(chapter);
		getShowComicsChapterImagePage().setImageIndex("0");
		return getShowComicsChapterImagePage();

	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		ComicsChapter chapter = (ComicsChapter) getCurrentChapter();
		Set selectedChapter = getSelectedChapters();
		// ѡ�����û�
		if (bSelected) {
			selectedChapter.add(chapter);
		} else {
			selectedChapter.remove(chapter);
		}
		// persist value
		setSelectedChapters(selectedChapter);

	}

	public boolean getCheckboxSelected() {
		return getSelectedChapters().contains(getCurrentChapter());
	}

	@SuppressWarnings("unchecked")
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedChapters();

	@SuppressWarnings("unchecked")
	public abstract void setSelectedChapters(Set set);

	/**
	 * ��õ�ǰ�½�
	 * 
	 * @return
	 */
	public abstract Object getCurrentChapter();

	@Override
	protected void delete(Object object) {
		try {
			getResourceService().deleteResourceChapter(object,
					ResourceAll.RESOURCE_TYPE_COMICS);

			
			//�����½����
			ComicsChapter currentChapter = (ComicsChapter)object;
			Integer index = currentChapter.getChapterIndex();//�õ�ɾ���� ���
			getResourceService().deleteResourceChapter(object,ResourceAll.RESOURCE_TYPE_COMICS);//ɾ������
			List<ComicsChapter> chapterList = getResourceService().getResourceChapter(ComicsChapter.class, currentChapter.getResourceId());//�õ��½ڵ��б�
			int i=0;
			if(chapterList !=null ){ //�Ѿ������½ڶ���
				Integer totalsize = chapterList.size();//��ȡ���½���
				for(i=index; i<=totalsize;i++)
				{
					ComicsChapter comicsChapter = chapterList.get(i-1);//������ţ��õ� �б��ж�Ӧ ��ŵ��Ǹ� ����
					comicsChapter.setChapterIndex(i);
					getResourceService().updateResourceChapter(comicsChapter);
				} 
			}
		
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public void onBatchDelete(IRequestCycle cycle) {
		String resourceId = "";
		for (Object obj : getSelectedChapters()) {
			
				if("".equals(resourceId)){
					ComicsChapter currentChapter = (ComicsChapter)obj;
					resourceId = currentChapter.getResourceId();
				}
				try{
				getResourceService().deleteResourceChapter(obj,ResourceAll.RESOURCE_TYPE_BOOK);//ɾ������
				}catch(Exception e){
					getDelegate().setFormComponent(null);
					getDelegate().record(e.getMessage(), null);
				}
		}
		//ɾ���ɹ��� �������е� �����������һ��
		List<ComicsChapter> chapterList = getResourceService().getResourceChapter(ComicsChapter.class,resourceId);//�õ���������е��½��б�
		if(chapterList!=null){
			for(int i=0;i<chapterList.size();i++){
				ComicsChapter comicsChapter = chapterList.get(i);//������ţ��õ� �б��ж�Ӧ ��ŵ��Ǹ�����
				comicsChapter.setChapterIndex(i+1);
				try {
					getResourceService().updateResourceChapter(comicsChapter);
				} catch (Exception e) {
					getDelegate().setFormComponent(null);
					getDelegate().record(e.getMessage(), null);
				}
			}
		}
		
		setSelectedChapters(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);

	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getChapterName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getChapterName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getChapterId() != null && getChapterId() > 0) {
			HibernateExpression useridE = new CompareExpression("id",
					getChapterId(), CompareType.Equal);
			hibernateExpressions.add(useridE);
		}

		if (getChapterIndex() != null && getChapterIndex() > 0) {
			HibernateExpression useridE = new CompareExpression("chapterIndex",
					getChapterIndex(), CompareType.Equal);
			hibernateExpressions.add(useridE);
		}
		if (getResourceAll() != null) {
			HibernateExpression resourceId = new CompareExpression(
					"resourceId", getResourceAll().getId(), CompareType.Equal);
			hibernateExpressions.add(resourceId);
		}
		return hibernateExpressions;
	}

	public void search() {
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("resourceAll");
		nameC.setValue(getResourceAll());
		searchConditions.add(nameC);
		return searchConditions;
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getResourceService().getResourceChapterCount(
						ComicsChapter.class, getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getResourceService().getResourceChapterList(
						ComicsChapter.class, pageNo, nPageSize, "chapterIndex", true,
						getSearchExpressions()).iterator();
			}
		};
	}

	public ITableColumn getTome() {
		return new SimpleTableColumn("tomId", "������",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {

						ComicsChapter chapter = (ComicsChapter) objRow;
						String tomeid = chapter.getTomeId();// ��ID
						if (tomeid == null || "".equals(tomeid))
							return " ";
						else {
							EbookTome tome = getResourceService().getEbookTome(
									chapter.getTomeId());
							return tome.getName();
						}
					}

				}, false);

	}

	public String getPreAddress() {
		// �õ���Դ�е�ͼƬ
		ComicsChapter chapter = (ComicsChapter) getCurrentChapter();
		String[] chapterImages = chapter.getImages();
		String imgPath = "";
		if (chapter.getImageName() == null || "".equals(chapter.getImageName()))
			imgPath = "<img src='../img/icon_001.gif' align='absmiddle'width='10' height='10' />";
		else {
			String url = getResourceService().getChapterImg(
					chapter.getResourceId(), chapterImages[0]);
			imgPath += "<img src='" + url
					+ "' align='absmiddle' width='40' height='40'/>";
		}
		return imgPath;
	}
}
