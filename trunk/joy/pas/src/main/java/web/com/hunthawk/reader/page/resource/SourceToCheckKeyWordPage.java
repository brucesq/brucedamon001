package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.CallbackPage;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.EbookKeyWord;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.page.util.DetailPageField;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.KeyWordFilter;

@Restrict(roles = { "resource" }, mode = Restrict.Mode.ROLE)
public abstract class SourceToCheckKeyWordPage extends CallbackPage {

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	//	
	public abstract ResourcePack getPack();

	public abstract void setPack(ResourcePack pack);

	/**
	 * 资源的list集合
	 */
	public abstract List getResources();

	public String getResourceIds() {
		List resources = getResources();
		List<String> rids = new ArrayList();
		for (int i = 0; i < resources.size(); i++) {
			EbookKeyWord item = (EbookKeyWord) resources.get(i);
			if (!rids.contains(item.getId())) {
				rids.add(item.getId());
			}
		}
		StringBuffer sb = new StringBuffer();
		for (String str : rids) {
			sb.append(str).append(",");
		}
		return sb.toString();
	}

	public abstract void setResources(List resourceList);

	@InjectPage("resource/SourceToReCheck")
	public abstract SourceToReCheck getSourceToReCheck();

	/**
	 * 敏感词分类list集合
	 */
	@InitialValue("new java.util.ArrayList()")
	@Persist("session")
	public abstract List<KeyWord> getTypeList();

	public abstract void setTypeList(List<KeyWord> list);

	public void cancel(IRequestCycle cycle) {
		try {
			getCallbackStack().popPreviousCallback();
			ICallback callback = (ICallback) getCallbackStack().getStack()
					.pop();
			callback.performCallback(cycle);
		} catch (Exception e) {
			cycle.activate("resource/ShowEbookPage");
		}
	}

	public boolean hasRole(String role) {
		return super.hasRole(role);
	}

	public abstract DetailPageField getCurrentDetailField();

	public List<DetailPageField> getDetailList() {

		return getObjectFields(getCurrentProduct());
	}

	@InjectObject("spring:keywordFilter")
	public abstract KeyWordFilter getKeyWordFilter();

	private List<DetailPageField> getObjectFields(Object obj) {
		EbookKeyWord chapter = (EbookKeyWord) obj;
		chapter.getKeyWordConent();// 书的内容
		List<DetailPageField> details = new ArrayList<DetailPageField>();
		DetailPageField field = new DetailPageField();
		field.setTitle("内容");
		field.setValue(getKeyWordFilter().highlight(chapter.getKeyWordConent(),
				getTypeList()));
		details.add(field);
		return details;
	}

	public abstract Object getCurrentProduct();

