/**
 * 
 */
package com.hunthawk.reader.pps;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.device.MobileInfo;
import com.hunthawk.reader.domain.device.PersonInfo;
import com.hunthawk.reader.domain.device.UAInfo;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.GuestService;
import com.hunthawk.tag.util.ParamUtil;

/**
 * @author BruceSun
 * 
 */
public class HwfRequestInfo {

	private static GuestService guestService;
	private static BussinessService bussinessService;
	
	private static String cookieDomain = "";

	private static String cookiePath = "/";

	private String mobile;
	private MobileInfo mobileInfo;
	private UAInfo uainfo;
	private String ua;
	private Product product;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private List<PersonInfo> personInfos;

	public HwfRequestInfo(HttpServletRequest request,HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public int getMobileType(){
		return 1;
	}
	
	public void addCookie(String name,String value){
		Cookie mycookies = new Cookie(name, value);
		mycookies.setMaxAge(315360000);
		if (cookieDomain != null && cookieDomain.length() > 0) {
			mycookies.setDomain(cookieDomain);
		}
		mycookies.setPath(cookiePath);
		response.addCookie(mycookies);
	}

	public String getMobile() {
		if (ParameterCheck.isNullOrEmpty(mobile)) {
			mobile = request.getHeader("x-up-calling-line-id");
			if (mobile != null && mobile.length() > 11) {
				if (13 == mobile.length() && mobile.startsWith("86")) {
					mobile = mobile.substring(2, mobile.length());
				} else if (14 == mobile.length() && mobile.startsWith("+86")) {
					mobile = mobile.substring(3, mobile.length());
				}
			}

		}
		if (ParameterCheck.isNullOrEmpty(mobile)) {
			mobile = (String) request.getSession().getAttribute(
					"x-up-calling-line-id");
			if (ParameterCheck.isNullOrEmpty(mobile)) {
				Cookie cookies[] = request.getCookies();
				Cookie sCookie = null;
				if (cookies != null && cookies.length > 0) {
					for (int i = 0; i < cookies.length; i++) {
						sCookie = cookies[i];
						if (sCookie.getName().equals("sid")) {
							mobile = sCookie.getValue();
							request.getSession().setAttribute(
									"x-up-calling-line-id", mobile);
						}
					}
				}
			}

		}
		if (ParameterCheck.isNullOrEmpty(mobile)) {
			mobile = "3"+StrUtil.randomString(10);//guestService.registerNewMobile();
			if(!mobile.equals("10000000000")){
				addCookie("sid",mobile);
				request.getSession().setAttribute(
						"x-up-calling-line-id", mobile);
			}
			
		}
		return mobile;
	}

	public List<PersonInfo> getPersonInfo() {
		if (personInfos == null) {
			personInfos = getGuestService().getPersonInfo(getMobile());
		}
		return personInfos;
	}

	public String getAreaId() {
		if (mobileInfo == null) {
			mobileInfo = getGuestService().getMobileInfo(getMobile());
			if (mobileInfo == null) {
				mobileInfo = new MobileInfo();
				mobileInfo.setArea("001");
				mobileInfo.setBrand("");
				try {
					mobileInfo.setPrefix(getMobile().substring(0, 8));
				} catch (Exception e) {
					mobileInfo.setPrefix(getMobile());
				}
			}
		}
		return mobileInfo.getArea();
	}

	public String getUa() {
		if (ParameterCheck.isNullOrEmpty(ua)) {
			ua = request.getHeader("user-agent");
			if (ua != null) {
				// Matcher m = p.matcher(ua);
				// if (m.find()) {
				// ua = m.group(0);
				// } else {
				// int idx = ua.indexOf('/');
				// if (-1 != idx) {
				// ua = ua.substring(0, idx);
				// }
				// }
				ua = getUserAgent(ua);
			}
		}
		if (ua == null) {
			ua = "TenFenUA";
		}
		return ua;
	}

	public UAInfo getUAInfo() {
		try{
		if (uainfo == null) {
			uainfo = getGuestService().getUAInfo(getUa());
			if (uainfo == null) {
				StatisticsUALog.logUA(getUa(), request.getHeader("user-agent")); 
				uainfo = new UAInfo();
				uainfo.setHeight(128);
				uainfo.setWidth(128);
				uainfo.setRingTypes("");
				uainfo.setScreenType(1);
				uainfo.setUa(getUa());
				uainfo.setVideoTypes("");
				uainfo.setWapType(1);
			}
			String vs = request.getParameter(ParameterConstants.CHANNEL_ID);
			if(vs != null && StringUtils.isNumeric(vs)){
				uainfo.setWapType(Integer.parseInt(vs));
			}
			Cookie cookies[] = request.getCookies();
			Cookie sCookie = null;
			if (cookies != null && cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					sCookie = cookies[i];
					if (sCookie.getName().equals(ParameterConstants.VERSION_SET)) {
						uainfo.setWapType(Integer.parseInt(sCookie.getValue()));
						break;
					}
				}
			}
		}
		}catch(Exception e){
			
		}
		Cookie cookies[] = request.getCookies();
    Cookie sCookie = null;
    boolean noCookie = true;
    if(cookies != null && cookies.length > 0) {
      for(int i = 0; i < cookies.length; i ++) {
        sCookie = cookies[i];
        if(sCookie.getName().equals(ParameterConstants.VERSION_SET)) {
          noCookie = false;
          break;
        }
      }
    }
    if(noCookie) {
      RequestUtil.addCookie(ParameterConstants.VERSION_SET, String.valueOf(uainfo.getWapType()));
      RequestUtil.addJoyCookie(ParameterConstants.VERSION_SET, String.valueOf(uainfo.getWapType()));
    }
		return uainfo;
	}

//	public Product getProduct() {
//		if (product == null) {
//			String productId = ParamUtil.getParameter(request,
//					ParameterConstants.PRODUCT_ID);
//			if (StringUtils.isNotEmpty(productId)) {
//				product = getBussinessService().getProduct(productId);
//			}
//		}
//		return product;
//	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
    return response;
  }

  public void setResponse(HttpServletResponse response) {
    this.response = response;
  }

  private GuestService getGuestService() {
		if (guestService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			guestService = (GuestService) wac.getBean("guestService");
		}
		return guestService;
	}

	private BussinessService getBussinessService() {
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

	private static final String sRegx = "(Dopod|dopod|SGH|SCH|SAMSUNG|Nokia|MOT|Motorola|LENOVO|SonyEricsson|NEC|Panasonic|BIRD|Sharp|Haier|HAIER|SEC|KEJIAN|VK|Amoi|PHILIPS|GIONEE|BenQ|ZTE|OT|SKYWORTH|SZV|SIE|TCL|BlackBerry|AHONG|SAGEM|SHARP|Mitsu|LG|SOUTEC|CLOVE|Yulong|YuLong|PANTECH|Huawei|HS|EASTCOM|IAC|Bird|Philips|kejian|Dnet|Compal|ASUS|KONKA|AMOI|CECT)[^/^\\*\\s]*";
	private static final Pattern p = Pattern.compile(sRegx);

	private static final Pattern UA_PATTERN = Pattern
			.compile("(([a-zA-Z\\d]+)([^a-zA-Z\\d]|[^a-zA-Z\\d]+\\s*)([a-zA-Z\\d]+)([^a-zA-Z\\d]?)([^\\/\\s\\-\\_]*))");

	private static final Pattern SUB_UA_PATTERN = Pattern
			.compile("(([a-zA-Z]+)([\\d])+)");

	private static final Pattern PHILIPS_UA_PATTERN = Pattern
			.compile("(([a-zA-Z\\d]+)(.)([a-zA-Z\\d\\@]+|\\/{1}))");

	private static final Pattern SPECIFIC_UA_PATTERN = Pattern
			.compile("(([a-zA-Z\\d]+)([^a-zA-Z\\d][a-zA-Z]+)([0-9]+)(\\+?))");

	public static void main(String[] args) {
		System.out
				.println(getUserAgent("NokiaN72/5.0741.4.0.1 Series60/2.8 Profile/MIDP-2.0 Configuration/CLDC-1.1"));
		System.out
				.println(getUserAgent("NokiaN70/ 5.0741.4.0.1 Series60/2.8 Profile/MIDP-2.0 Configuration/CLDC-1.1"));
		System.out
				.println(getUserAgent("NokiaN81/ SymbianOS/9.2 Series60/3.1 Release/10.1.039 Mozilla/5.0 Profile/MIDP-2.0 Configuration/CLDC-1.1 AppleWebKit/413 (KHTML, like Gecko) Safari/413"));
		System.out
				.println(getUserAgent("SonyEricssonW830c/ R1GA Release/Oct-10-2006 Browser/NetFront/3.3 Profile/MIDP-2.0"));
		System.out.println(getUserAgent("NokiaN72"));
		System.out.println(getUserAgent("NokiaN81"));
		System.out.println(getUserAgent("Nokia 6122c 3.2.3.081103"));
		System.out
				.println(getUserAgent("MOT-MOTORAZRV8_CMCC/1.0 linuxOS/2.6.10 Release/06.30.2007.Browser/Opera8.50 Profile/MIDP-2.0Configuration/CLDC-1.1software/R601_G_90.44.2BR"));
		System.out
				.println(getUserAgent("Mozilla/5.0 (SymbianOS/9.2; U; Series60/3.1 NokiaE90-1/07.40.1.2; Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML, like Gecko) Safari/413"));
		System.out
				.println(getUserAgent("Mozilla/5.0 (SymbianOS/9.2; U; Series60/3.1 1.0/SamsungSGHi568/I568ZTHA1 Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML, like Gecko) Safari/413"));

		System.out
				.println(getUserAgent("UNTRUSTED/1.0 NokiaN73/5.0741.4.0.1 Series60/2.8 Profile/MIDP-2.0 Configuration/CLDC-1.1"));
		System.out
				.println(getUserAgent("TRUSTED/1.0 NokiaN72/5.0741.4.0.1 Series60/2.8 Profile/MIDP-2.0 Configuration/CLDC-1.1"));
		System.out
				.println(getUserAgent("Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; zh-cn) "));

		System.out
		.println(getUserAgent("SAMSUNG-SGH-U108_CMCC/1.0 Release/1.24.2007 Browser/NetFront3.2 Profile/MIDP-2.0 Configuration/CLDC-1.1/*MzU2OTQ5MDEwMjA5OTc5"));

		System.out
		.println(getUserAgent("Palm680/RC1 (iPhone; U; CPU iPhone OS 2_2_1 like Mac OS X; zh-cn)"));

		System.out
		.println(getUserAgent("MOT-ROKR E2/1.0 R564_G_12.01.46P/12.28.2005 Mozilla/4.0 (compatible; MSIE 6.0; Linux; Motorola ROKR E2; 2445) Profile/MIDP-2.0"));
	}

	public static String getUserAgent(String s) {
		StringBuffer stringbuffer = new StringBuffer();
		int index = s.indexOf(" ");
		if (index > 0) {
			String prefix = s.substring(0, index);
			if (prefix.indexOf("UNTRUSTED") >= 0
					|| prefix.indexOf("TRUSTED") >= 0) {
				s = s.substring(index);
			}
		}
		if (s.startsWith("LG") || s.startsWith("LENOVO")
				|| s.startsWith("KONKA") || s.startsWith("Capitel")) {
			Matcher matcher = SPECIFIC_UA_PATTERN.matcher(s);
			if (matcher.find()) {

				stringbuffer.append(matcher.group());
			}
		} else if (s.startsWith("Mozilla")
				&& (s.indexOf("MOT") != -1 || s.indexOf("Nokia") != -1
						|| s.indexOf("Samsung") != -1 || s.indexOf("iPhone") != -1 
						|| s.indexOf("HTC") != -1 || s.indexOf("Android") != -1)) {

			int end = 0;
			int start = 0;
			if (s.indexOf("MOT") != -1) {
				start = s.indexOf("MOT");
				end = s.indexOf("/", start);
				stringbuffer.append(s.substring(start, end));
			} else if (s.indexOf("Nokia") != -1) {

				/*
				 * start = s.indexOf("Nokia"); end = s.indexOf("/", start);
				 * String temp = s.substring(start, end);
				 */
				/*
				 * if(temp.indexOf(" ")!=-1){ temp =
				 * temp.substring(start,temp.indexOf(" ",start)); }
				 */
				String temp = getUserAgent(s.substring(s.indexOf("Nokia"))
						.replaceAll("/", " "));

				stringbuffer.append(temp);

			} else if (s.indexOf("Samsung") != -1) {
				String temp = getUserAgent(s.substring(s.indexOf("Samsung"))
						.replaceAll("/", " "));

				stringbuffer.append(temp);
			} else if (s.indexOf("iPhone") != -1) {
				stringbuffer.append("iPhone");
			}else if (s.indexOf("HTC") != -1) {
				stringbuffer.append("HTC");
			}else if (s.indexOf("Android") != -1) {
				stringbuffer.append("Android");
			}
		} else if (s.startsWith("Philips") || s.startsWith("CECT")
				|| s.startsWith("TCL") || s.startsWith("HAIER")) {
			Matcher matcher1 = PHILIPS_UA_PATTERN.matcher(s);
			if (matcher1.find()) {
				if (matcher1.group(3).matches("\\/")) {
					stringbuffer.append(matcher1.group(2));
				} else if (matcher1.group(4).matches("\\/")) {
					stringbuffer.append(matcher1.group(2)).append(
							matcher1.group(3));
				} else {
					stringbuffer.append(matcher1.group(2)).append(
							matcher1.group(3)).append(matcher1.group(4));
				}
			}
		} else {

			Matcher matcher2 = UA_PATTERN.matcher(s);
			if (matcher2.find()) {

				/*
				 * System.out.println(matcher2.group(2));
				 * System.out.println(matcher2.group(3));
				 * System.out.println(matcher2.group(4));
				 * System.out.println(matcher2.group(5));
				 */

				stringbuffer.append(matcher2.group(2));

				if (!matcher2.group(3).matches("\\/")
						&& !matcher2.group(3).matches("\\/.+")
						&& (!matcher2.group(2).startsWith("Nokia") || matcher2
								.group(2).startsWith("Nokia")
								&& matcher2.group(4).length() > 3)
						&& !matcher2.group(2).startsWith("SonyEricsson")) {
					if (!matcher2.group(5).matches("\\/")
							&& !matcher2.group(5).matches("\\*")
							&& !matcher2.group(5).matches("\\s")
							&& !matcher2.group(5).matches("-")
							&& !matcher2.group(5).matches("_")
							&& !matcher2.group(5).equals("")
							&& !matcher2.group(5).matches("\\+")
							&& !matcher2.group(5).matches("\\(")) {
						stringbuffer.delete(0, stringbuffer.length()).append(
								matcher2.group());
					} else if (!matcher2.group(3).matches("\\s")) {
						// System.out.println("Æ½µ­ÊÇ¸£");
						if (matcher2.group(5).matches("\\s")
								&& matcher2.group(6).indexOf(".") == -1
								&& matcher2.group(2).equals("MOT")) {
							stringbuffer.append(matcher2.group(3)).append(
									matcher2.group(4))
									.append(matcher2.group(5)).append(
											matcher2.group(6));
						} else if (matcher2.group(5).matches("\\+")) {
							stringbuffer.append(matcher2.group(3)).append(
									matcher2.group(4))
									.append(matcher2.group(5));
						} else if (matcher2.group(5).matches("\\-")) {
							Matcher matcher3 = SUB_UA_PATTERN.matcher(matcher2
									.group(6));
							if (matcher3.find()) {
								stringbuffer.append(matcher2.group(3)).append(
										matcher2.group(4)).append(
										matcher2.group(5)).append(
										matcher3.group());
							} else {
								stringbuffer.append(matcher2.group(3)).append(
										matcher2.group(4)).append(
										matcher2.group(5)).append(
										matcher2.group(6));
							}
						} else if (matcher2.group(3).matches("\\/++\\s*")) {

						} else if (matcher2.group(5).matches("\\/")
								&& matcher2.group(4)
										.matches("[cC][mM][cC][cC]")) {

						} else if (matcher2.group(2).matches("TIANYU")
								&& matcher2.group(3).matches("-")
								&& matcher2.group(5).matches("\\/")) {
							stringbuffer.append(matcher2.group(3)).append(
									matcher2.group(4)).append("-").append(
									matcher2.group(6));
						}

						else {
							stringbuffer.append(matcher2.group(3)).append(
									matcher2.group(4));
						}
					} else if (matcher2.group(5).matches("\\-")) {

						stringbuffer.append(matcher2.group(3)).append(
								matcher2.group(4)).append(matcher2.group(5))
								.append(matcher2.group(6));
					} else if (matcher2.group(5).matches("\\s")) {
						if (matcher2.group(2).matches("TIANYU")) {
							stringbuffer.append(matcher2.group(3)).append(
									matcher2.group(4));
						} else if (matcher2.group(2).matches("Nokia")) {
							stringbuffer.append(matcher2.group(4));
						} else if (matcher2.group(2).matches("Samsung")) {
							stringbuffer.append(matcher2.group(4));
						} else if (matcher2.group(2).startsWith("SamsungSGH")) {

						} else {
							stringbuffer.append(matcher2.group(3)).append(
									matcher2.group(4))
									.append(matcher2.group(5)).append(
											matcher2.group(6));
						}
					} else if (matcher2.group(3).matches("\\s")
							&& matcher2.group(2).startsWith("Nokia")) {
						/*
						 * System.out.println("stringbuffer is "+stringbuffer);
						 * stringbuffer.append(matcher2.group(2));
						 */
					} else if (((matcher2.group(2).startsWith("Dopod") || matcher2
							.group(2).startsWith("PHILIPS")) && matcher2.group(
							3).matches("\\s+"))) {

					} else {

						stringbuffer.append(matcher2.group(3)).append(
								matcher2.group(4));
					}
				} else if (matcher2.group(3).matches("\\/")
						&& matcher2.group(2).matches("Typhoon")) {
					stringbuffer.append(matcher2.group(3)).append(
							matcher2.group(4));
				} else if (matcher2.group(3).matches("\\s")
						&& matcher2.group(2).matches("Nokia")) {
					stringbuffer.append(matcher2.group(4));
				} else if (matcher2.group(2).matches("Samsung")
						&& matcher2.group(3).matches("\\/")) {
					s = s.replaceAll("\\/", "-");
					return getUserAgent(s);
				} else if (matcher2.group(2).matches("PalmCentro")
						&& matcher2.group(3).matches("\\/")) {
					stringbuffer.append("-").append(matcher2.group(4));
				}
			} else if (s.indexOf("/") == -1) {

				stringbuffer.append(s);
			} else {

				stringbuffer.append(s.substring(0, s.indexOf("/")));
			}
		}
		// System.out.println(stringbuffer.toString());

		return stringbuffer.toString();
	}

}
