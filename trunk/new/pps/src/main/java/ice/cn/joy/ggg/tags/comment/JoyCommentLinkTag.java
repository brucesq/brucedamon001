package cn.joy.ggg.tags.comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.joy.ggg.commons.JoyConstants;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.inter.MsgRecord;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * Joy评论链接标签 标签名称：joy_comment_link
 * 参数说明：
 *   templateId: 模板ID 发表留言成功提示页
 *   boardId：板块ID
 *   msgType(*)：留言类型 参考MsgRecord定义的 1.内容 2.栏目 3.产品
 *   id：目标对象的ID号(如栏目ID、产品ID、资源ID) 如果ID为空则取当前URL中的参数对应的ID值
 *   title: 链接文字
 *   split:分隔符号 (<br/>) 用于解决输入框与提交链接之间控制是否折行
 *
 * @author jiaoyz
 */
public class JoyCommentLinkTag extends BaseTag {
  
  private BussinessService bussinessService;
  private String split;

  @SuppressWarnings("unchecked")
  @Override
  public Map parseTag(HttpServletRequest request, String tagName) {
    this.split = getParameter("split", "");
    String title = getParameter("title", "发言");
    int templateId = getIntParameter("templateId", -1); //留言成功提示页模板ID
    int msgType = getIntParameter("msgType", -1);
    int plate = getIntParameter("boardId", -1);
    String id = "";
    if(msgType == MsgRecord.TYPE_CONTENT) { //内容
      id = URLUtil.getResourceId(request);
    }
    else if(msgType == MsgRecord.TYPE_COLUMN) { //栏目
      id = request.getParameter(ParameterConstants.COLUMN_ID);
    }
    else if(msgType == MsgRecord.TYPE_PRODUCT) { //产品
      id = request.getParameter(ParameterConstants.PRODUCT_ID);
    }
    if(StringUtils.isEmpty(id)) {
      TagLogger.debug(tagName, "id为空", request.getQueryString(), null);
      return new HashMap();
    }
    
    StringBuilder url = new StringBuilder();
    url.append(request.getContextPath());
    url.append(ParameterConstants.PORTAL_PATH);
    url.append("?");
    url.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID, ParameterConstants.SEARCH_PARAM_VALUE, ParameterConstants.SEARCH_TYPE, ParameterConstants.QUICK_SEARCH_LINK_NAME));
    if(templateId > 0) {
      url.append("&");
      url.append(ParameterConstants.TEMPLATE_ID);
      url.append("=");
      url.append(templateId);
    }
    
    Map velocityMap = new HashMap();
    velocityMap.put(ParameterConstants.COMMENT_PLATE, String.valueOf(plate)); //板块
    velocityMap.put(ParameterConstants.COMMENT_TARGET, String.valueOf(msgType)); //留言类型
    velocityMap.put(ParameterConstants.COMMENT_TARGET_ID, id); //目标对象ID
    velocityMap.put("title", title);
    velocityMap.put("url", url.toString());
    String userId = JoyConstants.getCookieVal(request, JoyConstants.COOKIE_USER_ID);
    if(StringUtils.isNotBlank(userId) && ! "-1".equals(userId)) {
      velocityMap.put("login", true);
    }
    
    //将参数循环放入Map
    List<Object> lsRess = new ArrayList<Object>();
    String[] pams = url.toString().split("&");
    for(int i = 0; i < pams.length; i ++) {
      String str[] = pams[i].split("=");
      /** 保存单条记录 */
      Map<String, String> obj = new HashMap<String, String>();
      if(!str[0].contains(ParameterConstants.SEARCH_TYPE)) {
        obj.put("key", i == 0 ? str[0].substring((str[0].indexOf("?") + 1)) : str[0]);
        if(str.length > 1 && str[1] != null && StringUtils.isNotEmpty(str[1])) {
          obj.put("value", str[1]);
        }
        else {
          obj.put("value", "");
        }
        lsRess.add(obj);
      }
    }
    velocityMap.put("objs", lsRess);
    
    int tagTemplateId = this.getIntParameter("tmd", 0);
    TagTemplate tagTem = null;
    if(tagTemplateId > 0) {
      tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
    }

    Map resultMap = new HashMap();
    resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
    return resultMap;
  }

  private BussinessService getBussinessService(HttpServletRequest request) {
    if(bussinessService == null) {
      ServletContext servletContext = request.getSession().getServletContext();
      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
      bussinessService = (BussinessService) wac.getBean("bussinessService");
    }
    return bussinessService;
  }

  public String getSplit() {
    return split;
  }

  public void setSplit(String split) {
    this.split = split;
  }
}
