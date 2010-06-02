package cn.joy.ggg.commons;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class JoyConstants {

  public static final String COOKIE_DOMAIN = ".joy.cn";
  public static final String JOY_PATH = "/joy";
  public static final String FAVORITE_ADD = "jfa";
  public static final String COMMENT_PARAM = "jcp";
  public static final String COOKIE_USER_ID = "SESSION_LOGIN_USERID";
  public static final String COMMENT_SUCCESS = "000000";
  public static final int DEFAULT_PAGE_SIZE = 10;
  public static final SimpleDateFormat SDF_COMMENT_VIEW = new SimpleDateFormat("MM/dd HH:mm");
  public static final SimpleDateFormat SDF_COMMON = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  private static String appId = "";
  private static String loginUrl = "";
  private static String personalIndex = "";
  
  public static String getCookieVal(HttpServletRequest request, String key) {
    String value = "";
    Cookie cookie[] = request.getCookies();
    if(cookie != null) {
      for(int i = 0; i < cookie.length; i ++) {
        if(cookie[i].getName().equalsIgnoreCase(key)) {
          value = cookie[i].getValue();
          break;
        }
      }
    }
    return value;
  }
  
  public static void setCookieVal(HttpServletResponse httpResponse, String key, String value, String domain, int second) {
    Cookie cookie = new Cookie(key, value);
    cookie.setPath("/"); //这个要设置
    cookie.setDomain(domain);
    cookie.setMaxAge(second); //以秒为单位
    httpResponse.addCookie(cookie);
  }

  public static String getAppId() {
    if(StringUtils.isBlank(appId)) {
      InputStream fis = null;
      try {
        Properties pro = new Properties();
        fis = JoyConstants.class.getClassLoader().getResourceAsStream("joyConstants.properties");
        pro.load(fis);
        appId = pro.getProperty("appId");
      }
      catch(Exception e) {
        e.printStackTrace();
      }
      finally {
        if(fis != null) {
          try {
            fis.close();
          }
          catch(IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return appId;
  }
  
  public static String getCommentAppId(int msgType, int boardId) {
    return "3g.joy.cn_" + msgType + "_" + boardId;
  }

  public static String getLoginUrl() {
    if(StringUtils.isBlank(loginUrl)) {
      InputStream fis = null;
      try {
        Properties pro = new Properties();
        fis = JoyConstants.class.getClassLoader().getResourceAsStream("joyConstants.properties");
        pro.load(fis);
        loginUrl = pro.getProperty("club.login");
      }
      catch(Exception e) {
        e.printStackTrace();
      }
      finally {
        if(fis != null) {
          try {
            fis.close();
          }
          catch(IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return loginUrl;
  }

  public static String getPersonalIndex(String userId) {
    if(StringUtils.isBlank(userId)) {
      return "";
    }
    if(StringUtils.isBlank(personalIndex)) {
      InputStream fis = null;
      try {
        Properties pro = new Properties();
        fis = JoyConstants.class.getClassLoader().getResourceAsStream("joyConstants.properties");
        pro.load(fis);
        personalIndex = pro.getProperty("club.index");
      }
      catch(Exception e) {
        e.printStackTrace();
      }
      finally {
        if(fis != null) {
          try {
            fis.close();
          }
          catch(IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return personalIndex + userId;
  }
}
