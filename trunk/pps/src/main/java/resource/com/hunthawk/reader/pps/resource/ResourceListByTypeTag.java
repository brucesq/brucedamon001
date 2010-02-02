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
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
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
import com.hunthawk.tag.vm.VmInstance;

/**
 * ͬ����Դ�б��ǩ ��ǩ���ƣ�sametype_resource_list ����˵���� /** ����˵����
 * 
 * @param notDisSN
 *            �Ƿ���ʾ��� ( 1.�� -1.��)
 * @param isFee
 *            �Ƿ���ʾ�Ʒ� ( 1.�� -1.��)
 * @param isRoll
 *            �Ƿ���ѭ ( 1.�� -1.��)
 * @param pageSize  ��ʾ����Դ����
 */

public class ResourceListByTypeTag extends BaseTag {

	private static final long serialVersionUID = -498520875520787274L;
	private static final int DEFAULT_COUNT = 20;

	private ResourceService resourceService;
	private BussinessService bussinessService;
	private CustomService customService;
	private FeeLogicService feeLogicService;
//	private MemCachedClientWrapper memcachedClient;
//
//	public MemCachedClientWrapper getMemcachedClient() {
//		return memcachedClient;
//	}

	/** �Ƿ�Ʒ� */
	private boolean isFee;

	public boolean isFee() {
		return isFee;
	}
	/** ���۰�Id */
	private int resourcePackId;
	/** �������� */
	private String titlePattern;
	/** ����ʾ��� */
	private boolean notDisSN;


	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String mobile = RequestUtil.getMobile();
		String productId = request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id = ParamUtil.getIntParameter(request,
				ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId = ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_ID, -1);
		String resId = URLUtil.getResourceId(request);

		String mix = getParameter("mix", "0");// title��� Ĭ��ֻ��ʾ��Դ���Ʋ������
		// ���ڼ����ǩ����ʱ��
//		long currentTime = System.currentTimeMillis();
//		long tagBegin = currentTime;
		// �Ƿ���ʾ���
		notDisSN = getIntParameter("isDisSN", -1) < 0 ? true : false;
		// �Ƿ�Ʒ�
		isFee = getIntParameter("isFee", -1) > 0 ? true : false; // (1.url���ּƷѴ���
		// �ƷѴ���)
		int pageGroupId = ParamUtil.getIntParameter(request,
				ParameterConstants.PAGEGROUP_ID, -1);
		if (pageGroupId < 0) {
			TagLogger.debug(tagName, "ҳ�������Ϊ��", request.getQueryString(), null);
			return new HashMap();
		}
		int count = getIntParameter("pageSize", 5);
		if (count < 0 || count > DEFAULT_COUNT)
			count = DEFAULT_COUNT;
		List<Map> list=getResourceService(request).findCategoryWideResource(resId, pageGroupId, count);
		/** ��Ź�����Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		Map resultMap = new HashMap();
		String result = "";
		/** ������Դ�б���󣬷��ؽ�����ʾ */
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		ResourceAll resource=null;
		for (Iterator it = list.iterator(); it.hasNext();) {
			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			Map<String,Object> m=(Map)it.next();
			ResourcePackReleation rpr =(ResourcePackReleation)m.get("releation"); //(ResourcePackReleation) it.next();
			resource = resourceService.getResource(rpr.getResourceId());
			String title = "";
			loop++;
			// �ж��Ƿ���ʾ���
			if (!isNotDisSN()) {
				title = loop + "." + title;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap = null;
			if (isFee()) {
				feeMap = getFeeLogicService(request).isFee(productId,
						resource.getId(), mobile, rpr, packId,
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
			sb.append(m.get("columnId"));
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_ID);
			sb.append("=");
			sb.append(rpr.getPack().getId());
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
			sb.append("=");
			sb.append(rpr.getId());
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(resource.getId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
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
					System.out.println(str);
					if (str.equalsIgnoreCase("name")) {
						linkname.append(resource.getName());
					} else if (str.equalsIgnoreCase("bComment")) {
						linkname.append(resource.getBComment() == null ? ""
								: resource.getBComment());
					} else if (str.equalsIgnoreCase("authorId")) {
						if(!resource.getId().startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE)) && !resource.getId().startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){
							// ��������ID��ѯ������Ϣ
							Integer[] authorIds = resource.getAuthorIds();
							if (authorIds.length > 0) {
								for (int i = 0; i < authorIds.length; i++) {
									ResourceAuthor author = getResourceService(
											request).getResourceAuthor(
											resource.getAuthorIds()[i]);
									if (author != null) {
										linkname
												.append((author.getPenName() == null
														|| StringUtils
																.isEmpty(author
																		.getPenName()) ? author
														.getName()
														: author.getPenName()));
										break;
									} else {
										continue;
									}
								}

							}
						}
					} else if (str.equalsIgnoreCase("downnum")) {
						linkname.append(resource.getDownnum() == null ? ""
								: resource.getDownnum());
					}
					linkname.append(ParameterConstants.DEFAULT_SPLIT);
				}
				mixtitle = linkname.substring(0, linkname
						.lastIndexOf(ParameterConstants.DEFAULT_SPLIT));
			} else {
				linkname.append(resource.getName());
				mixtitle += linkname.toString();
			}
			// ȥ�����һ������
			obj.put("linkname", mixtitle);
			obj.put("resource", resource); // ��������� ��Դ����
			String imgUrl = CoverPreview.getPreview(
					getResourceService(request), resource, 51);// ��Ԥ��ͼ�Ž�ȥ
			obj.put("preview", imgUrl);
			obj.put("author", getAuthorName(resource, request));
			lsRess.add(obj);
		}

		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service", getResourceService(request));
		map.put("cname", "ͬ����Դ�б�");
		

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);

		/*
		 * result = VmInstance.getInstance().parseVM(map, this);
		 * resultMap.put(TagUtil.makeTag(tagName), result);
		 */
		return resultMap;
	}

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

	/**
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


	public void setFee(boolean isFee) {
		this.isFee = isFee;
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
	
	private String getAuthorName(ResourceAll resource,HttpServletRequest request){
		Integer [] authorids=resource.getAuthorIds();
		StringBuilder str=new StringBuilder();
		String name="";
		for(int i=0;i<authorids.length;i++){
			ResourceAuthor author=getResourceService(request).getResourceAuthor(authorids[i]);
			str.append((author.getPenName()==null || "".equals(author.getPenName()))?author.getName():author.getPenName());
			str.append(",");
			if(i==authorids.length-1){
				//ȥ�����һ��,
				str.replace(str.lastIndexOf(","), str.length(), "");
			}
		}
		String authorPenName=str.toString().trim();
		try {
			name = StrUtil.getLimitStr(authorPenName, ParameterConstants.AUTHOR_NAME_BYTES, ParameterConstants.REPLACE_SYMBOL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//����
		return name;
	}
}
