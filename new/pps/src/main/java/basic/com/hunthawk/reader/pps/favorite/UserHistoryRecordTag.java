package com.hunthawk.reader.pps.favorite;

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

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.UserFootprint;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * �û���ʷ��¼��ǩ ��ǩ���ƣ�uhistory_list pageSize:ҳ/�� noPageLink:����ʾ��ҳ��ص�����
 * 
 * @author liuxh
 * 
 */
public class UserHistoryRecordTag extends BaseTag {

	private CustomService customService;
	private ResourceService resourceService;
	private BussinessService bussinessService;
	private static final int DEFAULT_SIZE = 5;

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
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID)==null?"0":request.getParameter(ParameterConstants.PRODUCT_ID);
		int currentPage = Integer.parseInt((request
				.getParameter(ParameterConstants.PAGE_NUMBER) == null ? 1
				: request.getParameter(ParameterConstants.PAGE_NUMBER))
				.toString());
		int pageSize = getIntParameter("pageSize", DEFAULT_SIZE);

		List<UserFootprint> historys = getCustomService(request)
				.getUsetFootprints(RequestUtil.getMobile(), currentPage,
						pageSize,productId);
		// �ж��Ƿ񵼺�
		if (!isNoPageLink()) {
			int totalCount = getCustomService(request).getUsetFootprints(
					RequestUtil.getMobile(), 1, 1000,productId).size();
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		for (Iterator it = historys.iterator(); it.hasNext();) {
			loop++;
			UserFootprint history = (UserFootprint) it.next();
			String rid = history.getContentId();
			String url = history.getUrl();
			ResourceAll resource = getResourceService(request).getResource(rid);

			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", url);
			String historyTitle = "";// ��ǰ�Ķ����½���+����+ҳ��
			if (resource != null) {
				// ��ֲ���
				String[] arr = url.split("&");
				String chapterId = "";
				String pageNum = "";
				for (int i = 0; i < arr.length; i++) {
					if (arr[i].contains(ParameterConstants.CHAPTER_ID)) {
						chapterId = arr[i].split("=")[1];
					}
					if (arr[i].contains(ParameterConstants.PAGE_NUMBER)) {
						pageNum = arr[i].split("=")[1];
					}
				}
				EbookChapterDesc echapter = null;
				MagazineChapterDesc mchapter = null;
				ComicsChapter cchapter = null;
				NewsPapersChapterDesc nchapter = null;
				String chapterName = "";
				if (StringUtils.isNotEmpty(chapterId)) {
					if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_BOOK))) {
						echapter = getResourceService(request)
								.getEbookChapterDesc(chapterId);
						if (echapter != null) {
							chapterName = echapter.getName();
							obj.put("chapter", echapter); // ���½ڶ���Ž�ȥ
						}
					} else if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_MAGAZINE))) {
						mchapter = getResourceService(request)
								.getMagazineChapterDescById(chapterId);
						if (mchapter != null) {
							/**
							 * ��������
							 * modify by liuxh 09-11-10
							 */
							EbookTome tome=getResourceService(request).getEbookTomeById(mchapter.getTomeId());
							if(tome!=null){
								obj.put("chapter", tome);//�������� 
								chapterName=tome.getName();
							}
							/**
							 * end
							 */
//							chapterName = mchapter.getName();
//							obj.put("chapter", mchapter); // ���½ڶ���Ž�ȥ
						}
					} else if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_NEWSPAPERS))) {
						nchapter = getResourceService(request)
								.getNewsPapersChapterDescById(chapterId);
						if (nchapter != null) {
							chapterName = nchapter.getName();
							obj.put("chapter", nchapter); // ���½ڶ���Ž�ȥ
						}
					} else if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_COMICS))) {
						cchapter = getResourceService(request)
								.getComicsChapterById(chapterId);
						if (cchapter != null) {
							chapterName = cchapter.getName();
							obj.put("chapter", cchapter); // ���½ڶ���Ž�ȥ
						}
					}
					historyTitle = chapterName
							+ "-"
							+ resource.getName()
							+ (StringUtils.isNotEmpty(pageNum) ? ("-P" + pageNum)
									: "");
				} else {
					historyTitle = resource.getName();
				}
				obj.put("resource", resource); // ����Դ����Ž�ȥ
				obj.put("pageNum", pageNum);// ��ҳ��Ž�ȥ
				String imgUrl = CoverPreview.getPreview(
						getResourceService(request), resource, 51);// ��Ԥ��ͼ�Ž�ȥ
				obj.put("preview", imgUrl);
			} else {
				TagLogger.debug(tagName, "�û���ʷ��¼�б�idΪ" + rid + "����Դδ�ҵ�",
						request.getQueryString(), null);
				continue;
			}
			obj.put("author", getAuthorName(resource, request));
			obj.put("title", historyTitle);

			lsRess.add(obj);
		}

		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service",getResourceService(request));
		// ����ֻ���
		map.put("mobile", RequestUtil.getMobile());
		// ����ֻ�����
		map.put("mobileType", RequestUtil.getMobileType());

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
	
	private String getAuthorName(ResourceAll resource,
			HttpServletRequest request) {
		String name = "";
		Integer[] authorids = resource.getAuthorIds();
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < authorids.length; i++) {
			ResourceAuthor author = getResourceService(request)
					.getResourceAuthor(authorids[i]);
			str.append((author.getPenName() == null || "".equals(author
					.getPenName())) ? author.getName() : author.getPenName());
			str.append(",");
			if (i == authorids.length - 1) {
				// ȥ�����һ��,
				str.replace(str.lastIndexOf(","), str.length(), "");
			}
		}
		try {
			name = StrUtil.getLimitStr(str.toString().trim(),
					ParameterConstants.AUTHOR_NAME_BYTES,
					ParameterConstants.REPLACE_SYMBOL);
		} catch (Exception e) {

			e.printStackTrace();
		}// ����
		return name;
	}
}
