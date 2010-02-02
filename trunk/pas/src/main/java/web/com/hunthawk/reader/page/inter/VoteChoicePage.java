package com.hunthawk.reader.page.inter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.inter.MsgBoard;
import com.hunthawk.reader.domain.inter.VoteAct;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.inter.InteractiveService;
import com.hunthawk.reader.service.system.UserService;

@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class VoteChoicePage  extends SecurityPage implements
IExternalPage, PageBeginRenderListener {
	@InjectComponent("table")
	public abstract TableView getTableView();

	@Bean
	public abstract ValidationDelegate getDelegate();
	
	@InjectObject("spring:interactiveService")
	public abstract InteractiveService getInteractiveService();
	
	@InjectObject("spring:userService")
	public abstract UserService getUserService();
	
	public abstract String getSearchId();
	public abstract void setSearchId(String id);
	
	public abstract VoteAct getVoteAct();
	public abstract void setVoteAct(VoteAct voteAct);

	public abstract String getVoteName();
	public abstract void setVoteName(String voteName);


	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getVoteName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getVoteName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (!ParameterCheck.isNullOrEmpty(getSearchId())) {
			HibernateExpression idE = new CompareExpression("id",
					Integer.parseInt(getSearchId()), CompareType.Like);
			hibernateExpressions.add(idE);
		}
		
		return hibernateExpressions;
	}

	/**
	 * 获得板块列表
	 * 
	 * @return
	 */
	public IBasicTableModel getChooseVote() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return new Integer(getInteractiveService().getVoteActList(getSearchExpressions()).intValue());
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;
				
				return getInteractiveService().findVoteActs(pageNo, nPageSize, "id", false, getSearchExpressions()).iterator();
			}

		};

	}

	/**
	 * 搜索列表
	 * 
	 * @param cycle
	 */
	public void search(IRequestCycle cycle) {
		setChoose("false");
		setSearch(true);
		getTableView().reset();

	}

	public abstract String getRadioValue();

	public abstract void setRadioValue(String radioValue);


	
	public void chooseSubmit(IRequestCycle cycle) {
		setChoose("true");

		String value = getRadioValue();

		ValidationDelegate delegate = getDelegate();

		if (getVoteAct() == null) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个资源(或资源已被删除)", null);

		} else {
			setRadioValue(String.valueOf((getVoteAct().getId())));
		}
	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);
	
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);

		// 显示模板类型
		if (parameters.length == 1) {
			setReturnElement((String) parameters[0]);
		}
	}

	
	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();

	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);


	/**
	 * 设置初始属性
	 */
	public void pageBeginRender(PageEvent arg0) {

	}
	
	public ITableColumn getDisplayCrator() {
		return new SimpleTableColumn("creator", "创建人",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						VoteAct o1 = (VoteAct) objRow;

						if (o1.getCreator() != null) {
							UserImpl user = (UserImpl) getUserService()
									.getObject(UserImpl.class, o1.getCreator());
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
}
