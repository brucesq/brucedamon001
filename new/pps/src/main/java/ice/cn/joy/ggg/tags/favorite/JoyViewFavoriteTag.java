package cn.joy.ggg.tags.favorite;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.joy.ggg.api.service.CommunityService;
import cn.joy.ggg.commons.JoyConstants;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * Joy�ղع��ܱ�ǩ ��ǩ���ƣ�joy_favorite_view ����˵���� title:������������
 * success:����ղسɹ���ʾ���� failure:����ղ�ʧ����ʾ����
 * 
 * @author jiaoyz
 */
public class JoyViewFavoriteTag extends BaseTag {

  private BussinessService bussinessService;
  private ResourceService resourceService;
  private CommunityService communityService;
  
  @SuppressWarnings("unchecked")
  @Override
  public Map parseTag(HttpServletRequest request, String tagName) {
    String flag = request.getParameter(ParameterConstants.COMMON_PAGE);
    if(StringUtils.isEmpty(flag)) {
      TagLogger.debug(tagName, "fn������ȡʧ��", request.getQueryString(), null);
    }
    else {
      if(flag.equalsIgnoreCase(JoyConstants.FAVORITE_ADD)) {
        return addFavorite(request, tagName);
      }
    }
    return new HashMap();
  }

  @SuppressWarnings("unchecked")
  private Map addFavorite(HttpServletRequest request, String tagName) {
    boolean isAdd = true;
    String title = getParameter("title", "����");
    String success_word = getParameter("success", "���Ѿ��ɹ��ղظ���Ƶ");
    String failure_word = getParameter("failure", "����ղ�ʧ�ܣ�������");
    String rid = URLUtil.getResourceId(request);
    if(StringUtils.isBlank(rid)) {
      TagLogger.debug(tagName, "��ԴID��ȡʧ��", request.getQueryString(), null);
      return new HashMap();
    }
    ResourceAll resource = getResourceService(request).getResource(rid);
    if(resource == null) {
      TagLogger.debug(tagName, "��Դ��ȡʧ��", request.getQueryString(), null);
      return new HashMap();
    }
    String userId = JoyConstants.getCookieVal(request, JoyConstants.COOKIE_USER_ID);
    if(StringUtils.isBlank(userId)) {
      TagLogger.debug(tagName, "��ȡ�û�IDʧ��", request.getQueryString(), null);
      return new HashMap();
    }
    StringBuilder sb = new StringBuilder();
    sb.append(request.getScheme());
    sb.append("://");
    sb.append(request.getServerName());
    sb.append(":");
    sb.append(request.getServerPort());
    sb.append(request.getContextPath());
    sb.append(ParameterConstants.PORTAL_PATH);
    sb.append("?");
    URLUtil.append(sb, ParameterConstants.PAGE, request);
    URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
    URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
    URLUtil.append(sb, ParameterConstants.AREA_ID, request);
    URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
    URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
    URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
    URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
    URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
    int status = getCommunityService(request).addCollect(userId, resource.getName(), sb.toString().replaceAll("&", "&amp;"));
    Map velocityMap = new HashMap();
    velocityMap.put("title", title);
    velocityMap.put("url", sb.toString());
    velocityMap.put("isAdd", isAdd);
    if(status == 0) {
      velocityMap.put("addResult", success_word);
    }
    else {
      velocityMap.put("addResult", failure_word);
    }
    int tagTemplateId = this.getIntParameter("tmd", 0);
    TagTemplate tagTem = null;
    if(tagTemplateId > 0) {
      tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
    }
    Map resultMap = new HashMap();
    resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this, tagTem));
    return resultMap;
  }
  
  private ResourceService getResourceService(HttpServletRequest request) {
    if(resourceService == null) {
      ServletContext servletContext = request.getSession().getServletContext();
      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
      resourceService = (ResourceService) wac.getBean("resourceService");
    }
    return resourceService;
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
}
