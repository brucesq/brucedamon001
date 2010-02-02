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
 * �����б��ǩ
 * ��ǩ���ƣ�comment_list
 * ����˵����
 * 		pageSize: ÿҳ��ʾ������ (3��10��20)
 * 		noPageLink:����ʾ��ҳ��ص����� 	
 * 		boardId��	���ID
 *  	msgType(*)���������� �ο�MsgRecord����� 1.���� 2.��Ŀ 3.��Ʒ 4.�û�����
 *  	id:�û�����ID��
 *  	title:������������
 *  	showBack:�Ƿ���ʾ��������
 * @author liuxh
 *
 */
public class CommentListTag extends BaseTag {
	private InteractiveService interactiveService;
	private BussinessService bussinessService;
	private GuestService guestService;
	private static final int DEFAULT_PAGE_SIZE = 10; //Ĭ����ʾ10��
	/** ����ʾ��ҳ��ص����� */
	private boolean noPageLink;
	/**
	 * �Ƿ���ʾ��������
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
		//��ȡ���� ��һ���ȴ�URL�л�ȡ����  ���Ϊ�ձ�ʾ ���Ǵ�������ת������    �ڶ����ӱ�ǩ�л�ȡ���� 
		this.showBack=getIntParameter("showBack",1)==1;//Ĭ����ʾ��������
		if(this.showBack){
			if(request.getParameter(ParameterConstants.COMMENT_TARGET)==null){
				this.showBack=false;//����ת����ʾ��������
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
		System.out.println("�Ƿ񵼺�-->"+this.noPageLink);
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		int pageSize = getIntParameter("pageSize", -1);
		if (pageSize < 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}
		int currentPage = 1;// ��ǰҳ����Ĭ��Ϊ��һҳ
		try {
			currentPage = Integer.parseInt(request
					.getParameter(ParameterConstants.PAGE_NUMBER));
		} catch (Exception e) {
			TagLogger.debug(tagName,
					"[��ǩ��="+tagName+",����ԭ��=currentPageת����ʱ�쳣]",
					request.getQueryString(), e);
		}
		if(msgType==MsgRecord.TYPE_CUSTOM){//�û�����
			id=id==null?getParameter("id",""):id;
			if(StringUtils.isEmpty(id)){
				TagLogger.debug(tagName, "��������Ϊ�û�����ʱ��ID����ֵΪ��", request.getQueryString(), null);
				return new HashMap();
			}
			msgRecords= getInteractiveService(request).getMsgRecordList(msgType, boardId, null, null, null, id, isNoPageLink()?1:currentPage, pageSize);
			msgCount= getInteractiveService(request).getMsgRecordCount(msgType, boardId, null, null, null, id);
		}else if(msgType==MsgRecord.TYPE_CONTENT){//����
			id=id==null?URLUtil.getResourceId(request):id;
			msgRecords= getInteractiveService(request).getMsgRecordList(msgType, boardId, id, null, null, null,isNoPageLink()?1:currentPage, pageSize);
			msgCount= getInteractiveService(request).getMsgRecordCount(msgType, boardId, id, null, null, null);
		}else if(msgType==MsgRecord.TYPE_COLUMN){//��Ŀ
			id=id==null?request.getParameter(ParameterConstants.COLUMN_ID):id;
			msgRecords= getInteractiveService(request).getMsgRecordList(msgType, boardId, null, Integer.parseInt(id), null, null, isNoPageLink()?1:currentPage, pageSize);
			msgCount= getInteractiveService(request).getMsgRecordCount(msgType, boardId, null, Integer.parseInt(id), null, null);
		}else if(msgType==MsgRecord.TYPE_PRODUCT){//��Ʒ
			id=id==null?request.getParameter(ParameterConstants.PRODUCT_ID):id;
			msgRecords= getInteractiveService(request).getMsgRecordList(msgType, boardId, null, null, id, null, isNoPageLink()?1:currentPage, pageSize);
			msgCount= getInteractiveService(request).getMsgRecordCount(msgType, boardId, null, null, id, null);
		}
		
		//�ж��Ƿ񵼺�
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
			sb.append(time);//����ʱ��
			sb.append(" ");
			String mobile=msg.getMobile();
			//System.out.println("mobile-->"+mobile);
			//@TODO
			sb.append("������"+mobile.substring(mobile.length()-4)+"��");//�û���������
			sb.append("<br/>");
			sb.append(msg.getContent());//��������
			//sb.append(" ");
			//sb.append("<a href=\"#\">�ظ�</a>");
			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("title",sb.toString());
			obj.put("msgRecord",msg); //���������ݶ���Ž�ȥ
			/**
			 * ����ʾ���ݲ�
			 */
			obj.put("user", mobile.substring(mobile.length()-4));
			obj.put("desc", msg.getContent());//��������
			obj.put("time", time);//����ʱ��
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
		map.put("backTitle", getParameter("title","�����ϼ�"));
//		map.put("board","�������");//�������
//		map.put("boardUrl",request.getQueryString());
//		map.put("subject", "��������");//����������� ����Ŀ���� ����Ʒ���� ����Դ���ơ��û��Զ�������
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
