/**
 * 
 */
package com.hunthawk.framework.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author BruceSun
 *
 */
public class StringGenerator {

	
	public static String generatorNext(String str)throws Exception{
		if(!StringUtils.isAlpha(str))
			throw new Exception("String must be alpha.");
		char[] cs = str.toCharArray();
		for(int i=cs.length-1;i>=0;i--){
			char c = cs[i];
			if(c<'z'){
				cs[i] = (char)(c+1);
				break;
			}else{
				cs[i]='a';
			}
		}
		
		return new String(cs);
	}
	
	
	
	public static void main(String[] args) throws Exception{
		String str = "zzzz";
		for(int i=0;i<1;i++){
			str = generatorNext(str);
			System.out.println(str);
		}
		
	}
}
