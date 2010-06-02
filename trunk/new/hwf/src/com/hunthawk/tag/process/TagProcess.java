/**
 * 
 */
package com.hunthawk.tag.process;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.Tag;
import com.hunthawk.tag.TagConstants;
import com.hunthawk.tag.TagFactory;
import com.hunthawk.tag.util.PropertyUtil;

/**
 * <p>
 * Tag 标签替换处理类
 * </p>
 * 
 * @author sunquanzhi
 * 
 */
public class TagProcess {

	private static Logger logger = Logger.getLogger(TagProcess.class);

	/**
	 * @since 1.1
	 * @param template
	 * @param factory
	 * @param request
	 * @return
	 */
	public static String process(String template, TagFactory factory,
			HttpServletRequest request) {
		Map resultMap = new HashMap();
		Map processMap = new HashMap();
		Map parameterMap = new HashMap();
		return parse(template, resultMap, processMap, parameterMap, factory,
				request);
	}

	/**
	 * @since 1.1
	 * @param template
	 * @param resultMap
	 * @param processMap
	 * @param factory
	 * @param request
	 * @return
	 */
	private static String parse(String template, Map resultMap, Map processMap,
			Map parameterMap, TagFactory factory, HttpServletRequest request) {
		PatternCompiler compiler = new Perl5Compiler();
		StringBuffer buffer = new StringBuffer();
		int index = 0;
		try {

			Pattern pattern = compiler.compile("\\"
					+ TagConstants.PRE_TAG_SUFFIX + "[^\\"
					+ TagConstants.END_TAG_SUFFIX + "]*\\"
					+ TagConstants.END_TAG_SUFFIX);
			Pattern tagpattern = compiler.compile("\\"
					+ TagConstants.PRE_TAG_SUFFIX + "([^\\"
					+ TagConstants.TAG_PARAMETERS
					+ TagConstants.TAG_VALUE_INSTANCE + "]*)\\"
					+ TagConstants.TAG_VALUE_INSTANCE + "?([^\\"
					+ TagConstants.TAG_PARAMETERS + "]*)\\"
					+ TagConstants.TAG_PARAMETERS + "?(.*)"
					+ TagConstants.END_TAG_SUFFIX);
			PatternMatcher matcher = new Perl5Matcher();
			PatternMatcherInput input = new PatternMatcherInput(template);
			while (matcher.contains(input, pattern)) {

				MatchResult result = matcher.getMatch();
				buffer.append(template.substring(index, result.beginOffset(0)));
				index = result.endOffset(0);
				String tag = result.group(0);
				PatternMatcher tagmatcher = new Perl5Matcher();
				if (tagmatcher.contains(tag, tagpattern)) {
					MatchResult rt = tagmatcher.getMatch();
					int count = rt.groups();
					if (count >= 1) {
						String tagName = rt.group(1);
						String strParameter = "";
						if (count >= 3) {
							strParameter = rt.group(3);
						}
						if (!processMap.containsKey(tagName
								+ TagConstants.TAG_PARAMETERS + strParameter)) {
							processMap.put(tagName
									+ TagConstants.TAG_PARAMETERS
									+ strParameter, "");
							Tag tagClass = factory.getTagClass(tagName);
							// boolean bParameter = false;
							if (tagClass != null) {
								if (!"".equals(strParameter)) {
									setParameter(tagClass, strParameter);
									// bParameter = true;
								}
								try {
									long beginTime = System.currentTimeMillis();
									Map map = tagClass.parseTag(request,
											tagName);
									long endTime = System.currentTimeMillis();
									long spendTime = endTime - beginTime;
									if (spendTime > 1000) {
										logger
												.warn("CachedTagProcess：[info=标签执行时间过长,SPEND="
														+ spendTime
														+ " ms,TagName="
														+ tagName
														+ ",Request="
														+ request
																.getQueryString()
														+ "]");
									}
									factory.returnTag(tagName, tagClass);
									//										
									// if(bParameter)
									// {
									// setParameter(tagClass,"");
									// }
									if (RedirectUtil.isRedirect()) {
										return "";
									}
									if (map != null) {
										Iterator iter = map.keySet().iterator();
										while (iter.hasNext()) {
											String key = (String) iter.next();
											String value = (String) map
													.get(key);
											if (!"".equals(strParameter)) {
												key = key
														.substring(
																0,
																key.length()
																		- TagConstants.END_TAG_SUFFIX
																				.length());
												key += TagConstants.TAG_PARAMETERS
														+ strParameter
														+ TagConstants.END_TAG_SUFFIX;
											}
											value = parse(value, resultMap,
													processMap, parameterMap,
													factory, request);
											resultMap.put(key, value);

										}
									}else{
										logger.error("["+tagName+"'s value is null]");
//										resultMap.put(tag,"["+tagName+"'s value is null]");
									}
								} catch (Exception e) {
									e.printStackTrace();
									logger.error("Run " + tagName + " error: ",
											e);
//									resultMap.put(tag,"["+tagName+" throws a exception]");
								}
							} else {
								logger.info(tagName
										+ "'s class has not defined!");
//								resultMap.put(tag,"["+tagName+" not found.]");
								
							}
						}
						replaceTag(buffer, tag, resultMap);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (index < template.length()) {
			buffer.append(template.substring(index, template.length()));
		}
		return buffer.toString();
	}

	/**
	 * <p>
	 * Format the content if tag's value is null
	 * </p>
	 * 
	 * @since 1.0
	 * @param content
	 * @param buffer
	 * @return
	 */
	private static String replaceSpace(String content, StringBuffer buffer) {
		String temp = buffer.toString();
		temp = temp.trim();

		if (temp.length() < 4)
			return content;
		if (!(buffer.charAt(temp.length() - 1) == '>')) {
			return content;
		}
		if (!(buffer.charAt(temp.length() - 2) == '/')) {
			return content;
		}
		if (!(buffer.charAt(temp.length() - 3) == 'r')) {
			return content;
		}

		while (content.startsWith(" ")) {
			content = content.substring(1);
		}
		if (content.startsWith("<br/>")) {
			content = content.substring(5);
		}
		return content;
	}

	/**
	 * @since 1.0
	 * @param buffer
	 * @param tag
	 * @param map
	 * @return
	 */
	private static boolean replaceTag(StringBuffer buffer, String tag, Map map) {
		String value = (String) map.get(tag);
		if (value != null && !"".equals(value)) {
			buffer.append(value);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * Set Tag class parameter property
	 * </p>
	 * 
	 * @since 1.1
	 * @param obj
	 * @param parameter
	 */
	private static void setParameter(Object obj, String parameter) {
		// long startTime = System.currentTimeMillis();
		if (obj instanceof BaseTag) {
			BaseTag bTag = (BaseTag) obj;
			bTag.setParameter(parameter);
		} else {
			PropertyUtil.setProperty(obj, "parameter", parameter);
		}

		// long endTime = System.currentTimeMillis();
		// logger.info((endTime - startTime)+" ms");
	}

}
