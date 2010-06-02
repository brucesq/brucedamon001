package cn.joy.ggg.tags.comment;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.joy.ggg.api.model.CommentResponse;
import cn.joy.ggg.api.service.CommentService;
import cn.joy.ggg.commons.JoyConstants;

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
 * Joy�������Գɹ���ʾҳ
 * ��ǩ���ƣ�joy_comment_view
 * ����˵����
 *    title: ��������
 *    result: �������Գɹ���ʧ�ܺ���ʾ����
 *
 * @author jiaoyz
 */
public class JoyViewCommentMsgTag extends BaseTag {
  
  private CommentService commentService;
  private InteractiveService interactiveService;
  private BussinessService bussinessService;

  @SuppressWarnings("unchecked")
  @Override
  public Map parseTag(HttpServletRequest request, String tagName) {
    String title=getParameter("title", "����");
    Integer timer = getIntParameter("timer", 3);
    String result = getParameter("result", "�������Գɹ���ϵͳ���Ժ���ʾ��ҳ�����ڷ���...");
    if(request.getParameter(ParameterConstants.COMMENT_TARGET) == null) {
      return new HashMap();
    }
    int status = -1; //����״̬
    int flag = 1; //Ĭ�ϲ����ɹ�
    int msgType = Integer.parseInt(request.getParameter(ParameterConstants.COMMENT_TARGET));
    int boardId = Integer.parseInt(request.getParameter(ParameterConstants.COMMENT_PLATE));
    String mobile = RequestUtil.getMobile();
    String content = request.getParameter(JoyConstants.COMMENT_PARAM);
    String id = request.getParameter(ParameterConstants.COMMENT_TARGET_ID);
    boolean isAnonymity = true;
    String userId = JoyConstants.getCookieVal(request, JoyConstants.COOKIE_USER_ID);
    if(StringUtils.isNotBlank(userId) && ! "-1".equals(userId)) {
      isAnonymity = false;
    }
    String anonParam = request.getParameter("anon");
    if(StringUtils.isNotBlank(anonParam) && "1".equals(anonParam)) {
      isAnonymity = true;
    }
    try {
      if(msgType == MsgRecord.TYPE_CONTENT) { //����
        status = getInteractiveService(request).addMsgRecord(msgType, boardId, mobile, content, -1, id, null, null, null, null);
      }
      else if(msgType == MsgRecord.TYPE_COLUMN) { //��Ŀ
        status = getInteractiveService(request).addMsgRecord(msgType, boardId, mobile, content, -1, null, Integer.parseInt(id), null, null, null);
      }
      else if(msgType == MsgRecord.TYPE_PRODUCT) { //��Ʒ
        status = getInteractiveService(request).addMsgRecord(msgType, boardId, mobile, content, -1, null, null, id, null, null);
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
      flag = 0;
    }
    Map velocityMap = new HashMap();
    if(status == 2) {
      result = "�������Գɹ������ǻᾡ����������Խ�����ˡ�";
    }
    else if(status == 3) {
      result = "��������ʧ�ܣ�������������к������дʻ㡣";
    }
    else if(status == 4) {
      result = "��������ʧ�ܣ������԰�鲻���ڻ��������ߡ�";
    }
    else if(status == 5) {
      result = "��������ʧ�ܣ��������ݲ���Ϊ�ա�";
    }
    if(flag == 1 && (status == 1 || status == 2)) {
      content = URLUtil.chineseFilter(content, 2);
      if(content.length() > 255) {
        flag = 0;
        result = "��������ʧ�ܣ��������ݲ�Ҫ����255���֡�";
      }
      else {
        CommentResponse commentResponse = getCommentService(request).postComment(JoyConstants.getCommentAppId(msgType, boardId), userId, String.valueOf(id), content, isAnonymity);
        if(commentResponse == null) {
          flag = 0;
          result = "��������ʧ�ܣ������ԡ�";
        }
        else {
          if(! JoyConstants.COMMENT_SUCCESS.equals(commentResponse.getCode())) {
            flag = 0;
            result = "��������ʧ�ܣ������ԡ�";
          }
        }
      }
    }
    velocityMap.put("result", result);
    velocityMap.put("flag", flag);
    velocityMap.put("title", title);
    StringBuilder backUrl = new StringBuilder();
    backUrl.append(request.getContextPath());
    backUrl.append(ParameterConstants.PORTAL_PATH);
    backUrl.append("?");
    backUrl.append(URLUtil.removeParameter(request.getQueryString(), "anon", ParameterConstants.TEMPLATE_ID, ParameterConstants.COMMENT_PARAM_VALUE, ParameterConstants.COMMENT_TARGET, ParameterConstants.COMMENT_PLATE, ParameterConstants.COMMENT_TARGET_ID, ParameterConstants.CUSTOM_KEY_VALUE));
    if(StringUtils.isNotEmpty(title)) {
      velocityMap.put("url", backUrl);
    }
    int tagTemplateId = this.getIntParameter("tmd", 0);
    TagTemplate tagTem = null;
    if(tagTemplateId > 0){
      tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
    }
    Map resultMap = new HashMap();
    resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
    
    com.hunthawk.tag.process.Refresh.pageRefresh(timer,backUrl.toString()); //3���� �󷵻�
    return resultMap;
  }

  private CommentService getCommentService(HttpServletRequest request) {
    if(commentService == null) {
      ServletContext servletContext = request.getSession().getServletContext();
      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
      commentService = (CommentService) wac.getBean("commentService");
    }
    return commentService;
  }
  
  private InteractiveService getInteractiveService(HttpServletRequest request) {
    if(interactiveService == null) {
      ServletContext servletContext = request.getSession().getServletContext();
      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
      interactiveService = (InteractiveService) wac.getBean("interactiveService");
    }
    return interactiveService;
  }
  
  private BussinessService getBussinessService(HttpServletRequest request) {
    if(bussinessService == null) {
      ServletContext servletContext = request.getSession().getServletContext();
      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
      bussinessService = (BussinessService) wac.getBean("bussinessService");
    }
    return bussinessService;
  }
}
