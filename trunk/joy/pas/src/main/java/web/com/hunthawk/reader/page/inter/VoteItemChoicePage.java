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

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.inter.VoteAct;
import com.hunthawk.reader.domain.inter.VoteItem;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.inter.InteractiveService;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

public abstract class VoteItemChoicePage extends SecurityPage implements
IExternalPage, PageBeginRenderListener {
	@InjectComponent("table")
	public abstract TableView getTableView();

	@Bean
	public abstract ValidationDelegate getDelegate();
	
	@InjectObject("spring:interactiveService")
	public abstract InteractiveService getInteractiveService();
	
	@InjectObject("spring:userService")
	public abstract UserService getUserService();
	
	@InjectObject("spring:materialService")
	public abstract MaterialService getMaterialService();
	
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();
	
	public abstract String getSearchId();
	public abstract void setSearchId(String id);
	
	public abstract int getVoteId();
	public abstract void setVoteId(int voteId);
	
	public abstract VoteItem getVoteItem();
	public abstract void setVoteItem(VoteItem voteItem);

	public abstract String getVoteItemName();
	public abstract void setVoteItemName(String voteItemName);


	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression voteIdE = new CompareExpression("voteId",
				getVoteId(), CompareType.Equal);
		hibernateExpressions.add(voteIdE);
		
		if (!ParameterCheck.isNullOrEmpty(getVoteItemName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getVoteItemName() + "%", CompareType.Like);
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
	public IBasicTableModel getChooseVoteItem() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return new Integer(getInteractiveService().getVoteItemList(getSearchExpressions()).intValue());
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;
				
				return getInteractiveService().findVoteItemsByVote(getVoteId()).iterator();
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

		if (getVoteItem() == null) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个选项(或选项已被删除)", null);

		} else {
			setRadioValue(String.valueOf((getVoteItem().getId())));
		}
	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);
	
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);

		if(parameters.length==1)
			setReturnElement((String) parameters[0]);
		else if(parameters.length==2){
			setReturnElement((String) parameters[0]);
			int voteId = Integer.parseInt(parameters[1].toString());
			setVoteId(voteId);
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
						VoteItem o1 = (VoteItem) objRow;

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
	
	public ITableColumn getDisplayVoteParent() {
		return new SimpleTableColumn("voteId", "所属投票",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						VoteItem o1 = (VoteItem) objRow;

						if (o1.getVoteId() != null) {
							VoteAct va=getInteractiveService().getVoteAct(o1.getId());
							if (va == null) {
								return "";
							} else {
								return va.getName();
							}
						} else {
							return "";
						}

					}

				}, false);

	}
	
	
//	public ITableColumn getDisplayImg() {
//		return new SimpleTableColumn("imgId", "图标",
//				new ITableColumnEvaluator() {
//
//					private static final long serialVersionUID = 31491600745851970L;
//
//					public Object getColumnValue(ITableColumn objColumn,
//							Object objRow) {
//						VoteItem o1 = (VoteItem) objRow;
//						String url = getSystemService().getVariables("media_url").getValue();
//						if(o1.getImgId()!=null && !"".equals(o1.getImgId())){
//							Material mater = getMaterialService().getMaterial(o1.getImgId());
//							return "<img src=\""+url + mater.getFilename() + "." + mater.getExtName()+"\" alt=\""+o1.getName()+"\"/>";
//						}else{
//							return "无图片";
//						}
//
//					}
//
//				}, false);
//
//	}
}
