package com.hunthawk.reader.pps.resource;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterCommonTextLink;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeLogicService;
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * ͼ�����Ա�ǩ
 * 
 * @author liuxh ����˵���� property:ͼ������ number:���ֵ�ҳ��(�ڵڼ�ҳ֮ǰ��ʾ������һҳ) title:��������
 *         modify by liuxh :������ ���ϴο����� split �����зָ��� add by liuxh 09-09-02
 */
public class EbookTag extends BaseTag {
	private ResourceService resourceService;
	private CustomService customService;
	private IphoneService iphoneService;
	private FeeLogicService feeLogicService;
	private BussinessService bussinessService;
	private boolean isCatalog;
	
	private TagTemplate tagTem ;

	public boolean isCatalog() {
		return isCatalog;
	}

	public void setCatalog(boolean isCatalog) {
		this.isCatalog = isCatalog;
	}

	private String split;

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	// private boolean notControl;
	// public boolean isNotControl() {
	// return notControl;
	// }
	// public void setNotControl(boolean notControl) {
	// this.notControl = notControl;
	// }
	private int number;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	private int currentPage;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String n = getParameter("number", "0");
		int tagTemplateId = this.getIntParameter("tmd", 0);
		
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
			
		}else{
			tagTem = null;
		}
		split = getParameter("split", "");
		if (!StringUtils.isEmpty(n)) {
			try {
				this.number = Integer.parseInt(n);
			} catch (Exception e) {
				TagLogger.debug(tagName, "��ǩnumber����ֵ��Ч", request
						.getQueryString(), e);
			}
		}
		if (request.getParameter(ParameterConstants.PAGE_NUMBER) != null
				&& !"".equals(request
						.getParameter(ParameterConstants.PAGE_NUMBER))) {
			this.currentPage = Integer.parseInt(request
					.getParameter(ParameterConstants.PAGE_NUMBER));
		} else {
			this.currentPage = 1;
		}
		// �õ���ԴID ��ѯ��Դʵ���װ��Դ�������Ա�ǩ

		String property = getParameter("property", "");
		// �õ���ԴID
		String resID = URLUtil.getResourceId(request);// request.getParameter(ParameterConstants.RESOURCE_ID);
		if (StringUtils.isEmpty(property)) {
			TagLogger.debug(tagName, "EbookTag��ǩproperty����Ϊ�ա�", request
					.getQueryString(), null);
		} else {
			if (StringUtils.isNotEmpty(resID)) {
				ResourceAll resource = getResourceService(request).getResource(
						resID);
				if (resource != null) {
					if (property.equalsIgnoreCase("name")) {
						return resourceName(request, tagName, resource);
					} else if (property.equalsIgnoreCase("read")) {
						return ebookRead(request, tagName, resource);
					} else if (property.equalsIgnoreCase("status")) {
						return ebookStatus(request, tagName, resource);
					} else if (property.equalsIgnoreCase("nextchapter")) {
						return ebookNextChapter(request, tagName, resource);
					} else if (property.equalsIgnoreCase("prechapter")) {
						return ebookPreChapter(request, tagName, resource);
					}
					// else if(property.equalsIgnoreCase("back")){
					// return ebookBackToCatalog(request,tagName,resource);
					// }
					else if (property.equalsIgnoreCase("updatetime")) {
						return ebookUpdateTime(request, tagName, resource);
					} else if (property.equalsIgnoreCase("catalog")) {
						return ebookCatalog(request, tagName, resource);
					} else if (property.equalsIgnoreCase("lastcontent")) {// ���ϴο�
						return ebookLastContent(request, tagName, resource);
					}
				} else {
					TagLogger.debug(tagName, "IDΪ" + resID + "����Դ������!", request
							.getQueryString(), null);
				}
			}
		}
		return new HashMap();
	}

	private String getBookStatus(int status) {
		String typeName = "δ֪״̬";
		for (Map.Entry<String, Integer> entry : Constants.getResourceFinished()
				.entrySet()) {
			if (entry.getValue().equals(status))
				return entry.getKey();
		}
		return typeName;
	}

	/** ���ϴο���ǩ */
	private Map ebookLastContent(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			String mobile=RequestUtil.getMobile();
			String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
			String resourceId=resource.getId();
			int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
			int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
			
			ResourcePackReleation rel = null;
			if (ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1)!= -1) {
				rel = getResourceService(request).getResourcePackReleation(ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1));
			}
			String title = getParameter("title", "���ϴο�");
			String url = getCustomService(request).getLastUserFootprint(RequestUtil.getMobile(), resource.getId());
			StringBuilder sb = new StringBuilder();
			if (StringUtils.isEmpty(url)) {// �����һ�µ�һҳ �жϼƷ���Ϣ
				sb.append(request.getContextPath());
				Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rel, packId, month_fee_bag_id);
				if(feeMap==null){
					sb.append(ParameterConstants.PORTAL_PATH);
					sb.append("?");
				}else{
					sb.append(feeMap.get("builder"));
				}
				sb.append(ParameterConstants.PAGE);
				sb.append("=");
				sb.append(ParameterConstants.PAGE_DETAIL);
				sb.append("&");
				URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
				URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(sb, ParameterConstants.AREA_ID, request);
				URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
				URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
				URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID,
						request);
				URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
				sb.append(ParameterConstants.CHAPTER_ID);
				sb.append("=");
				sb.append(URLUtil.getResourceId(request) + "001");
				sb.append("&");
				URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
				sb.append(ParameterConstants.PAGE_NUMBER);
				sb.append("=");
				sb.append("1");
				sb.append("&");
				sb.append(ParameterConstants.WORDAGE);
				sb.append("=");
				sb.append(ParameterConstants.CHAPTER_CONTENT_WORD_SET);
				if(feeMap!=null){
					sb.append("&");
					sb.append(ParameterConstants.FEE_ID);
					sb.append("=");
					sb.append(feeMap.get("feeId"));
				}
			}
			Map velocityMap = new HashMap();
			velocityMap.put("title", title);
			velocityMap.put("url", StringUtils.isEmpty(url) ? sb.toString()
					: url);
			/**
			 * ����ֻ��ź��ֻ����͵�map���� start
			 */
			// ����ֻ���
			velocityMap.put("mobile", RequestUtil.getMobile());
			// ����ֻ�����
			velocityMap.put("mobileType", RequestUtil.getMobileType());
			velocityMap.put("strUtil", new StrUtil());
			velocityMap.put("currentURL", URLUtil.getCurrentURL(request));
			/**
			 * end
			 * @author penglei 2009.10.30
			 */
			Map resultMap = new HashMap();
			/**
			 * ��ǩģ�������
			 * modify by liuxh 09-11-03
			 */
			String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
