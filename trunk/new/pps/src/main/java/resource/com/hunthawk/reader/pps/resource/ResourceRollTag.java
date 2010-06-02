package com.hunthawk.reader.pps.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.aspire.gentox.rollrule.DefaultRollRule;
import com.aspire.gentox.rollrule.IRollRule;
import com.aspire.gentox.rollrule.IntervalRandomRollRule;
import com.aspire.gentox.rollrule.IntevalPageRollRule;
import com.aspire.gentox.rollrule.PageRollRule;
import com.aspire.gentox.rollrule.PushRollRule;
import com.aspire.gentox.rollrule.RandomRollRule;
import com.aspire.gentox.rollrule.RollRuleContext;
import com.aspire.gentox.rollrule.RollRuleManager;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.ArrayUtil;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.PpsUtil;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeLogicService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * ��Դ������ǩ������ͨ����ѭ���򣬵�����Ϣ��ʾ˳����ʾʱ�䡢��ʾ���� ����˵���� notDisSN���Ƿ���ʾ��� ( 1.�� -1.��)
 * isFee���Ƿ���ʾ�Ʒ� ( 1.�� -1.��) isRoll���Ƿ���ѭ ( 1.�� -1.��) noPageLink���Ƿ񵼺� ( 1.�� -1.��)
 * totalCount����ѭ�ܷ�Χ(Ĭ�����Ϊ200��) columnId����ĿID rollCount����ѭ���� rollTime����ѭʱ�� (��λ����)
 * rollRuleId����ѭ���� (�ö���ѯ����=0,��ҳ��ѯ����=1,�����ȡ��ѯ����=2,
 * ���һ��ʱ�������ȡ��ѯ����=3,���һ��ʱ��_��ѯһ����¼=4) mix : ����� (�Ƽ���+��Դ����) 0.����� 1.�Ƽ��� modify by
 * liuxh :�����������-->��Բ���ѭ��Դ listCount:�б��ܷ�Χ --��Բ���ѭ����� ���δ������Ĭ��Ϊ��¼����
 * 
 * param:ɸѡ���� 1.ȫ�� 2.���� 3.�ѳ��� 4.δ���� add by liuxh 09-12-16
 * 
 * @author liuxh
 */
public class ResourceRollTag extends BaseTag {

	private static final long serialVersionUID = -498520875520787274L;

	private CustomService customService;

	private ResourceService resourceService;

	private BussinessService bussinessService;
	private FeeLogicService feeLogicService;

	// private MemCachedClientWrapper memcachedClient;
	//
	// public MemCachedClientWrapper getMemcachedClient() {
	// return memcachedClient;
	// }

	/** �Ƿ�Ʒ� */
	private boolean isFee;

	public boolean isFee() {
		return isFee;
	}

	/** �����ѭ��Χ */
	private static final int MAXNUMBER_RANGE = 200;
	/** ��ʾ���� */
	private static final int DEFAULT_PAGE_SIZE = 20;
	/** Ĭ�Ϲ�����ʾ���� */
	private static final int DEFAULT_ROLL_COUNT = 5;
	/** Ĭ�Ϲ�����ʾʱ�䣬��λ������ */
	private static final int DEFAULT_ROLL_TIME = 2;
	/** ������Ĭ����ʾ��ҳ���� */
	private static final int DEFAULT_NAVIGATOR_SIZE = 5;
	/** ��ĿId */
	private int columnId;
	/** ���۰�Id */
	private int resourcePackId;
	/** �������� */
	private String titlePattern;
	/** ������ѭ������ */
	private int totalCount;
	/** �б��ܷ�Χ --��Բ�������ѭ����Դ */
	private int listCount;

	public int getListCount() {
		return listCount;
	}

	public void setListCount(int listCount) {
		this.listCount = listCount;
	}

