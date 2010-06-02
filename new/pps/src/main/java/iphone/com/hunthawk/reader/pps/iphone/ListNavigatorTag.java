package com.hunthawk.reader.pps.iphone;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.components.wml.Anchor;
import com.hunthawk.tag.components.wml.Go;
import com.hunthawk.tag.components.wml.IPostfieldModel;
import com.hunthawk.tag.components.wml.SimplePostfieldModel;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 用来在列表级别显示分页的标签 标签名:list_navigator 参数说明： style:
 * 决定标签显示的风格。(1-上下页；2-多页面导航；3-输入跳转) naviNum:在style=2的时候决定导航页面的条数
 * 
 * @author liuxh
 */
public class ListNavigatorTag extends BaseTag {

	private String style;

	/**
	 * 样式标志
	 */
	private int flag;

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	private int curPage = 0;
	
	private TagTemplate tagTem = null;
	
	private BussinessService bussinessService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		TagLogger.debug(tagName, "=======开始解析", request.getQueryString(), null);
		long tagBegin = System.currentTimeMillis();
		this.style = getParameter("style", "");
		// preParseRequest(request);
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
			
		}else{
			tagTem = null;
		}
		
		Navigator navi = (Navigator) request
				.getAttribute(ParameterConstants.PAMS_NAVIGATOR);

		if (navi == null || navi.getList() < 1) {
			return new HashMap();
		} else {
			if (null != navi) {
//				StringBuffer sb = new StringBuffer();
				curPage = navi.getCurrentpage();

				List<String> black = new ArrayList<String>();
				black.add(ParameterConstants.PAGE_NUMBER);
				black.add("GO");
				black.add("fid");
				black.add("title");
				black.add("backURL");
				black.add("seq");
				black.add("gambleScope");
				
				boolean isJump=false;
				int flag=Integer.parseInt(style);
				if (style.equals("3")) {// 跳转
					isJump=true;
					Map velocityMap = new HashMap();
					String title = "GO";
					velocityMap.put("title", title);
					StringBuilder url = new StringBuilder();
					url.append(request.getContextPath());
					url.append(ParameterConstants.PORTAL_PATH);
					url.append("?");
					url.append(URLUtil.removeParameter(
							request.getQueryString(),
							ParameterConstants.PAGE_NUMBER));
					velocityMap.put("url", url.toString());
					// velocityMap.put("flag",this.flag);
					// 将参数循环放入Map
					List<Object> lsRess = new ArrayList<Object>();
					String[] pams = url.toString().split("&");
					for (int i = 0; i < pams.length; i++) {
						String str[] = pams[i].split("=");
						/** 保存单条记录 */
						Map<String, String> obj = new HashMap<String, String>();
						obj.put("key", i == 0 ? str[0].substring((str[0]
								.indexOf("?") + 1)) : str[0]);
						if (str.length > 1 && str[1] != null
								&& StringUtils.isNotEmpty(str[1])){
//							obj.put("value", str[1]);
							try {
								obj.put("value",java.net.URLDecoder.decode(str[1],"utf-8"));
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else
							obj.put("value", "");
						lsRess.add(obj);
					}
					velocityMap.put("isJump", isJump);
					velocityMap.put("objs", lsRess);
					Map resultMap = new HashMap();
					String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
					resultMap.put(TagUtil.makeTag(tagName), result);
					return resultMap;
				} else if (style.equals("1")) {// 上下页
					
					Map velocityMap = new HashMap();
					Map map = makeUpDownPage(request, black);
					velocityMap.put("flag", flag);
					velocityMap.put("prelink", (map.get("prelink") == null ? ""
							: map.get("prelink")));
					velocityMap.put("nextlink",
							(map.get("nextlink") == null ? "" : map
									.get("nextlink")));
					velocityMap.put("current", (map.get("current") == null ? ""
							: map.get("current")));
					velocityMap.put("total", (map.get("total") == null ? ""
							: map.get("total")));
					Map resultMap = new HashMap();
					String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
					resultMap.put(TagUtil.makeTag(tagName), result);
					return resultMap;
				} else if (style.equals("2")) {
					Map<String, String> res = new HashMap<String, String>();
					res.put(ParameterConstants.PRE_TAG_SUFFIX + tagName
							+ ParameterConstants.END_TAG_SUFFIX, makeNaviPage(request, black));
					return res;
				} else {
					TagLogger.debug(tagName, "错误的显示风格", request
							.getQueryString(), null);
					return new HashMap();
				}

			}

			TagLogger.debug(tagName, "=======解析耗时: "
					+ (System.currentTimeMillis() - tagBegin) + " ms", request
					.getQueryString(), null);
			return new HashMap();
		}
	}

	// private String makeGoPage(HttpServletRequest request, List<String> black)
	// {
	// StringBuffer sb = new StringBuffer();
	// sb.append("页");
	// // sb.append("${pageJump}");
	// sb
	// .append("<input type=\"text\" name=\"su\" format=\"*N\" size=\"3\"/>");
	//
	// Go go = new Go();
	// go.setCharset("UTF-8");
	// go.setMethod("POST");
	// go.setUrl(URLUtil.urlChange(request, new HashMap(), black));
	// Map<String, String> postMap = new HashMap<String, String>();
	// postMap.put(ParameterConstants.PAGE_NUMBER, "$su");
	// postMap.put("GO", "1");
	//
	// IPostfieldModel model = new SimplePostfieldModel(postMap);
	//
	// go.setPostfieldModel(model);
	//
	// Anchor anchor = new Anchor();
	// anchor.setGo(go);
	// // anchor.setTitle("${pageJumpSubmit}");
	// // anchor.setText("${pageJumpSubmit}");
	// anchor.setTitle("GO");
	// anchor.setText("GO");
	//
	// sb.append(anchor.renderComponent());
	//
	// return sb.toString();
	// }

	private String makeNaviPage(HttpServletRequest request, List<String> black) {
		Navigator navi = (Navigator) request
				.getAttribute(ParameterConstants.PAMS_NAVIGATOR);

		Map<String, String> para = new HashMap<String, String>();
		para.put(ParameterConstants.PAGE_NUMBER, String.valueOf(curPage));

		StringBuffer sb = new StringBuffer();
		int minIdx = navi.getMinindex() + 1;
		int maxIdx = navi.getMaxindex() + 1;

		for (int i = minIdx; i <= maxIdx; ++i) {
			if (i == curPage) {
				sb.append("[");
				// sb.append(i + 1);
				sb.append(i);
				sb.append("]");
			} else {
				para.clear();
				para.put(ParameterConstants.PAGE_NUMBER, "" + i);
				sb.append("<a href=\"");
				sb.append(URLUtil.urlChange(request, para, black));
				sb.append("\">");
				// sb.append(i + 1);
				sb.append(i);
				sb.append("</a>");
			}
		}

		sb.append("/");
		sb.append(navi.getPagecount());
		// sb.append("${pageJumpPostfix}");
		sb.append("页");

		return sb.toString();

	}

	// /**
	// * 上下翻页的导航页面
	// *
	// * @return
	// */
	// private String makeUpDownPage(HttpServletRequest request, List<String>
	// black) {
	// Navigator navi = (Navigator) request
	// .getAttribute(ParameterConstants.PAMS_NAVIGATOR);
	//
	// Map<String, String> para = new HashMap<String, String>();
	// para.put(ParameterConstants.PAGE_NUMBER,String.valueOf(curPage));
	//
	// StringBuffer sb = new StringBuffer();
	// int pageCount = navi.getPagecount();
	//
	// if (curPage < pageCount) {
	// para.clear();
	// para.put(ParameterConstants.PAGE_NUMBER, "" + (curPage + 1));
	// //sb.append("<a href=\"");
	// sb.append(URLUtil.urlChange(request, para, black));
	// //sb.append("\">下一页</a>");
	// }
	//
	// if (curPage > 1) {
	// para.clear();
	// para.put(ParameterConstants.PAGE_NUMBER, "" + (curPage - 1));
	// //sb.append("<a href=\"");
	// sb.append(URLUtil.urlChange(request, para, black));
	// //sb.append("\">上一页</a>");
	// }
	//
	// return sb.toString();
	// }

	private Map makeUpDownPage(HttpServletRequest request, List<String> black) {
		Navigator navi = (Navigator) request
				.getAttribute(ParameterConstants.PAMS_NAVIGATOR);
		Map map = new HashMap();
		Map<String, String> para = new HashMap<String, String>();
		para.put(ParameterConstants.PAGE_NUMBER, String.valueOf(curPage));

		StringBuffer sbPre = new StringBuffer();
		StringBuffer sbNext = new StringBuffer();
		int pageCount = navi.getPagecount();
		
		if (curPage < pageCount) {
			para.clear();
			para.put(ParameterConstants.PAGE_NUMBER, "" + (curPage + 1));
			sbPre.append(URLUtil.urlChange(request, para, black));
			map.put("nextlink", sbPre.toString());
		}else {
			para.clear();
			para.put(ParameterConstants.PAGE_NUMBER, "" + (pageCount));
			sbPre.append(URLUtil.urlChange(request, para, black));
			map.put("nextlink", sbPre.toString());
		}
		if (curPage > 1) {
			para.clear();
			para.put(ParameterConstants.PAGE_NUMBER, "" + (curPage - 1));
			sbNext.append(URLUtil.urlChange(request, para, black));
			map.put("prelink", sbNext.toString());
		}else{
			para.clear();
			para.put(ParameterConstants.PAGE_NUMBER, "" + 1);
			sbNext.append(URLUtil.urlChange(request, para, black));
			map.put("prelink", sbNext.toString());
		}
		map.put("current", curPage);
		map.put("total", pageCount);
		return map;
	}

	// /**
	// * 对参数做必要的解析
	// *
	// * @param request
	// */
	// private void preParseRequest(HttpServletRequest request) {
	// if (StringUtils.isEmpty(getStyle())) {
	// istyles = new int[1];
	// istyles[0] = 1;
	// } else {
	// String[] tmp = style.split("\\|");
	//
	// TagLogger.debug("ListNavigatorTag", "style = " + getStyle(),
	// request.getQueryString(), null);
	// TagLogger.debug("ListNavigatorTag", "tmp = " + tmp, request
	// .getQueryString(), null);
	// istyles = new int[tmp.length];
	//
	// for (int i = 0; i < tmp.length; ++i) {
	// TagLogger.debug("ListNavigatorTag", "tmp = " + tmp[i], request
	// .getQueryString(), null);
	// istyles[i] = NumberUtils.toInt(tmp[i], 0);
	// }
	// }
	// curPage = NumberUtils.toInt(request
	// .getParameter(ParameterConstants.PAGE_NUMBER), 1);
	// }

	// public int getNaviNum() {
	// return naviNum;
	// }
	//
	// public void setNaviNum(int naviNum) {
	// this.naviNum = naviNum;
	// }

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
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
}