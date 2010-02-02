package com.hunthawk.reader.page.inter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;
import org.apache.tapestry.dojo.html.Dialog;
import org.apache.tapestry.event.BrowserEvent;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.LogicalExpression;
import com.hunthawk.framework.hibernate.LogicalType;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.inter.MsgKeyWord;
import com.hunthawk.reader.domain.inter.MsgOperateLog;
import com.hunthawk.reader.domain.inter.MsgRecord;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.page.util.SelectKeyValuePO;
import com.hunthawk.reader.page.util.SelectUtil;
import com.hunthawk.reader.service.inter.InteractiveService;
import com.hunthawk.reader.service.resource.ResourceService;

/**
 * 
 * @author BruceSun
 * 
 */
@Restrict(roles = { "msgaudit" }, mode = Restrict.Mode.ROLE)
public abstract class ShowMsgRecordPage extends SecurityPage implements
		PageBeginRenderListener {
	private static Logger log = Logger.getLogger(ShowMsgRecordPage.class);

	@InjectObject("spring:interactiveService")
	public abstract InteractiveService getInteractiveService();

	@InjectObject("service:tapestry.globals.HttpServletRequest")
	public abstract HttpServletRequest getServletRequest();

	public abstract SearchMsgRecord getSearchPO();

	public abstract void setSearchPO(SearchMsgRecord searchMsgRecordPO);

	@InjectComponent("recordContentDialog")
	public abstract Dialog getTestDialog();

	@InitialValue("new com.hunthawk.reader.domain.inter.MsgRecord()")
	public abstract MsgRecord getEditMsgRecord();

	public abstract void setEditMsgRecord(MsgRecord editMsgRecord);

	// @InjectPage("message/ShowOneMsgLogList")
	// public abstract ShowOneMsgLogList getShowOneMsgLogList();
	//
	// @InjectPage("message/ExcelResourcePage")
	// public abstract ExcelResourcePage getExcelResourcePage();

	@InitialValue("-1")
	public abstract int getBlockId();

	public abstract void setBlockId(int blockId);

	@InitialValue("0")
	public abstract int getPageMsgCount();

	public abstract void setPageMsgCount(int pageMsgCount);

	// 树的参数值
//	public abstract int getParamValue();
//
//	public abstract void setParamValue(int paramId);

	public abstract Collection<HibernateExpression> getSearchCollection();

	public abstract void setSearchCollection(
			Collection<HibernateExpression> collection);

	public abstract List getExportMsgList();

	public abstract void setExportMsgList(List exportMsgList);

	public void pageBeginRender(PageEvent event) {

		// String paramZb = CommonPropertyReader.getInstance("cam.properties")
		// .getProperty("param_zb");
		// String paramBl = CommonPropertyReader.getInstance("cam.properties")
		// .getProperty("param_bl");

		// String zbParam = getServletRequest().getParameter(paramZb);
		// String blParam = getServletRequest().getParameter(paramBl);

		// if (!StringUtils.isBlank(zbParam)) {
		// setParamValue(Integer.parseInt(zbParam));
		// setBlockId(1);
		// } else if (!StringUtils.isBlank(blParam)) {
		// setParamValue(Integer.parseInt(blParam));
		// setBlockId(Integer.parseInt(blParam));
		// }

		if (getSearchCollection() == null) {
			setSearchCollection(new ArrayList());
		}

		 if (getSearchPO() == null) {
			setSearchPO(new SearchMsgRecord());
		}

		buildSearchExpression();
		setSearchCollection(getSearchCollection());
//		setBlockId(getBlockId());
//		setParamValue(getParamValue());

		// 得到板块页面的当前总留言数
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("boardId", getBlockId(),
				CompareType.Equal);
		expressions.add(ex);
		int pageMsgCount = getInteractiveService().getMsgRecordCount(
				expressions).intValue();
		setPageMsgCount(pageMsgCount);
	}

	public IBasicTableModel getMsgList() {
		return new IBasicTableModel() {

			// 获得记录总数
			int count = 0;

			public int getRowCount() {
				if (count > 0) {
					return count;
				}
				buildSearchExpression();
				count = getInteractiveService().getMsgRecordCount(
						getSearchCollection()).intValue();
				log.info("【功能= 获取留言列表总条数：】" + count);
				return count;
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				log.info("【得到留言列表=================】");
				int beginIndex = nFirst;
				beginIndex = beginIndex / nPageSize;
				beginIndex++;
				int endIndex = nFirst + nPageSize - 1;
				if (endIndex > count - 1) {
					endIndex = count - 1;
				}

				buildSearchExpression();
				setSearchCollection(getSearchCollection());
				return getInteractiveService().findMsgRecords(beginIndex,
						nPageSize, "createTime", false, getSearchCollection())
						.iterator();

			}
		};
	}

	public void buildSearchExpression() {
		log.info("【搜索参数=================】");
		CompareExpression ce = new CompareExpression("status",
				MsgRecord.MSG_STATUS_DELETED, CompareType.NotEqual);
		this.getSearchCollection().add(ce);

		// 设置搜索的条件及对应的值
		if (getBlockId() != -1) {

			ce = new CompareExpression("boardId", getBlockId(),
					CompareType.Equal);

			this.getSearchCollection().add(ce);
		}
		if (getSearchPO().getKeyStr() != null) {
			ce = new CompareExpression("content", "%"
					+ getSearchPO().getKeyStr() + "%", CompareType.Like);
			this.getSearchCollection().add(ce);
		}
		if (getSearchPO().getUserMobile() != null) {
			ce = new CompareExpression("mobile", "%"
					+ getSearchPO().getUserMobile() + "%", CompareType.Like);
			this.getSearchCollection().add(ce);
		}

		if (getSearchPO().getResourcName() != null) {
			ce = new CompareExpression("name", "%"
					+ getSearchPO().getResourcName() + "%", CompareType.Like);
			this.getSearchCollection().add(ce);
		}
		if (getSearchPO().getMsgStatus() != null) {
			ce = new CompareExpression("status", getSearchPO().getMsgStatus()
					.getId(), CompareType.Equal);
			this.getSearchCollection().add(ce);
		}
		if (getSearchPO().getMsgReason() != null) {
			ce = new CompareExpression("reason", getSearchPO().getMsgReason()
					.getId(), CompareType.Equal);
			this.getSearchCollection().add(ce);
		}
		if (getSearchPO().getStartTime() != null) {
			ce = new CompareExpression("createTime", getSearchPO()
					.getStartTime(), CompareType.Ge);
			this.getSearchCollection().add(ce);
		}
		if (getSearchPO().getEndTime() != null) {
			ce = new CompareExpression("createTime",
					getSearchPO().getEndTime(), CompareType.Le);
			this.getSearchCollection().add(ce);
		}

		if (getSearchPO().getCustomId() != null) {

			String strValue = isPatternContent(getSearchPO().getCustomId(), 1,
					"([a-zA-Z]+)(\\d*)");
			String strNumValue = isPatternContent(getSearchPO().getCustomId(),
					2, "([a-zA-Z]+)(\\d*)");

			int numValue = !StringUtils.isBlank(strNumValue) ? Integer
					.parseInt(strNumValue) : 0;

			if (strValue.equalsIgnoreCase("p")) {
				if (numValue != 0) {
					ce = new CompareExpression("productId", numValue,
							CompareType.Equal);
				} else {
					ce = new CompareExpression("productId", numValue,
							CompareType.NotEqual);
				}
				this.getSearchCollection().add(ce);
			} else if (strValue.equalsIgnoreCase("c")) {
				if (numValue != 0) {
					ce = new CompareExpression("columnId", numValue,
							CompareType.Equal);
				} else {
					ce = new CompareExpression("columnId", numValue,
							CompareType.NotEqual);
				}
				this.getSearchCollection().add(ce);
			} else if (strValue.equalsIgnoreCase("r")) {
				if (numValue != 0) {
					ce = new CompareExpression("contentId", numValue,
							CompareType.Equal);
				} else {
					ce = new CompareExpression("contentId", numValue,
							CompareType.NotEqual);
				}
				this.getSearchCollection().add(ce);
			}
		}
	}

	/**
	 * 批量删除方法
	 */
	public void onBatchDelete() {
		// 获得保存用户选择对象的集合
		Set setSelectedObjects = getSelectedObjects();
		int setSize = setSelectedObjects.size();
		log.info("【批量删除留言=================需要删除的个数：】" + setSize);

		ValidationDelegate delegate = getDelegate();
		if (setSize == 0) {
			delegate.setFormComponent(null);
			delegate.record("删除时必须选择内容", null);
		} else {
			Iterator iterator = setSelectedObjects.iterator();

			MsgOperateLog mol = new MsgOperateLog();
			mol.setUserName(getUser().getName());
			mol.setUserId(getUser().getId());
			mol.setCreateTime(new Date());

			// 迭代删除
			while (iterator.hasNext()) {
				MsgRecord p = (MsgRecord) iterator.next();
				p.setModifyTime(new Date());
				getInteractiveService().auditMsgRecord(p,
						MsgRecord.MSG_STATUS_DELETED);

				// 操作日志

				mol.setItemId(p.getId());
				mol.setProjectId(p.getBoardId());
				mol.setOperateOldStatus(p.getStatus());
				mol.setOperateNewStatus(MsgRecord.MSG_STATUS_DELETED);
				mol.setOperateAction(MsgOperateLog.ADUIT_OPERATE_DELETE);
				getInteractiveService().addMsgOperateLog(mol);

			}
		}

		// clear selection
		setSelectedObjects(new HashSet());
	}

	/**
	 * 批量发布方法
	 */
	public void onBatchPub() {
		// 获得保存用户选择对象的集合
		Set setSelectedObjects = getSelectedObjects();
		int setSize = setSelectedObjects.size();
		log.info("【批量发布留言=================需要发布的个数：】" + setSize);

		ValidationDelegate delegate = getDelegate();
		boolean statesFlag = false;
		if (setSize == 0) {
			delegate.setFormComponent(null);
			delegate.record("发布时必须选择内容", null);
		} else {
			Iterator iterator = setSelectedObjects.iterator();

			MsgOperateLog mol = new MsgOperateLog();
			mol.setUserName(getUser().getName());
			mol.setUserId(getUser().getId());
			mol.setCreateTime(new Date());

			// 迭代发布
			while (iterator.hasNext()) {
				MsgRecord p = (MsgRecord) iterator.next();
				if (p.getStatus() != 0) {
					statesFlag = true;
					continue;
				}

				p.setModifyTime(new Date());
				getInteractiveService().auditMsgRecord(p,
						MsgRecord.MSG_STATUS_PUB_ADUIT);

				// 操作日志

				mol.setItemId(p.getId());
				mol.setProjectId(p.getBoardId());
				mol.setOperateOldStatus(p.getStatus());
				mol.setOperateNewStatus(MsgRecord.MSG_STATUS_PUB_ADUIT);
				mol.setOperateAction(MsgOperateLog.ADUIT_OPERATE_PUB);

				getInteractiveService().addMsgOperateLog(mol);

			}
		}

		if (statesFlag) {
			delegate.setFormComponent(null);
			delegate.record("选择待审状态的留言进行批量发布才有效，其他状态点击批量发布无效！", null);
			setSelectedObjects.clear();
		}
		// clear selection
		setSelectedObjects(new HashSet());
	}

	public void searchMsgRecord() {
	}

	public IPage exportMsgToExcel(IRequestCycle cycle) {
		//TODO:导出excel
		logger.info("进入导出页面");

//		if (!StringUtils.isBlank(cycle.getParameter("paramValue"))) {
//			setParamValue(Integer.parseInt(cycle.getParameter("paramValue")));
//		}
		// if (!StringUtils.isBlank(cycle.getParameter("blockId"))) {
		// setBlockId(Integer.parseInt(cycle.getParameter("blockId")));
		//		}

		// ExcelResourcePage excelPage = getExcelResourcePage();
		// excelPage.setDisplayColumn("=createTime,=msgStatusLabel,msgContent:留言内容:msgContent,=updateTime,mobile:手机号码:mobile,=prov,=city,=recordReasonLabel,=resourceName");
		// excelPage.setBlockId(getBlockId());
		// excelPage.setSearchCollection(getSearchCollection());
		// excelPage.setParamValue(getParamValue());
		// return excelPage;
		return this;

	}

	/**
	 * 发布、撤销、恢复、屏蔽、删除等操作
	 * 
	 * @param cycle
	 */
	public void onAjax(IRequestCycle cycle) {
		Object[] params = cycle.getListenerParameters();
		String[] backArr = null;
		try {
			if (params != null || params.length >= 2) {
				String sOperType = params[0].toString();
				String sRecordId = params[1].toString();

				if (StringUtils.isNumeric(sOperType)
						&& StringUtils.isNumeric(sRecordId)) {
					int operType = Integer.parseInt(sOperType);
					int recordId = Integer.parseInt(sRecordId);
					backArr = new String[4];
					backArr[0] = sRecordId;
					backArr[1] = sOperType;

					MsgRecord po = getInteractiveService().getMsgRecord(
							recordId);

					if (po != null) {
						MsgOperateLog mol = new MsgOperateLog();
						mol.setItemId(recordId);
						mol.setProjectId(po.getBoardId());
						mol.setUserName(getUser().getName());
						mol.setUserId(getUser().getId());
						mol.setCreateTime(new Date());

						if (operType == MsgRecord.MSG_STATUS_HIDED) { // 删除操作
							mol.setOperateOldStatus(po.getStatus());
							mol
									.setOperateNewStatus(MsgRecord.MSG_STATUS_DELETED);
							mol
									.setOperateAction(MsgOperateLog.ADUIT_OPERATE_DELETE);

							po.setModifyTime(new Date());
							getInteractiveService().auditMsgRecord(po,
									MsgRecord.MSG_STATUS_DELETED);
							getInteractiveService().addMsgOperateLog(mol);

						} else if (operType == MsgRecord.MSG_STATUS_PUB_NOADUIT) { // 屏蔽、恢复操作
							po.setModifyTime(new Date());
							if (po.getStatus() == MsgRecord.MSG_STATUS_HIDED) {
								po
										.setReason(po.getStatus() == MsgRecord.MSG_STATUS_HIDED ? SelectUtil.ITEM_REASON_KEY[0]
												: po.getReason());
								mol.setOperateOldStatus(po.getStatus());
								mol
										.setOperateNewStatus(MsgRecord.MSG_STATUS_WAIT_ADUIT);
								mol
										.setOperateAction(MsgOperateLog.ADUIT_OPERATE_HIDE_CANCEL);
								Integer status = po.getStatus() == MsgRecord.MSG_STATUS_HIDED ? MsgRecord.MSG_STATUS_WAIT_ADUIT
										: MsgRecord.MSG_STATUS_HIDED;

								getInteractiveService().auditMsgRecord(po,
										status);
							} else {
								po
										.setReason((po.getStatus() == MsgRecord.MSG_STATUS_WAIT_ADUIT || po
												.getStatus() == MsgRecord.MSG_STATUS_PUB_NOADUIT) ? SelectUtil.ITEM_REASON_KEY[4]
												: po.getReason());
								int tempStatus = po.getStatus();
								mol.setOperateOldStatus(po.getStatus());
								mol
										.setOperateNewStatus(MsgRecord.MSG_STATUS_HIDED);
								mol
										.setOperateAction(MsgOperateLog.ADUIT_OPERATE_HIDE);
								Integer status = po.getStatus() == MsgRecord.MSG_STATUS_HIDED ? MsgRecord.MSG_STATUS_WAIT_ADUIT
										: MsgRecord.MSG_STATUS_HIDED;

								getInteractiveService().auditMsgRecord(po,
										status);

							}
							getInteractiveService().addMsgOperateLog(mol);

						} else if (operType == MsgRecord.MSG_STATUS_PUB_ADUIT) { // 发布、撤销操作

							po.setModifyTime(new Date());

							if (po.getStatus() == MsgRecord.MSG_STATUS_PUB_ADUIT
									|| po.getStatus() == MsgRecord.MSG_STATUS_PUB_NOADUIT) {
								mol.setOperateOldStatus(po.getStatus());
								mol
										.setOperateNewStatus(MsgRecord.MSG_STATUS_WAIT_ADUIT);
								mol
										.setOperateAction(MsgOperateLog.ADUIT_OPERATE_PUB_CANCEL);
								Integer status = po.getStatus() == MsgRecord.MSG_STATUS_WAIT_ADUIT ? MsgRecord.MSG_STATUS_PUB_ADUIT
										: MsgRecord.MSG_STATUS_WAIT_ADUIT;

								getInteractiveService().auditMsgRecord(po,
										status);

							} else {// 发布
								mol.setOperateOldStatus(po.getStatus());
								mol
										.setOperateNewStatus(MsgRecord.MSG_STATUS_PUB_ADUIT);
								mol
										.setOperateAction(MsgOperateLog.ADUIT_OPERATE_PUB);
								Integer status = po.getStatus() == MsgRecord.MSG_STATUS_WAIT_ADUIT ? MsgRecord.MSG_STATUS_PUB_ADUIT
										: MsgRecord.MSG_STATUS_WAIT_ADUIT;
								getInteractiveService().auditMsgRecord(po,
										status);

							}
							getInteractiveService().addMsgOperateLog(mol);

						}
						backArr[2] = String.valueOf(po.getStatus());
						backArr[3] = String.valueOf(po.getReason());
						
						
						cycle.setListenerParameters(backArr);
					} else {
						log.warn("用户session没有得到，留言信息为空！");
						backArr[2] = "0";
						backArr[3] = "0";
						cycle.setListenerParameters(backArr);
					}
				}
			} else {
				log.warn("参数为空！");
				return;
			}
		} catch (Exception e) {
			log.error("onAjax 处理错误:", e);
		}
	}

	public void getNewMsgNote(IRequestCycle cycle) {
		Object[] params = cycle.getListenerParameters();

		String newContent = "留言信息：";
		String errorMsg = "";
		try {
			if (params != null || params.length >= 2) {
				String blockIdStr = params[0].toString();
				String pageMsgCountStr = params[1].toString();

				if (StringUtils.isNumeric(blockIdStr)
						&& StringUtils.isNumeric(pageMsgCountStr)) {
					int blockId = Integer.parseInt(blockIdStr);
					int pageMsgCount = Integer.parseInt(pageMsgCountStr);
					// 得到板块总留言数
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression("boardId",
							blockId, CompareType.Equal);
					expressions.add(ex);
					int dbMsgCount = getInteractiveService().getMsgRecordCount(
							expressions).intValue();

					if (dbMsgCount > pageMsgCount) {
						newContent += "<b><font color=\"red\">有新留言！</font></b>&nbsp;&nbsp;&nbsp;&nbsp;";
					} else {
						newContent += "无新留言！&nbsp;&nbsp;&nbsp;&nbsp;";
					}
				}
			} else {
				errorMsg = "板块参数错误！";
			}

		} catch (Exception e) {
			log.error("新留言信息提示内容出错:", e);
		}
		cycle.setListenerParameters(new String[] { errorMsg, newContent });
	}

	public IPropertySelectionModel getMsgStatusList() {
		return new ObjectPropertySelectionModel(
				SelectUtil.getItemCheckedList(), SelectKeyValuePO.class,
				"getLabel", "getId", true, "所有状态");

	}

	public IPropertySelectionModel getRecordReasonList() {
		return new ObjectPropertySelectionModel(SelectUtil.getItemReasonList(),
				SelectKeyValuePO.class, "getLabel", "getId", true, "所有理由");
	}

	public ITableColumn getRecordReasonLabel() {
		return new SimpleTableColumn("recordReason", "屏蔽理由",
				new ITableColumnEvaluator() {
					private static final long serialVersionUID = 30418912741159998L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						MsgRecord mr = (MsgRecord) objRow;
						return SelectUtil.getReasonValue(mr.getReason());
					}
				}, true);
	}

	public ITableColumn getProvCity() {
		return new SimpleTableColumn("prov", "省份/地方",
				new ITableColumnEvaluator() {
					private static final long serialVersionUID = 30418912741159998L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						MsgRecord mr = (MsgRecord) objRow;
						String reName = "";
						if (StringUtils.isNotEmpty(mr.getMobile())) {
							String provName = "";
							String cityName = "";
							reName = provName + "/" + cityName;

							MsgRecord mrr = getInteractiveService()
									.getMsgRecord(mr.getId());

							// 同时入库
							if ((mr.getProv() == null || mr.getCity() == null)
									&& mrr.getProv() != null
									&& mrr.getCity() != null) {
								mr.setProv(provName);
								mr.setCity(cityName);
								// TODO:更新数据库
								// getInteractiveService().updateMsgRecord(mr);
							}
						}
						return reName;
					}
				}, true);
	}

	public String getMsgStatus() {
		String returnStr = "";
		MsgRecord mr = ((MsgRecord) getCurrentObject());
		if (mr != null) {
			returnStr = "<a href class=\"handLink\" onclick=\"openLog('"
					+ mr.getId() + "');\">"
					+ SelectUtil.getCheckedValue(mr.getStatus()) + "</a>";
		}
		return returnStr;
	}

	public String getResourceName() {
		String returnStr = "";
		// TODO:从数据库中获取
		String prefix = "http://book.moluck.com/pps/s.do?";

		MsgRecord mr = ((MsgRecord) getCurrentObject());
		if (mr != null) {
			String resourceName = mr.getName();
			String url = prefix + " ";
			if (StringUtils.isNotBlank(resourceName)) {
				String subResourceName = resourceName;

				// 截取信息来源前几个字
				if (resourceName.length() > 4) {
					subResourceName = resourceName.substring(0, 4);
				}

				// 得到前台访问url

				// if (mr.getResourceId() != 0) {
				// if (mb != null && mb.getPamsVersion() == 1) {
				// url = pams1PpsUrl + "?p=" + mr.getProductId()
				// + "&j=d&c=" + mr.getCatalogId() + "&r="
				// + mr.getResourceId();
				// } else if (mb.getPamsVersion() == 0) {
				// url = pams2PpsUrl + "?p=" + mr.getProductId()
				// + "&j=d&c=" + mr.getCatalogId() + "&r="
				// + mr.getResourceId();
				// }
				// } else if (mr.getCatalogId() != 0) {
				// if (mb != null && mb.getPamsVersion() == 1) {
				// url = pams1PpsUrl + "?p=" + mr.getProductId() + "&c="
				// + mr.getCatalogId();
				// } else if (mb.getPamsVersion() == 0) {
				// url = pams2PpsUrl + "?p=" + mr.getProductId() + "&c="
				// + mr.getCatalogId();
				// }
				// } else if (mr.getProductId() != 0) {
				// if (mb != null && mb.getPamsVersion() == 1) {
				// url = pams1PpsUrl + "?p=" + mr.getProductId();
				// } else if (mb.getPamsVersion() == 0) {
				// url = pams2PpsUrl + "?p=" + mr.getProductId();
				// }
				// }

				returnStr = "<a href class=\"handLink\" title=\""
						+ mr.getName() + "\" onclick=\"" + "copy('" + url
						+ "');" + "\">" + subResourceName + "</a>";
			}
		}
		return returnStr;
	}

	public String getMsgContent() {
		MsgRecord msgRecord = (MsgRecord) getCurrentObject();
		String msgContent = msgRecord.getContent();
		return addRedColor(msgContent, msgRecord.getBoardId());
	}

	public String addRedColor(String content, int projectId) {
		Collection<HibernateExpression> mappingExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression defultEx = new CompareExpression("projectId", 0,
				CompareType.Equal);
		HibernateExpression projectEx = new CompareExpression("projectId",
				projectId, CompareType.Equal);
		LogicalExpression le = new LogicalExpression(defultEx, projectEx,
				LogicalType.Or);
		mappingExpressions.add(le);

		CompareExpression ce = new CompareExpression("keyType", 1,
				CompareType.Equal);
		mappingExpressions.add(ce);

		// ResultFilter<MsgKeyWord> result = getManagerService().getMsgKeyWords(
		// projectId, mappingExpressions, Integer.MAX_VALUE, 1);
		List<MsgKeyWord> mkwList = new ArrayList<MsgKeyWord>();

		String redCssBegin = "<font color=\"red\">";
		String redCssEnd = "</font>";

		if (content != null) {
			for (MsgKeyWord mkw : mkwList) {
				if (mkw.getKeyName() != null) {
					content = content.replace(mkw.getKeyName(), "#&#"
							+ mkw.getKeyName() + "#~#");
				}
			}
			content = content.replaceAll("#&#", redCssBegin);
			content = content.replaceAll("#~#", redCssEnd);
		}
		return content;
	}

	public ITableColumn getUpdateTime() {
		return new SimpleTableColumn("updateTime", "审核时间",
				new ITableColumnEvaluator() {
					private static final long serialVersionUID = 10181100745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						MsgRecord mr = (MsgRecord) objRow;
						String msgDate = ToolDateUtil.dateToString(mr
								.getModifyTime(), ToolDateUtil.DATE_FORMAT3);
						if (StringUtils.isBlank(msgDate)) {
							msgDate = "";
						}
						return msgDate;
					}
				}, true);
	}

	public ITableColumn getCreateTime() {
		return new SimpleTableColumn("createTime", "留言时间",
				new ITableColumnEvaluator() {
					private static final long serialVersionUID = 10181100745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						MsgRecord mr = (MsgRecord) objRow;
						String msgDate = ToolDateUtil.dateToString(mr
								.getCreateTime(), ToolDateUtil.DATE_FORMAT3);
						if (StringUtils.isBlank(msgDate)) {
							msgDate = "";
						}
						return msgDate;
					}
				}, true);
	}

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	public ITableColumn getResName() {
		return new SimpleTableColumn("resName", "资源名称",
				new ITableColumnEvaluator() {
					private static final long serialVersionUID = 10181100745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						MsgRecord mr = (MsgRecord) objRow;
						Integer msgType = mr.getMsgType();
						if(msgType==1){ //对内容留言（资源）
							String contentId = mr.getContentId();//资源ID
							if(contentId == null || "".equals(contentId))
								return "";
							ResourceAll res = getResourceService().getResource(contentId);
							if(res!=null)
								return res.getName();
						}
						return "";
					}
				}, true);
	}
	
	/**
	 * 显示对话框
	 * 
	 * @param event
	 * @param id
	 */
	public void showDialog(BrowserEvent event, int id) {
		try {
			this.setEditMsgRecord(getInteractiveService().getMsgRecord(id));
			// this.setResourceTitle(getPostTitleService().getTitle(getEditMsgRecord().getPostid()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		getTestDialog().show();
	}

	/**
	 * 修改留言内容
	 * 
	 * @param cycle
	 */
	public void editContent(IRequestCycle cycle) {
		Object[] params = cycle.getListenerParameters();
		int id = 0;
		String newContent = null;
		String errorMsg = "";
		try {
			// 更改操作
			id = Integer.parseInt(params[0].toString());
			newContent = params[1].toString();
			// newContent = Escape.unescape(newContent);
			if (StringUtils.isEmpty(newContent) || newContent.length() > 1000) {
				errorMsg = "新留言内容为空或查处1000个字符！";
			} else { // 更改操作
				MsgRecord po = getInteractiveService().getMsgRecord(id);
				po.setContent(newContent);
				// getBlockService().updateMsgRecord(po);
			}
			if (log.isDebugEnabled()) {
				log.debug("修改留言内容：id=" + id + ", content=" + newContent);
			}
		} catch (Exception e) {
			log.error("更改留言内容出错:", e);
		}
		cycle.setListenerParameters(new String[] { errorMsg,
				String.valueOf(id), StringEscapeUtils.escapeHtml(newContent) });
	}

	/**
	 * 设置checkbox的值
	 * 
	 * @param bSelected
	 */
	public void setCheckboxSelected(boolean bSelected) {
		Object object = getCurrentObject();
		// 从session中获取集合对象，保证数据的正确
		Set selectedObject = getSelectedObjects();
		// 如果对象被选择
		if (bSelected) {
			selectedObject.add(object);
		} else {
			selectedObject.remove(object);
		}
		// persist value
		setSelectedObjects(selectedObject);

	}

	/**
	 * 判断当前对象是否被选择
	 * 
	 * @return
	 */
	public boolean getCheckboxSelected() {
		return getSelectedObjects().contains(getCurrentObject());
	}

	/**
	 * 获得存入对象的集合
	 * 
	 * @return
	 */
	public abstract Set getSelectedObjects();

	/**
	 * 将选择的对象放入集合中
	 * 
	 * @param set
	 */
	public abstract void setSelectedObjects(Set set);

	/**
	 * 获得当前选择的对象
	 * 
	 * @return
	 */
	public abstract Object getCurrentObject();

	public static String isPatternContent(String content, int group,
			String pattern) {

		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(content);
		String str = "";
		if (m.find()) {
			str = m.group(group);
		}
		return str;
	}
}