	/** ��ʾ����ѭ���� */
	private int rollCount;
	/** �ö�������ֻ�������ö���ѭ���� */
	private int topCount;
	/** ��ѭ��ʱ�䣬��λ������ */
	private int rollTime;
	/** ��ѭ���� */
	private int rollRuleId;
	/** ����ʾ��� */
	private boolean notDisSN;
	/** ��һҳ���� */
	private String nextPageLink = "";
	/** ��һҳ���� */
	private String prePageLink = "";
	/** ҳ���������� */
	private String navigatorPageLink = "";
	/** ��תҳ������ */
	private String gotoPageLink = "";
	/** ����ʾ��ҳ��ص����� */
	private boolean noPageLink;
	/** �Ƿ���ѭ */
	private boolean isRoll;

	private int urlOrder = -1;

	private int urlOrderSub = -1;

	public boolean isRoll() {
		return isRoll;
	}

	public void setRool(boolean isRoll) {
		this.isRoll = isRoll;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		/** �б��ѯ���� add by liuxh 09-11-16 */
		int param = getIntParameter("param", -1);
		/** end */
		String mobile = RequestUtil.getMobile();
		String productId = request.getParameter(ParameterConstants.PRODUCT_ID);
		// ��ȡurl ������� start
		/**
		 * @author penglei 2009-10-29 13:34:17
		 * 
		 */

		urlOrder = ParamUtil.getIntParameter(request,
				ParameterConstants.ORDER, -1);
		urlOrderSub = ParamUtil.getIntParameter(request,
				ParameterConstants.ORDERSUB, -1);
		
		if (urlOrder < 0 && urlOrderSub < 0) {
			urlOrder = getIntParameter(ParameterConstants.ORDER, -1);
			urlOrderSub = getIntParameter(ParameterConstants.ORDERSUB, -1);	
		}

		// ��ȡurl ������� end

		int month_fee_bag_id = ParamUtil.getIntParameter(request,
				ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId = ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_ID, -1);

		String mix = getParameter("mix", "1");// title��� Ĭ��ֻ��ʾ��Դ���Ʋ������

		// ���ڼ����ǩ����ʱ��
		long currentTime = System.currentTimeMillis();
		long tagBegin = currentTime;
		// �Ƿ���ʾ���
		notDisSN = getIntParameter("isDisSN", -1) < 0 ? true : false;
		// �Ƿ�Ʒ�
		isFee = getIntParameter("isFee", -1) > 0 ? true : false; // (1.url���ּƷѴ���
		// -1.url������
		// �ƷѴ���)
		// �Ƿ���ѭ
		isRoll = getIntParameter("isRoll", -1) > 0 ? true : false; // (1.��ѭ
		// -1.����ѭ)
		// �Ƿ���ҳ�浼��
		noPageLink = getIntParameter("pageLink", -1) > 0 ? false : true;// ��һҳ
		// ��ѭ�ܷ�Χ
		totalCount = getIntParameter("rollTotalCount", -1);// �õ��û�ѡ����ѭ����������
		// �б��ܷ�Χ --��Բ�������ѭ����Դ
		listCount = getIntParameter("listCount", -1);
		// �õ���Ŀ����
		columnId = getIntParameter("columnId", -1);
		// �õ���ѭ����
		rollCount = getIntParameter("rollCount", -1);
		// ������ʾ����
		if (rollCount < 1) {
			rollCount = DEFAULT_ROLL_COUNT;
		}
		// ������ʾ����
		if (rollCount < 1) {
			rollCount = DEFAULT_ROLL_COUNT;
		}
		TagLogger.debug(tagName, tagName + "=======��ʼ����", request
				.getQueryString(), null);
		// �õ��������õ���ѭʱ��
		rollTime = Integer.parseInt(getParameter("rollTime", "-1"));
		if (rollTime < 1) {
			rollTime = DEFAULT_ROLL_TIME;
		} else {// ����ѭʱ���ӳ���������ѹ��
			rollTime = rollTime * 3;
		}
		// �õ���ѭ����
		rollRuleId = getIntParameter("rollRuleId", -1) < 0 ? 0
				: getIntParameter("rollRuleId", -1);

		/***************************************** ������ȡ���� ********************************************************/
		if (columnId < 0) {
			// ��URL�л�ȡcolumnId
			columnId = Integer.parseInt(request
					.getParameter(ParameterConstants.COLUMN_ID) == null ? "-1"
					: request.getParameter(ParameterConstants.COLUMN_ID));
		}
		if (columnId < 0) {
			TagLogger
					.debug(tagName, "��ȡ��ĿIDʧ��", request.getQueryString(), null);
			return new HashMap();
		}
		Columns column = getBussinessService(request).getColumns(columnId); // ��Ŀ
		// ����ò�����Ŀ ��ֱ�ӷ���
		if (column == null) {
			TagLogger
					.debug(tagName, "��ȡ��Ŀ��Ϣʧ��", request.getQueryString(), null);
			return new HashMap();
		}
		// �õ����۰���ID
		resourcePackId = column.getPricepackId() == null ? 0 : column
				.getPricepackId(); // ���۰�ID
		// �������۰���ID�鵽���۰��������Ϣ
		ResourcePack resourcePack = getResourceService(request)
				.getResourcePack(resourcePackId);
		// ����ò������۰����� ����ֱ�ӷ���
		if (resourcePack == null)
			return new HashMap();

		int currentPage = request.getParameter(ParameterConstants.PAGE_NUMBER) == null ? 1
				: Integer.parseInt(request
						.getParameter(ParameterConstants.PAGE_NUMBER));// ��ǰҳ����Ĭ��Ϊ��һҳ

		/**
		 * �ж��Ƿ��url���Ƿ��������ֶδ��ڡ�����У�ʹ��url�����ֶ����򣬷���ʹ�ñ�ǩ�ֶ�����
		 * 
		 * @author penglei 2009-10-29 13:40:49
		 * 
		 */
		int order = 0;

		if (urlOrder > 0 && urlOrderSub > 0) {
			order = getResourceService(request).getUrlOrderType(urlOrder,
					urlOrderSub);
		} else {
			order = column.getOrderType();// ��������
		}
		// ����

		// ��ס��ʼ��totalCount���������ڹ���ruleId
		int paraTotalCount = -1;
		List<ResourcePackReleation> resourcePackReleation = null;// ��Դ���۹�ϵ���б���
		List list = null;
		if (isNoPageLink()) {// �������ʾ���� ��ǰҳ����Ϊ1
			currentPage = 1;
		}
		int navigatorPageSize = rollCount;// ÿҳ��ʾ������

		if (isRoll) {// ��ѭ ���û�ѡ��������ѭ������Ϊ��ҳ��ѯ��sizeֵ
			// ȡ�����������ļ�¼����
			int recordCount = getResourceService(request)
					.getResourcePackReleationsByOrderCount(resourcePackId,
							order, totalCount, param);
			totalCount = totalCount > MAXNUMBER_RANGE ? MAXNUMBER_RANGE
					: totalCount;// ���б߽�У��
			// �����󷶳볬����¼���������ΧΪ��¼����
			totalCount = totalCount > recordCount ? recordCount : totalCount;

			if (rollRuleId == ParameterConstants.ROLL_RULE_RANDOM
					|| rollRuleId == ParameterConstants.ROLL_RULE_RANDOM_INTERVAL) {// ����ҳ
				currentPage = 1;
			}
			resourcePackReleation = getResourceService(request)
					.getResourcePackReleationsByOrder(resourcePackId, 1,
							totalCount, order, totalCount, param);

			if (rollRuleId == ParameterConstants.ROLL_RULE_RANDOM
					|| rollRuleId == ParameterConstants.ROLL_RULE_RANDOM_INTERVAL
					&& resourcePackReleation.size() < totalCount) {
				totalCount = resourcePackReleation.size();
			} else {// ��ҳ
				// ���һҳ��ѯ��������� rollCountֵʱ ����rollCount��ֵ
				if (currentPage == (totalCount % rollCount != 0 ? totalCount
						/ rollCount + 1 : totalCount / rollCount)) {
					rollCount = (totalCount - (currentPage - 1) * rollCount);
				}
			}
			paraTotalCount = totalCount;
			String ruleId = resourcePackId + "_" + paraTotalCount + "_"
					+ rollCount + "_" + rollTime + "_" + rollRuleId;
			RollRuleManager rrm = RollRuleManager.getInstance();
			IRollRule rollRule = getRollRule(ruleId);
			RollRuleContext rrc = new RollRuleContext(Long.valueOf(totalCount),
					currentPage, new Integer(rollCount));
			if (rollRuleId == ParameterConstants.ROLL_RULE_PUSH) {
				topCount = getIntParameter("topCount", -1);
				// ���������ж� ��ֹ�ö�����������ѭ������
				if (topCount > rollCount) {
					topCount = rollCount;
				} else {
					topCount = topCount < 0 ? ParameterConstants.TOP_COUNT
							: topCount;// �ö�����
				}
				rrc.attach(Integer.valueOf(topCount));
			}
			long[] indexes = rollRule.obtainIndexes(rrc);

			/**
			 * modify by liuxh 2010.01.14 ����Ƿ�ҳ��ѭ�Ļ� �����¸�ֵindexes
			 */
			if (rollRuleId == ParameterConstants.ROLL_RULE_PAGE) {
				/** �������X�����ظ����� */
				Integer arr[] = ArrayUtil.getRandom(totalCount - 1,
						rollCount > totalCount ? totalCount : rollCount);
				int i = 0;
				for (Integer n : arr) {
					indexes[i] = Long.parseLong(n.toString());
					i++;
				}
			}
			/*** �ö���ѭ���⴦�� */
			if (rollRuleId == ParameterConstants.ROLL_RULE_PUSH) {
				Integer arr[] = ArrayUtil.getRandom(totalCount - 1, rollCount
						- topCount > totalCount ? totalCount : rollCount
						- topCount);
				int i = topCount;
				for (int index = 0; index < topCount; index++) {
					indexes[index] = Long.parseLong(String.valueOf(index));
				}
				for (Integer n : arr) {
					indexes[i] = Long.parseLong(n.toString());
					i++;
				}
			}
			/**
			 * end
			 */
			// System.out.print("���id����=");
			// for(int i=0;i<indexes.length;i++){
			// System.out.print(indexes[i]+",");
			// }
			// System.out.println("");
			if (resourcePackReleation.size() < indexes.length) {
				resourcePackReleation = getResourceService(request)
						.getResourcePackReleationsByOrder(resourcePackId,
								currentPage - 1 < 0 ? 1 : currentPage - 1,
								totalCount, order, totalCount, param);
			}
			list = getIndexResourceBagRelations(indexes, resourcePackReleation);
			System.out.println("��ѭ ---->���۰�=" + resourcePackId + ";��¼����="
					+ recordCount + ";�û�ѡȡ��ѭ��Χ=" + totalCount + ";��ѭ����="
					+ rollRuleId + ";ҳ/��=" + rollCount);
		} else {// ����ѭ
			// ȡ�����������ļ�¼����
			int recordCount = getResourceService(request)
					.getResourcePackReleationsByOrderCount(resourcePackId,
							order, listCount, param);
			if (listCount < 0 || listCount > recordCount) {
				listCount = recordCount;// ���δѡ���б� �����б����������� ����ʾ��¼����
			}

			rollCount = rollCount < 0 ? DEFAULT_PAGE_SIZE : rollCount;// �����Դ����Ϊ�������Ĭ�ϴ�С20��
			resourcePackReleation = getResourceService(request)
					.getResourcePackReleationsByOrder(resourcePackId,
							currentPage, rollCount, order, listCount, param);
			list = resourcePackReleation;
			System.out.println("����ѭ---->���۰�=" + resourcePackId + ";��¼����="
					+ recordCount + ";�û�ѡȡ����ѭ���б�Χ=" + listCount + ";ҳ/��"
					+ rollCount);
		}

		/** ��Ź�����Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		Map resultMap = new HashMap();
		String result = "";
		/** ������Դ�б���󣬷��ؽ�����ʾ */
		List<Object> lsRess = new ArrayList<Object>();
		Iterator it = list.iterator();
		int loop = 0;
		while (it.hasNext()) {
			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			ResourcePackReleation resourceBagRelation = (ResourcePackReleation) it
					.next();
			ResourceAll resource = resourceService
					.getResource(resourceBagRelation.getResourceId());
			String title = "";
			loop++;
			// �ж��Ƿ���ʾ���
			if (!isNotDisSN()) {
				title = rollCount * (currentPage - 1) + loop + "." + title;
			}
			/**
			 * ��װ��תURL ҳ�漶�����+��ƷID+ҳ����ID+����ID+��ĿID+���۰�ID+���۰�����ID+��ԴID+�ƹ�����ID
			 * +��ͨPT����+ҳ�� ��URL �ӵ�ַ����
			 */
			if (resource == null) {
				continue;
			}

			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap = null;
			if (isFee()) {
				feeMap = getFeeLogicService(request).isFee(productId,
						resource.getId(), mobile, resourceBagRelation, packId,
						month_fee_bag_id);
				if (feeMap == null) {
					sb.append(ParameterConstants.PORTAL_PATH);
					sb.append("?");
				} else {
					sb.append(feeMap.get("builder"));
				}
			} else {
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_RESOURCE);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			sb.append(ParameterConstants.COLUMN_ID);
			sb.append("=");
			sb.append(columnId);
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_ID);
			sb.append("=");
			sb.append(resourcePackId);
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
			sb.append("=");
			sb.append(resourceBagRelation.getId());
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(resource.getId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			/**
			 * �������url��
			 * 
			 * @author penglei 2009-10-29 13:46:19
			 * 
			 */
			URLUtil.append(sb, ParameterConstants.ORDER, request);
			URLUtil.append(sb, ParameterConstants.ORDERSUB, request);
			/**
			 * end
			 */
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append(1);
			if (feeMap != null) {
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}
			String url = URLUtil.trimUrl(sb).toString();
			obj.put("url", url);
			StringBuilder linkname = new StringBuilder();
			linkname.append(title);
			String mixtitle = title;
			// System.out.println("mix--->"+mix);
			if (!mix.equals("1")) {
				List<String> mixparams = PpsUtil.getParameters(mix);
				if (mixparams.size() < 1) {
					linkname.append(resource.getName());
				}
				for (String str : mixparams) {

					if (str.equalsIgnoreCase("name")) {
						linkname.append(resource.getName());
					} else if (str.equalsIgnoreCase("bComment")) {
						String bComment = resource.getBComment() == null ? ""
								: resource.getBComment();
						if (StringUtils.isNotEmpty(bComment)) {
							linkname.append(bComment);
							linkname.append(":");
						}
					} else if (str.equalsIgnoreCase("authorId")) {
						// ��������ID��ѯ������Ϣ
						Integer[] authorIds = resource.getAuthorIds();
						if (authorIds.length > 0) {
							for (int i = 0; i < authorIds.length; i++) {
								ResourceAuthor author = getResourceService(
										request).getResourceAuthor(
										resource.getAuthorIds()[i]);
								if (author != null) {
									linkname.append("(");
									linkname
											.append((author.getPenName() == null
													|| StringUtils
															.isEmpty(author
																	.getPenName()) ? author
													.getName()
													: author.getPenName()));
									linkname.append(")");
									break;
								} else {
									continue;
								}
							}

						}
					} else if (str.equalsIgnoreCase("downnum")) {
						linkname.append("(");
						linkname.append(getResourceService(request)
								.getResourceVisits(resource.getId()));
						linkname.append("��)");
					}
					// linkname.append(":");
				}
				mixtitle = linkname.toString();// .substring(0,linkname.lastIndexOf(":"));
			} else {
				linkname.append(resource.getName());
				mixtitle = linkname.toString();
			}
			// ȥ�����һ������
			obj.put("linkname", mixtitle);
			obj.put("resource", resource); // ��������� ��Դ����
			String imgUrl = CoverPreview.getPreview(
					getResourceService(request), resource, 51);// ��Ԥ��ͼ�Ž�ȥ
			obj.put("preview", imgUrl);
			lsRess.add(obj);
		}

		/**
		 * penglei
		 */
		List dateList = getCharts(request); // ��ȡ���а��б�
		String showType = getChartsName(); // ��ȡ���а��б�����
		map.put("showType", showType);
		map.put("date", dateList);
		/**
		 * end
		 */

		map.put("objs", lsRess);

		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service", getResourceService(request));
		/**
		 * ����order��map����
		 * 
		 * @author penglei 2009.10.29
		 */
		map.put("urlOrder", urlOrder);
		map.put("urlOrderSub", urlOrderSub);
		/**
		 * end
		 */
		// �ж������ѭ������(2.�����ȡ��ѯ���� 3���һ��ʱ�������ȡ��ѯ���� )ʱ����ʾ����
		if (!isRoll
				|| (rollRuleId != ParameterConstants.ROLL_RULE_RANDOM && rollRuleId != ParameterConstants.ROLL_RULE_RANDOM_INTERVAL)) {
			// �ж��Ƿ񵼺�
			if (!isNoPageLink()) {// ��ʾ����
				// List listAll = new ArrayList();
				// for (int i = 0; i < (!isRoll?listCount:totalCount); i++) {
				// listAll.add(i);
				// }
				Navigator navi = new Navigator((!isRoll ? listCount
						: totalCount), currentPage, navigatorPageSize, 5);
				request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
			}
		}

		// result = VmInstance.getInstance().parseVM(map, this);

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this, tagTem);

