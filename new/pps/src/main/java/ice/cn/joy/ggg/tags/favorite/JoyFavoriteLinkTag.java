package cn.joy.ggg.tags.favorite;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.joy.ggg.commons.JoyConstants;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * Joy收藏标签(链接) 标签名称：joy_favorite_link 参数说明： title:链接名称
 * templateId:模板ID number:在第几页之前显示 split:分割符号
 * 
 * @author jiaoyz
 */
public class JoyFavoriteLinkTag extends BaseTag {

  private BussinessService bussinessService;
  private int number;
  private int currentPage;
  private String split;
  
  @SuppressWarnings("unchecked")
  @Override
  public Map parseTag(HttpServletRequest request, String tagName) {
    String n = getParameter("number", "0");
    try {
      Integer.parseInt(n);
    }
    catch(Exception ex) {
      TagLogger.debug(tagName, "number属性值不是有效的属性值", request.getQueryString(), null);
      return new HashMap();
    }
    this.number = Integer.parseInt(n);
    this.currentPage = Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER) == null ? "1" : request.getParameter(ParameterConstants.PAGE_NUMBER));
    this.split = getParameter("split", "");
    
    String title = getParameter("title", "加入收藏");
    int templateId = getIntParameter("templateId", -1);
    if(templateId < 0) {
      TagLogger.debug(tagName, "模板ID为空", request.getQueryString(), null);
      return new HashMap();
    }
    
    StringBuilder sb = new StringBuilder();
    sb.append(request.getContextPath());
    sb.append(JoyConstants.JOY_PATH);
    sb.append(ParameterConstants.PORTAL_PATH);
    sb.append("?");
    sb.append(ParameterConstants.COMMON_PAGE);
    sb.append("=");
    sb.append(JoyConstants.FAVORITE_ADD);
    sb.append("&");
    sb.append(ParameterConstants.TEMPLATE_ID);
    sb.append("=");
    sb.append(templateId);
    sb.append("&");
    URLUtil.append(sb, ParameterConstants.PAGE, request);
    URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
    URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
    URLUtil.append(sb, ParameterConstants.AREA_ID, request);
    URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
    URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
    URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
    URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
    URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
    
    Map velocityMap = new HashMap();
    velocityMap.put("title", title);
    velocityMap.put("url", sb.toString());
    
    int tagTemplateId = getIntParameter("tmd", 0);
    TagTemplate tagTem = null;
    if (tagTemplateId > 0) {
      tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
    }
    
    Map resultMap = new HashMap();
    resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this, tagTem));

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

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public String getSplit() {
    return split;
  }

  public void setSplit(String split) {
    this.split = split;
  }
}
