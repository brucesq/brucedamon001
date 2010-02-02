package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
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

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.service.resource.ResourceService;

@Restrict(roles = { "resource" }, mode = Restrict.Mode.ROLE)
public abstract class ShowTomePage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("resource/EditTomePage")
	public abstract EditTomePage getEditTomePage();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	public abstract ResourceAll getResourceAll();

	public abstract void setResourceAll(ResourceAll resourceAll);

	public IPage onEdit(EbookTome tome) {
		getEditTomePage().setModel(tome);
		return getEditTomePage();
	}

	public IPage editChapter(ResourceAll resourceAll) {
		getEditTomePage().setResourceAll(resourceAll);
		return getEditTomePage();

	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		EbookTome tome = (EbookTome) getCurrentChapter();
		Set selectedChapter = getSelectedChapters();
		// 选择了用户
		if (bSelected) {
			selectedChapter.add(tome);
		} else {
			selectedChapter.remove(tome);
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
	 * 获得当前卷
	 * 
	 * @return
	 */
	public abstract Object getCurrentChapter();

	@Override
	protected void delete(Object object) {
		try {
			getResourceService().deleteEbookTome((EbookTome) object);

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedChapters()) {
			delete(obj);
		}
		setSelectedChapters(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);

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
				return getResourceService().getEbookTomeByResourceId(
						getResourceAll().getId()).size();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getResourceService().getEbookTomeByResourceId(
						getResourceAll().getId()).iterator();
			}
		};
	}
}
