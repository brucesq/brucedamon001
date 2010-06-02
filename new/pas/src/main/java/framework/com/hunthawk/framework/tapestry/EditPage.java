/**
 * 
 */
package com.hunthawk.framework.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.callback.ICallback;

import com.hunthawk.framework.tapestry.callback.EditCallback;
import com.hunthawk.framework.tapestry.callback.SearchCallback;

/**
 * @author sunquanzhi
 * 
 */
public abstract class EditPage extends CallbackPage {

	public void cancel(IRequestCycle cycle) {
		ICallback callback = (ICallback) getCallbackStack()
				.popPreviousCallback();
		if (callback == null) {
			defaultReturn(cycle);
			return;
		}
		if (callback != null && callback instanceof SearchCallback) {
			searchReturn(false,(SearchCallback)callback,cycle);
			return;
		}else if(callback != null){
			SearchCallback searchCallback = getNextSearchCallback();
			if (searchCallback == null){
				defaultReturn(cycle);
				return;
			}
			searchReturn(false,searchCallback,cycle);
			return;
		}
		
	}

	private void defaultReturn(IRequestCycle cycle) {
		String page = this.getPageName();
		String searchPage = page.replaceAll("Edit", "Show");
		if (cycle.getPage(searchPage) != null) {
			cycle.activate(searchPage);
		} else {
			cycle.activate(HomePage);
		}
	}
	
	private void searchReturn(boolean isModelNew ,SearchCallback callback,IRequestCycle cycle){
		
		String page = this.getPageName();
		String searchPage = page.replaceAll("Edit", "Show");
		if(searchPage.equals(callback.getPageName())){
			callback.setFirstPage(isModelNew);
			callback.performCallback(cycle);
		}else{
			SearchCallback searchCallback = getNextSearchCallback();
			if (searchCallback == null){
				defaultReturn(cycle);
				return;
			}
			searchReturn(isModelNew,(SearchCallback)callback,cycle);
			
		}
		
	}
	
	private SearchCallback getNextSearchCallback(){
		ICallback nextCallback = getCallbackStack().popPreviousCallback();
		if(nextCallback == null)
			return null;
		if( nextCallback instanceof SearchCallback){
			return  (SearchCallback)nextCallback;
		}
		return getNextSearchCallback();
	}

	public void saveAndReturn(IRequestCycle cycle) {
		if (save()) {
			ICallback currentCallback = (ICallback) getCallbackStack()
					.getCurrentCallback();
			if (currentCallback instanceof EditCallback) {
				boolean isModelNew = ((EditCallback) currentCallback)
						.isModelNew();
				ICallback callback = getCallbackStack().popPreviousCallback();
				
				if (callback != null && callback instanceof SearchCallback) {
					searchReturn(isModelNew,(SearchCallback)callback,cycle);
					return;
				}else if(callback != null){
					SearchCallback searchCallback = getNextSearchCallback();
					if (searchCallback == null){
						defaultReturn(cycle);
						return;
					}
					searchReturn(isModelNew,searchCallback,cycle);
					return;
				}

			}
			defaultReturn(cycle);
			return;

		} else {
			setCancelPullback(true);
		}
	}

	// 保存或更新对象
	protected boolean save() {
		return persist(getModel());

	}

	/**
	 * <p>
	 * 持久化对象,需要子类实现该方法
	 * </p>
	 * 
	 * @param object
	 * @return
	 */
	protected abstract boolean persist(Object object);

	protected boolean isModelNew(Object model) {
		try {
			if (getHibernateGenericController().getId(getModelClass(), model) == null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return true;
		}

	}

	public abstract Class getModelClass();

	// 对象数据
	public abstract Object getModel();

	public abstract void setModel(Object model);

	public boolean isModelNew() {
		return isModelNew(getModel());
	}

	@Override
	public void pushCallback() {
		getCallbackStack().push(
				new EditCallback(getPageName(), getModel(), isModelNew()));
	}
}
