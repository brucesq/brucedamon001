package com.hunthawk.reader.page.system;

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
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.system.KeyWordService;
import com.hunthawk.reader.service.system.UserService;

@Restrict(roles = { "keyword" }, mode = Restrict.Mode.ROLE)
public abstract class ShowKeyWordTypePage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("system/EditKeyWordTypePage")
	public abstract EditKeyWordTypePage getEditKeyWordTypePage();

	@InjectObject("spring:keywordService")
	public abstract KeyWordService getKeyWordService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	public abstract String getType();

	public abstract void setType(String type);

	
	public IPage onEdit(KeyWordType keyWordType) {
		//getEditKeyWordTypePage().setType("true");
		getEditKeyWordTypePage().setModel(keyWordType);
		return getEditKeyWordTypePage();
	}

	@InjectPage("system/ShowKeyWordPage")
	public abstract ShowKeyWordPage getShowKeyWordPage();
	
	public IPage showKeyWords(KeyWordType keyWordType) {	
		getShowKeyWordPage().setKeyWordType(keyWordType);
		return getShowKeyWordPage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		KeyWordType keyWord = (KeyWordType) getKeyWordType();
		Set selectedKeyWord = getSelectedKeyWords();
		if (bSelected) {
			selectedKeyWord.add(keyWord);
		} else {
			selectedKeyWord.remove(keyWord);
		}
		setSelectedKeyWords(selectedKeyWord);

	}

	public boolean getCheckboxSelected() {
		return getSelectedKeyWords().contains(getKeyWordType());
	}

	@SuppressWarnings("unchecked")
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedKeyWords();

	@SuppressWarnings("unchecked")
	public abstract void setSelectedKeyWords(Set set);

	/**
	 * 获得当前关键字
	 * 
	 * @return
	 */
	public abstract Object getKeyWordType();

	@Override
	protected void delete(Object object) {
		try {
			KeyWordType type = (KeyWordType)object;
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			HibernateExpression ex = new CompareExpression("type", type
					, CompareType.Equal);
			expressions.add(ex);
			List<KeyWord> keyWordList = 
				getKeyWordService().findKeyWordBy(1, Integer.MAX_VALUE, "id", false, expressions);
			if(keyWordList==null || keyWordList.size()<1)
				getKeyWordService().delKeyWordType((KeyWordType) object);
			else
				throw new Exception("删除失败,此分类已被敏感词引用!要删除此分类请先将引用的敏感词删除");
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedKeyWords()) {
			delete(obj);
		}
		setSelectedKeyWords(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);

	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getType())) {
			HibernateExpression nameE = new CompareExpression("type", "%"
					+ getType() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		return hibernateExpressions;
	}

	public void search() {
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getKeyWordService().getKeyWordTypeCount(
						getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getKeyWordService().getKeyWordTypeList(pageNo, nPageSize,
						"id", false, getSearchExpressions()).iterator();
			}
		};
	}

	public ITableColumn getDisplayCreator() {
		return new SimpleTableColumn("creator", "创建者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						KeyWordType o1 = (KeyWordType) objRow;

						UserImpl user = (UserImpl) getUserService().getObject(
								UserImpl.class, o1.getCreator());
						if (user == null) {
							return "";
						} else {
							return user.getName();
						}

					}

				}, false);

	}

}
