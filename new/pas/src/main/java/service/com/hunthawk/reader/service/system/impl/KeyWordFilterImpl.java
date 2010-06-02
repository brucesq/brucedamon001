/**
 * 
 */
package com.hunthawk.reader.service.system.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.service.system.KeyWordFilter;

/**
 * @author BruceSun
 *
 */
public class KeyWordFilterImpl implements KeyWordFilter {

	private int reloadInterval = 60*60*1000;
	
	private Long lastReloadTime = null;
	
	private Map map = new HashMap();
	
	private char[] firstChar = null;
	
	private HibernateGenericController controller;
	
	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}
	
	public void setReloadInterval(int reloadInterval) {
		this.reloadInterval = reloadInterval;
	}

	public List<String> getContentKeyWord(String content,List<KeyWord> keyWordList) {
		List<String> keywords = new ArrayList<String>();
		process(content,2,"",keywords,keyWordList);
		return keywords;
	}
	
	public String replaceContentKeyWord(String content,String replaceWord) {
		return process(content,2,replaceWord,new ArrayList<String>(),new ArrayList<KeyWord>());
		
	}

	/* (non-Javadoc)
	 * @see com.hunthawk.reader.service.system.KeyWordFilter#highlight(java.lang.String)
	 */
	public String highlight(String content,List<KeyWord> keyWordList) {
		
		return process(content,3,"",new ArrayList<String>(),keyWordList);
	}
	
	public boolean hasKeyWord(String content,List<KeyWord> keyWordList){
		return process(content,1,"",null,keyWordList) == null;
	}

	private synchronized void judgeLoad(List<KeyWord> keyWordList){
//		Long currentTime = System.currentTimeMillis();
//		if(lastReloadTime == null || (currentTime - lastReloadTime) > reloadInterval){
//			lastReloadTime = currentTime;
			load(keyWordList);
			
//		}
	}
	
	private void load(List<KeyWord> keywords){
//		List<KeyWord> keywords = new ArrayList<KeyWord>();
//		KeyWord w = new KeyWord();
//		w.setKeyWord("����");
//		keywords.add(w);
//		KeyWord o = new KeyWord();
//		o.setKeyWord("����");
//		keywords.add(o);
		//�����б�
		//List<KeyWordType> typeSet =  controller.getAll(KeyWordType.class);
		/*List<KeyWord> keywords = new ArrayList<KeyWord>();
		if(typeList!=null && typeList.size()>0){
			for(KeyWordType type : typeList){
				List<KeyWord> sublist = 
					controller.findBy(KeyWord.class, "type", type,"id",false);
				if(sublist!=null && sublist.size()>0)
					keywords.addAll(sublist);
			}
		}else{
			keywords = controller.getAll(KeyWord.class);
		}*/
		if(keywords==null && keywords.size()>0)
			keywords = controller.getAll(KeyWord.class);
		map.clear();
		for(KeyWord word : keywords){
			String line = word.getKeyWord();
			if(StringUtils.isEmpty(line))
				continue;
			String firstStr = line.substring(0, 1);
			ArrayList arr = null;
			if (map.containsKey(firstStr)) {
				arr = (ArrayList) map.get(firstStr);
			} else {
				arr = new ArrayList();
				map.put(firstStr, arr);
			}
			arr.add(line);
		}
		Set set = map.keySet();
		firstChar = new char[set.size()];
		Iterator iter = set.iterator();
		int i = 0;
		while (iter.hasNext()) {
			firstChar[i] = ((String) iter.next()).charAt(0);
			i++;
		}
	}
	/**
	 * <p>
	 * ���������໰�����type=1��ֱ�ӷ��أ������໰�滻���趨������
	 * </p>
	 * 
	 * @param content
	 * @param type
	 * @param replaceContent
	 * @return
	 */
	private  String process(String content, int type,
			String replaceContent,List<String> keywords,List<KeyWord> keyWordList ) {
		judgeLoad(keyWordList);
		
		StringBuffer buffer = new StringBuffer(content.length());
		int index = 0;
		int length = content.length();
		
		char ch;
		while (index < length) {
			ch = content.charAt(index);
			int i = 0;
			for (; i < firstChar.length; i++) {
				if (firstChar[i] == ch) {
					break;
				}
			}
			if (i < firstChar.length) {
				String line = getDirtyItem(i, content, index, length);
				if (line != null) {
					if (type == 1) {
						return null;
					} else {
//						if(!keywords.contains(line)){
							//keywords.add(line);
//							String start=content.substring((content.indexOf(line)-20)<0?0:(content.indexOf(line)-20),content.indexOf(line));//��ǰȡ20����
//							String end=content.substring((content.indexOf(line)+line.length()),((content.indexOf(line)+line.length())+20)>content.length()?content.length():((content.indexOf(line)+line.length())+20));//���ȡ20����
//							keywords.add(start+line+end);
//						}
						int begin=index+line.length();
						String start=content.substring(((index-20)<0?0:(index-20)),index);//����һ��lineλ��+1��ʼ ���� ������  �� ��ǰȡ20����
						String end=content.substring(begin,((begin)+20)>content.length()?content.length():(begin+20));//���ȡ20����
						keywords.add(start+line+end);
						
						if(type == 2){
							buffer.append(replaceContent);
						}else{
							buffer.append("<font color=\"red\">"+line+"</font>");
						}
						index = index + line.length() - 1;
					}

				}else{
					buffer.append(ch);
				}
			}else{
				buffer.append(ch);
			}
			index++;
		}
		return buffer.toString();
	}

	/**
	 * <p>
	 * ��������д��ض�λ�ÿ�ʼ�������໰������������໰����null
	 * </p>
	 * 
	 * @param id
	 *            firstChar�е����
	 * @param content
	 *            �Ƚϵ�����
	 * @param index
	 *            ���ݵ���ʼλ��
	 * @param length
	 *            ���ݵĳ���
	 * @return
	 */
	private  String getDirtyItem(int id, String content, int index,
			int length) {
		String key = (new Character(firstChar[id])).toString();
		List list = (List) map.get(key);
		for (int i = 0; i < list.size(); i++) {
			String line = (String) list.get(i);
			int ll = line.length();
			if (ll <= length - index) {
				if ((content.substring(index, index + ll)).compareTo(line) == 0) {
					return line;
				}
			}
		}
		return null;
	}
	
	/*public static void main(String[] args){
		KeyWordFilterImpl filter = new KeyWordFilterImpl();
		String content = "���������������";
		List<String> keywords = filter.getContentKeyWord(content);
		for(String key : keywords){
			System.out.println(key);
		}
		System.out.println(filter.highlight(content));
		System.out.println(filter.replaceContentKeyWord(content,"###"));
	}*/

}
