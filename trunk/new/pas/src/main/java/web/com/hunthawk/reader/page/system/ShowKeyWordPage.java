package com.hunthawk.reader.page.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.system.KeyWordService;
import com.hunthawk.reader.service.system.UserService;

@Restrict(roles = { "keyword" }, mode = Restrict.Mode.ROLE)
public abstract class ShowKeyWordPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("system/EditKeyWordPage")
	public abstract EditKeyWordPage getEditKeyWordPage();

	@InjectObject("spring:keywordService")
	public abstract KeyWordService getKeyWordService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	public abstract String getKeywordname();

	public abstract void setKeywordname(String name);

	public abstract Integer getKeywordid();

	public abstract void setKeywordid(Integer id);

	public abstract KeyWordType getKeyWordType();
	public abstract void setKeyWordType(KeyWordType keyWordType);
	
	
	public IPage onEdit(KeyWord keyWord) {
		getEditKeyWordPage().setType("true");
		getEditKeyWordPage().setModel(keyWord);
		return getEditKeyWordPage();
	}

	public IPage editkeyWord(KeyWordType keyWordType) {
		getEditKeyWordPage().setType("true");
		getEditKeyWordPage().setModel(getKeyWord());
		getEditKeyWordPage().setKeyWordType(keyWordType);
		return getEditKeyWordPage();
	}

	public IPage editkeyWordAll(KeyWordType keyWordType) {
		getEditKeyWordPage().setType("false");
		getEditKeyWordPage().setKeyWordType(keyWordType);
		return getEditKeyWordPage();
	}

	
	public IPropertySelectionModel getSearchTypeList() {
		List<KeyWordType> typeList = getKeyWordService().getKeyWordTypeList
						(1, Integer.MAX_VALUE, "id", false,
						new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				typeList, KeyWordType.class, "getType", "getId",
				false, null);
		return model;
	}
	
	public abstract KeyWordType getKeyType();
	public abstract void setKeyType(KeyWordType keyWordType);
	/**
	 * 批量修改分类
	 */
		public void onBatchChangeType(IRequestCycle cycle){
			UserImpl user = (UserImpl)getUser();
			String message ="";
			try{
				for(Object obj : getSelectedKeyWords()){
					KeyWord  type = (KeyWord)obj;
					type.setType(getKeyType());
					type.setModifier(user.getId());
					type.setModifyTime(new Date());	
					message += getKeyWordService().updateKeyWord(type);
					}
				if(message!=null && !"".equals(message))
					throw new Exception(message);
				
				setSelectedKeyWords(new HashSet());
				ICallback callback = (ICallback) getCallbackStack()
						.getCurrentCallback();
				callback.performCallback(cycle);			
			} catch (Exception e) {
				getDelegate().setFormComponent(null);
				getDelegate().record(e.getMessage(), null);
				setSelectedKeyWords(new HashSet());
			}
			
		}
		
	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		KeyWord keyWord = (KeyWord) getKeyWord();
		Set selectedKeyWord = getSelectedKeyWords();
		if (bSelected) {
			selectedKeyWord.add(keyWord);
		} else {
			selectedKeyWord.remove(keyWord);
		}
		setSelectedKeyWords(selectedKeyWord);

	}

	public boolean getCheckboxSelected() {
		return getSelectedKeyWords().contains(getKeyWord());
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
	public abstract Object getKeyWord();

	@Override
	protected void delete(Object object) {
		try {

			getKeyWordService().delKeyWord((KeyWord) object);
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
		if (!ParameterCheck.isNullOrEmpty(getKeywordname())) {
			HibernateExpression nameE = new CompareExpression("keyWord", "%"
					+ getKeywordname() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getKeywordid() != null && getKeywordid() > 0) {
			HibernateExpression useridE = new CompareExpression("id",
					getKeywordid(), CompareType.Equal);
			hibernateExpressions.add(useridE);
		}
		if (getKeyWordType() != null) {
			HibernateExpression useridE = new CompareExpression("type",
					getKeyWordType(), CompareType.Equal);
			hibernateExpressions.add(useridE);
		}
		return hibernateExpressions;
	}

	public void search() {
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getKeyWordService().getKeyWordResultCount(
						getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getKeyWordService().findKeyWordBy(pageNo, nPageSize,
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
						KeyWord o1 = (KeyWord) objRow;

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

	public ITableColumn getDisplayModifier() {
		return new SimpleTableColumn("modifier", "修改人",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						KeyWord o1 = (KeyWord) objRow;

						if (o1.getModifier() != null) {
							UserImpl user = (UserImpl) getUserService()
									.getObject(UserImpl.class, o1.getModifier());
							if (user == null) {
								return "";
							} else {
								return user.getName();
							}
						} else {
							return "";
						}

					}

				}, false);

	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("keyWordType");
		nameC.setValue(getKeyWordType());
		searchConditions.add(nameC);

		return searchConditions;
	}
}
