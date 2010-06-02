package com.hunthawk.tag.util;



import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import org.apache.commons.lang.text.StrSubstitutor;

public class LocalizationWriter extends PrintWriter {
	private Logger log = Logger.getLogger(LocalizationWriter.class);
	private String local;

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public LocalizationWriter(OutputStreamWriter writer) {
		super((OutputStreamWriter)writer);
		/*	加载默认的本地化文件 message.properties   */
		LocalizationMessageFactory factory = LocalizationMessageFactory.getInstance();
		factory.getLocalizationMessage(null);
	}

	public void println(String str){
//		log.debug(str);
		
		str = localizeProcess2(str); 
		
		super.println(str);
		flush();
	}
	
	@SuppressWarnings("unused")
	private String localizeProcess2(String str) {
		LocalizationMessageFactory factory = LocalizationMessageFactory.getInstance();
		LocalizationMessage msg = factory.getLocalizationMessage(local);
		if(null != msg){
			Set keys = msg.keySet();
			Iterator iter = keys.iterator();
			Map<String,String> valueMap = new HashMap<String,String>();
			String key = null;
			while(iter.hasNext()) {
				key = (String)iter.next();
				valueMap.put(key, msg.getMessage(key));
			}
			
			StrSubstitutor sub = new StrSubstitutor(valueMap);
			str = sub.replace(str);
		}
		
		return str;
	}
	
	/**
	 * 将内容中的${xxx}串替换为properites文件对应的字符
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unused")
	private String localizeProcess(String str) {
		log.debug("org str : \r\n" + str);
		
		StringBuffer sb = null;
		LocalizationMessageFactory factory = LocalizationMessageFactory.getInstance();
		LocalizationMessage msg = factory.getLocalizationMessage(local);
		/*	按照本地化信息查找资源文件映射，如果有则进行替换		*/
		
		if(null != msg){
			String regx = "\\$\\{([^\\}]+)\\}";
			PatternCompiler compiler = new Perl5Compiler();
			
			try {
				Pattern pattern = compiler.compile(regx);
				PatternMatcher matcher = new Perl5Matcher();
				PatternMatcherInput input =  new PatternMatcherInput(str);
				
				int index = 0;
				String key = "";
				String value = "";
				
				while(matcher.contains(input, pattern)) {
//					log.debug("index = " + index);
					if(null == sb)
						sb = new StringBuffer();
					
					MatchResult result = matcher.getMatch();
					key = result.group(1);
					sb.append(str.substring(index, result.beginOffset(0)));
					index = result.endOffset(0);
					value = msg.getMessage(key) == null ?"${" + key + "}" : msg.getMessage(key);
					sb.append(value);
					log.debug("sb = \r\n" + sb.toString());
				}
				if(null != sb && index < str.length() - 1){
					sb.append(str.substring(index));
				}
				log.debug("str length = " + str.length());
				log.debug("index over = " + index);
				
			} catch (MalformedPatternException e) {
				e.printStackTrace();
			}
		}
		
		if(null != sb)
			return sb.toString();
		else
			return str;
	}
	
//	public static void main(String[] args) {
//		String regx = "\\$\\{([^\\}]+)\\}";
//		String str = "${nextPage}${a$}d{}b123${prePage}";
//		PatternCompiler compiler = new Perl5Compiler();
//		try {
//			Pattern pattern = compiler.compile(regx);
//			PatternMatcher matcher = new Perl5Matcher();
//			PatternMatcherInput input =  new PatternMatcherInput(str);
////			System.out.println("match = " + matcher.contains(input, pattern));
//			while(matcher.contains(input, pattern)) {
//				MatchResult result = matcher.getMatch();
////				log.debug(result.group(0));
////				log.debug(result.group(1));
//				for(int i = 0; i < result.groups(); ++i)
//					System.out.println("____ " + i + ":" + result.group(i));
//			}
//		} catch (MalformedPatternException e) {
//			e.printStackTrace();
//		}
//	}
}
