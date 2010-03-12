package com.hunthawk.reader.pps.favorite;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.BookBag;
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
import com.hunthawk.tag.vm.VmInstance;
/**
 * 书包功能标签
 * 标签名称：bookbag_view
 * 参数说明：
 * 		title:返回链接文字
 * 		addsuccess:添加 收藏成功显示文字
 * 		delsuccess:删除 收藏成功显示文字
 * @author liuxh
 *
 */
public class ViewBookbagTag extends BaseTag {

	private CustomService customService;
	private ResourceService resourceService;
	private BussinessService bussinessService;
	
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		//String flag=request.getParameter(ParameterConstants.BAG_FUNCTION_FLAG);
		String flag=request.getParameter(ParameterConstants.COMMON_PAGE);
		if(StringUtils.isEmpty(flag)){
			TagLogger.debug(tagName, "fn参数获取失败", request.getQueryString(), null);
		}else{
			if(flag.equalsIgnoreCase(ParameterConstants.COMMON_PAGE_BOOKBAG_ADD)){//添加操作
				return addBookBagFunction(request,tagName);
			}else if(flag.equalsIgnoreCase(ParameterConstants.COMMON_PAGE_BOOKBAG_DEL)){//删除操作
				return deleteBookBagFunction(request,tagName);
			}
		}
		return new HashMap();
	}

	/**
	 * 书包添加方法
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map addBookBagFunction(HttpServletRequest request, String tagName){
		boolean isAdd=true;
		String title=getParameter("title","返回");
		String rid=URLUtil.getResourceId(request);
		if(rid==null || "".equals(rid)){
			TagLogger.debug(tagName, "资源ID获取失败", request.getQueryString(),null);
			return new HashMap();
		}
		boolean ERROR_FLAG=false;
		BookBag bag=new BookBag();
		bag.setContentId(rid);
		bag.setCreateTime(new Date());
		bag.setFeeId(request.getParameter(ParameterConstants.FEE_ID));
		bag.setMobile(RequestUtil.getMobile());
		bag.setPid(request.getParameter(ParameterConstants.PRODUCT_ID));
		bag.setChannelId(request.getParameter(ParameterConstants.CHANNEL_ID));
		int flag=1;//默认成功
		try {
			getCustomService(request).addBookbag(bag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			flag=0;
			TagLogger.debug(tagName, "书包添加失败", request.getQueryString(), e);
			ERROR_FLAG=true;
		}
		String addsuccess_msg=getParameter("addsuccess","已经将本书添加到您的书包");
		if(ERROR_FLAG){
			addsuccess_msg="您的书包中已经添加过这本书";
		}
		
		StringBuilder backUrl = new StringBuilder();
		backUrl.append( request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,ParameterConstants.COMMON_PAGE));
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", backUrl);
		velocityMap.put("isAdd", isAdd);
		velocityMap.put("addsuccess", addsuccess_msg);
		/**
		 * modify by liuxh 09-11-12
		 * 添加操作标识  0.失败 1.成功
		 */
		velocityMap.put("flag", flag);
		/**
		 * end
		 */
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		
	/*	String content  = VmInstance.getInstance().parseVM(velocityMap, this);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), content);*/
		
		return resultMap;
	}
	/**
	 * 书包删除方法
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map deleteBookBagFunction(HttpServletRequest request, String tagName){
		boolean isAdd=false;
		String title=getParameter("title","返回");
		boolean ERROR_FLAG=false;
		String cid=request.getParameter(ParameterConstants.RESOURCE_ID);
	
		if("".equals(cid)){
			return new HashMap();
		}
		int flag=1;//默认成功
		try {
			getCustomService(request).deleteUserBookbag(RequestUtil.getMobile(),cid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			flag=0;
			TagLogger.debug(tagName, "书包删除失败", request.getQueryString(), e);
			ERROR_FLAG=true;
		}
		String delsuccess_msg=getParameter("delsuccess","已经将本书从您的书包中删除");
		if(ERROR_FLAG){
			delsuccess_msg="书包删除失败";
		}
		StringBuilder backUrl = new StringBuilder();
		backUrl.append( request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,ParameterConstants.COMMON_PAGE));
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", backUrl);
		velocityMap.put("isAdd", isAdd);
		velocityMap.put("delsuccess", delsuccess_msg);
		// 添加手机号
		velocityMap.put("mobile", RequestUtil.getMobile());
		// 添加手机类型
		velocityMap.put("mobileType", RequestUtil.getMobileType());
		velocityMap.put("strUtil", new StrUtil());
		/**
		 * modify by liuxh 09-11-12
		 * 添加操作标识  0.失败 1.成功
		 */
		velocityMap.put("flag", flag);
		/**
		 * end
		 */
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		
		/*String content  = VmInstance.getInstance().parseVM(velocityMap, this);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), content);*/
		return resultMap;
	}
	private CustomService getCustomService(HttpServletRequest request){
		if(customService == null){
			ServletContext servletContext = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			customService = (CustomService)wac.getBean("customService");
		}
		return customService;
	}
	private ResourceService getResourceService(HttpServletRequest request){
		if(resourceService == null){
			ServletContext servletContext = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			resourceService = (ResourceService)wac.getBean("resourceService");
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
}
