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
 * �������Գɹ���ʾҳ
 * ��ǩ���ƣ�comment_view
 * ����˵����
 * 		title:��������		*ȥ���˲���  �Զ�����
 * 		successTitle:�������Գɹ���ʾ����
 * @author liuxh
 *
 */
public class ViewCommentMsgTag extends BaseTag {

	private InteractiveService interactiveService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String title=getParameter("title","");
		String successTitle=getParameter("successTitle","�������Գɹ���ϵͳ���Ժ���ʾ��ҳ�����ڷ���...");
		int status=-1;//����״̬
		//�õ�����
		if(request.getParameter(ParameterConstants.COMMENT_TARGET)==null){
			return new HashMap();
		}
		int  msgType =Integer.parseInt(request.getParameter(ParameterConstants.COMMENT_TARGET));
		int  boardId =Integer.parseInt(request.getParameter(ParameterConstants.COMMENT_PLATE));
		String mobile=RequestUtil.getMobile();
		String contents=request.getParameter(ParameterConstants.COMMENT_PARAM_VALUE);
		String id=request.getParameter(ParameterConstants.COMMENT_TARGET_ID);
		int flag=1;//Ĭ�ϲ����ɹ�
		try{
			if(msgType==MsgRecord.TYPE_CUSTOM){//�û�����
				String customName=request.getParameter(ParameterConstants.CUSTOM_KEY_VALUE);
				status=getInteractiveService(request).addMsgRecord(msgType, boardId, mobile, contents, -1, null, null, null, id, customName);
			}else if(msgType==MsgRecord.TYPE_CONTENT){//����
				status=getInteractiveService(request).addMsgRecord(msgType, boardId, mobile, contents, -1, id, null, null, null, null);
			}else if(msgType==MsgRecord.TYPE_COLUMN){//��Ŀ
				status=getInteractiveService(request).addMsgRecord(msgType, boardId, mobile, contents, -1, null, Integer.parseInt(id), null, null, null);
			}else if(msgType==MsgRecord.TYPE_PRODUCT){//��Ʒ
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
		com.hunthawk.tag.process.Refresh.pageRefresh(3,backUrl.toString()) ;//3���� �󷵻�
		//1�ɹ���2�ȴ���ˡ�3���������֡�4���԰�첻���ڻ��������ߡ�5��������Ϊ��
		if(status==2){
			successTitle="�������Գɹ������ǻᾡ����������Խ������.";
		}else if(status==3){
			successTitle="��������ʧ�ܣ�������������к������дʻ�.";
		}else if(status==4){
			successTitle="��������ʧ�ܣ������԰�鲻���ڻ���������.";
		}else if(status==5){
			successTitle="��������ʧ�ܣ��������ݲ���Ϊ��.";
		}
		velocityMap.put("successTitle", successTitle);
		/**
		 * modify by liuxh 09-11-12
		 * ��Ӳ�����ʶ  0.ʧ�� 1.�ɹ�
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
