package cn.joy.ggg.tags.comment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.joy.ggg.api.model.Comment;
import cn.joy.ggg.api.model.CommentResponse;
import cn.joy.ggg.api.service.CommentService;
import cn.joy.ggg.api.service.CommunityService;
import cn.joy.ggg.commons.JavaEscape;
import cn.joy.ggg.commons.JoyConstants;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.inter.MsgRecord;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * Joy�����б��ǩ ��ǩ���ƣ�joy_comment_list 
 * ����˵����
 *   pageSize: ÿҳ��ʾ������ (3��10��20)
 *   noPageLink: ����ʾ��ҳ��ص�����
 *   boardId�����ID
 *   msgType(*)���������� �ο�MsgRecord����� 1.���� 2.��Ŀ 3.��Ʒ
 *   title: ������������
 *   showBack: �Ƿ���ʾ��������
 * 
 * @author jiaoyz
 */
public class JoyCommentListTag extends BaseTag {
  
  private CommentService commentService;
  private BussinessService bussinessService;
  private CommunityService communityService;
  private boolean noPageLink;
  private boolean showBack;

  @SuppressWarnings("unchecked")
  @Override
  public Map parseTag(HttpServletRequest request, String tagName) {
    //��ȡ���� ��һ���ȴ�URL�л�ȡ���� ���Ϊ�ձ�ʾ ���Ǵ�������ת������ �ڶ����ӱ�ǩ�л�ȡ����
    this.showBack = getIntParameter("showBack", 1) == 1; //Ĭ����ʾ��������
    if(this.showBack) {
      if(request.getParameter(ParameterConstants.COMMENT_TARGET) == null) {
        this.showBack = false; //����ת����ʾ��������
      }
      else {
        this.showBack = true;
      }
    }
    else {
      this.showBack = false;
    }
    int msgType = request.getParameter(ParameterConstants.COMMENT_TARGET) == null ? getIntParameter("msgType", -1) : Integer.parseInt(request.getParameter(ParameterConstants.COMMENT_TARGET));
    int boardId = request.getParameter(ParameterConstants.COMMENT_PLATE) == null ? getIntParameter("boardId", -1) : Integer.parseInt(request.getParameter(ParameterConstants.COMMENT_PLATE));
    String id = request.getParameter(ParameterConstants.COMMENT_TARGET_ID);
    int msgCount = 0;
    this.noPageLink = getIntParameter("noPageLink", -1) < 0;
    int pageSize = getIntParameter("pageSize", -1);
    if(pageSize < 0) {
      pageSize = JoyConstants.DEFAULT_PAGE_SIZE;
    }
    int currentPage = 1; //��ǰҳ����Ĭ��Ϊ��һҳ
    try {
      currentPage = Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER));
    }
    catch(Exception e) {
      TagLogger.debug(tagName, "[��ǩ��=" + tagName + ",����ԭ��=currentPageת����ʱ�쳣]", request.getQueryString(), e);
    }
    
    List<Comment> commentList = new ArrayList<Comment>();
    if(msgType == MsgRecord.TYPE_CONTENT) { //����
      id = StringUtils.isBlank(id) ? URLUtil.getResourceId(request) : id;
    }
    else if(msgType == MsgRecord.TYPE_COLUMN) { //��Ŀ
      id = StringUtils.isBlank(id) ? request.getParameter(ParameterConstants.COLUMN_ID) : id;
    }
    else if(msgType == MsgRecord.TYPE_PRODUCT) { //��Ʒ
      id = StringUtils.isBlank(id) ? request.getParameter(ParameterConstants.PRODUCT_ID) : id;
    }
    CommentResponse commentResponse = getCommentService(request).getCommentList(JoyConstants.getCommentAppId(msgType, boardId), String.valueOf(id), isNoPageLink() ? 0 : (currentPage - 1) * pageSize, pageSize);
    if(commentResponse != null) {
      commentList = commentResponse.getList();
      msgCount = commentResponse.getTotalNum();
    }
    if(! isNoPageLink()) {
      Navigator navi = new Navigator(msgCount, currentPage, pageSize, 5);
      request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
    }
    List<Object> lsRess = new ArrayList<Object>();
    int startCount = msgCount - (currentPage - 1) * pageSize + 1;
    int loop = 0;
    if(commentList != null && commentList.size() > 0) {
      for(Comment comment : commentList) {
        loop ++;
        StringBuilder sb = new StringBuilder();
        sb.append(startCount - loop);
        sb.append(".");
        String time = "";
        try {
          time = JoyConstants.SDF_COMMENT_VIEW.format(StringUtils.isBlank(comment.getCreateTime()) ? new Date() : JoyConstants.SDF_COMMON.parse(comment.getCreateTime()));
        }
        catch(ParseException e) {
          TagLogger.debug(tagName, "[��ǩ��=" + tagName + ",����ԭ��=comment.getCreateTimeת��������ʱ�쳣]", request.getQueryString(), e);
        }
        sb.append(time); //����ʱ��
        sb.append(" ");
        String user = "";
        if(StringUtils.isBlank(comment.getUserId()) || "-1".equals(comment.getUserId())) {
          user = "�ο�";
        }
        else {
          String nickName = getCommunityService(request).getUser(comment.getUserId()).getNick();
          if(StringUtils.isBlank(nickName)) {
            nickName = StringUtils.isBlank(comment.getNick()) ? comment.getUserName() : comment.getNick();
          }
          user = "<a href='" + JoyConstants.getPersonalIndex(comment.getUserId()) + "'>" + nickName + "</a>";
        }
        sb.append(user);
        sb.append("<br/>");
        sb.append(JavaEscape.unescape(comment.getContent())); //��������
        
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("title", sb.toString());
        obj.put("user", user);
        obj.put("desc", JavaEscape.unescape(comment.getContent())); //��������
        obj.put("time", time); //����ʱ��
        obj.put("loop", startCount - loop);
        lsRess.add(obj);
      }
    }
    
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("objs", lsRess);
    map.put("strUtil", new StrUtil());
    StringBuilder back = new StringBuilder();
    back.append(request.getContextPath());
    back.append(ParameterConstants.PORTAL_PATH);
    back.append("?");
    back.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.PAGE_NUMBER, ParameterConstants.TEMPLATE_ID, ParameterConstants.COMMENT_PARAM_VALUE, ParameterConstants.CUSTOM_KEY_VALUE, ParameterConstants.COMMENT_PLATE, ParameterConstants.COMMENT_TARGET, ParameterConstants.COMMENT_TARGET_ID));
    back.append("&");
    back.append(ParameterConstants.PAGE_NUMBER);
    back.append("=");
    back.append(1);
    map.put("back", back.toString());
    map.put("backTitle", getParameter("title", "�����ϼ�"));

    int tagTemplateId = this.getIntParameter("tmd", 0);
    TagTemplate tagTem = null;
    if(tagTemplateId > 0) {
      tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
    }
    String result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
    Map resultMap = new HashMap();
    resultMap.put(TagUtil.makeTag(tagName), result);

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
  
  private CommunityService getCommunityService(HttpServletRequest request) {
    if(communityService == null) {
      ServletContext servletContext = request.getSession().getServletContext();
      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
      communityService = (CommunityService) wac.getBean("communityService");
    }
    return communityService;
  }
  
  private BussinessService getBussinessService(HttpServletRequest request) {
    if(bussinessService == null) {
      ServletContext servletContext = request.getSession().getServletContext();
      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
      bussinessService = (BussinessService) wac.getBean("bussinessService");
    }
    return bussinessService;
  }

  public boolean isNoPageLink() {
    return noPageLink;
  }

  public void setNoPageLink(boolean noPageLink) {
    this.noPageLink = noPageLink;
  }

  public boolean isShowBack() {
    return showBack;
  }

  public void setShowBack(boolean showBack) {
    this.showBack = showBack;
  }
}
