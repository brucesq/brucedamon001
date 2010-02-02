package com.hunthawk.framework.util;

/**
 * 自定义 string工具类
 * @author yuzs
 *
 */
public class StrUtil {

	private static final int arr1[]={1000,500,100,50,12,11,10,9,8,7,6,5,4,3,2,1};
	private static final String arr2[]={"M","D","C","L","Ⅻ", "Ⅺ","Ⅹ","Ⅸ","Ⅷ","Ⅶ","Ⅵ","Ⅴ", "Ⅳ","Ⅲ","Ⅱ","Ⅰ"};
	
	/**
	 * 输入数字变成罗马数字,
	 * @param n
	 * @return
	 */
	 public static String toRoman(int n){
		 String roman="";
			int i = 0;
			do{
				if(n>=arr1[i]){
					if(n==12){
						roman += "Ⅻ";	
						break;
					}else if(n==11){
						roman += "Ⅺ";
						break;
					}else if(n>12 && n<50){
						i = 6;
						n -= arr1[i];
						roman += arr2[i];
					}else{
						n -= arr1[i];
						roman += arr2[i];
					}
				}else{
					i++;
				}
			}while(n>0 && i<arr1.length);
			
			return roman;
	 }
}