		resultMap.put(TagUtil.makeTag(tagName), result);
		TagLogger.debug("ResourceRoollTag", tagName + "=======������ʱ: "
				+ (System.currentTimeMillis() - tagBegin) + " ms", request
				.getQueryString(), null);
		return resultMap;
	}

	private String getChartsName() {
		String showType = "";// ��ʾ����
		switch (urlOrder) {
		case 6:
			showType = "���ʰ�";
			break;
		case 2:
			showType = "������";
			break;
		case 3:
			showType = "�ղذ�";
			break;
		case 4:
			showType = "������";
			break;
		case 5:
			showType = "���۰�";
			break;
		case 7:
			showType = "ͶƱ��";
			break;
		case 1:
			showType = "���ذ�";
			break;

		default:
			break;
		}
		return showType;
	}

	private List getCharts(HttpServletRequest request) {
		List dateList = new ArrayList();
		String[] times = { "��", "��", "��", "��" };

		for (int i = 0; i < times.length; i++) {
			Map<String, String> temp = new HashMap<String, String>();
			String nameLink = times[i];
			Map<String, String> values = new HashMap<String, String>();
			values.put(ParameterConstants.COLUMN_ID, String.valueOf(columnId));
			values.put(ParameterConstants.PAGE, "c");
			values.put(ParameterConstants.ORDER, String.valueOf(urlOrder));
			values.put(ParameterConstants.ORDERSUB, String.valueOf(times.length
					- i));
			String url = URLUtil.getUrl(values, request);

			temp.put("name", nameLink);
			temp.put("url", urlOrderSub == times.length - i ? "hidden" : url);
			dateList.add(temp);
		}
		return dateList;
	}

	/**
	 * ƴ��ȫ��URL �б��п��ܳ����ڲ�Ʒҳ ��Ŀҳ ��Դҳ ����ҳ
	 * ҳ�漶�����+��ƷID+ҳ����ID+����ID+��ĿID+���۰�ID+���۰�����ID+��ԴID+�½�ID+�ƹ�����ID
	 * +��ͨPT����+ҳ��+ÿҳ��ʾ����+�Ʒ�ID
	 * 
	 * @param request
	 * @param currentPage
	 *            ��ǰҳ ����URL
	 * @param templateid
	 *            ɾ�� ����Դ URL
	 * @param flag
	 *            ɾ������Դ���ӵ��жϱ�־
	 * @param contentid
	 *            ɾ������Դ�����贫�������ID����
	 * @return
	 */
	private String getPageUrl(HttpServletRequest request, String currentPage) {
		StringBuilder sb = new StringBuilder();
		// �õ���ǰ���̵�����
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		URLUtil.append(sb, ParameterConstants.PAGE, request);
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.PRODUCT_ID))) {
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.PAGEGROUP_ID))) {
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.AREA_ID))) {
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.COLUMN_ID))) {
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_BAG_ID))) {
			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_BAG_RELATION_ID))) {
			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_BAG_RELATION_ID))) {
			URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.CHAPTER_ID))) {
			URLUtil.append(sb, ParameterConstants.CHAPTER_ID, request);
		}
		if (!StringUtils.isEmpty(request
				.getParameter(ParameterConstants.CHANNEL_ID))
				|| request.getParameter(ParameterConstants.CHANNEL_ID) == null) {
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		}

		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append(currentPage);
		sb.append("&");
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.WORDAGE))) {
			URLUtil.append(sb, ParameterConstants.WORDAGE, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_ID))) {
			URLUtil.append(sb, ParameterConstants.FEE_ID, request);
		}
		return sb.toString();
	}

	/**
	 * ������ѯ����
	 * 
	 * @param ruleId
	 * @return
	 */
	private IRollRule getRollRule(String ruleId) {
		RollRuleManager rrm = RollRuleManager.getInstance();
		IRollRule rollRule = rrm.getRollRule(ruleId);
		TagLogger.debug("ResorceRollTag",
				"[��ǩ=ResourceRollTag,����=����ͨ�ù��߽ӿڻ�ù����������]", "", null);
		if (rollRule == null) {
			switch (rollRuleId) {
			case ParameterConstants.ROLL_RULE_PUSH:
				rollRule = new PushRollRule(ruleId, rollTime * 60);
				break;
			case ParameterConstants.ROLL_RULE_PAGE:
				rollRule = new PageRollRule(ruleId);
				break;
			case ParameterConstants.ROLL_RULE_RANDOM:
				rollRule = new RandomRollRule(ruleId);
				break;
			case ParameterConstants.ROLL_RULE_RANDOM_INTERVAL:
				rollRule = new IntervalRandomRollRule(ruleId, rollTime * 60);
				break;
			case ParameterConstants.ROLL_RULE_PAGE_INTERVAL:
				// ð����ѭʱ���ö�������ð������
				if (topCount > 0) {
					if (topCount > rollCount)
						topCount = rollCount;
					rollRule = new IntevalPageRollRule(ruleId, rollTime * 60,
							topCount);
				} else
					rollRule = new IntevalPageRollRule(ruleId, rollTime * 60,
							rollCount);
				break;
			default:
				rollRule = new DefaultRollRule(ruleId, rollTime * 60);
				break;
			}
			rrm.addRollRule(rollRule);
		}
		// System.out.println("rollTime -->>" + rollTime);
		rollRule = rrm.getRollRule(ruleId);
		return rollRule;
	}

	/**
	 * ������ѯ�����ж��Ƿ���Ҫ��ѵ
	 * 
	 * @return
	 */
	// private boolean isRollTimes(String key, long currentTime) {
	// TagLogger.debug("ResourceRollTag",
	// "[��ǩ=ResourceRollTag,����=�ж��Ƿ���Ҫ��ѵ,��Դ������Ϊ��,memkey=" + key
	// + ",rollTime=" + rollTime + "]", "", null);
	// if (rollTime <= 0) {
	// return false;
	// }
	// try {
	// // ��ѭ����ʱ��
	// Long strCtime = (Long) getMemcachedClient().get(key + "_time");
	// if (strCtime == null) {
	// getMemcachedClient().set(key + "_time", currentTime,
	// MemCachedClientWrapper.HOUR * 24);
	// getMemcachedClient().set(key + "_rollTime", "0",
	// MemCachedClientWrapper.HOUR * 24);
	// return true;
	// }
	// long times = ((currentTime - strCtime) / 1000 / 60) / rollTime;
	// // ��ǰ��ѭ����
	// String strRtime = (String) getMemcachedClient().get(
	// key + "_rollTime");
	// if (strRtime == null) {
	// strRtime = "0";
	// }
	// long timesBefore = Long.parseLong(strRtime);
	// if (times <= timesBefore) {
	// return false;
	// } else {
	// getMemcachedClient().set(key + "_rollTime", "" + times,
	// MemCachedClientWrapper.HOUR * 24);
	// }
	// } catch (Exception e) {
	// TagLogger.debug("ResourceRollTag",
	// "[��ǩ=ResourceRollTag,����=������ѯ�����ж���ѵ����,ʧ��ԭ��=��MEMCACHE��ȡʱ���ʱ��������]memkey="
	// + key, "", e);
	// }
	// return true;
	// }
	private List getIndexResourceBagRelations(long[] indexes, List list) {
		List result = new ArrayList();
		for (int i = 0; i < indexes.length; i++) {
			// ResourcePackReleation rpr = (ResourcePackReleation) list
			// .get(Integer.parseInt(String.valueOf(indexes[i])));
			// System.out.println("***************" + rpr.getId());
			result.add(list.get(Integer.parseInt(String.valueOf(indexes[i]))));
		}
		return result;
	}

	public String getNextPageLink() {
		return nextPageLink;
	}

	public void setNextPageLink(String nextPageLink) {
		this.nextPageLink = nextPageLink;
	}

	public String getPrePageLink() {
		return prePageLink;
	}

	public void setPrePageLink(String prePageLink) {
		this.prePageLink = prePageLink;
	}

	public String getNavigatorPageLink() {
		return navigatorPageLink;
	}

	public void setNavigatorPageLink(String navigatorPageLink) {
		this.navigatorPageLink = navigatorPageLink;
	}

	public String getGotoPageLink() {
		return gotoPageLink;
	}

	public void setGotoPageLink(String gotoPageLink) {
		this.gotoPageLink = gotoPageLink;
	}

	public int getColumnId() {
		return columnId;
	}

	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}

	public int getResourcePackId() {
		return resourcePackId;
	}

	public void setResourcePackId(int resourcePackId) {
		this.resourcePackId = resourcePackId;
	}

	public String getTitlePattern() {
		return titlePattern;
	}

	public void setTitlePattern(String titlePattern) {
		this.titlePattern = titlePattern;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getRollCount() {
		return rollCount;
	}

	public void setRollCount(int rollCount) {
		this.rollCount = rollCount;
	}

	public int getTopCount() {
		return topCount;
	}

	public void setTopCount(int topCount) {
		this.topCount = topCount;
	}

	public int getRollTime() {
		return rollTime;
	}

	public void setRollTime(int rollTime) {
		this.rollTime = rollTime;
	}

	public int getRollRuleId() {
		return rollRuleId;
	}

	public void setRollRuleId(int rollRuleId) {
		this.rollRuleId = rollRuleId;
	}

	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
	}

	/**
	 * @return the notDisSN
	 */
	public boolean isNotDisSN() {
		return this.notDisSN;
	}

	/**
	 * @param notDisSN
	 *            the notDisSN to set
	 */
	public void setNotDisSN(boolean notDisSN) {
		this.notDisSN = notDisSN;
	}

	private CustomService getCustomService(HttpServletRequest request) {
		if (customService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			customService = (CustomService) wac.getBean("customService");
		}
		return customService;
	}

	private ResourceService getResourceService(HttpServletRequest request) {
		if (resourceService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			resourceService = (ResourceService) wac.getBean("resourceService");
		}
		return resourceService;
	}

	private BussinessService getBussinessService(HttpServletRequest request) {
		if (bussinessService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			bussinessService = (BussinessService) wac
					.getBean("bussinessService");
		}
		return bussinessService;
	}

	private FeeLogicService getFeeLogicService(HttpServletRequest request) {
		if (feeLogicService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			feeLogicService = (FeeLogicService) wac.getBean("feeLogicService");
		}
		return feeLogicService;
	}
}
