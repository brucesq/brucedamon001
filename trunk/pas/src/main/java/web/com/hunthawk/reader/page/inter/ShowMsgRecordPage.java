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

	// ���Ĳ���ֵ
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

		// �õ����ҳ��ĵ�ǰ��������
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

			// ��ü�¼����
			int count = 0;

			public int getRowCount() {
				if (count > 0) {
					return count;
				}
				buildSearchExpression();
				count = getInteractiveService().getMsgRecordCount(
						getSearchCollection()).intValue();
				log.info("������= ��ȡ�����б�����������" + count);
				return count;
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				log.info("���õ������б�=================��");
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
		log.info("����������=================��");
		CompareExpression ce = new CompareExpression("status",
				MsgRecord.MSG_STATUS_DELETED, CompareType.NotEqual);
		this.getSearchCollection().add(ce);

		// ������������������Ӧ��ֵ
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
	 * ����ɾ������
	 */
	public void onBatchDelete() {
		// ��ñ����û�ѡ�����ļ���
		Set setSelectedObjects = getSelectedObjects();
		int setSize = setSelectedObjects.size();
		log.info("������ɾ������=================��Ҫɾ���ĸ�������" + setSize);

		ValidationDelegate delegate = getDelegate();
		if (setSize == 0) {
			delegate.setFormComponent(null);
			delegate.record("ɾ��ʱ����ѡ������", null);
		} else {
			Iterator iterator = setSelectedObjects.iterator();

			MsgOperateLog mol = new MsgOperateLog();
			mol.setUserName(getUser().getName());
			mol.setUserId(getUser().getId());
			mol.setCreateTime(new Date());

			// ����ɾ��
			while (iterator.hasNext()) {
				MsgRecord p = (MsgRecord) iterator.next();
				p.setModifyTime(new Date());
				getInteractiveService().auditMsgRecord(p,
						MsgRecord.MSG_STATUS_DELETED);

				// ������־

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
	 * ������������
	 */
	public void onBatchPub() {
		// ��ñ����û�ѡ�����ļ���
		Set setSelectedObjects = getSelectedObjects();
		int setSize = setSelectedObjects.size();
		log.info("��������������=================��Ҫ�����ĸ�������" + setSize);

		ValidationDelegate delegate = getDelegate();
		boolean statesFlag = false;
		if (setSize == 0) {
			delegate.setFormComponent(null);
			delegate.record("����ʱ����ѡ������", null);
		} else {
			Iterator iterator = setSelectedObjects.iterator();

			MsgOperateLog mol = new MsgOperateLog();
			mol.setUserName(getUser().getName());
			mol.setUserId(getUser().getId());
			mol.setCreateTime(new Date());

			// ��������
			while (iterator.hasNext()) {
				MsgRecord p = (MsgRecord) iterator.next();
				if (p.getStatus() != 0) {
					statesFlag = true;
					continue;
				}

				p.setModifyTime(new Date());
				getInteractiveService().auditMsgRecord(p,
						MsgRecord.MSG_STATUS_PUB_ADUIT);

				// ������־

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
			delegate.record("ѡ�����״̬�����Խ���������������Ч������״̬�������������Ч��", null);
			setSelectedObjects.clear();
		}
		// clear selection
		setSelectedObjects(new HashSet());
	}

	public void searchMsgRecord() {
	}

	public IPage exportMsgToExcel(IRequestCycle cycle) {
		//TODO:����excel
		logger.info("���뵼��ҳ��");

//		if (!StringUtils.isBlank(cycle.getParameter("paramValue"))) {
//			setParamValue(Integer.parseInt(cycle.getParameter("paramValue")));
//		}
		// if (!StringUtils.isBlank(cycle.getParameter("blockId"))) {
		// setBlockId(Integer.parseInt(cycle.getParameter("blockId")));
		//		}

		// ExcelResourcePage excelPage = getExcelResourcePage();
		// excelPage.setDisplayColumn("=createTime,=msgStatusLabel,msgContent:��������:msgContent,=updateTime,mobile:�ֻ�����:mobile,=prov,=city,=recordReasonLabel,=resourceName");
		// excelPage.setBlockId(getBlockId());
		// excelPage.setSearchCollection(getSearchCollection());
		// excelPage.setParamValue(getParamValue());
		// return excelPage;
		return this;

	}

	/**
	 * �������������ָ������Ρ�ɾ���Ȳ���
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

						if (operType == MsgRecord.MSG_STATUS_HIDED) { // ɾ������
							mol.setOperateOldStatus(po.getStatus());
							mol
									.setOperateNewStatus(MsgRecord.MSG_STATUS_DELETED);
							mol
									.setOperateAction(MsgOperateLog.ADUIT_OPERATE_DELETE);

							po.setModifyTime(new Date());
							getInteractiveService().auditMsgRecord(po,
									MsgRecord.MSG_STATUS_DELETED);
							getInteractiveService().addMsgOperateLog(mol);

						} else if (operType == MsgRecord.MSG_STATUS_PUB_NOADUIT) { // ���Ρ��ָ�����
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

						} else if (operType == MsgRecord.MSG_STATUS_PUB_ADUIT) { // ��������������

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

							} else {// ����
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
						log.warn("�û�sessionû�еõ���������ϢΪ�գ�");
						backArr[2] = "0";
						backArr[3] = "0";
						cycle.setListenerParameters(backArr);
					}
				}
			} else {
				log.warn("����Ϊ�գ�");
				return;
			}
		} catch (Exception e) {
			log.error("onAjax �������:", e);
		}
	}

	public void getNewMsgNote(IRequestCycle cycle) {
		Object[] params = cycle.getListenerParameters();

		String newContent = "������Ϣ��";
		String errorMsg = "";
		try {
			if (params != null || params.length >= 2) {
				String blockIdStr = params[0].toString();
				String pageMsgCountStr = params[1].toString();

				if (StringUtils.isNumeric(blockIdStr)
						&& StringUtils.isNumeric(pageMsgCountStr)) {
					int blockId = Integer.parseInt(blockIdStr);
					int pageMsgCount = Integer.parseInt(pageMsgCountStr);
					// �õ������������
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression("boardId",
							blockId, CompareType.Equal);
					expressions.add(ex);
					int dbMsgCount = getInteractiveService().getMsgRecordCount(
							expressions).intValue();

					if (dbMsgCount > pageMsgCount) {
						newContent += "<b><font color=\"red\">�������ԣ�</font></b>&nbsp;&nbsp;&nbsp;&nbsp;";
					} else {
						newContent += "�������ԣ�&nbsp;&nbsp;&nbsp;&nbsp;";
					}
				}
			} else {
				errorMsg = "����������";
			}

		} catch (Exception e) {
			log.error("��������Ϣ��ʾ���ݳ���:", e);
		}
		cycle.setListenerParameters(new String[] { errorMsg, newContent });
	}

	public IPropertySelectionModel getMsgStatusList() {
		return new ObjectPropertySelectionModel(
				SelectUtil.getItemCheckedList(), SelectKeyValuePO.class,
				"getLabel", "getId", true, "����״̬");

	}

	public IPropertySelectionModel getRecordReasonList() {
		return new ObjectPropertySelectionModel(SelectUtil.getItemReasonList(),
				SelectKeyValuePO.class, "getLabel", "getId", true, "��������");
	}

	public ITableColumn getRecordReasonLabel() {
		return new SimpleTableColumn("recordReason", "��������",
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
		return new SimpleTableColumn("prov", "ʡ��/�ط�",
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

							// ͬʱ���
							if ((mr.getProv() == null || mr.getCity() == null)
									&& mrr.getProv() != null
									&& mrr.getCity() != null) {
								mr.setProv(provName);
								mr.setCity(cityName);
								// TODO:�������ݿ�
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
		// TODO:�����ݿ��л�ȡ
		String prefix = "http://book.moluck.com/pps/s.do?";

		MsgRecord mr = ((MsgRecord) getCurrentObject());
		if (mr != null) {
			String resourceName = mr.getName();
			String url = prefix + " ";
			if (StringUtils.isNotBlank(resourceName)) {
				String subResourceName = resourceName;

				// ��ȡ��Ϣ��Դǰ������
				if (resourceName.length() > 4) {
					subResourceName = resourceName.substring(0, 4);
				}

				// �õ�ǰ̨����url

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
		return new SimpleTableColumn("updateTime", "���ʱ��",
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
		return new SimpleTableColumn("createTime", "����ʱ��",
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
		return new SimpleTableColumn("resName", "��Դ����",
				new ITableColumnEvaluator() {
					private static final long serialVersionUID = 10181100745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						MsgRecord mr = (MsgRecord) objRow;
						Integer msgType = mr.getMsgType();
						if(msgType==1){ //���������ԣ���Դ��
							String contentId = mr.getContentId();//��ԴID
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
	 * ��ʾ�Ի���
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
	 * �޸���������
	 * 
	 * @param cycle
	 */
	public void editContent(IRequestCycle cycle) {
		Object[] params = cycle.getListenerParameters();
		int id = 0;
		String newContent = null;
		String errorMsg = "";
		try {
			// ���Ĳ���
			id = Integer.parseInt(params[0].toString());
			newContent = params[1].toString();
			// newContent = Escape.unescape(newContent);
			if (StringUtils.isEmpty(newContent) || newContent.length() > 1000) {
				errorMsg = "����������Ϊ�ջ�鴦1000���ַ���";
			} else { // ���Ĳ���
				MsgRecord po = getInteractiveService().getMsgRecord(id);
				po.setContent(newContent);
				// getBlockService().updateMsgRecord(po);
			}
			if (log.isDebugEnabled()) {
				log.debug("�޸��������ݣ�id=" + id + ", content=" + newContent);
			}
		} catch (Exception e) {
			log.error("�����������ݳ���:", e);
		}
		cycle.setListenerParameters(new String[] { errorMsg,
				String.valueOf(id), StringEscapeUtils.escapeHtml(newContent) });
	}

	/**
	 * ����checkbox��ֵ
	 * 
	 * @param bSelected
	 */
	public void setCheckboxSelected(boolean bSelected) {
		Object object = getCurrentObject();
		// ��session�л�ȡ���϶��󣬱�֤���ݵ���ȷ
		Set selectedObject = getSelectedObjects();
		// �������ѡ��
		if (bSelected) {
			selectedObject.add(object);
		} else {
			selectedObject.remove(object);
		}
		// persist value
		setSelectedObjects(selectedObject);

	}

	/**
	 * �жϵ�ǰ�����Ƿ�ѡ��
	 * 
	 * @return
	 */
	public boolean getCheckboxSelected() {
		return getSelectedObjects().contains(getCurrentObject());
	}

	/**
	 * ��ô������ļ���
	 * 
	 * @return
	 */
	public abstract Set getSelectedObjects();

	/**
	 * ��ѡ��Ķ�����뼯����
	 * 
	 * @param set
	 */
	public abstract void setSelectedObjects(Set set);

	/**
	 * ��õ�ǰѡ��Ķ���
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
