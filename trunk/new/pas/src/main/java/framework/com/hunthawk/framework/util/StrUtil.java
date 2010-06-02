package com.hunthawk.framework.util;

/**
 * �Զ��� string������
 * @author yuzs
 *
 */
public class StrUtil {

	private static final int arr1[]={1000,500,100,50,12,11,10,9,8,7,6,5,4,3,2,1};
	private static final String arr2[]={"M","D","C","L","��", "��","��","��","��","��","��","��", "��","��","��","��"};
	
	/**
	 * �������ֱ����������,
	 * @param n
	 * @return
	 */
	 public static String toRoman(int n){
		 String roman="";
			int i = 0;
			do{
				if(n>=arr1[i]){
					if(n==12){
						roman += "��";	
						break;
					}else if(n==11){
						roman += "��";
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
