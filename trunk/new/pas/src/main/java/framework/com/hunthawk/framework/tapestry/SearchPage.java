/**
 * 
 */
package com.hunthawk.framework.tapestry;

import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;

import com.hunthawk.framework.tapestry.callback.SearchCallback;
import com.hunthawk.reader.page.DetailPage;

/**
 * @author sunquanzhi
 * 
 */
public abstract class SearchPage extends CallbackPage {

	public void invokeSearchCondition(List<SearchCondition> conditions) {
		if (conditions == null)
			return;
		for (SearchCondition condition : conditions) {
			condition.invoke(this);
		}
	}

	/**
	 * <p>
	 * 需要实现，查询条件集合
	 * </p>
	 * 
	 * @return
	 */
	public abstract List<SearchCondition> getSearchConditions();

	@Override
	public void pushCallback() {
		getCallbackStack().push(
				new SearchCallback(getPageName(), getSearchConditions()));
	}

	/**
	 * <p>
	 * 需要实现,实际删除操作
	 * </p>
	 * 
	 * @param object
	 */
	protected abstract void delete(Object object);

	public void onDelete(IRequestCycle cycle, Object object) {
		delete(object);
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		if (callback != null)
			callback.performCallback(cycle);
	}

	/**
	 * 设置初始页
	 * 
	 * @param initialPage
	 */
	public void setInitialPage(int initialPage) {
		this.getTableView().getTableModel().getPagingState().setCurrentPage(
				initialPage);
	}

	@InjectComponent("table")
	public abstract TableView getTableView();

	/**
	 * <p>
	 * 需要实现，获得列表集
	 * </p>
	 * 
	 * @return
	 */
	public abstract IBasicTableModel getTableModel();
	
	public IPage onDetail(Object obj){
		getDetailPage().setCurrentObject(obj);
		return getDetailPage();
	}

	@InjectPage("DetailPage")
	public abstract DetailPage getDetailPage();
	
	public Integer getTablePageSize(){
		return 50;
	}
}
