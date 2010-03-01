/**
 * 
 */
package com.hunthawk.reader.pps;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.tag.util.ParamUtil;

/**
 * @author BruceSun
 * 
 */
public class URLUtil {

	public static void append(StringBuilder builder, String paraName,
			HttpServletRequest request) {
		String value = request.getParameter(paraName);
		if (StringUtils.isNotEmpty(value)) {
			builder.append(paraName);
			builder.append("=");
			builder.append(value);
			builder.append("&");
		}
	}

	public static void trimURL(StringBuilder sb) {
		if (sb.charAt(sb.length() - 1) == '&') {
			sb.deleteCharAt(sb.length() - 1);
		}
	}

	public static StringBuilder trimUrl(StringBuilder sb) {
		if (sb.charAt(sb.length() - 1) == '&') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb;
	}

	public static String getBaseURL(HttpServletRequest request) {
		return request.getContextPath();
	}
	/**
	 * 获取当前URL地址
	 * @param request
	 * @return
	 */
	public static String getCurrentURL(HttpServletRequest request){
		StringBuilder builder = new StringBuilder();
		builder.append(request.getScheme());
		builder.append("://");
		builder.append(request.getServerName());
		if(request.getServerPort() != 80){
			builder.append(":");
			builder.append(request.getServerPort());
		}
		builder.append(request.getRequestURI());
		builder.append("?");
		builder.append(request.getQueryString());
		
		return builder.toString();
	}

	public static String removeParameter(String queryString, String... params) {
		queryString = queryString.replaceAll("&amp;", "&");
		String[] querys = queryString.split("&");
		StringBuilder sb = new StringBuilder(queryString.length());
		for (String query : querys) {
			String[] kv = query.split("=");
			boolean isInclude = false;
			for (String param : params) {
				if (kv[0].equals(param)) {
					isInclude = true;
					break;
				}
			}
			if (!isInclude) {
				sb.append("&");
				sb.append(query);
			}
		}
		if (sb.length() > 0)
			return sb.substring(1);
		else
			return sb.toString();

	}