//			resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
//					.parseVM(velocityMap, this));
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** �Ķ���ǩ */
	private Map ebookRead(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			String mobile=RequestUtil.getMobile();
			String resourceId=resource.getId();
			String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
			int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
			int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
			
			String title = getParameter("title", "�����Ķ�");
			ResourcePackReleation rel = null;
			if (ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1)!= -1) {
				rel = getResourceService(request).getResourcePackReleation(ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1));
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rel, packId, month_fee_bag_id);
			/**�½ڿ��Ƶ�*/
			int choicePoint =rel==null?0:rel.getChoice()==null?0:rel.getChoice();
			
			if(feeMap==null  || choicePoint!=1 ){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_DETAIL);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID,request);
			sb.append(ParameterConstants.CHAPTER_ID);
			sb.append("=");
			sb.append(resource.getId() + "001");
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if (StringUtils.isNotEmpty(request.getParameter(ParameterConstants.WORDAGE))) {
				sb.append("&");
				sb.append(ParameterConstants.WORDAGE);
				sb.append("=");
				sb.append(request.getParameter(ParameterConstants.WORDAGE));
			} 
			if(feeMap!=null && choicePoint==1){
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}

			Map velocityMap = new HashMap();
			velocityMap.put("title", title);
			velocityMap.put("url", URLUtil.trimUrl(sb).toString());
			Map resultMap = new HashMap();
			/**
			 * ��ǩģ�������
			 * modify by liuxh 09-11-03
			 */
			String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
