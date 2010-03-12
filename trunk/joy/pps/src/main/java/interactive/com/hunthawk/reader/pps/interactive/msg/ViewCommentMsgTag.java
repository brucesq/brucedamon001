package com.hunthawk.reader.pps.interactive.msg;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.inter.MsgRecord;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.InteractiveService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
/**
 * 发表留言成功提示页
 * 标签名称：comment_view
 * 参数说明：
 * 		title:链接名称		*去掉此参数  自动返回
 * 		successTitle:发表留言成功显示文字
 * @author liuxh
 *
 */
public class ViewCommentMsgTag extends BaseTag {

	private InteractiveService interactiveService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String title=getParameter("title","");
		String successTitle=getParameter("successTitle","发表留言成功！系统将稍后显示，页面正在返回...");
		int status=-1;//留言状态
		//得到参数
		if(request.getParameter(ParameterConstants.COMMENT_TARGET)==null){
			return new HashMap();
		}
		int  msgType =Integer.parseInt(request.getParameter(ParameterConstants.COMMENT_TARGET));
		int  boardId =Integer.parseInt(request.getParameter(ParameterConstants.COMMENT_PLATE));
		String mobile=RequestUtil.getMobile();
		String contents=request.getParameter(ParameterConstants.COMMENT_PARAM_VALUE);
		String id=request.getParameter(ParameterConstants.COMMENT_TARGET_ID);
		int flag=1;//默认操作成功
		try{
			if(msgType==MsgRecord.TYPE_CUSTOM){//用户定制
				String customName=request.getParameter(ParameterConstants.CUSTOM_KEY_VALUE);
				status=getInteractiveService(request).addMsgRecord(msgType, boardId, mobile, contents, -1, null, null, null, id, customName);
			}else if(msgType==MsgRecord.TYPE_CONTENT){//内容
				status=getInteractiveService(request).addMsgRecord(msgType, boardId, mobile, contents, -1, id, null, null, null, null);
			}else if(msgType==MsgRecord.TYPE_COLUMN){//栏目
				status=getInteractiveService(request).addMsgRecord(msgType, boardId, mobile, contents, -1, null, Integer.parseInt(id), null, null, null);
			}else if(msgType==MsgRecord.TYPE_PRODUCT){//产品
				status=getInteractiveService(request).addMsgRecord(msgType, boardId, mobile, contents, -1, null, null, id, null, null);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			flag=0;
		}
		StringBuilder backUrl = new StringBuilder();
		backUrl.append( request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,ParameterConstants.COMMENT_PARAM_VALUE,
				ParameterConstants.COMMENT_TARGET,ParameterConstants.COMMENT_PLATE,ParameterConstants.COMMENT_TARGET_ID,ParameterConstants.CUSTOM_KEY_VALUE));
		
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		if(StringUtils.isNotEmpty(title)){
			velocityMap.put("url", backUrl);
		}
//		
		com.hunthawk.tag.process.Refresh.pageRefresh(3,backUrl.toString()) ;//3秒钟 后返回
		//1成功。2等待审核。3含有敏感字。4留言板快不存在或者已下线。5留言内容为空
		if(status==2){
			successTitle="发表留言成功！我们会尽快对您的留言进行审核.";
		}else if(status==3){
			successTitle="发表留言失败！您发表的留言中含有敏感词汇.";
		}else if(status==4){
			successTitle="发表留言失败！该留言板块不存在或者已下线.";
		}else if(status==5){
			successTitle="发表留言失败！留言内容不能为空.";
		}
		velocityMap.put("successTitle", successTitle);
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
	private InteractiveService getInteractiveService(HttpServletRequest request){
		if(interactiveService == null){
			ServletContext servletContext = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			interactiveService = (InteractiveService)wac.getBean("interactiveService");
		}
		return interactiveService;
	}
	
	private BussinessService bussinessService;
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
