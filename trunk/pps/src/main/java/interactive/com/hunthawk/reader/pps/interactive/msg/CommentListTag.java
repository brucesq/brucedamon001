package com.hunthawk.reader.pps.interactive.msg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.hunthawk.reader.domain.inter.MsgRecord;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.GuestService;
import com.hunthawk.reader.pps.service.InteractiveService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
/**
 * 留言列表标签
 * 标签名称：comment_list
 * 参数说明：
 * 		pageSize: 每页显示的条数 (3、10、20)
 * 		noPageLink:不显示翻页相关的链接 	
 * 		boardId：	板块ID
 *  	msgType(*)：留言类型 参考MsgRecord定义的 1.内容 2.栏目 3.产品 4.用户定制
 *  	id:用户订制ID号
 *  	title:返回链接文字
 *  	showBack:是否显示返回链接
 * @author liuxh
 *
 */
public class CommentListTag extends BaseTag {
	private InteractiveService interactiveService;
	private BussinessService bussinessService;
	private GuestService guestService;
	private static final int DEFAULT_PAGE_SIZE = 10; //默认显示10条
	/** 不显示翻页相关的链接 */
	private boolean noPageLink;
	/**
	 * 是否显示返回链接
	 */
	private boolean showBack;
	public boolean isShowBack() {
		return showBack;
	}
	public void setShowBack(boolean showBack) {
		this.showBack = showBack;
	}
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		//获取参数 第一步先从URL中获取参数  如果为空表示 不是从链接跳转过来的    第二步从标签中获取参数 
		this.showBack=getIntParameter("showBack",1)==1;//默认显示返回链接
		if(this.showBack){
			if(request.getParameter(ParameterConstants.COMMENT_TARGET)==null){
				this.showBack=false;//非跳转不显示返回链接
			}else{
				this.showBack=true;
			}
		}else{
			this.showBack=false;
		}
		int msgType=request.getParameter(ParameterConstants.COMMENT_TARGET)==null?getIntParameter("msgType",-1):Integer.parseInt(request.getParameter(ParameterConstants.COMMENT_TARGET));
		int boardId=request.getParameter(ParameterConstants.COMMENT_PLATE)==null?getIntParameter("boardId",-1):Integer.parseInt(request.getParameter(ParameterConstants.COMMENT_PLATE));
//		int msgType=getIntParameter("msgType",-1);
//		int boardId=getIntParameter("boardId",-1);
		String id=request.getParameter(ParameterConstants.COMMENT_TARGET_ID);
		int msgCount=0;
		List<MsgRecord> msgRecords=null;
		
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		System.out.println("是否导航-->"+this.noPageLink);
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		int pageSize = getIntParameter("pageSize", -1);
		if (pageSize < 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}
		int currentPage = 1;// 当前页数，默认为第一页
		try {
			currentPage = Integer.parseInt(request
					.getParameter(ParameterConstants.PAGE_NUMBER));
		} catch (Exception e) {
			TagLogger.debug(tagName,
					"[标签名="+tagName+",出错原因=currentPage转整型时异常]",
					request.getQueryString(), e);
		}
		if(msgType==MsgRecord.TYPE_CUSTOM){//用户定制
			id=id==null?getParameter("id",""):id;
			if(StringUtils.isEmpty(id)){
				TagLogger.debug(tagName, "留言类型为用户定制时，ID属性值为空", request.getQueryString(), null);
				return new HashMap();
			}
			msgRecords= getInteractiveService(request).getMsgRecordList(msgType, boardId, null, null, null, id, isNoPageLink()?1:currentPage, pageSize);
			msgCount= getInteractiveService(request).getMsgRecordCount(msgType, boardId, null, null, null, id);
		}else if(msgType==MsgRecord.TYPE_CONTENT){//内容
			id=id==null?URLUtil.getResourceId(request):id;
			msgRecords= getInteractiveService(request).getMsgRecordList(msgType, boardId, id, null, null, null,isNoPageLink()?1:currentPage, pageSize);
			msgCount= getInteractiveService(request).getMsgRecordCount(msgType, boardId, id, null, null, null);
		}else if(msgType==MsgRecord.TYPE_COLUMN){//栏目
			id=id==null?request.getParameter(ParameterConstants.COLUMN_ID):id;
			msgRecords= getInteractiveService(request).getMsgRecordList(msgType, boardId, null, Integer.parseInt(id), null, null, isNoPageLink()?1:currentPage, pageSize);
			msgCount= getInteractiveService(request).getMsgRecordCount(msgType, boardId, null, Integer.parseInt(id), null, null);
		}else if(msgType==MsgRecord.TYPE_PRODUCT){//产品
			id=id==null?request.getParameter(ParameterConstants.PRODUCT_ID):id;
			msgRecords= getInteractiveService(request).getMsgRecordList(msgType, boardId, null, null, id, null, isNoPageLink()?1:currentPage, pageSize);
			msgCount= getInteractiveService(request).getMsgRecordCount(msgType, boardId, null, null, id, null);
		}
		