	int start = 0;
	int end = 0;

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getResources().size();// 敏感字的个数
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				List<EbookKeyWord> list = new ArrayList<EbookKeyWord>();
				int pageNo = nFirst / nPageSize;
				pageNo++;
				start = (pageNo - 1) * nPageSize;
				end = start + nPageSize;
				for (int i = 0; i < getResources().size(); i++) {
					if (i >= start && i < end) {
						list.add((EbookKeyWord) getResources().get(i));
					}
					if (i == end) {
						start = end;
						start++;
						break;
					}
				}
				return list.iterator();// 敏感字列表

			}
		};
	}

	public boolean isReCheck() {
		boolean flag = false;
		List resources = getResources();
		if (getResources() != null) {
			List<String> rids = new ArrayList();
			for (int i = 0; i < resources.size(); i++) {
				EbookKeyWord item = (EbookKeyWord) resources.get(i);
				if (!rids.contains(item.getId())) {
					rids.add(item.getId());
				}
			}
			if (rids != null && rids.size() == 1) {
				flag = true;
			}
		}
		return flag;
	}

	@InjectPage("resource/EditChapterFromKeyWordPage")
	public abstract EditChapterFromKeyWordPage getEditChapterFromKeyWordPage();

	public IPage onEditChapter(String rid, String chapId) {

		EbookChapter ec = null;
		MagazineChapter mc = null;
		NewsPapersChapter nc = null;
		ComicsChapter cc = null;

		ResourceAll resource = getResourceService().getResource(rid);

		if (chapId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))) {// 图书
			ec = getResourceService().getEbookChapterById(chapId);
			getEditChapterFromKeyWordPage().setContent(ec.getBContent());
			getEditChapterFromKeyWordPage().setChapterName(ec.getName());
			getEditChapterFromKeyWordPage().setChapterOrder(
					ec.getChapterIndex().toString());
			getEditChapterFromKeyWordPage().setModel(ec);
		} else if (chapId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))) {// 漫画
			cc = getResourceService().getComicsChapterById(chapId);
			getEditChapterFromKeyWordPage().setContent(cc.getContent());
			getEditChapterFromKeyWordPage().setChapterName(cc.getName());
			getEditChapterFromKeyWordPage().setChapterOrder(
					cc.getChapterIndex().toString());
			getEditChapterFromKeyWordPage().setModel(cc);
		} else if (chapId
				.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))) {// 杂志
			mc = getResourceService().getMagazineChapterById(chapId);
			getEditChapterFromKeyWordPage().setContent(mc.getContent());
			getEditChapterFromKeyWordPage().setChapterName(mc.getName());
			getEditChapterFromKeyWordPage().setChapterOrder(
					mc.getChapterIndex().toString());
			getEditChapterFromKeyWordPage().setModel(mc);
		} else if (chapId.startsWith(String
				.valueOf(ResourceType.TYPE_NEWSPAPERS))) {// 报纸
			nc = getResourceService().getNewsPapersChapterById(chapId);
			getEditChapterFromKeyWordPage().setContent(nc.getContent());
			getEditChapterFromKeyWordPage().setChapterName(nc.getName());
			getEditChapterFromKeyWordPage().setChapterOrder(
					nc.getChapterIndex().toString());
			getEditChapterFromKeyWordPage().setModel(nc);
		}
		getEditChapterFromKeyWordPage().setResourceAll(resource);
		return getEditChapterFromKeyWordPage();
	}

	public IPage onDetail(String rid, String chapId) {

		EbookChapter ec = null;
		MagazineChapter mc = null;
		NewsPapersChapter nc = null;
		ComicsChapter cc = null;

		// 根据资源ID 和章节ID查询 章节信息 并将敏感内容高亮显示

		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression resourceIdE = new CompareExpression("resourceId",
				rid, CompareType.Equal);
		hibernateExpressions.add(resourceIdE);
		HibernateExpression parentE = new CompareExpression("id", chapId,
				CompareType.Equal);
		hibernateExpressions.add(parentE);
		if (chapId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))) {// 图书
			ec = (EbookChapter) getResourceService().getResourceChapterList(
					EbookChapter.class, 1, Integer.MAX_VALUE, "id", false,
					hibernateExpressions).get(0);
			ec.setBContent(getKeyWordFilter().highlight(ec.getBContent(),
					getTypeList()));
			getCheckKeyWordPage().setCurrentObject(ec);
		} else if (chapId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))) {// 漫画
			cc = (ComicsChapter) getResourceService().getResourceChapterList(
					ComicsChapter.class, 1, Integer.MAX_VALUE, "id", false,
					hibernateExpressions).get(0);
			cc.setContent(getKeyWordFilter().highlight(cc.getContent(),
					getTypeList()));
			getCheckKeyWordPage().setCurrentObject(cc);
		} else if (chapId
				.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))) {// 杂志
			mc = (MagazineChapter) getResourceService().getResourceChapterList(
					MagazineChapter.class, 1, Integer.MAX_VALUE, "id", false,
					hibernateExpressions).get(0);
			mc.setContent(getKeyWordFilter().highlight(mc.getContent(),
					getTypeList()));
			getCheckKeyWordPage().setCurrentObject(mc);
		} else if (chapId.startsWith(String
				.valueOf(ResourceType.TYPE_NEWSPAPERS))) {// 报纸
			nc = (NewsPapersChapter) getResourceService()
					.getResourceChapterList(NewsPapersChapter.class, 1,
							Integer.MAX_VALUE, "id", false,
							hibernateExpressions).get(0);
			nc.setContent(getKeyWordFilter().highlight(nc.getContent(),
					getTypeList()));
			getCheckKeyWordPage().setCurrentObject(nc);
		}
		getCheckKeyWordPage().setTypeList(getTypeList());
		return getCheckKeyWordPage();
	}

	/*
	 * public IPage onDetail(Object obj,List<KeyWord> wordList){ EbookKeyWord
	 * ekw=(EbookKeyWord)obj; String rid=ekw.getId(); EbookChapter ec=null;
	 * MagazineChapter mc=null; NewsPapersChapter nc=null; ComicsChapter
	 * cc=null; String chapId=ekw.getChapterId(); //根据资源ID 和章节ID查询 章节信息
	 * 并将敏感内容高亮显示
	 * 
	 * Collection<HibernateExpression> hibernateExpressions = new
	 * ArrayList<HibernateExpression>(); HibernateExpression resourceIdE = new
	 * CompareExpression("resourceId",rid , CompareType.Equal);
	 * hibernateExpressions.add(resourceIdE); HibernateExpression parentE = new
	 * CompareExpression("id",chapId, CompareType.Equal);
	 * hibernateExpressions.add(parentE);
	 * if(chapId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){//图书
	 * ec=(EbookChapter
	 * )getResourceService().getResourceChapterList(EbookChapter.class, 1,
	 * Integer.MAX_VALUE, "id", false, hibernateExpressions).get(0);
	 * ec.setBContent(getKeyWordFilter().highlight(ec.getBContent(),wordList));
	 * getCheckKeyWordPage().setCurrentObject(ec); }else
	 * if(chapId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){//漫画
	 * cc=(ComicsChapter
	 * )getResourceService().getResourceChapterList(ComicsChapter.class, 1,
	 * Integer.MAX_VALUE, "id", false, hibernateExpressions).get(0);
	 * cc.setContent(getKeyWordFilter().highlight(cc.getContent(),wordList));
	 * getCheckKeyWordPage().setCurrentObject(cc); }else
	 * if(chapId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){//杂志
	 * mc=
	 * (MagazineChapter)getResourceService().getResourceChapterList(MagazineChapter
	 * .class, 1, Integer.MAX_VALUE, "id", false, hibernateExpressions).get(0);
	 * mc.setContent(getKeyWordFilter().highlight(mc.getContent(),wordList));
	 * getCheckKeyWordPage().setCurrentObject(mc); }else
	 * if(chapId.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){//报纸
	 * nc=(NewsPapersChapter)getResourceService().getResourceChapterList(
	 * NewsPapersChapter.class, 1, Integer.MAX_VALUE, "id", false,
	 * hibernateExpressions).get(0);
	 * nc.setContent(getKeyWordFilter().highlight(nc.getContent(),wordList));
	 * getCheckKeyWordPage().setCurrentObject(nc); }
	 * getCheckKeyWordPage().setTypeList(wordList); return
	 * getCheckKeyWordPage(); //return getDetailPage(); }
	 */

	@InjectPage("resource/CheckKeyWordPage")
	public abstract CheckKeyWordPage getCheckKeyWordPage();

	public IPage addReCheckMsg(String resources) {
		ValidationDelegate delegate = getDelegate();
		String str = null;
		try {
			if (StringUtils.isNotEmpty(resources)) {
				String[] ids = resources.split(",");
				if (ids != null && ids.length == 1) {
					str = ids[0];
				} else {
					throw new Exception("审核的资源数量不能大于1!");
				}
			} else {
				throw new Exception("没人任何资源");
			}

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return this;
		}
		getSourceToReCheck().setResourceId(str);
		return getSourceToReCheck();
	}

	// @InjectPage("DetailPage")
	// public abstract DetailPage getDetailPage();

}