	/**
	 * 从请求中获取资源ID，如果没有则取章节ID去掉后三位
	 * 
	 * @param request
	 * @return
	 */
	public static String getResourceId(HttpServletRequest request) {
		String rid = ParamUtil.getParameter(request,
				ParameterConstants.RESOURCE_ID);
		if (StringUtils.isEmpty(rid)) {
			rid = ParamUtil
					.getParameter(request, ParameterConstants.CHAPTER_ID);
			if (StringUtils.isNotEmpty(rid)) {
				rid = rid.substring(0, rid.length() - 3);
			}
		}
		return rid;
	}
	/**
	 * URL参数值替换
	 * @param url	原始URL
	 * @param params	要替换的键值MAP
	 * @param changeParamName	需要替换的参数
	 * @return
	 * @author liuxh  09-11-05
	 */
	public static String urlChangeParam(String url,
			Map<String, String> params,List<String> changeParamName){
		StringBuilder sb = new StringBuilder();
		String queryString = url.replaceAll("&amp;", "&");
		String[] querys = queryString.split("&");
		for (String query : querys) {
			String[] kv = query.split("=");
			if (params.containsKey(kv[0])) {
				sb.append(kv[0]);
				sb.append("=");
				sb.append(params.get(kv[0]));
				sb.append("&");
				params.remove(kv[0]);
			} else {
				sb.append(kv[0]);
				sb.append("=");
				sb.append(kv[1]);
				sb.append("&");
				params.remove(kv[0]);
			}
		}
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			sb.append("&");
		}
		trimURL(sb);
		return sb.toString();
	}
	public static String urlChange(HttpServletRequest request,
			Map<String, String> params, List<String> blackName) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");

		String queryString = request.getQueryString();
		queryString = queryString.replaceAll("&amp;", "&");
		String[] querys = queryString.split("&");

		for (String query : querys) {
			String[] kv = query.split("=");
			if (blackName.contains(kv[0])) {
				continue;
			}
			if (params.containsKey(kv[0])) {
				sb.append(kv[0]);
				sb.append("=");
				sb.append(params.get(kv[0]));
				sb.append("&");
				params.remove(kv[0]);
			} else {
				sb.append(query);
				sb.append("&");
			}
		}
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			sb.append("&");
		}
		trimURL(sb);
		return sb.toString();
	}

	public static String getURLvalue(String url, String param) {
		if (url.indexOf(param) == -1)// 没有查到返回"";
			return "";
		String queryString = url.replaceAll("&amp;", "&");
		String[] querys = queryString.split("&");

		for (String query : querys) {
			String[] kv = query.split("=");
			if (param.equals(kv[0])) {
				return kv[1];
			}
		}

		return "";
	}

	/**
	 * 过滤中文乱码的方法
	 * 
	 * @param oriStr
	 * @return
	 */
	public static String chineseFilter(String oriStr, int num) {

		if (num < 0) {
			num = 1;
		}
		String filtedStr = "";
		try {
			filtedStr = new String(oriStr.getBytes("UTF-8"), "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			// log.warn("[标签名=CommonUtil,功能=中文乱码过滤,]");
		}

		System.out.println("CCCCCCC:" + oriStr);
		System.out.println("BBBBBBB:" + filtedStr);

		String titleRegex = "(\\?{" + num + "})";

		Pattern pattern = Pattern.compile(titleRegex);
		Matcher matcher = pattern.matcher(oriStr);

		if (matcher.find()) {
			return filtedStr;
		}
		return oriStr;
	}

	/****
	 * 获取标签通用url连接地址<br>
	 * 用法<br>
	 * Map<String,String> values=new HashMap<String,String>();
	 * values.put(ParameterConstants.PAGE,"r");
	 * 
	 * @param values
	 *            map对象，分别是url参数的key和value
	 * @param request
	 * @return String 标签url
	 * @author penglei
	 */
	public static String getUrl(Map<String, String> values,
			HttpServletRequest request) {
		StringBuffer sb = new StringBuffer(); // 临时url
		List<String> list = new ArrayList<String>(); // 存放以替换的key
		StringBuffer url = new StringBuffer(); // url
		url.append(request.getContextPath());
		url.append(ParameterConstants.PORTAL_PATH);
		url.append("?");

		String page = values.get("pg");
		String queryString = request.getQueryString();
		String[] querys = queryString.split("&");
		String[] kv = null;
		Set<String> keys = values.keySet();
		if (StringUtils.isNotEmpty(page)) { // 默认 由PG在第一位
			url.append("pg");
			url.append("=");
			url.append(values.get("pg"));
			list.add("pg");
		}
		// 从传过来的参数和url对比，并且替换
		for (String key : keys) {
			if (key.equalsIgnoreCase("pg")) {
				continue;
			} else {
				for (int i = 0; i < querys.length; i++) {
					String query = querys[i];
					kv = query.split("=");
					// map里面有和url上参数一直的 替换url里面的参数
					if (key.equalsIgnoreCase(kv[0])) {
						sb.append("&");
						sb.append(key);
						sb.append("=");
						sb.append(values.get(key));
						list.add(key);
						break;
					} else if (i == querys.length - 1) {
						sb.append("&");
						sb.append(key);
						sb.append("=");
						sb.append(values.get(key));
						break;
					}
				}
			}

		}
		// 没传参数来的，自动解析url获取
		for (String query : querys) {
			kv = query.split("=");
			if(kv.length<2){
				continue;
			}
			int flag = 0;
			for (String paramType : list) {
				if (kv[0].equalsIgnoreCase(paramType)) {
					flag = 1;
					break;
				}
			}
			if (flag == 0) { // 解析url没有替换的直接使用url的参数
				if (kv[0].equalsIgnoreCase("pg")) {
					url.append(kv[0]);
					url.append("=");
					url.append(kv[1]);
				} else {
					url.append("&");
					url.append(kv[0]);
					url.append("=");
					url.append(kv[1]);
				}
			}

		}
		url.append(sb.toString());
		return url.toString();
	}
	/**
	 * 处理URL中包含&转义符的情况
	 * @param request
	 * @return
	 */
	public static Map<String,String> getParameters(String queryString){
		Map<String,String> parameters = new HashMap<String,String>();
		queryString = queryString.replaceAll("\\?", "&");
		queryString = queryString.replaceAll("%26", "&");
		String[] params =  queryString.split("&");
		for(String str : params){
			int index = str.indexOf("=");
			if(index > 0){
				String name = str.substring(0,index);
				String value = str.substring(index+1);
				parameters.put(name, value);
			}
		}
		return parameters;
	}

	public static void main(String[] args) {
		String query = "a=2&s=1&as=4&fr=as";
		/*
		 * System.out.println(removeParameter(query, "fr", "as")); StringBuilder
		 * sb = new StringBuilder(); sb.append(query); sb.append("&");
		 * trimURL(sb); System.out.println(sb.toString());
		 */
//		System.out.println(getURLvalue(query, "as"));
//		String url="pg=r&pd=150000000&gd=4073&ad=001&cd=2947&fd=7101&nd=19896&rd=10003948&pn=1&ed=350070000005";
//		System.out.println(url);
//		Map<String,String> values=new HashMap<String,String>();
//		values.put(ParameterConstants.FEE_BAG_ID, "6951");
//		values.put(ParameterConstants.FEE_BAG_RELATION_ID,"19800");
//		List<String> list=new ArrayList();
//		list.add(ParameterConstants.FEE_BAG_ID);
//		list.add(ParameterConstants.FEE_BAG_RELATION_ID);
//		System.out.println(urlChangeParam(url,values,list));
		
		String text = "pg=d%26pd=150000001%26gd=4070%26ad=001%26cd=2901%26fd=6801%26nd=49885%26zd=10014268004%26fc=15000000%26pn=1%26ed=222090000011?linkid=&productid=0551499901";
		Map<String,String> map = getParameters(text);
		for(Map.Entry<String, String> entry:map.entrySet()){
			System.out.println(entry.getKey()+"="+entry.getValue());
		}
			
	}

}
