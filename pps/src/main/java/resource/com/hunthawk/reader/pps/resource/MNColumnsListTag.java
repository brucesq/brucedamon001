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

import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
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
 * ��ֽ/��־�� ��Ŀ�б��ǩ ��ǩ���ƣ� ����˵��: noPageLink:�Ƿ񵼺� (-1.�� 1.��) pageSize:ÿҳ��ʾ����Դ����
 * //showType:��Դ���� (1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ)
 * 
 * @author liuxh
 * 
 */
public class MNColumnsListTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	private FeeLogicService feeLogicService;
	private BussinessService bussinessService;
	private static final int DEFAULT_PAGE_SIZE = 10; // Ĭ����ʾ 10��
	/** ����ʾ��ҳ��ص����� */
	private boolean noPageLink;

	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		// �õ���Դ����
		int showType = Integer.parseInt(URLUtil.getResourceId(request)
				.substring(0, 1));
		if (showType == ResourceType.TYPE_MAGAZINE) {// ��־
			return magazineTag(request, tagName);
		} else if (showType == ResourceType.TYPE_NEWSPAPERS) {// ��ֽ
			return newspapers(request, tagName);
		} else if (showType == ResourceType.TYPE_COMICS) {// ����
			return comicsTag(request, tagName);
		}
		return new HashMap();
	}

	/**
	 * ���� �½��б�
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map comicsTag(HttpServletRequest request, String tagName) {
		
		String rid = URLUtil.getResourceId(request);
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE: getIntParameter("pageSize", -1);
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));
		// �ж��Ƿ񵼺�
		if (!isNoPageLink()) {
			int totalCount = getResourceService(request)
					.getComicsChaptersByResourceIDCount(rid);
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		int relId = ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_RELATION_ID, -1);// ���۰�������ϵID
		ResourcePackReleation rel = null;
		if (relId != -1) {
			rel = getResourceService(request).getResourcePackReleation(relId);
		}
		List<ComicsChapter> comics = getResourceService(request)
				.getComicsChaptersByResourceId(rid, currentPage, pageSize);
		Map feeMap=getFeeLogicService(request).isFee(productId, rid, mobile, rel, packId, month_fee_bag_id);
		for (Iterator it = comics.iterator(); it.hasNext();) {
			loop++;
			ComicsChapter com = (ComicsChapter) it.next();
			String title = com.getName();
			String tomeId = com.getTomeId();

			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());

			/**�½ڿ��Ƶ�*/
			int choicePoint =rel==null?0: rel.getChoice()==null?0:rel.getChoice();
			if(feeMap==null  || com.getChapterIndex()<choicePoint){
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
			sb.append(com.getId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if(feeMap!=null){
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}

			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			if (tomeId != null && !"".equals(tomeId)) {
				EbookTome tome = getResourceService(request).getEbookTomeById(
						tomeId);
				obj.put("tomeTitle", tome.getName());
			}
			obj.put("url", sb.toString());
			obj.put("title", title);
			obj.put("chapter", com);
			lsRess.add(obj);
		}
		map.put("isTomeList", false);
		map.put("byTomeId", false);
		map.put("objs", lsRess);
		
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		
		
	/**	result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);*/
		return resultMap;
	}

	/**
	 * ��־
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map magazineTag(HttpServletRequest request, String tagName) {
		String rid = URLUtil.getResourceId(request);
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		
		// �Ƿ񵼺�
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		// ÿҳ��ʾ����
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER));// ��ǰҳ����Ĭ��Ϊ��һҳ

		// �ж��Ƿ񵼺�
		if (!isNoPageLink()) {
			int totalCount = getResourceService(request)
					.getMagazineChaptersByResourceIDCount(rid);
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		
		List<MagazineChapterDesc> magazineChapters = getResourceService(request)
				.getMagazineChaptersByResourceID(rid, currentPage, pageSize);
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		int relId = ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_RELATION_ID, -1);// ���۰�������ϵID
		ResourcePackReleation rel = null;
		if (relId != -1) {
			rel = getResourceService(request).getResourcePackReleation(relId);
		}
		Map feeMap=getFeeLogicService(request).isFee(productId, rid, mobile, rel, packId, month_fee_bag_id);
		for (Iterator it = magazineChapters.iterator(); it.hasNext();) {
			loop++;
			MagazineChapterDesc magazine = (MagazineChapterDesc) it.next();
			String title = magazine.getName();
			String tomeId = magazine.getTomeId();

			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
//			/**�½ڿ��Ƶ�*/
			int choicePoint = rel.getChoice()==null?0:rel.getChoice();
			/**
			 * �޸ļƷѿ��Ƶ�Ϊ��
			 * modify by liuxh 09-11-06
			 */
			EbookTome tome=getResourceService(request).getEbookTomeById(magazine.getTomeId());
			if(feeMap==null  || tome.getTomeIndex()<choicePoint){
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
			sb.append(magazine.getId());
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
			if(feeMap!=null){
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}

			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			if (tomeId != null && !"".equals(tomeId)) {
//				EbookTome tome = getResourceService(request).getEbookTomeById(
//						tomeId);
				obj.put("tomeTitle", tome.getName());
			}
			obj.put("url", sb.toString());
			obj.put("title", title);
			obj.put("chapter", magazine);
			lsRess.add(obj);
		}
		map.put("isTomeList", false);
		map.put("byTomeId", false);
		map.put("objs", lsRess);
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
	/**	result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);*/
		return resultMap;
	}

	/**
	 * ��ֽ
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map newspapers(HttpServletRequest request, String tagName) {
		// �Ƿ���ʾ����
		String flag = request.getParameter(ParameterConstants.SHOW_FLAG); // show=1
		// ��ʾ
		// show=0����ʾ
		boolean isShowTitle = flag == null ? true : (flag.equals("1") ? true
				: false);
		String tomeId = request.getParameter(ParameterConstants.TOME_ID);
		if (isShowTitle && tomeId == null) {// ��ʾ���±���
			return getChaptersByResourceID(request, tagName);
		} else {
			if (tomeId != null && !"".equals(tomeId)) {// ���ݾ�ID��ѯ��Ŀ�µ������б�
				return getChaptersByTomeID(request, tagName);
			} else {// ��ѯ���б�
				return getTomesList(request, tagName);
			}
		}
	}

	/**
	 * ���б�
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map getTomesList(HttpServletRequest request, String tagName) {
		String tomeId = "";
		boolean isOut = false;// �Ƿ񵯳��ʷ���ʾ
		// �õ���ԴID
		String rid = URLUtil.getResourceId(request);
		// �Ƿ񵼺�
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		// ÿҳ��ʾ����
		int pageSize = getIntParameter("pageSize", -1);
		// �Ƿ���ʾ����
		boolean isShowTitle = getIntParameter("isShowTitle", 1) > 0;// Ĭ����ʾ
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		if (pageSize < 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}
		int currentPage = 1;// ��ǰҳ����Ĭ��Ϊ��һҳ
		try {
			currentPage = Integer.parseInt(request
					.getParameter(ParameterConstants.PAGE_NUMBER));
		} catch (Exception e) {
			TagLogger.debug(tagName, "����ԭ��=currentPageת����ʱ�쳣]", request
					.getQueryString(), e);
		}
		// �ж��Ƿ񵼺�
		if (!isNoPageLink()) {
//			List resAll = new ArrayList();
			int totalCount = getResourceService(request).getEbookTomeCount(rid);
//			for (int i = 0; i < totalCount; i++) {
//				resAll.add(new Object());
//			}
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		int relId = -1;// ���۰���ϵID
		List<EbookTome> newspagerTomes = getResourceService(request)
				.getEbookTomeByResourceId(rid,1,1000);
		System.out.println("��Դ" + rid + "���б����Ϊ��" + newspagerTomes.size());
		for (Iterator it = newspagerTomes.iterator(); it.hasNext();) {
			loop++;
			EbookTome tome = (EbookTome) it.next();
			String tomeName = tome.getName();// ��(��Ŀ)����
			tomeId = tome.getId();// ��ID

			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());

			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");

			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_RESOURCE);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(rid);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeId);
			sb.append("&");
			sb.append(ParameterConstants.TOME_ID);
			sb.append("=");
			sb.append(tomeId);

			/** ���浥����¼ */
			Map<String, String> obj = new HashMap<String, String>();
			obj.put("url", sb.toString());
			obj.put("title", tomeName);
			lsRess.add(obj);
		}
		map.put("isTomeList", true);
		map.put("byTomeId", false);
		map.put("objs", lsRess);
		result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);
		return resultMap;
	}

	/**
	 * ��ѯ�½��б� ����ԴID��ѯ
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map getChaptersByResourceID(HttpServletRequest request,
			String tagName) {
		String rid = URLUtil.getResourceId(request);
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));
		if (!isNoPageLink()) {
			int totalCount = getResourceService(request)
					.getNewsPapersChaptersByResourceIDCount(rid);
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		List<NewsPapersChapterDesc> newsPaperChapters = getResourceService(
				request).getNewsPapersChaptersByResourceID(rid, currentPage,
				pageSize);
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		int relId = ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_RELATION_ID, -1);// ���۰�������ϵID
		ResourcePackReleation rel = null;
		if (relId != -1) {
			rel = getResourceService(request).getResourcePackReleation(relId);
		}
		Map feeMap=getFeeLogicService(request).isFee(productId, rid, mobile, rel, packId, month_fee_bag_id);
		for (Iterator it = newsPaperChapters.iterator(); it.hasNext();) {
			loop++;
			NewsPapersChapterDesc newsPaper = (NewsPapersChapterDesc) it.next();
			String title = newsPaper.getName();
			String tomeId = newsPaper.getTomeId();
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());

			/**�½ڿ��Ƶ�*/
			int choicePoint =rel==null?0: rel.getChoice()==null?0:rel.getChoice();
			if(feeMap==null  || newsPaper.getChapterIndex()<choicePoint){
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
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(rid);
			sb.append("&");
			sb.append(ParameterConstants.CHAPTER_ID);
			sb.append("=");
			sb.append(newsPaper.getId());
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
			if(feeMap!=null){
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}

			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			// if(loop!=1){
			// if(tomeId!=start){
			EbookTome tome = getResourceService(request).getEbookTomeById(
					tomeId);
			// obj.put("num",String.valueOf(loop));
			obj.put("tomeTitle", tome.getName());
			// }
			// }
			obj.put("url", sb.toString());
			obj.put("title", title);
			obj.put("chapter", newsPaper);
			lsRess.add(obj);
		}
		map.put("isTomeList", false);
		map.put("byTomeId", false);
		map.put("objs", lsRess);
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		/**result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);*/
		return resultMap;
	}

	/**
	 * ��ѯ�½��б� ����ID��ѯ
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map getChaptersByTomeID(HttpServletRequest request, String tagName) {
		String tomeId = request.getParameter(ParameterConstants.TOME_ID);
		EbookTome tome = getResourceService(request).getEbookTomeById(tomeId);
		boolean isOut = false;// �Ƿ񵯳��ʷ���ʾ
		// �õ���ԴID
		String rid = URLUtil.getResourceId(request);
		// �Ƿ񵼺�
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		// ÿҳ��ʾ����
		int pageSize = getIntParameter("pageSize", -1);
		// �Ƿ���ʾ����
		boolean isShowTitle = getIntParameter("isShowTitle", 1) > 0;// Ĭ����ʾ
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		if (pageSize < 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}
		int currentPage = 1;// ��ǰҳ����Ĭ��Ϊ��һҳ
		try {
			currentPage = Integer.parseInt(request
					.getParameter(ParameterConstants.PAGE_NUMBER));
		} catch (Exception e) {
			TagLogger.debug(tagName, "����ԭ��=currentPageת����ʱ�쳣]", request
					.getQueryString(), e);
		}
		// �ж��Ƿ񵼺�
		if (!isNoPageLink()) {
//			List resAll = new ArrayList();
			int totalCount = getResourceService(request)
					.getNewsPapersChapterDescCountByTomeId(tomeId);
//			for (int i = 0; i < totalCount; i++) {
//				resAll.add(new Object());
//			}
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		List<NewsPapersChapterDesc> newspapers = getResourceService(request)
				.getNewsPapersChapterDescByTomeId(tomeId, currentPage, pageSize);
		System.out.println("idΪ" + tomeId + "��Դ�б����Ϊ" + newspapers.size());
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		int relId = ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_RELATION_ID, -1);// ���۰�������ϵID
		ResourcePackReleation rel = null;
		if (relId != -1) {
			rel = getResourceService(request).getResourcePackReleation(relId);
		}

		boolean isWhiteList = RequestUtil.isFeeDisabled();// �Ƿ��ǰ������û�
		// ��ѯ�û��������� �˱��д��������Դ����ӼƷ�·��
		boolean isBuy = getCustomService(request).isUserBuyBook(
				RequestUtil.getMobile(), rid);// �ж��û��Ƿ��ѹ���

		for (Iterator it = newspapers.iterator(); it.hasNext();) {
			loop++;
			NewsPapersChapterDesc newsPaper = (NewsPapersChapterDesc) it.next();
			String title = newsPaper.getName();

			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			if (isWhiteList || isBuy || rel == null
					|| rel.getPack().getType() == Constants.FEE_TYPE_FREE) {
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			} else {
				feeId = request.getParameter(ParameterConstants.FEE_ID);
				if (feeId == null || "".equals(feeId) || "null".equals(feeId)) {
					feeId = rel.getFeeId();
					if (feeId == null || "".equals(feeId)) {

						feeId = rel.getPack().getFeeId();
						if (feeId == null || "".equals(feeId)) {
							TagLogger.debug("ChapterListTag", "feeIdΪ��",
									request.getQueryString(), null);
							return new HashMap();
						}
					}
				}
				Fee fee = getCustomService(request).getFee(feeId);

				isOut = fee.getIsout() == 1;// �Ƿ��ʷ���ʾ

				if (!isOut) {
					sb.append("/");
					sb.append(fee.getUrl());
				}
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
				if (isOut) {
					sb.append(ParameterConstants.COMMON_PAGE);
					sb.append("=");
					sb.append(ParameterConstants.COMMON_PAGE_FEE);
					sb.append("&");
					sb.append(ParameterConstants.TEMPLATE_ID);
					sb.append("=");
					sb.append(fee.getTemplateId());
					sb.append("&");
				}

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
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(rid);
			sb.append("&");
			sb.append(ParameterConstants.CHAPTER_ID);
			sb.append("=");
			sb.append(newsPaper.getId());
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
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeId);

			/** ���浥����¼ */
			Map<String, String> obj = new HashMap<String, String>();

			obj.put("url", sb.toString());
			obj.put("title", title);
			lsRess.add(obj);
		}
		map.put("isTomeList", false);
		map.put("byTomeId", true);
		map.put("tomeTitle", tome.getName());
		map.put("objs", lsRess);
		result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);
		return resultMap;
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
