package cn.joy.ggg.commons;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class LoginFilter implements Filter {

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    String userId = JoyConstants.getCookieVal(request, JoyConstants.COOKIE_USER_ID);
    if(StringUtils.isBlank(userId)) {
      String loginPage = JoyConstants.getLoginUrl();
      String goto_url = request.getRequestURL() + "?" + request.getQueryString();
      if(loginPage.contains("?")) {
        response.sendRedirect(loginPage + "&goto=" + URLEncoder.encode(goto_url, "UTF-8"));
      }
      else {
        response.sendRedirect(loginPage + "?goto=" + URLEncoder.encode(goto_url, "UTF-8"));
      }
    }
    filterChain.doFilter(request, response);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

}