		//判断是否导航
		if(!isNoPageLink()){
//			List resAll=new ArrayList();
//			for(int i=0;i<msgCount;i++){
//				resAll.add(i);
//			}
			Navigator navi=new Navigator(msgCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
		}
		
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		for(Iterator it=msgRecords.iterator();it.hasNext();){
			loop++;
			MsgRecord msg=(MsgRecord)it.next();
			StringBuilder sb=new StringBuilder();
			sb.append(loop);
			sb.append(".");
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
			String time=sdf.format(msg.getCreateTime()==null?new Date():msg.getCreateTime());
			sb.append(time);//留言时间
			sb.append(" ");
			String mobile=msg.getMobile();
			//System.out.println("mobile-->"+mobile);
			//@TODO
			sb.append("【网友"+mobile.substring(mobile.length()-4)+"】");//用户所属地区
			sb.append("<br/>");
			sb.append(msg.getContent());//留言内容
			//sb.append(" ");
			//sb.append("<a href=\"#\">回复</a>");
			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("title",sb.toString());
			obj.put("msgRecord",msg); //把留言内容对象放进去
			/**
			 * 把显示内容拆开
			 */
			obj.put("user", mobile.substring(mobile.length()-4));
			obj.put("desc", msg.getContent());//留言内容
			obj.put("time", time);//留言时间
			lsRess.add(obj);
		}
		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
		StringBuilder back=new StringBuilder();
		back.append(request.getContextPath());
		back.append(ParameterConstants.PORTAL_PATH);
		back.append("?");
		back.append(URLUtil.removeParameter(request.getQueryString(),ParameterConstants.PAGE_NUMBER, ParameterConstants.TEMPLATE_ID,ParameterConstants.COMMENT_PARAM_VALUE,ParameterConstants.CUSTOM_KEY_VALUE,ParameterConstants.COMMENT_PLATE,ParameterConstants.COMMENT_TARGET,ParameterConstants.COMMENT_TARGET_ID));
		back.append("&");
		back.append(ParameterConstants.PAGE_NUMBER);
		back.append("=");
		back.append(1);
		map.put("back", back.toString());
		map.put("backTitle", getParameter("title","返回上级"));
//		map.put("board","版块名称");//版块名称
//		map.put("boardUrl",request.getQueryString());
//		map.put("subject", "留言主题");//留言主题可能 是栏目名称 、产品名称 、资源名称、用户自定义名称
//		map.put("subjectUrl", request.getQueryString());
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		
		/*result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);*/
		return resultMap;
	}
	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
	}
	private InteractiveService getInteractiveService(HttpServletRequest request){
		if(interactiveService == null){
			ServletContext servletContext = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			interactiveService = (InteractiveService)wac.getBean("interactiveService");
		}
		return interactiveService;
	}
	private GuestService getGuestService(HttpServletRequest request){
		if(guestService == null){
			ServletContext servletContext = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			guestService = (GuestService)wac.getBean("guestService");
		}
		return guestService;
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
