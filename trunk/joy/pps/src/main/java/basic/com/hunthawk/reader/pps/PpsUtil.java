/**
 * 
 */
package com.hunthawk.reader.pps;

import java.util.ArrayList;
import java.util.List;

import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * @author BruceSun
 *
 */
public class PpsUtil {
	private static final String MUTI_START = "<%";
	private static final String MUTI_END = "%>";
	private static final String MUTI_LETTER = "%><";
	
	public static List<String> getParameters(String para){
		PatternCompiler compiler = new Perl5Compiler();
		List<String> list = new ArrayList<String>();
		try {
			Pattern pattern = compiler.compile(MUTI_START
					+ "[^\\" + MUTI_LETTER + "]*" + MUTI_END);
			PatternMatcherInput input = new PatternMatcherInput(
					para);
			PatternMatcher matcher = new Perl5Matcher();
			while (matcher.contains(input, pattern)) {
				MatchResult result = matcher.getMatch();
				String value = result.group(0);
				value = value.substring(MUTI_START.length(),
						result.length() - MUTI_END.length());
				list.add(value);
			}
		} catch (Exception e) {
		}
		return list;
	}
	public static int getRandom(int start, int end) {
		Double l = Math.random();
		Integer lv = end - start;
		Double k = l * lv;
		return start + k.intValue();
	}
	public static void main(String[] args){
//		String s = "<%asdas%><%1234%>";
//		List<String> list = getParameters(s);
//		for(String str : list){
//			System.out.println(str);
//		}
		
		System.out.println(getRandom(60,100));
	}
}
