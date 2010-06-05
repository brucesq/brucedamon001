package com.hunthawk.reader.page.resource;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
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
import org.apache.tapestry.dojo.form.DefaultAutocompleteModel;
import org.apache.tapestry.dojo.form.IAutocompleteModel;
import org.apache.tapestry.engine.ExternalServiceParameter;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.NotNullExpression;
import com.hunthawk.framework.hibernate.NullExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.Application;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.Infomation;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.system.SelectKeyWordTypePage;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.KeyWordFilter;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author xianlaichen
 * 
 */
@Restrict(roles = { "resource" }, mode = Restrict.Mode.ROLE)
public abstract class ShowEbookPage extends SearchPage implements
		IExternalPage, PageBeginRenderListener {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("resource/EditEbookPage")
	public abstract EditEbookPage getEditEbookPage();

	@InjectPage("resource/EditMagazinePage")
	public abstract EditMagazinePage getEditMagazinePage();

	@InjectPage("resource/SourceToPackPage")
	public abstract SourceToPackPage getSourceToPackPage();

	@InjectPage("resource/SourceToCheckKeyWordPage")
	public abstract SourceToCheckKeyWordPage getSourceToCheckKeyWordPage();

	@InjectPage("resource/EditNewsPapersPage")
	public abstract EditNewsPapersPage getEditNewsPapersPage();

	@InjectPage("resource/EditComicsPage")
	public abstract EditComicsPage getEditComicsPage();
	
	@InjectPage("resource/EditVideoPage")
	public abstract EditVideoPage getEditVideoPage();
	
	@InjectPage("resource/EditInfoPage")
	public abstract EditInfoPage getEditInfoPage();
	
	@InjectPage("resource/EditApplicationPage")
	public abstract EditApplicationPage getEditApplicationPage();

	@InjectPage("resource/ShowEbookChapterPage")
	public abstract ShowEbookChapterPage getShowEbookChapterPage();

	@InjectPage("resource/ShowMagazineChapterPage")
	public abstract ShowMagazineChapterPage getShowMagazineChapterPage();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	@InjectObject("spring:keywordFilter")
	public abstract KeyWordFilter getKeyWordFilterService();

	public abstract ResourcePack getPack();

	public abstract void setPack(ResourcePack pack);

	public abstract String getResourceId();

	public abstract void setResourceId(String resourceId);

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	@InjectPage("resource/ShowReCheckPage")
	public abstract ShowReCheckPage getShowReCheckPage();

	public boolean hasRole(String role) {
		return super.hasRole(role);
	}

	public void onNewDelete(IRequestCycle cycle, Object object,
			boolean otherPageToHere) {
		setOtherPageToHere(otherPageToHere);
		delete(object);
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		if (callback != null)
			callback.performCallback(cycle);
	}

	@Override
	protected void delete(Object object) {
		try {
			UserImpl user = (UserImpl)getUser();
			// �ж�����Դɾ��������Դ����ɾ��
			if (getOtherPageToHere()) {// ��������Դ��ɾ��
				getResourceService().deleteResourceResType(
						((ResourceAll) object).getId());
			} else {
				getResourceService().deleteResource((ResourceAll) object,user);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public IPage onEdit(ResourceAll resource) {
		if (resource instanceof Ebook) { // ͼ��
			getEditEbookPage().setModel(resource);
			getEditEbookPage().setResourceAll(resource);
			return getEditEbookPage();
		}
		if (resource instanceof Magazine) { // ��־
			getEditMagazinePage().setModel(resource);
			getEditMagazinePage().setResourceAll(resource);
			return getEditMagazinePage();
		}
		if (resource instanceof NewsPapers) { // ��ֽ
			getEditNewsPapersPage().setModel(resource);
			getEditNewsPapersPage().setResourceAll(resource);
			return getEditNewsPapersPage();
		}
		if (resource instanceof Comics) { // ����
			getEditComicsPage().setModel(resource);
			getEditComicsPage().setResourceAll(resource);
			return getEditComicsPage();
		}
		if (resource instanceof Video) { // Video
			getEditVideoPage().setModel(resource);
			getEditVideoPage().setResourceAll(resource);
			return getEditVideoPage();
		}
		if (resource instanceof Infomation) { // ��Ѷ
			getEditInfoPage().setModel(resource);
//			getEditInfoPage().setResourceAll(resource);
			return getEditInfoPage();
		}
		if (resource instanceof Application) { // Video
			getEditApplicationPage().setModel(resource);
			getEditApplicationPage().setResourceAll(resource);
			return getEditApplicationPage();
		}
		
		
		return this;
	}

	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedEbook();

	public abstract void setSelectedEbook(Set set);

	public abstract Object getCurrentObject();

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		ResourceAll pro = (ResourceAll) getCurrentProduct();
		Set selectedPros = getSelectedEbook();
		// ����ظ��ļ�¼
		if (bSelected) {
			selectedPros.add(pro);
		} else {
			selectedPros.remove(pro);
		}
		// persist value
		setSelectedEbook(selectedPros);

	}

	public boolean getCheckboxSelected() {
		return getSelectedEbook().contains(getCurrentProduct());
	}

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedEbook()) {
			delete(obj);
		}
		setSelectedEbook(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);
	}

	public void onCheckKeyWord(IRequestCycle cycle) {

		if (getSelectedEbook().size() == 0) {
			getDelegate().setFormComponent(null);
			getDelegate().record("������ѡ��һ����", null);
			return;
		}

	}

	// ��Ʒ����
	public abstract String getName();

	public abstract void setName(String name);

	public abstract void setResourceType(Integer type);

	public abstract void setSortId(Integer sort);// С���

	public abstract Integer getSortId();

	public abstract void setStatus(Integer status);

	@Persist("session")
	public abstract Integer getStatus();

	public abstract void setKeyname(Integer keyname);

	public abstract Integer getKeyname();

	public abstract UserImpl getCreator();

	public abstract void setCreator(UserImpl creator);

	public abstract Provider getCp();

	public abstract void setCp(Provider cp);

	public abstract void setPublisherId(Integer publisherId);

	@Persist("session")
	public abstract Integer getPublisherId();

	public abstract void setOtherPageToHere(boolean flag);

	public abstract boolean getOtherPageToHere();

	@Persist("session")
	public abstract Integer getResourceType();

	public abstract void setAuthorId(Integer authorId);

	@Persist("session")
	public abstract Integer getAuthorId();

	public abstract String getInitialLetter();

	public abstract void setInitialLetter(String initialLetter);

	public abstract Integer getExpNum();

	public abstract void setExpNum(Integer id);

	public abstract Integer getIsFirstpublish();

	public abstract void setIsFirstpublish(Integer id);

	public abstract Integer getIsOut();

	public abstract void setIsOut(Integer id);

	public abstract String getPublisher();

	public abstract void setPublisher(String publisher);

	/*
	 * yuzs ���������� 2009-11-06
	 */
	public abstract Integer getHealthNum();

	public abstract void setHealthNum(Integer id);

	public abstract Integer getIsSearchTop();

	public abstract void setIsSearchTop(Integer id);

	public abstract Integer getIsDivision();

	public abstract void setIsDivision(Integer id);

	/*
	 * ����
	 */
	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {

		Integer resourceType = (Integer) parameters[0];
		setResourceType(resourceType);
	}

	public String getEbookTypeUrl() {
		Object[] params = new Object[] { ResourceType.TYPE_BOOK };
		return PageHelper.getExternalURL(getExternalService(),
				"resource/ShowEbookPage", params);
	}

	public String getComicsTypeUrl() {
		Object[] params = new Object[] { ResourceType.TYPE_COMICS };
		return PageHelper.getExternalURL(getExternalService(),
				"resource/ShowEbookPage", params);
	}

	public String getNewsPagersTypeUrl() {
		Object[] params = new Object[] { ResourceType.TYPE_NEWSPAPERS };
		return PageHelper.getExternalURL(getExternalService(),
				"resource/ShowEbookPage", params);
	}

	public String getMagazineTypeUrl() {
		Object[] params = new Object[] { ResourceType.TYPE_MAGAZINE };
		return PageHelper.getExternalURL(getExternalService(),
				"resource/ShowEbookPage", params);
	}
	
	public String getVideoTypeUrl() {
		Object[] params = new Object[] { ResourceType.TYPE_VIDEO };
		return PageHelper.getExternalURL(getExternalService(),
				"resource/ShowEbookPage", params);
	}
	
	public String getInfoTypeUrl() {
		Object[] params = new Object[] { ResourceType.TYPE_INFO };
		return PageHelper.getExternalURL(getExternalService(),
				"resource/ShowEbookPage", params);
	}
	
	public String getApplicationTypeUrl() {
		Object[] params = new Object[] { ResourceType.TYPE_APPLICATION };
		return PageHelper.getExternalURL(getExternalService(),
				"resource/ShowEbookPage", params);
	}

	public abstract String getRKeyWord();

	public abstract void setRKeyWord(String keyWord);

	public abstract Integer getIsFinished();

	public abstract void setIsFinished(Integer isFinished);

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			if (getKeyname() == 0) {
				HibernateExpression nameE = new CompareExpression("name", "%"
						+ getName() + "%", CompareType.Like);
				hibernateExpressions.add(nameE);
			} else if (getKeyname() == 1) {
				HibernateExpression nameE = new CompareExpression("id", "%"
						+ getName() + "%", CompareType.Like);
				hibernateExpressions.add(nameE);
			}
		}
		if (getKeyname() == 3 && !ParameterCheck.isNullOrEmpty(getRKeyWord())) { // �ؼ���
			HibernateExpression nameE = new CompareExpression("RKeyword", "%"
					+ getRKeyWord() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getIsFinished() != null && !getIsFinished().equals(0)) {// �Ƿ�����
			HibernateExpression nameE = new CompareExpression("isFinished",
					getIsFinished(), CompareType.Equal);
			hibernateExpressions.add(nameE);
		}
		if (!ParameterCheck.isNullOrEmpty(getInitialLetter())
				&& !"0".equals(getInitialLetter())) {// ����ĸ
			HibernateExpression nameE = new CompareExpression("initialLetter",
					"%" + getInitialLetter() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getExpNum() != null && getExpNum() > 0) {// �Ƽ�ָ��
			HibernateExpression nameE = new CompareExpression("expNum",
					getExpNum(), CompareType.Equal);
			hibernateExpressions.add(nameE);
		}
		/*
		 * yuzs ���������� 2009-11-06
		 */
		if (getHealthNum() != null && getHealthNum() > 0) {// ����ָ��
			HibernateExpression nameE = new CompareExpression("healthNum",
					getHealthNum(), CompareType.Equal);
			hibernateExpressions.add(nameE);
		}

		if (getIsSearchTop() != null && getIsSearchTop() > -1) {// �Ƿ��ö�
			HibernateExpression nameE = new CompareExpression("searchTop",
					getIsSearchTop(), CompareType.Equal);
			hibernateExpressions.add(nameE);
		}

		if (getIsDivision() != null && getIsDivision() > -1) {// �Ƿ����鲿
			if (getIsDivision() == 0) { // ���鲿
				NullExpression name = new NullExpression("division");
				hibernateExpressions.add(name);
				// HibernateExpression nameE = new CompareExpression("division",
				// "��", CompareType.Like);
				// hibernateExpressions.add(nameE);
			} else { // ���鲿
				NotNullExpression name = new NotNullExpression("division");
				hibernateExpressions.add(name);
				// HibernateExpression nameE = new CompareExpression("division",
				// "��", CompareType.NotLike);
				// hibernateExpressions.add(nameE);
			}

		}

		/*
		 * ����
		 */
		if (getIsFirstpublish() != null && getIsFirstpublish() > 0) {// �Ƿ��׷�
			HibernateExpression nameE = new CompareExpression("isFirstpublish",
					getIsFirstpublish(), CompareType.Equal);
			hibernateExpressions.add(nameE);
		}
		if (getIsOut() != null && getIsOut() > 0) { // �Ƿ����
			HibernateExpression nameE = new CompareExpression("isOut",
					getIsOut(), CompareType.Equal);
			hibernateExpressions.add(nameE);
		}

		if (!ParameterCheck.isNullOrEmpty(getPublisher())) { // ������
			HibernateExpression nameE = new CompareExpression("publisher", "%"
					+ getPublisher() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}

		// ��������״̬������
		HibernateExpression statusE = new CompareExpression("status", 2,
				CompareType.NotEqual);
		hibernateExpressions.add(statusE);

		UserImpl user = (UserImpl) getUser();
		if (user.isRoleProvider()) {// SPCPֻ�ܿ����Լ�������
			Integer cpid = user.getProvider() != null ? user.getProvider()
					.getId() : null;
			HibernateExpression cpE = new CompareExpression("cpId", cpid,
					CompareType.Equal);
			hibernateExpressions.add(cpE);

		}

		HibernateExpression nameE = new CompareExpression("id", String
				.valueOf(getResourceType())
				+ "%", CompareType.Like);
		hibernateExpressions.add(nameE);

		if (getAuthorId() != null && !getAuthorId().equals(0)) { // �ӵ�һ����ѯ������д��ģ�
			HibernateExpression author = new CompareExpression("authorId", "%|"
					+ String.valueOf(getAuthorId()) + "|%", CompareType.Like);
			hibernateExpressions.add(author);
		} else { // �ڶ�����ѯ������ѡ��ģ�
			if (getAuthorListId() != null && !getAuthorListId().equals(0)) {
				HibernateExpression author = new CompareExpression("authorId",
						"%|" + String.valueOf(getAuthorListId()) + "|%",
						CompareType.Like);
				hibernateExpressions.add(author);
			}
		}

		if (getStatus() != 9) {
			HibernateExpression statusE2 = new CompareExpression("status",
					getStatus(), CompareType.Equal);
			hibernateExpressions.add(statusE2);
		}
		// System.out.println("creator:"+getCreator().getId());
		if (getCreator() != null && getCreator().getId() != 0) {
			HibernateExpression creatorE = new CompareExpression("creatorId",
					getCreator().getId(), CompareType.Equal);
			hibernateExpressions.add(creatorE);
		}

		if (!user.isRoleProvider()) { // ��Ӫ�̵�ʱ���ܹ�ִ��CP��ѯ��
			if (getCp() != null) {
				if (getCp().getId() != 0) {
					HibernateExpression cpE = new CompareExpression("cpId",
							getCp().getId(), CompareType.Equal);
					hibernateExpressions.add(cpE);
				}
			}
		}
		// if(!ParameterCheck.isNullOrEmpty(getResourceId())){
		// HibernateExpression resourceId = new
		// CompareExpression("id",getResourceId(),CompareType.Equal);
		// hibernateExpressions.add(resourceId);
		// }
		return hibernateExpressions;
	}

	public void search() {
		if (getSortId() != null) {
			searchByHQL(getSortId());
		}
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("resourceType");
		nameC.setValue(getResourceType());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("authorId");
		nameC.setValue(getAuthorId());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("status");
		nameC.setValue(getStatus());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("creator");
		nameC.setValue(getCreator());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("cp");
		nameC.setValue(getCp());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("otherPageToHere");
		nameC.setValue(getOtherPageToHere());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("sortId");
		nameC.setValue(getSortId());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("authorListId");
		nameC.setValue(getAuthorListId());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("keyname");
		nameC.setValue(getKeyname());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("isFinished");
		nameC.setValue(getIsFinished());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("initialLetter");
		nameC.setValue(getInitialLetter());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("expNum");
		nameC.setValue(getExpNum());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("healthNum");
		nameC.setValue(getHealthNum());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("isFirstpublish");
		nameC.setValue(getIsFirstpublish());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("isOut");
		nameC.setValue(getIsOut());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("publisher");
		nameC.setValue(getPublisher());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("isSearchTop");
		nameC.setValue(getIsSearchTop());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("isDivision");
		nameC.setValue(getIsDivision());
		searchConditions.add(nameC);

		return searchConditions;
	}

	public IPropertySelectionModel getBookStatus() {
		return new MapPropertySelectModel(Constants.getReferenStatus());
	}

	public abstract int getResourceCount();

	public abstract void setResourceCount(int resourceCount);

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				int count = 0;
				Integer creatorId = null;
				if (getCreator() != null)
					creatorId = getCreator().getId();
				UserImpl user = (UserImpl) getUser();
				Integer cpid = null;
				if (user.isRoleProvider()) { // ������
					// cpsp,ֻ�ܹ����Լ��ģ�����û��cpsp��ѯ��ʾ��
					cpid = user.getProvider().getId();
				} else {
					if (getCp() != null) // ��ʾִ����spcp��ѯ
						cpid = getCp().getId();
				}
				// �ж�����д���1.д�� ��2.ѡ��
				Integer authorId = 0;
				if (getAuthorId() != null && !getAuthorId().equals(0))
					authorId = getAuthorId();
				else {
					if (getAuthorListId() != null
							&& !getAuthorListId().equals(0))
						authorId = getAuthorListId();
				}
				if (getOtherPageToHere()) {
					if (getKeyname() == 1
							&& !ParameterCheck.isNullOrEmpty(getName())) { // ����id�Ĳ�ѯ��ʱ�򣬷����HQLû��֧�֡��������������
						count = getResourceService().getResourceResultCount(
								getSearchExpressions()).intValue();
					} else {

						count = getResourceService()
								.getResourceResultCountByHQL(cpid,
										getResourceType(), getName(),
										authorId.toString(), getSortId(),
										getStatus(), creatorId, getRKeyWord(),
										getIsFinished(), getPublisher(),
										getInitialLetter(), getExpNum(),
										getIsFirstpublish(), getIsOut(),
										getHealthNum(), getIsSearchTop(),
										getIsDivision()).intValue();
					}
				} else {
					if (getSortId() != null && !getSortId().equals(0)) {
						// UserImpl user = (UserImpl) getUser();

						if (getKeyname() == 1
								&& !ParameterCheck.isNullOrEmpty(getName())) { // ����id�Ĳ�ѯ��ʱ�򣬷����HQLû��֧�֡��������������
							count = getResourceService()
									.getResourceResultCount(
											getSearchExpressions()).intValue();
						} else {
							count = getResourceService()
									.getResourceResultCountByHQL(cpid,
											getResourceType(), getName(),
											authorId.toString(), getSortId(),
											getStatus(), creatorId,
											getRKeyWord(), getIsFinished(),
											getPublisher(), getInitialLetter(),
											getExpNum(), getIsFirstpublish(),
											getIsOut(), getHealthNum(),
											getIsSearchTop(), getIsDivision())
									.intValue();
						}
					} else {
						count = getResourceService().getResourceResultCount(
								getSearchExpressions()).intValue();
					}
				}
				setResourceCount(count);
				return count;
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				Integer creatorId = null;
				if (getCreator() != null)
					creatorId = getCreator().getId();
				// if (getSortId() != null) {
				UserImpl user = (UserImpl) getUser();
				Integer cpid = null;
				if (user.isRoleProvider()) { // ������
					// cpsp,ֻ�ܹ����Լ��ģ�����û��cpsp��ѯ��ʾ��
					cpid = user.getProvider().getId();
				} else {
					if (getCp() != null) // ��ʾִ����spcp��ѯ
						cpid = getCp().getId();
				}

				// �ж�����д���1.д�� ��2.ѡ��
				Integer authorId = 0;
				if (getAuthorId() != null && !getAuthorId().equals(0))
					authorId = getAuthorId();
				else {
					if (getAuthorListId() != null
							&& !getAuthorListId().equals(0))
						authorId = getAuthorListId();
				}

				if (getOtherPageToHere()) {
					// UserImpl user = (UserImpl) getUser();
					if (getKeyname() == 1
							&& !ParameterCheck.isNullOrEmpty(getName())) { // ����id�Ĳ�ѯ��ʱ�򣬷����HQLû��֧�֡��������������
						return getResourceService().findResourceBy(pageNo,
								nPageSize, "id", false, getSearchExpressions())
								.iterator();
					} else {
						return getResourceService().findResourceByHQL(cpid,
								getResourceType(), getName(),
								authorId.toString(), getSortId(), getStatus(),
								creatorId, getRKeyWord(), getIsFinished(),
								getPublisher(), getInitialLetter(),
								getExpNum(), getIsFirstpublish(), getIsOut(),
								getHealthNum(), getIsSearchTop(),
								getIsDivision(), pageNo, nPageSize).iterator();
					}
				} else {
					if (getSortId() != null && !getSortId().equals(0)) {
						// UserImpl user = (UserImpl) getUser();
						if (getKeyname() == 1
								&& !ParameterCheck.isNullOrEmpty(getName())) { // ����id�Ĳ�ѯ��ʱ�򣬷����HQLû��֧�֡��������������
							return getResourceService().findResourceBy(pageNo,
									nPageSize, "id", false,
									getSearchExpressions()).iterator();
						} else {
							return getResourceService().findResourceByHQL(cpid,
									getResourceType(), getName(),
									authorId.toString(), getSortId(),
									getStatus(), creatorId, getRKeyWord(),
									getIsFinished(), getPublisher(),
									getInitialLetter(), getExpNum(),
									getIsFirstpublish(), getIsOut(),
									getHealthNum(), getIsSearchTop(),
									getIsDivision(), pageNo, nPageSize)
									.iterator();
						}
					}
					return getResourceService().findResourceBy(pageNo,
							nPageSize, "id", false, getSearchExpressions())
							.iterator();
				}

			}
		};
	}

	/**
	 * ��õ�ǰ��Ʒ
	 * 
	 * @return
	 */
	public abstract Object getCurrentProduct();

	public abstract int getStatusValue();

	public abstract void setStatusValue(int statusValue);

	public void onChangeStatus(IRequestCycle cycle) {
		Set setSelectedObjects = getSelectedEbook();
		int setSize = setSelectedObjects.size();
		String err = "";
		ValidationDelegate delegate = getDelegate();
		if (setSize == 0) {
			err = "�����ٵ�ѡ��һ��";
		} else {
			for (Object obj : setSelectedObjects) {
				try {
					getResourceService().auditResource((ResourceAll) obj,
							getStatusValue(),(UserImpl)getUser());
				} catch (Exception e) {
					err += e.getMessage();
					e.printStackTrace();
				}
			}

		}
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
		}

		// clear selection
		setSelectedEbook(new HashSet());
		getCallbackStack().popPreviousCallback();
	}

	public abstract int getTopValue();

	public abstract void setTopValue(int topValue);

	public void onChangeTop(IRequestCycle cycle) {
		Set setSelectedObjects = getSelectedEbook();
		int setSize = setSelectedObjects.size();
		String err = "";
		ValidationDelegate delegate = getDelegate();
		if (setSize == 0) {
			err = "�����ٵ�ѡ��һ��";
		} else {
			for (Object obj : setSelectedObjects) {
				try {
					getResourceService().auditResourceTop((ResourceAll) obj,
							getTopValue());
				} catch (Exception e) {
					err += e.getMessage();
				}
			}

		}
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
		}

		// clear selection
		setSelectedEbook(new HashSet());
		getCallbackStack().popPreviousCallback();

	}

	public IPage searchByHQL(Integer sortid) {
		this.setAuthorId(getAuthorId());
		this.setName(getName());
		this.setSortId(sortid);
		this.setResourceType(getResourceType());
		this.setStatus(getStatus());
		this.setCreator(getCreator());
		this.setCp(getCp());
		this.setOtherPageToHere(getOtherPageToHere());
		return this;
	}

	public IPage chooseResources() {
		String err = "";
		SourceToPackPage page = getSourceToPackPage();
		if (getSelectedEbook().size() == 0)
			err = "�����ٵ�ѡ��һ��";
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			return null;
		} else {
			// ���˵���ͣ�Ĳ�Ʒ��ֻѡ�������õĲ�Ʒ ����״̬��0
			Set<ResourceAll> resourceSet = getSelectedEbook();
			Set<ResourceAll> newSet = new HashSet<ResourceAll>();
			for (ResourceAll resource : resourceSet) {
				if (resource.getStatus() == 0)
					newSet.add(resource);
			}
			page.setResources(newSet);
			if (getPack() != null) {
				page.setPack(getPack());
				page.setPricepackName(getPack().getName());
			}
			setSelectedEbook(new HashSet());
			return page;
		}
	}

	@InjectPage("resource/BatchChangeResourceTypePage")
	public abstract BatchChangeResourceTypePage getBatchChangeResourceTypePage();

	public IPage changeResourceType() {
		String err = "";
		BatchChangeResourceTypePage page = getBatchChangeResourceTypePage();
		if (getSelectedEbook().size() == 0)
			err = "�����ٵ�ѡ��һ��";
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			return null;
		} else {
			page.setResources(getSelectedEbook());

			ResourceAll resource = (ResourceAll) getCurrentProduct();
			if (resource instanceof Ebook)
				page.setResourceType(ResourceAll.RESOURCE_TYPE_BOOK);
			if (resource instanceof Magazine)
				page.setResourceType(ResourceAll.RESOURCE_TYPE_MAGAZINE);
			if (resource instanceof NewsPapers)
				page.setResourceType(ResourceAll.RESOURCE_TYPE_NEWSPAPER);
			if (resource instanceof Comics)
				page.setResourceType(ResourceAll.RESOURCE_TYPE_COMICS);
			if (resource instanceof Video)
				page.setResourceType(ResourceAll.RESOURCE_TYPE_VIDEO);
			if (resource instanceof Infomation)
				page.setResourceType(ResourceAll.RESOURCE_TYPE_INFO);
			if (resource instanceof Application)
				page.setResourceType(ResourceAll.RESOURCE_TYPE_APPLICATION);
			setSelectedEbook(new HashSet());
			return page;
		}
	}

	@InjectObject("spring:keywordFilter")
	public abstract KeyWordFilter getKeyWordFilter();

	public List getResourceChapter(String resourceId) {
		return getResourceService().getResourceChapter(
				getNewChapter(Integer.parseInt(resourceId.substring(0, 1))),
				resourceId);
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

	@InjectPage("system/SelectKeyWordTypePage")
	public abstract SelectKeyWordTypePage getSelectKeyWordTypePage();

	public IPage checkKeyWord() {
		if (getPack() != null) {
			getSelectKeyWordTypePage().setPack(getPack());
		}
		getSelectKeyWordTypePage().setResourceSet(getSelectedEbook());
		setSelectedEbook(new HashSet());
		return getSelectKeyWordTypePage();
	}

	public ITableColumn getPublishDate() {
		return new SimpleTableColumn("publishdate", "��������",
				new ITableColumnEvaluator() {
					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceAll o1 = (ResourceAll) objRow;
						SimpleDateFormat dateFormate = new SimpleDateFormat(
								"yyyy-MM-dd");
						if(o1.getPublishTime() != null)
							return dateFormate.format(o1.getPublishTime());
						return "";
					}

				}, false);
	}

	public String getPreAddress() {
		ResourceAll resource = (ResourceAll) getCurrentProduct();
		String imgPath = "";
		if (resource instanceof Ebook) { // ͼ��
			Ebook book = (Ebook) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					book.getBookPic());
		}
		if (resource instanceof Magazine) { // ��־
			Magazine magazine = (Magazine) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					magazine.getImage());
		}
		if (resource instanceof NewsPapers) { // ��ֽ
			NewsPapers newsPapers = (NewsPapers) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					newsPapers.getImage());
		}
		if (resource instanceof Comics) { // ����
			Comics comics = (Comics) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					comics.getImage());
		}
		if (resource instanceof Video) { // video
			Video video = (Video) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					video.getImage());
		}
		if (resource instanceof Infomation) { // video
			Infomation infomation = (Infomation) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					infomation.getImage());
		}
		if (resource instanceof Application) { // video
			Application app = (Application) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					app.getImage());
		}
		return imgPath;
	}

	public ITableColumn getDisplayCreator() {
		return new SimpleTableColumn("creator", "�޸���",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceAll o1 = (ResourceAll) objRow;

						UserImpl user = (UserImpl) getUserService().getObject(
								UserImpl.class, o1.getModifierId());
						if (user == null) {
							return "";
						} else {
							return user.getName();
						}

					}

				}, false);

	}

	public ITableColumn getProvider() {
		return new SimpleTableColumn("cpId", "������",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceAll o1 = (ResourceAll) objRow;
						Provider provider = getPartnerService().getProvider(
								o1.getCpId());
						if (provider != null)
							return provider.getIntro();
						else
							return "��ʧ������";

					}

				}, false);

	}

	public String getTableColumns() {

		return getSystemService().getVariables(
				"table:resource_type_" + getResourceType()).getValue();
	}

	public IPropertySelectionModel getResourceTypeList() {
		Map<String, Integer> types = new OrderedMap<String, Integer>();
		types.put("ͼ��", ResourceType.TYPE_BOOK);
		types.put("��ֽ", ResourceType.TYPE_NEWSPAPERS);
		types.put("��־", ResourceType.TYPE_MAGAZINE);
		types.put("����", ResourceType.TYPE_COMICS);
		types.put("��Ƶ", ResourceType.TYPE_VIDEO);
		types.put("��Ѷ", ResourceType.TYPE_INFO);
		types.put("���", ResourceType.TYPE_APPLICATION);
		return new MapPropertySelectModel(types, false, "");
	}

	public IPropertySelectionModel getAuthorList() {
		List<ResourceAuthor> resourceauthor = getResourceService()
				.findResourceAuthorBy(1, Integer.MAX_VALUE, "initialLetter",
						false, new ArrayList<HibernateExpression>());
		Map<String, Integer> authors = new OrderedMap<String, Integer>();
		authors.put("ȫ��", 0);
		for (ResourceAuthor author : resourceauthor) {
			authors.put(author.getName(), author.getId());
		}
		return new MapPropertySelectModel(authors, false, "ȫ��");
	}

	public IPropertySelectionModel getSortList() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression typeF = new CompareExpression("showType",
				getResourceType(), CompareType.Equal);
		hibernateExpressions.add(typeF);
		List<ResourceType> sortTypes = getResourceService()
				.findResourceTypeBymemcached(getResourceType() + "", 1,
						Integer.MAX_VALUE, "id", false, hibernateExpressions);
		Map<String, Integer> sorts = new OrderedMap<String, Integer>();
		sorts.put("ȫ��", 0);
		for (ResourceType sort : sortTypes) {
			sorts.put(sort.getName(), sort.getId());
		}
		return new MapPropertySelectModel(sorts, false, "ȫ��");
	}

	public IPropertySelectionModel getStatusList() {
		Map<String, Integer> statuslist = new OrderedMap<String, Integer>();
		statuslist.put("ȫ��", 9);
		statuslist.put("����", 0);
		statuslist.put("����", 1);
		statuslist.put("��ͣ", 3);
		statuslist.put("����", 4);
		statuslist.put("���", 5);
		// ���ڵ���Դ״̬(���� 0,���� 1,���� 2,��ͣ 3, ���� 4 , ��� 5 )
		return new MapPropertySelectModel(statuslist, false, "ȫ��");
	}

	public IPropertySelectionModel getSearchScope() {
		Map<String, Integer> Scopelist = new OrderedMap<String, Integer>();
		Scopelist.put("����", 0);
		Scopelist.put("���", 1);
		Scopelist.put("����", 2);
		Scopelist.put("�ؼ���", 3);
		return new MapPropertySelectModel(Scopelist, false, "����");
	}

	public IPropertySelectionModel getInitialLetterList() { // ����ĸ
		return new MapPropertySelectModel(Constants.getInitialLetter(), true,
				"");
	}

	public IPropertySelectionModel getExpNumList() { // �Ƽ�ָ��
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("ȫ��", 0);
		map.put("һ��", 1);
		map.put("����", 2);
		map.put("����", 3);
		map.put("����", 4);
		map.put("����", 5);
		return new MapPropertySelectModel(map, false, "ȫ��");
	}

	public IPropertySelectionModel getIsFirstpublishList() { // �Ƿ�
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("ȫ��", 0);
		map.put("��", 1);
		map.put("��", 2);
		return new MapPropertySelectModel(map, false, "ȫ��");
	}

	public IPropertySelectionModel getIsSearchTopList() {
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("ȫ��", -1);
		map.put("��", 1);
		map.put("��", 0);
		return new MapPropertySelectModel(map, false, "ȫ��");
	}

	public IPropertySelectionModel getIsFinishedList() {
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("ȫ��", 0);
		map.put("ȫ��", 1);
		map.put("����", 2);
		return new MapPropertySelectModel(map, false, "ȫ��");
	}

	public boolean isShowDifferent() {
		if (getKeyname() == null)
			return true;
		else {
			if (getKeyname() == 2)
				return false;
			else
				return true;
		}
	}

	public IPropertySelectionModel getuserList() {
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getUserService().getAll(UserImpl.class), UserImpl.class,
				"getChName", "getId", true, "ȫ��");
		return parentProper;
	}

	public IPropertySelectionModel getcpList() {
		HibernateExpression ex = new CompareExpression("status", 5,
				CompareType.NotEqual);
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(ex);
		List<Provider> providers = getPartnerService().findProvider(1,
				Integer.MAX_VALUE, "id", false, expressions);

		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				providers, Provider.class, "getIntro", "getId", true, "ȫ��");
		return parentProper;
	}

	public IPropertySelectionModel getpublisherList() {
		Map<String, Integer> publisherlist = new OrderedMap<String, Integer>();
		//
		//
		return new MapPropertySelectModel(publisherlist, false, "ȫ��");
	}

	public void pageBeginRender(PageEvent event) {
		if (getResourceType() == null) {
			setResourceType(ResourceType.TYPE_VIDEO);
		}
		if (getAuthorId() == null)
			setAuthorId(0);
		if (getAuthorId() == null)
			setAuthorId(0);
		if (getStatus() == null)
			setStatus(9);
		if (getPublisherId() == null)
			setPublisherId(0);
		if (getKeyname() == null)
			setKeyname(0);
		if (getRKeyWord() == null)
			setRKeyWord("");
	}

	public ITableColumn getSort() {
		return new SimpleTableColumn("sort", "���", new ITableColumnEvaluator() {

			private static final long serialVersionUID = 625300745851970L;

			public Object getColumnValue(ITableColumn objColumn, Object objRow) {
				ResourceAll rp = (ResourceAll) objRow;
				StringBuffer type = new StringBuffer();
				// ������ԴID��ѯ��Դ������
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("rid", rp
						.getId(), CompareType.Equal);
				expressions.add(ex);
				List<ResourceResType> list = getResourceService()
						.findResourceResTypeBy(1, Integer.MAX_VALUE, "rid",
								false, expressions);
				for (Iterator it = list.iterator(); it.hasNext();) {
					ResourceResType rrt = (ResourceResType) it.next();
					ResourceType rt = getResourceService().getResourceType(
							rrt.getResTypeId());
					if(rt == null){
						continue;
					}
					type.append(rt.getName());
					type.append(";");
				}
				// ȥ�����һ���ֺ�
				if (list.size() > 0 && type.length() > 2) {
					
					return type.toString().substring(0, (type.length() - 1));
				} else {
					return "";
				}
			}

		}, false);

	}

	public ITableColumn getKeywords() {
		return new SimpleTableColumn("rKeyword", "�ؼ���",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceAll rp = (ResourceAll) objRow;
						if (rp != null) {
							return rp.getRKeyword();
						} else {
							return "";
						}
					}

				}, false);

	}

	@InjectPage("resource/EditAuthorPage")
	public abstract EditAuthorPage getEditAuthorPage();

	public IPage onEditAuthor(Object obj) {
		ResourceAll resource = (ResourceAll) obj;
		Integer[] authorIds = resource.getAuthorIds();
		ResourceAuthor rAuthor = getResourceService().getResourceAuthorById(
				authorIds[0]);
		getEditAuthorPage().setModel(rAuthor);
		return getEditAuthorPage();
	}

	public static String getExternalURL(IEngineService service,
			String pageName, Object[] params) {

		ExternalServiceParameter parameter = new ExternalServiceParameter(
				pageName, params);

		ILink link = service.getLink(false, parameter);
		String URL = link.getURL();
		// logger.info("URL:" + URL);
		return URL;
	}

	public String getAuthorNames() {
		String url = "";
		ResourceAll rp = (ResourceAll) getCurrentProduct();
		String author = ";";
		int i = 0;
		Integer[] authorIds = rp.getAuthorIds();
		for (Integer authorId : authorIds) {
			ResourceAuthor rAuthor = getResourceService()
					.getResourceAuthorById(authorId);
			String URL = getExternalURL(getExternalService(),
					"resource/EditAuthorPage", new Object[] { authorId });

			if (rAuthor != null) {
				author = rAuthor.getPenName();

				if (getSecurityComponent().hasRole(new String[] { "author" })) {
					url += "<a href=\"javascript:cuspopup_window('" + URL
							+ "');\">" + author + "</a>";
				} else {
					url += author;
				}

				if (++i < authorIds.length) {
					url += ";";
				}
			}
		}
		return url;
	}

	public ITableColumn getAuthorname() {
		return new SimpleTableColumn("authorname", "����",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceAll rp = (ResourceAll) objRow;
						String author = ";";
						int i = 0;
						Integer[] authorIds = rp.getAuthorIds();
						for (Integer authorId : authorIds) {
							ResourceAuthor rAuthor = getResourceService()
									.getResourceAuthorById(authorId);
							if (rAuthor != null) {
								author += rAuthor.getPenName();
								if (++i < authorIds.length) {
									author += ";";
								}
							}
						}
						return author.substring(1);

					}

				}, false);

	}

	public ITableColumn getProvidername() {
		return new SimpleTableColumn("providername", "CPID",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceAll rp = (ResourceAll) objRow;
						if (rp.getCpId() != null) {
							Provider provider = getPartnerService()
									.getProvider(rp.getCpId());

							return provider.getProviderId();
						} else {
							return "";
						}

					}

				}, false);

	}

	public IPage onEbookReCheck(Object obj) {
		ResourceAll resourceAll = (ResourceAll) obj;
		ShowReCheckPage showCheckPage = getShowReCheckPage();
		showCheckPage.setResourceId(resourceAll.getId());
		return showCheckPage;
	}

	public IPage onEbookDetail(Object obj) {
		getShowEbookDetialPage().setCurrentObject(obj);
		return getShowEbookDetialPage();
	}

	@InjectPage("resource/ShowEbookDetialPage")
	public abstract ShowEbookDetialPage getShowEbookDetialPage();

	public IAutocompleteModel getAutocompleterModel() {
		List<ResourceAuthor> resourceauthor = getResourceService()
				.findResourceAuthorBymemcached("resourceAll", 1,
						Integer.MAX_VALUE, "initialLetter", true,
						new ArrayList<HibernateExpression>());

		return new DefaultAutocompleteModel(resourceauthor, "id", "penName");
	}

	public IPropertySelectionModel getAutoList() {
		Map<String, Integer> types = new OrderedMap<String, Integer>();
		List<ResourceAuthor> resourceauthor = getResourceService()
				.findResourceAuthorBymemcached("resourceAll", 1,
						Integer.MAX_VALUE, "initialLetter", true,
						new ArrayList<HibernateExpression>());
		types.put("ȫ��", 0);
		for (ResourceAuthor author : resourceauthor)
			types.put(author.getPenName(), author.getId());
		return new MapPropertySelectModel(types, false, "ȫ��");
	}

	public abstract Integer getAuthorListId();

	public abstract void setAuthorListId(Integer authorListId);

	public ResourceAuthor getResourceAuthor() {
		if (getAuthorId() != null && getAuthorId() > 0) {
			return getResourceService().getResourceAuthorById(getAuthorId());
		}
		return null;
	}

	public void setResourceAuthor(ResourceAuthor author) {
		if (author != null) {
			setAuthorId(author.getId());
		} else {
			setAuthorId(0);
		}
	}

	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();

	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();

	public String getShowPackMsg() {
		// <img src="../img/arrow-down-02.png" align="absmiddle"
		// onMouseOver="this.style.background='#FFFFFF';"
		// onMouseOut="this.style.background='#F7F7F7';"
		// title="ognl:showPackMsg"/>
		ResourceAll resource = (ResourceAll) getCurrentProduct();
		String message = "";
		Set<ResourcePack> set = getResourcePackService()
				.findResourcePackByResource(resource.getId());

		if (set != null && set.size() > 0) {
			message += "<img src='../img/icon_09.png' align='absmiddle' onMouseOver=\"this.style.background='#FFFFFF';\"   onMouseOut=\"this.style.background='#F7F7F7';\" title=\"�������۰���</br>";
			for (ResourcePack pack : set) {
				// ���۰�����.�ƴ�1����Ŀ���£�VIP��2����Ŀ���£����ݿ��ƣ�3����Ŀ���£����棩4�����5
				String feeNames = "";
				if (pack.getType() == 1) {// �ƴ�
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression(
							"resourceId", resource.getId(), CompareType.Equal);
					expressions.add(ex);
					HibernateExpression ex1 = new CompareExpression("pack",
							pack, CompareType.Equal);
					expressions.add(ex1);
					List<ResourcePackReleation> rpr = getResourcePackService()
							.getResourceFromEpack(1, Integer.MAX_VALUE, "id",
									false, expressions);
					if (rpr != null && rpr.size() > 0) {
						ResourcePackReleation packReleation = rpr.get(0);
						if (packReleation != null) {
							if (packReleation.getFeeId() != null)
								;
							Fee fee = getFeeService().getFee(
									packReleation.getFeeId());
							if (fee != null)
								feeNames += "����(" + fee.getCode() + "Ԫ)";
						}
					}
				} else if (pack.getType() == 2) { // vip
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression(
							"resourceId", resource.getId(), CompareType.Equal);
					expressions.add(ex);
					HibernateExpression ex1 = new CompareExpression("pack",
							pack, CompareType.Equal);
					expressions.add(ex1);
					feeNames += "���۸�(" + pack.getCode() + "Ԫ);";
					;
					// vip
					List<ResourcePackReleation> rpr = getResourcePackService()
							.getResourceFromEpack(1, Integer.MAX_VALUE, "id",
									false, expressions);
					if (rpr != null && rpr.size() > 0) {
						ResourcePackReleation packReleation = rpr.get(0);
						if (packReleation != null) {
							if (packReleation.getFeeId() != null)
								;
							Fee fee = getFeeService().getFee(
									packReleation.getFeeId());
							if (fee != null)
								feeNames += "VIP����(" + fee.getCode() + "Ԫ)";
						}
					}

					feeNames += "";
				} else if (pack.getType() == 3) { // ���ݿ���
					feeNames += pack.getCode() + "Ԫ";
				} else if (pack.getType() == 4) { // ����
					feeNames += pack.getCode() + "Ԫ";
				} else if (pack.getType() == 5) { // ���
					feeNames += "���";
				}

				message += "ID:" + pack.getId()
						+ "&nbsp;<font color='red'>����</font>&nbsp;NAME:"
						+ pack.getName()
						+ "&nbsp;<font color='red'>����</font>&nbsp;�۸�:"
						+ feeNames + ";</br>";
			}
			message += "\"/>";
		} else {
			message += "<img src='../img/iconWarning.gif' align='absmiddle'";
		}
		return message;
	}

	// @InjectPage("resource/DownloadResourceCSV")
	// public abstract DownloadResourceCSV getDownloadResourceCSV();

	protected String creatCSV(Integer userId, Integer resourceType,
			Set<ResourceAll> set) {
		try {
			// �õ���ǰ�û����ص�·��·�� �û�ID+�ļ�����
			String csvName = "";
			if (resourceType == 1)
				csvName = "book.csv";
			else if (resourceType == 2)
				csvName = "newsPapers.csv";
			else if (resourceType == 3)
				csvName = "magazine.csv";
			else if (resourceType == 4)
				csvName = "comics.csv";
			else
				csvName = "book.csv";
			String fileDir = getSystemService().getVariables("userCVS_dir")
					.getValue();
			File fielDir1 = new File(fileDir);
			if (!fielDir1.exists())
				fielDir1.mkdirs();
			File userFile = new File(fileDir + userId);
			if (!userFile.exists())
				userFile.mkdirs();

			fileDir += userId + File.separator + csvName;
			File file = new File(fileDir);
			if (file.exists())
				file.delete();
			FileWriter fw = new FileWriter(fileDir);
			fw.write("���,����,����,��������,�ؼ���,����ĸ,CPID\r\n");
			for (ResourceAll resource : set) {
				String message = "";
				message += resource.getId() + ",";
				message += resource.getName() + ",";
				// ResourceAuthor authore =
				// getResourceService().getResourceAuthorById(resource.getAuthorId());
				Integer[] authorList = resource.getAuthorIds();// �õ���������
				String authorStr = "";
				if (authorList.length > 0) {
					for (int i = 0; i < authorList.length; i++) {
						ResourceAuthor author = getResourceService()
								.getResourceAuthorById(authorList[i]);
						authorStr += author.getPenName() + "/";
					}
				}
				if ("".equals(authorStr))
					message += "���߶�ʧ,";
				else {
					authorStr = authorStr.substring(0, authorStr
							.lastIndexOf("/"));
					message += authorStr + ",";
				}
				// ******************************************************************
				// ���ݵ��ڵ���ԴID �õ���Դ���������ϵ��Ȼ��õ��Ǹ����������
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("rid", resource
						.getId(), CompareType.Equal);
				expressions.add(ex);
				List<ResourceResType> resTypeList = getResourceService()
						.findResourceResTypeBy(1, Integer.MAX_VALUE, "id",
								true, expressions);
				String typeStr = "";
				if (resTypeList != null && resTypeList.size() > 0) {
					for (ResourceResType res : resTypeList) {
						ResourceType type = getResourceService()
								.getResourceType(res.getResTypeId());
						typeStr += type.getId() + "^";
					}
				}
				if ("".equals(typeStr))
					message += "0,";
				else {
					typeStr = typeStr.substring(0, typeStr.lastIndexOf("^"));
					message += typeStr + ",";
				}
				if (resource.getRKeyword() == null) {
					message += "�޹ؼ���,";
				} else
					message += resource.getRKeyword() + ",";
				message += resource.getInitialLetter() + ",";

				/*
				 * // // ����CPID�õ�cp Provider provider =
				 * getPartnerService().getProvider( resource.getCpId()); if
				 * (provider != null) { message += provider.getIntro() + "\r\n";
				 * } else { message += "��ʧCP\r\n"; }
				 */
				message += resource.getCpId() + "\r\n";
				fw.write(message);
			}
			fw.close();
			return fileDir;
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
		return getSystemService().getVariables("userCVS_dir").getValue();
	}

	@InjectObject("service:tapestry.globals.HttpServletResponse")
	public abstract HttpServletResponse getServletResponse();

	public void downloadCSV(IRequestCycle cycle) {
		String err = "";
		if (getSelectedEbook().size() == 0)
			err = "�����ٵ�ѡ��һ��";
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);

		} else {

			// ����CSV
			UserImpl user = (UserImpl) getUser();
			Integer resourceType = getResourceType();
			String fileDir = this.creatCSV(user.getId(), resourceType,
					getSelectedEbook());

			setSelectedEbook(new HashSet());
			// path��ָ�����ص��ļ���·����
			String contextPath = getServletRequest().getContextPath();
			throw new RedirectException("http://"
					+ PageUtil.getDomainName(getServletRequest()
							.getRequestURL().toString()) + contextPath
					+ "/DownLoadPage.jsp?file=" + fileDir);

		}
	}
}
