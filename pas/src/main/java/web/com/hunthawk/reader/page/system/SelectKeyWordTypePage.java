package com.hunthawk.reader.page.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.CallbackPage;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.callback.SearchCallback;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.EbookKeyWord;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.resource.SourceToCheckKeyWordPage;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.KeyWordFilter;
import com.hunthawk.reader.service.system.KeyWordService;

@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class SelectKeyWordTypePage extends CallbackPage{

	
	@InjectObject("spring:keywordService")
	public abstract KeyWordService getKeyWordService();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	@InjectPage("resource/SourceToCheckKeyWordPage")
	public abstract SourceToCheckKeyWordPage getSourceToCheckKeyWordPage();
	
	public abstract void setResourceSet(Set set);
	public abstract Set getResourceSet();
	
	public void cancel(IRequestCycle cycle) {
		try{
			ICallback callback = (ICallback) getCallbackStack().popPreviousCallback();
			callback.performCallback(cycle);
		}catch(Exception e){
			cycle.activate("resource/ShowEbookPage");
		}
	}
	
	public IPropertySelectionModel getPrivileges() {
		
		List<KeyWordType> keyWordType = getKeyWordService()
				.getKeyWordTypeList(1, Integer.MAX_VALUE, "id", false,
						new ArrayList<HibernateExpression>());
		
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				keyWordType, KeyWordType.class, "getType", "getId", false,
				null);
		return model;
	}
	
	@InjectComponent("roleList")
	public abstract Block getRoleList();

	@InjectComponent("roleExist")
	public abstract Block getRoleExist();

	//private List selectedResourceType;

	public abstract void setSelectedResourceTypes(List resourcetypes);
	public abstract List getSelectedResourceTypes();
	
	
	@InjectObject("spring:keywordFilter")
	public abstract KeyWordFilter getKeyWordFilter();

	public List getResourceChapter(String resourceId) {
		return getResourceService().getResourceChapter(
				getNewChapter(Integer.parseInt(resourceId.substring(0, 1))),
				resourceId);
	}
	
	private Class getNewChapter(Integer resourceType) {
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			return EbookChapter.class;
		} else if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			return ComicsChapter.class;
		} else if (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) {
			return MagazineChapter.class;
		} else if (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			return NewsPapersChapter.class;
		}
		return null;
	}
	
	private String getChapterContent(Object chapter) {
		try {
			if (chapter instanceof EbookChapter) {
				return (String) BeanUtils.forceGetProperty(chapter, "bContent");
			} else {
				return (String) BeanUtils.forceGetProperty(chapter, "content");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	/*public void savePage(){
		List<KeyWordType> type = getSelectedResourceTypes();
		System.out.println(type.size());
		System.out.println(type);
	}*/
	public IPage savePage(){

		String err = "";
		// SourceToPackPage page = getSourceToPackPage();
		SourceToCheckKeyWordPage page = getSourceToCheckKeyWordPage();
		//--------------根据所选的分类查询出敏感词-------------------
		List<KeyWordType> typeList = getSelectedResourceTypes();
		List<KeyWord> keywords = new ArrayList<KeyWord>();
		if(typeList!=null && typeList.size()>0){
			for(KeyWordType type : typeList){
				Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
				
				HibernateExpression typeF = new CompareExpression("type",
						type, CompareType.Equal);
				hibernateExpressions.add(typeF);
				
				List<KeyWord> sublist = 
					getKeyWordService().findKeyWordBy(1, Integer.MAX_VALUE, "id", false, hibernateExpressions);
				if(sublist!=null && sublist.size()>0)
					keywords.addAll(sublist);
			}
		}
		//-----------------------------------------------------------------
			List<EbookKeyWord> resSet = new ArrayList<EbookKeyWord>();
			List listRes = new ArrayList(getResourceSet());
			
			// for(Object obj : getSelectedEbook()){
			for (int i = 0; i < listRes.size(); i++) {
				// ResourceAll resource = (ResourceAll)obj;
				ResourceAll resource = (ResourceAll) listRes.get(i);
				List chapters = getResourceChapter(resource.getId());
				int loop = 0;
				for (Object chapter : chapters) {
					loop++;
					String chapterId = "";
					String chapterName = "未知";
					String tomeId = "";
					String content = "";
					if (chapter instanceof EbookChapter) {// 图书
						EbookChapter e = (EbookChapter) chapter;
						chapterId = e.getId();
						chapterName = e.getName();
						tomeId = e.getTomeId();
						if (e.getBContent() == null
								|| StringUtils.isEmpty(e.getBContent())) {
							continue;
						}
						content = e.getBContent();
					} else if (chapter instanceof NewsPapersChapter) {// 报纸
						NewsPapersChapter n = (NewsPapersChapter) chapter;
						chapterId = n.getId();
						chapterName = n.getName();
						tomeId = n.getTomeId();
						if (n.getContent() == null
								|| StringUtils.isEmpty(n.getContent())) {
							continue;
						}
						content = n.getContent();
					} else if (chapter instanceof MagazineChapter) {// 杂志
						MagazineChapter m = (MagazineChapter) chapter;
						chapterId = m.getId();
						chapterName = m.getName();
						tomeId = m.getTomeId();
						if (m.getContent() == null
								|| StringUtils.isEmpty(m.getContent())) {
							continue;
						}
						content = m.getContent();
					} else if (chapter instanceof ComicsChapter) {// 漫画
						ComicsChapter c = (ComicsChapter) chapter;
						chapterId = c.getId();
						chapterName = c.getName();
						tomeId = c.getTomeId();
						if (c.getContent() == null
								|| StringUtils.isEmpty(c.getContent())) {
							continue;
						}
						content = c.getContent();
					}
					if (loop == chapters.size()) {
						break;
					}
					List<String> words = getKeyWordFilter().getContentKeyWord(
							getChapterContent(chapter),keywords);
					//System.out.println("敏感词个数" + words.size());

					if (words.size() > 0) {
						for (String s : words) {
							// System.out.println("@@@@@@@@@@@@@@@@@@@@@@content
							// :"+s);
							// 得到当前章节内容
							EbookKeyWord ekw = new EbookKeyWord();
							ekw.setChapterId(chapterId);
							ekw.setId(resource.getId());
							ekw.setCpId(resource.getCpId());
							ekw.setName(resource.getName());
							if (tomeId != null && !"".equals(tomeId)) {
								ekw
										.setTomeName(String
												.valueOf(Integer
														.parseInt(tomeId
																.substring((tomeId
																		.indexOf(resource
																				.getId()) + resource
																		.getId()
																		.length()) + 1))));
							} else {
								ekw.setTomeName("无卷信息");
							}
							ekw
									.setChapterName("第"
											+ Integer
													.parseInt(chapterId
															.substring((chapterId
																	.indexOf(resource
																			.getId()) + resource
																	.getId()
																	.length()) + 1))
											+ "章：" + chapterName);
							ekw.setKeyWordConent(s);
							resSet.add(ekw);
						}
					}

				}
			}

			page.setResources(resSet);
			page.setTypeList(keywords);
			if (getPack() != null) {
				page.setPack(getPack());
			}
			//setSelectedEbook(new HashSet());
			setSelectedResourceTypes(new ArrayList());
			return page;
		
	}
	
	public abstract ResourcePack getPack();

	public abstract void setPack(ResourcePack pack);
	
	public   IBasicTableModel getTableModel()
	{
		return new IBasicTableModel()
		{
			public int getRowCount()
			{
				return getResourceSet().size();
			}
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder)
			{
				//List<ResourceAll> list  = new ArrayList<ResourceAll>();
				//list.addAll(getResources());
				return  getResourceSet().iterator();
			}
		};
	}
	
	public ITableColumn getSort() {
		return new SimpleTableColumn("sort", "类别", new ITableColumnEvaluator() {

			private static final long serialVersionUID = 625300745851970L;

			public Object getColumnValue(ITableColumn objColumn, Object objRow) {
				ResourceAll rp = (ResourceAll) objRow;
				StringBuffer type = new StringBuffer();
				// 根据资源ID查询资源子类型
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("rid", rp
						.getId(), CompareType.Equal);
				expressions.add(ex);
				List<ResourceResType> list = getResourceService()
						.findResourceResTypeBy(1, Integer.MAX_VALUE, "rid", false,
								expressions);
				for (Iterator it = list.iterator(); it.hasNext();) {
					ResourceResType rrt = (ResourceResType) it.next();
					ResourceType rt = getResourceService().getResourceType(
							rrt.getResTypeId());
					type.append(rt.getName());
					type.append(";");
				}
				// 去掉最后一个分号
				if (list.size() > 0) {
					return type.toString().substring(0, (type.length() - 1));
				} else {
					return "";
				}
			}

		}, false);

	}

}
