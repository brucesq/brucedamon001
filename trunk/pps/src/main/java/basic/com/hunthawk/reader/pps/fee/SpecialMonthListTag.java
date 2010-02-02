package com.hunthawk.reader.pps.fee;

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
import com.hunthawk.reader.domain.custom.UserBuyMonthChoice;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.ArrayUtil;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.components.wml.Anchor;
import com.hunthawk.tag.components.wml.Go;
import com.hunthawk.tag.components.wml.IPostfieldModel;
import com.hunthawk.tag.components.wml.SimplePostfieldModel;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 20选3资源列表(已选/未选) 
 * 标签名称：special_month_list 
 * 参数说明： 
 * packId:批价包ID
 * buttonTitle:功能按钮链接文字
 * templateId:选择结果提示页
 * orderTitle:订购链接文字 (未订购包月用户)
 * 
 * @author liuxh
 * 
 */
public class SpecialMonthListTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	private BussinessService bussinessService;
	private boolean showButton;
	public boolean isShowButton() {
		return showButton;
	}

	public void setShowButton(boolean showButton) {
		this.showButton = showButton;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		TagTemplate tagTem=null;
		int tagTemplateId = this.getIntParameter("tmd", 0);
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
			
		}else{
			tagTem = null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		Map resultMap = new HashMap();
		String result = "";
		// 得到批价包的ID
		String packId = request
				.getParameter(ParameterConstants.MONTH_FEE_BAG_ID) == null ? ""
				: request.getParameter(ParameterConstants.MONTH_FEE_BAG_ID);
		String mobile =RequestUtil.getMobile();
		if (StringUtils.isEmpty(packId)) {
			packId = getParameter("packId", "");
			if (StringUtils.isEmpty(packId)) {
				TagLogger.debug(tagName, "批价包ID为空", request.getQueryString(),
						null);
				return new HashMap();
			}
		}
		// 根据批价包的ID查找相关信息
		ResourcePack pack = getResourceService(request).getResourcePack(
				Integer.parseInt(packId));
		if (pack == null) {
			TagLogger.error(tagName, "N选X特殊包月类型批价包不存在", request
					.getQueryString(), null);
			return new HashMap();
		}
		int type=pack.getType();//判断类型  是否是20选3
		if(type==Constants.FEE_TYPE_CHOICE){//内容控制
			boolean isVip = getCustomService(request).isOrderMonth(mobile,
					pack.getFeeId());
			map.put("isVip", isVip);
			if (isVip) {
				// 根据批价包信息得到feeId
				String feeId = pack.getFeeId();
				// 获取用户当月通过N选X的方式 选订的图书
				List<UserBuyMonthChoice> selectedList = getCustomService(request)
						.getUserChoiceBooks(mobile, feeId);
				int allowChoise = pack.getChoice();// 允许选择的个数
				int userChoise = selectedList.size();// 用户已经选择的个数
				String[] selectIds = new String[userChoise];// 用户已选资源ID集合
				List<Object> selectedRess = new ArrayList<Object>();
				int loop = 0;
				for (Iterator it = selectedList.iterator(); it.hasNext();) {
					loop++;
					UserBuyMonthChoice user = (UserBuyMonthChoice) it.next();
					// 根据资源ID查询资源相关信息
					ResourceAll resource = getResourceService(request).getResource(
							user.getContentId());
					selectIds[loop-1] = resource.getId();
					StringBuilder sb = new StringBuilder();
					// sb.append("已选择的图书:");
					// sb.append("<br/>");
					sb.append(loop);
					sb.append(".");
					sb.append(resource.getName());
					//sb.append("<br/>");
					/** 保存单条记录 */
					Map<String, Object> obj = new HashMap<String, Object>();
					obj.put("selectTitle", sb.toString());
					StringBuilder url=new StringBuilder();
					url.append(request.getContextPath());
					url.append(ParameterConstants.PORTAL_PATH);
					url.append("?");
					url.append(ParameterConstants.PAGE);
					url.append("=");
					url.append(ParameterConstants.PAGE_RESOURCE);
					url.append("&");
					URLUtil.append(url, ParameterConstants.PRODUCT_ID, request);
					URLUtil.append(url, ParameterConstants.PAGEGROUP_ID, request);
					URLUtil.append(url, ParameterConstants.AREA_ID, request);
					URLUtil.append(url, ParameterConstants.COLUMN_ID, request);
					url.append(ParameterConstants.FEE_BAG_ID);
					url.append("=");
					url.append(packId);
					url.append("&");
//					url.append(ParameterConstants.FEE_BAG_RELATION_ID);
//					url.append("=");
//					ResourcePackReleation rpr=getResourceService(request).getResourcePackReleationByResourceId(resource.getId()).get(0);
//					url.append(rpr.getId());
//					url.append("&");
					url.append(ParameterConstants.RESOURCE_ID);
					url.append("=");
					url.append(resource.getId());
					url.append("&");
					URLUtil.append(url, ParameterConstants.CHANNEL_ID, request);
					URLUtil.append(url, ParameterConstants.UNICOM_PT, request);
					url.append(ParameterConstants.PAGE_NUMBER);
					url.append("=");
					url.append(1);
					url.append("&");
					url.append(ParameterConstants.FEE_ID);
					url.append("=");
					url.append(feeId);
					obj.put("url", url.toString());
					/**放入资源对象*/
					obj.put("resource", resource);
					selectedRess.add(obj);
				}
				map.put("selectedobjs", selectedRess);
				// 用户未选订的图书 列表
				// 第一步：判断用户选择的个数 如果已经选满 则不需要再有选项列表
				if (userChoise < allowChoise) {
					// 根据批价包ID查询该包下的所有资源ID集合
					String[] ids = null;
					List<ResourcePackReleation> rprs = getResourceService(request)
							.getResourcePackReleations(Integer.parseInt(packId), 1,
									1000);
					if (rprs != null && rprs.size() > 0) {
						ids = new String[rprs.size()];
						for (int i = 0; i < rprs.size(); i++) {
							ResourcePackReleation rpr = rprs.get(i);
							ids[i] = rpr.getResourceId();
						}
					}
					// 从集合中除去用户已经选择的资源
					String[] noSelectIds = ArrayUtil.isContentDiffKey(selectIds,
							ids);
					List<Object> noSelectedRess = new ArrayList<Object>();
					int loop2 = 0;
					for (String rid : noSelectIds) {
						loop2++;
						ResourceAll resource = getResourceService(request)
								.getResource(rid);
						StringBuilder sb = new StringBuilder();
						//sb.append("<input type=\"checkbox\"  name=\"ids"+(loop2-1)+"\"  "+(loop2<=allowChoise-userChoise?"checked=\"checked\"":"")+" value=\""+resource.getId()+"\"/>");//默认选中前三项
						sb.append(loop2);
						sb.append(".");
						sb.append(resource.getName());
						//提交URL
						StringBuilder back = new StringBuilder();
						back.append(request.getContextPath());
						back.append(ParameterConstants.PORTAL_PATH);
						back.append("?");
						back.append(URLUtil.removeParameter(request.getQueryString(),
								ParameterConstants.PAGE_NUMBER,
								ParameterConstants.TEMPLATE_ID,
								ParameterConstants.COMMENT_PARAM_VALUE,
								ParameterConstants.CUSTOM_KEY_VALUE,
								ParameterConstants.COMMENT_PLATE,
								ParameterConstants.COMMENT_TARGET,
								ParameterConstants.COMMENT_TARGET_ID,
								ParameterConstants.FEE_ID,
								ParameterConstants.FEE_BAG_ID));
						back.append("&");
						back.append(ParameterConstants.FEE_BAG_ID);
						back.append("=");
						back.append(packId);
						back.append("&");
						back.append(ParameterConstants.FEE_ID);
						back.append("=");
						back.append(feeId);
						back.append("&");
						back.append(ParameterConstants.PAGE_NUMBER);
						back.append("=");
						back.append(1);
						back.append("&");
						back.append(ParameterConstants.TEMPLATE_ID);
						back.append("=");
						back.append(getIntParameter("templateId",-1));
						
						/**
						 * 修改链接 支持2.0
						 * modify by liuxh 
						 * 09-11-11 
						 */
						StringBuilder builder=new StringBuilder();
						builder.append(URLUtil.removeParameter(back.toString(), ParameterConstants.SEARCH_PARAM_VALUE,"old","allow"));
						builder.append("&");
						builder.append(ParameterConstants.SEARCH_PARAM_VALUE);
						builder.append("=");
						builder.append(rid);
						builder.append("&");
						builder.append("old");
						builder.append("=");
						builder.append(String.valueOf(userChoise));
						builder.append("&");
						builder.append("allow");
						builder.append("=");
						builder.append(String.valueOf(allowChoise));
						/**
						 * end
						 */
//						Go go = new Go();
//						go.setCharset("UTF-8");
//						go.setMethod("POST");
//						go.setUrl(back.toString());
//						Map<String, String> postMap = new HashMap<String, String>();
//						postMap.put(ParameterConstants.SEARCH_PARAM_VALUE, rid);
//						postMap.put("old", String.valueOf(userChoise));
//						postMap.put("allow", String.valueOf(allowChoise));
//						IPostfieldModel model = new SimplePostfieldModel(postMap);
//						go.setPostfieldModel(model);
//						Anchor anchor = new Anchor();
//						anchor.setGo(go);
//						anchor.setTitle(getParameter("buttonTitle", "选择"));
//						anchor.setText(getParameter("buttonTitle", "选择"));
//						builder.append(anchor.renderComponent());
						
						//sb.append("<br/>");
						/** 保存单条记录 */
						Map<String, Object> obj = new HashMap<String, Object>();
						obj.put("noSelectTitle", sb.toString());
						obj.put("selectUrl", builder.toString());
						/**放入资源对象 */
						obj.put("resource", resource);
						noSelectedRess.add(obj);
					}
					map.put("noselectedobjs", noSelectedRess);
				}
				this.showButton=true;
				map.put("selects", allowChoise-userChoise);
				map.put("allowChoise", allowChoise);
				map.put("userChoise", userChoise);
//				map.put(ParameterConstants.PRE_TAG_SUFFIX + tagName
//						+ ParameterConstants.END_TAG_SUFFIX, builder.toString());
				result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
//				result = VmInstance.getInstance().parseVM(map, this);
//				resultMap.put(TagUtil.makeTag(tagName), result);
//				return resultMap;
			}else{
				//订购链接取代列表链接
				Fee fee = getCustomService(request).getFee(pack.getFeeId());
				String url=request.getQueryString();
				url = URLUtil.removeParameter(url,ParameterConstants.FEE_ID,ParameterConstants.MONTH_FEE_BAG_ID);
				url += "&" + ParameterConstants.FEE_ID + "=" + fee.getId()+"&"+ParameterConstants.MONTH_FEE_BAG_ID+"="+pack.getId();
				StringBuilder builder = new StringBuilder();
				if(fee.getIsout() == 0){
					builder.append(request.getContextPath());
					builder.append( "/");
					builder.append(fee.getUrl());
					builder.append(ParameterConstants.PORTAL_PATH);
					builder.append( "?");
					builder.append( url);
				}else{
					builder.append( request.getContextPath());
					builder.append( ParameterConstants.PORTAL_PATH);
					builder.append( "?");
					builder.append( ParameterConstants.COMMON_PAGE);
					builder.append("=");
					builder.append(ParameterConstants.COMMON_PAGE_FEE);
					builder.append("&");
					builder.append(ParameterConstants.TEMPLATE_ID);
					builder.append("=");
					builder.append(fee.getTemplateId());
					builder.append("&");
					builder.append(url);
				}
				map.put("msg", "您还没有订购此包月业务");
				map.put("orderUrl", builder.toString());
				map.put("title", "马上订购");
//				result = VmInstance.getInstance().parseVM(map, this);
//				resultMap.put(TagUtil.makeTag(tagName), result);
//				return resultMap;
			}
		}else{
			System.out.println("包月类型不是20选3");
			TagLogger.debug(tagName, "包月类型不是20选3", request.getQueryString(), null);
			return new HashMap();
		}
		
		/**
		 * 标签模版可配置
		 * modify by liuxh 09-11-11
		 */
		map.put("strUtil", new StrUtil());
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		/**
		 * end
		 */
		return resultMap;
//		return new HashMap();
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
			bussinessService = (BussinessService) wac.getBean("bussinessService");
		}
		return bussinessService;
	}
}
