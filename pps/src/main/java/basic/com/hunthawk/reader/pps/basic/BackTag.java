package com.hunthawk.reader.pps.basic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 返回上级标签 标签名称：back 参数说明： title:链接文字
 * 
 * @author liuxh
 * 
 */
public class BackTag extends BaseTag {

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {

		String title = getParameter("title", "返回上级");
		Map resultMap = new HashMap();
		int td = ParamUtil.getIntParameter(request,
				ParameterConstants.TEMPLATE_ID, -1);
		String fn = request.getParameter(ParameterConstants.COMMON_PAGE);
		if (td != -1 && !ParameterConstants.COMMON_PAGE_FEE.equals(fn)) {
			Map velocityMap = new HashMap();
			velocityMap.put("title", title);
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			sb.append(URLUtil.removeParameter(request.getQueryString(),
					ParameterConstants.TEMPLATE_ID,ParameterConstants.COMMON_PAGE,ParameterConstants.AUTHOR_ID,ParameterConstants.VOTE_CONTENT_ID,ParameterConstants.VOTE_ITEM_ID,ParameterConstants.VOTE_VOTE_TYPE));
			velocityMap.put("url", sb.toString());
			resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
					.parseVM(velocityMap, this));
			return resultMap;
		}
		// 判断是在哪个页面

		String page = request.getParameter(ParameterConstants.PAGE);
		if (StringUtils.isEmpty(page)) {
			resultMap.put(TagUtil.makeTag(tagName), "");
			return resultMap;
		}
		String url = "";
		if (page.equals(ParameterConstants.PAGE_PRODUCT)) {// 产品页
			String unicom_pt = request
					.getParameter(ParameterConstants.UNICOM_PT);
			if (unicom_pt != null && !"".equals(unicom_pt)) {
				url = unicom_pt;
			} else {
				return new HashMap();
			}
		} else {
			StringBuilder back = new StringBuilder();
			back.append(request.getContextPath());
			back.append(ParameterConstants.PORTAL_PATH);
			back.append("?");
			back.append(ParameterConstants.PAGE);
			back.append("=");
			if (page.equals(ParameterConstants.PAGE_COLUMN)) {// 栏目页-->产品页
				back.append(ParameterConstants.PAGE_PRODUCT);
				back.append("&");
				URLUtil.append(back, ParameterConstants.PRODUCT_ID, request);
				URLUtil.append(back, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(back, ParameterConstants.AREA_ID, request);
				URLUtil.append(back, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(back, ParameterConstants.UNICOM_PT, request);
				//带上搜索参数
//				if(request.getQueryString().indexOf(ParameterConstants.RESOURCE_TYPE)>0){
//					back.append("&");
//					back.append(request.getQueryString().substring(request.getQueryString().indexOf(ParameterConstants.RESOURCE_TYPE)));
//				}
			} else if (page.equals(ParameterConstants.PAGE_RESOURCE)) {// 介绍页-->栏目页
				back.append(ParameterConstants.PAGE_COLUMN);
				back.append("&");
				URLUtil.append(back, ParameterConstants.PRODUCT_ID, request);
				URLUtil.append(back, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(back, ParameterConstants.AREA_ID, request);
				URLUtil.append(back, ParameterConstants.COLUMN_ID, request);
				URLUtil.append(back, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(back, ParameterConstants.UNICOM_PT, request);
				back.append(ParameterConstants.PAGE_NUMBER);
				back.append("=");
				back.append(1);
				back.append("&");
				URLUtil.append(back, ParameterConstants.FEE_ID, request);
				//带上搜索参数
//				if(request.getQueryString().indexOf(ParameterConstants.RESOURCE_TYPE)>0){
//					back.append("&");
//					back.append(request.getQueryString().substring(request.getQueryString().indexOf(ParameterConstants.RESOURCE_TYPE)));
//				}
			} else if (page.equals(ParameterConstants.PAGE_DETAIL)) {// 内容页-->介绍页
				back.append(ParameterConstants.PAGE_RESOURCE);
				back.append("&");
				URLUtil.append(back, ParameterConstants.PRODUCT_ID, request);
				URLUtil.append(back, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(back, ParameterConstants.AREA_ID, request);
				URLUtil.append(back, ParameterConstants.COLUMN_ID, request);
				URLUtil.append(back, ParameterConstants.FEE_BAG_ID, request);
				URLUtil.append(back, ParameterConstants.FEE_BAG_RELATION_ID,
						request);
				back.append(ParameterConstants.RESOURCE_ID);
				back.append("=");
				back.append(URLUtil.getResourceId(request));
				back.append("&");
				URLUtil.append(back, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(back, ParameterConstants.UNICOM_PT, request);
				back.append(ParameterConstants.PAGE_NUMBER);
				back.append("=");
				back.append(1);
				back.append("&");
				URLUtil.append(back, ParameterConstants.FEE_ID, request);
				//带上搜索参数
//				if(request.getQueryString().indexOf(ParameterConstants.RESOURCE_TYPE)>0){
//					back.append("&");
//					back.append(request.getQueryString().substring(request.getQueryString().indexOf(ParameterConstants.RESOURCE_TYPE)));
//				}
			} else {// 通用、资费页
				return new HashMap();
			}
			url = URLUtil.trimUrl(back).toString();
			
			
		}
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", url);

		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
				.parseVM(velocityMap, this));
		return resultMap;
	}

}
