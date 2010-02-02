package com.hunthawk.reader.pps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author liuxh
 *
 */
public class ArrayUtil {
	 /**
     * 返回两个数组中不相同的元素
     * @param arr1	小集合
     * @param arr2  大集合
     * @return
     */
    public static String[] isContentDiffKey(String[] arr1,String[] arr2){ 
    	String[] diffKey=new String[arr2.length-arr1.length];
    	java.util.Arrays.sort(arr1); 
    	int loop=0;
    	for(int i=0;i<arr2.length;i++){ 
    		if(java.util.Arrays.binarySearch(arr1, arr2[i])<0){
    			diffKey[loop]=arr2[i];
    			loop++;
    		}
    	} 
    	return diffKey; 
    }
    /**
     * 随机生成count个num以内的不重复的数字集合
     * @param number
     * @param count
     * @return
     */
    public static Integer[] getRandom(int num,int count){
    	if(num == 0 || num < count){
    		return new Integer[0];
    	}
    	
    	Integer[] n = new Integer[count];
    	ArrayList<Integer> nums = new ArrayList<Integer>();
    	for(int i=0; i<num; i++){
    		nums.add(i);
    	}
    	
    	Random r=new Random();
    	for(int i=0; i<count; i++){
    		int index = r.nextInt(nums.size());
    		n[i] = nums.get(index);
    		nums.remove(index);
    	}
        return n;
    }
    /**
     * 过滤集合中重复的元素 
     * @param l
     * @return
     * @author liuxh 09-11-13
     */
    public static List<Integer> filterRedundanceData(List<Integer> l){
    	if(l==null)
    		return new ArrayList<Integer>();
    	 Set   set=new   HashSet();   
		 set.addAll(l);   
		 Integer[] arr=(Integer[])set.toArray(new Integer[0]);
		 return  Arrays.asList(arr);
    }
    public static boolean isHave(String str,String arr[]){
    	if(arr==null || arr.length==0)
    		return false;
    	if(arr.length==1){
    		return str.equals(arr[0]);
    	}
    	 java.util.Arrays.sort(arr); 
    	 return  Arrays.binarySearch(arr, str)>0;
    }
    public static void main(String [] args){
    	System.out.println(isHave("7350",new String[]{"73501"}));
    	List<Integer> relids=new ArrayList<Integer>();
    	relids.add(1);
    	relids.add(2);
    	relids.add(3);
    	relids.add(1);
    	relids.add(3);
    	relids=filterRedundanceData(relids);
    	for(Integer id:relids){
    		System.out.println(id);
    	}
//    	String [] strs1=new String[]{"11","22"};
//    	String [] strs2=new String[]{"11","22","33","44","55"};
//    	String [] list=isContentDiffKey(strs1,strs2);
//    	System.out.println("不同的元素为：");
//    	for(String id:list){
//    		System.out.println(""+id);
//    	}
//    	String str="6100";
//    	String [] arr=new String[]{"1234","61001","3278","6100"};
//    	System.out.println(isHave(str,arr));
//    	
//    	Integer arr[]=getRandom(30,20);
//    	for(Integer i:arr){
//    		System.out.println(i);
//    	}
    	//String s=" A Test ";
//    	String s=null;
//    	System.out.println(StringUtils.trim(s));
//    	System.out.println(StringUtils.trimToEmpty(s));
//    	System.out.println(StringUtils.trimToNull(s));
//    	Integer[] rams = getRandom(5,3);
//    	
//    	for(Integer i : rams){
//    		System.out.println(i);
//    	}
    }
}