//			resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
//					.parseVM(velocityMap, this));
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** Ŀ¼���� */
	private Map ebookCatalog(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		this.isCatalog = true;
		if (this.currentPage >= this.number || this.number == 0) {
			String title = getParameter("title", "����Ŀ¼");
			StringBuilder builder = new StringBuilder();
			builder.append(request.getContextPath());
			builder.append(ParameterConstants.PORTAL_PATH);
			builder.append("?");
			builder.append(ParameterConstants.PAGE);
			builder.append("=");
			builder.append(ParameterConstants.PAGE_RESOURCE);
			builder.append("&");
			URLUtil.append(builder, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(builder, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(builder, ParameterConstants.AREA_ID, request);
			URLUtil.append(builder, ParameterConstants.COLUMN_ID, request);
			URLUtil.append(builder, ParameterConstants.FEE_BAG_ID, request);
			URLUtil.append(builder, ParameterConstants.FEE_BAG_RELATION_ID,
					request);
			builder.append(ParameterConstants.RESOURCE_ID);
			builder.append("=");
			builder.append(URLUtil.getResourceId(request));
			builder.append("&");
			URLUtil.append(builder, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(builder, ParameterConstants.UNICOM_PT, request);
			builder.append(ParameterConstants.PAGE_NUMBER);
			builder.append("=");
			builder.append(1);
			builder.append("&");
			URLUtil.append(builder, ParameterConstants.FEE_ID, request);

			Map velocityMap = new HashMap();
			velocityMap.put("title", title);
			velocityMap.put("url", URLUtil.trimUrl(builder).toString());
			Map resultMap = new HashMap();
			/**
			 * ��ǩģ�������
			 * modify by liuxh 09-11-03
			 */
			String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
//			resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
//					.parseVM(velocityMap, this));
			return resultMap;
		} else {
			return new HashMap();
		}
		// this.currentPage=this.number+1;
		// String
		// title=getParameter("title",ParameterCommonTextLink.PARAM_BOOK_CATALOG);
		// StringBuilder sb=new StringBuilder();
		// sb.append(request.getContextPath());
		// sb.append(ParameterConstants.PORTAL_PATH);
		// sb.append("?");
		// URLUtil.append(sb, ParameterConstants.PAGE, request);
		// URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		// URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		// URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		// URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		// URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		// URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		// URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
		// URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		// sb.append(ParameterConstants.PAGE_NUMBER);
		// sb.append("=");
		// sb.append(1);
		// sb.append("&");
		// URLUtil.append(sb, ParameterConstants.FEE_ID, request);
		// URLUtil.trimURL(sb);
		//		
		// Map velocityMap = new HashMap();
		// velocityMap.put("title", title);
		// velocityMap.put("url",sb.toString());
		// Map resultMap = new HashMap();
		// resultMap.put(TagUtil.makeTag(tagName),VmInstance.getInstance().parseVM(velocityMap,
		// this));
		// return resultMap;
	}

	/** ����ʱ�� */
	private Map ebookUpdateTime(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			Map resultMap = new HashMap();
			String title = getParameter("title", "");
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(resource.getCreateTime());
			String time = title + calendar.get(calendar.YEAR) + "��"
					+ (calendar.get(calendar.MONTH) + 1) + "��"
					+ calendar.get(calendar.DAY_OF_MONTH) + "��";
			resultMap.put(TagUtil.makeTag(tagName), time + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	public static void main(String[] args) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(new java.util.Date());
		String date = calendar.get(calendar.YEAR) + "��"
				+ (calendar.get(calendar.MONTH) + 1) + "��"
				+ calendar.get(calendar.DAY_OF_MONTH) + "��";
		// + calendar.get(calendar.HOUR_OF_DAY) + ":"
		// +
		// (calendar.get(calendar.MINUTE)<10?"0"+calendar.get(calendar.MINUTE):calendar.get(calendar.MINUTE));
		System.out.println(date);
	}

	/** ͼ��״̬ */
	private Map ebookStatus(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			Map resultMap = new HashMap();
			String title = getParameter("title", "")
					+ getBookStatus(resource.getIsFinished());
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** ͼ������ */
	private Map resourceName(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number || this.number == 0) {
			String title = getParameter("title", "") + resource.getName();
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), title + split);
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** �½��� */
	private Map ebookChapterCount(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		if (this.currentPage <= this.number) {
			Map resultMap = new HashMap();
			String label = getParameter("title",
					ParameterCommonTextLink.PARAM_BOOK_CHAPTER_COUNT);
			int count = 0;
			if (resource.getId().startsWith("1")) {
				Ebook ebook = (Ebook) resource;
				count = Integer.parseInt(getResourceService(request)
						.getEbookChaptersByResourceIDCount(ebook.getId())
						.toString());
			} else {
				count = 0;
				TagLogger.debug(tagName, "����ͼ����Դû���½���Ϣ", request
						.getQueryString(), null);
			}
			resultMap.put(TagUtil.makeTag(tagName), label
					+ (count == 0 ? "" : count));
			return resultMap;
		} else {
			return new HashMap();
		}
	}

	/** ��һ�� */
	private Map ebookPreChapter(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		String resourceId=resource.getId();
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		
		String title = getParameter("title", "");
//		Ebook ebook = (Ebook) resource;
		/**��ǰ�½�ID*/
		String chapterID = request.getParameter(ParameterConstants.CHAPTER_ID);
		/**��һ��ID*/
		String preChapterId = getResourceService(request).browseResourceChapter(chapterID, true);
		if (title.indexOf("!chaptername!") < 0) {
			if (StringUtils.isEmpty(preChapterId)) {//
				TagLogger.debug(tagName, "�Ѿ��ǵ�һ��  ����һ����Ϣ", request
						.getQueryString(), null);
				return new HashMap();
			}
		} else {
			if (StringUtils.isEmpty(preChapterId)) {
				preChapterId = URLUtil.getResourceId(request) + "001";
			}
		}
		ResourcePackReleation rel = null;
		if (ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1)!= -1) {
			rel = getResourceService(request).getResourcePackReleation(ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1));
		}
	
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rel, packId, month_fee_bag_id);
		/**�½ڿ��Ƶ�*/
		int choicePoint = rel.getChoice()==null?0:rel.getChoice();
		// �����½�ID�����½�����
		EbookChapterDesc ec = getResourceService(request).getEbookChapterDesc(preChapterId);
		if(feeMap==null  || ec.getChapterIndex()<choicePoint){
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
		}else{
			sb.append(feeMap.get("builder"));
		}
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_DETAIL);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		sb.append(ParameterConstants.CHAPTER_ID);
		sb.append("=");
		sb.append(preChapterId);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		if (StringUtils.isNotEmpty(request.getParameter(ParameterConstants.WORDAGE))) {
			sb.append("&");
			sb.append(ParameterConstants.WORDAGE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.WORDAGE));
		} 
		if(feeMap!=null && ec.getChapterIndex()>=choicePoint){
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeMap.get("feeId"));
		}

	
		if (ec != null) {
			Map velocityMap = new HashMap();

			if (title.indexOf("!chaptername!") > 0) {
				title = title.replaceAll("!chaptername!", "");
			} else {
				title = ("".equals(getParameter("title", "")) ? ParameterConstants.PRE_CHAPTER_LINK
						: getParameter("title", ""))
						+ " " + ec.getName();
			}
			velocityMap.put("title", title);
			velocityMap.put("url", sb.toString());
			Map resultMap = new HashMap();
			/**
			 * ��ǩģ�������
			 * modify by liuxh 09-11-03
			 */
			String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
//			resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
//					.parseVM(velocityMap, this));
			return resultMap;
		} else {
			TagLogger.debug(tagName, "IDΪ" + preChapterId+ "���½���Ϣ������,�޷���ʾ��һ������", request.getQueryString(), null);
			return new HashMap();
		}
	}

	/** ��һ�� */
	private Map ebookNextChapter(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		String resourceId=resource.getId();
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		
		String title = getParameter("title", "");
//		Ebook ebook = (Ebook) resource;
		String currChapter = request.getParameter(ParameterConstants.CHAPTER_ID);
		String nextChapterId = getResourceService(request).browseResourceChapter(currChapter, false);
		if (title.indexOf("!chaptername!") < 0) {
			if (StringUtils.isEmpty(nextChapterId)) {// �Ѿ������һ�� û����һ����Ϣ
				return new HashMap();
			}
		} else {
			if (StringUtils.isEmpty(nextChapterId)) {
				String rid = URLUtil.getResourceId(request);
				// �ж���Դ���� ��ȡ�½�����
				int chapterTotalCount = 0;
				if (rid.startsWith(String.valueOf(ResourceType.TYPE_BOOK))) {
					chapterTotalCount = Integer.parseInt(getResourceService(
							request).getEbookChaptersByResourceIDCount(rid)
							.toString());
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_COMICS))) {
					chapterTotalCount = getResourceService(request)
							.getComicsChaptersByResourceIDCount(rid);
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_MAGAZINE))) {
					chapterTotalCount = getResourceService(request)
							.getMagazineChaptersByResourceIDCount(rid);
				} else if (rid.startsWith(String
						.valueOf(ResourceType.TYPE_NEWSPAPERS))) {
					chapterTotalCount = getResourceService(request)
							.getNewsPapersChaptersByResourceIDCount(rid);
				}
				String endCount = "";
				if (chapterTotalCount < 100) {
					if (chapterTotalCount < 10) {
						endCount = "00" + chapterTotalCount;
					} else {
						endCount = "0" + chapterTotalCount;
					}
				} else {
					endCount = String.valueOf(chapterTotalCount);
				}
				nextChapterId = rid + endCount;
			}
		}
		ResourcePackReleation rel = null;
		if (ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1)!= -1) {
			rel = getResourceService(request).getResourcePackReleation(ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1));
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rel, packId, month_fee_bag_id);
		/**�½ڿ��Ƶ�*/
		int choicePoint =rel==null?0: rel.getChoice()==null ?0:rel.getChoice();
		EbookChapterDesc ec = getResourceService(request).getEbookChapterDesc(nextChapterId);
		if(feeMap==null  || ec.getChapterIndex()<choicePoint){
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
		}else{
			sb.append(feeMap.get("builder"));
		}
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_DETAIL);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		sb.append(ParameterConstants.CHAPTER_ID);
		sb.append("=");
		sb.append(nextChapterId);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		if (StringUtils.isNotEmpty(request.getParameter(ParameterConstants.WORDAGE))) {
			sb.append("&");
			sb.append(ParameterConstants.WORDAGE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.WORDAGE));
		} 
		if(feeMap!=null && ec.getChapterIndex()>=choicePoint){
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeMap.get("feeId"));
		}

		if (ec != null) {
			Map velocityMap = new HashMap();

			if (title.indexOf("!chaptername!") > 0) {
				// title = title.replaceAll("!chaptername!",
				// String.valueOf(ec.getName()));
				title = title.replaceAll("!chaptername!", "");
			} else {
				title = ("".equals(getParameter("title", "")) ? ParameterConstants.NEXT_CHAPTER_LINK
						: getParameter("title", ""))
						+ " " + ec.getName();
			}
			// String title = ("".equals(getParameter("title", "")) ?
			// ParameterConstants.NEXT_CHAPTER_LINK
			// : getParameter("title", ""))
			// + " " + ec.getName();
			velocityMap.put("title", title);
			velocityMap.put("url", sb.toString());
			Map resultMap = new HashMap();
			/**
			 * ��ǩģ�������
			 * modify by liuxh 09-11-03
			 */
			String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
//			resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
//					.parseVM(velocityMap, this));
			return resultMap;
		} else {
			TagLogger.debug(tagName, "IDΪ" + nextChapterId
					+ "���½���Ϣ������,�޷���ʾ��һ�½�����", request.getQueryString(), null);
			return new HashMap();
		}
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

	private IphoneService getIphoneService(HttpServletRequest request) {
		if (iphoneService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			iphoneService = (IphoneService) wac.getBean("iphoneService");
		}
		return iphoneService;
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
	private BussinessService getBussinessService(HttpServletRequest request) {
		if (bussinessService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			bussinessService = (BussinessService) wac.getBean("bussinessService");
		}
		return bussinessService;
	}
}
