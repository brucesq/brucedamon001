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
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.service.resource.ResourceService;

@Restrict(roles = { "resource" }, mode = Restrict.Mode.ROLE)
public abstract class ShowNewsPapersChapterPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("resource/EditNewsPapersChapterPage")
	public abstract EditNewsPapersChapterPage getEditNewsPapersChapterPage();

	@InjectPage("resource/ShowNewsPapersChapterImagePage")
	public abstract ShowNewsPapersChapterImagePage getShowNewsPapersChapterImagePage();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	public abstract String getChapterName();

	public abstract void setChapterName(String name);

	public abstract Integer getChapterId();

	public abstract void setChapterId(Integer id);

	public abstract Integer getChapterIndex();

	public abstract void setChapterIndex(Integer id);
	
	@Persist("session")
	public abstract ResourceAll getResourceAll();

	public abstract void setResourceAll(ResourceAll resourceAll);

	public IPage onEdit(NewsPapersChapter chapter, ResourceAll resourceAll) {
		getEditNewsPapersChapterPage().setModel(chapter);
		getEditNewsPapersChapterPage().setResourceAll(resourceAll);
		return getEditNewsPapersChapterPage();
	}

	public IPage editChapter(ResourceAll resourceAll) {
		EditNewsPapersChapterPage page = getEditNewsPapersChapterPage();
		page.setResourceAll(resourceAll);
		return page;

	}

	public IPage onImage(NewsPapersChapter chapter) {
		getShowNewsPapersChapterImagePage().setModel(chapter);
		getShowNewsPapersChapterImagePage().setNewsPapersChapter(chapter);
		getShowNewsPapersChapterImagePage().setImageIndex("0");
		return getShowNewsPapersChapterImagePage();

	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		NewsPapersChapter chapter = (NewsPapersChapter) getCurrentChapter();
		Set selectedChapter = getSelectedChapters();
		// 选择了用户
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
	 * 获得当前章节
	 * 
	 * @return
	 */
	public abstract Object getCurrentChapter();

	@Override
	protected void delete(Object object) {
		try {
			
			//更改章节序号
			NewsPapersChapter currentChapter = (NewsPapersChapter)object;
			Integer index = currentChapter.getChapterIndex();//得到删除的 序号
			getResourceService().deleteResourceChapter(object,ResourceAll.RESOURCE_TYPE_NEWSPAPER);
			List<NewsPapersChapter> chapterList = getResourceService().getResourceChapter(NewsPapersChapter.class, currentChapter.getResourceId());//得到章节的列表
			int i=0;
			if(chapterList !=null ){ //已经存在章节对象
				Integer totalsize = chapterList.size();//获取总章节数
				for(i=index; i<=totalsize;i++)
				{
					NewsPapersChapter newsPapersChapter = chapterList.get(i-1);//根据序号，得到 列表中对应 序号的那个 对象
					newsPapersChapter.setChapterIndex(i);
					getResourceService().updateResourceChapter(newsPapersChapter);
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
					NewsPapersChapter currentChapter = (NewsPapersChapter)obj;
					resourceId = currentChapter.getResourceId();
				}
				try{
				getResourceService().deleteResourceChapter(obj,ResourceAll.RESOURCE_TYPE_BOOK);//删除对象
				}catch(Exception e){
					getDelegate().setFormComponent(null);
					getDelegate().record(e.getMessage(), null);
				}
		}
		//删除成功后 ，把所有的 序号重新排序一次
		List<NewsPapersChapter> chapterList = getResourceService().getResourceChapter(NewsPapersChapter.class,resourceId);//得到按序号排列的章节列表
		if(chapterList!=null){
			for(int i=0;i<chapterList.size();i++){
				NewsPapersChapter newsPapersChapter = chapterList.get(i);//根据序号，得到 列表中对应 序号的那个对象
				newsPapersChapter.setChapterIndex(i+1);
				try {
					getResourceService().updateResourceChapter(newsPapersChapter);
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
						NewsPapersChapter.class, getSearchExpressions())
						.intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getResourceService().getResourceChapterList(
						NewsPapersChapter.class, pageNo, nPageSize, "chapterIndex", true,
						getSearchExpressions()).iterator();
			}
		};
	}

	public ITableColumn getTome() {
		return new SimpleTableColumn("tomId", "所属卷",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {

						NewsPapersChapter chapter = (NewsPapersChapter) objRow;
						String tomeid = chapter.getTomeId();// 卷ID
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
		// 得到资源中的图片
		NewsPapersChapter chapter = (NewsPapersChapter) getCurrentChapter();
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
