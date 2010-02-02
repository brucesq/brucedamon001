/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.annotations.InjectObject;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.page.util.KeyWordField;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.KeyWordFilter;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class CheckResourceKeyWordPage extends SecurityPage {
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	@InjectObject("spring:keywordFilter")
	public abstract KeyWordFilter getKeyWordFilter();
	
	public abstract void setResources(Set resources);
	
	public abstract Set getResources();

	public List getTableModel(){
		List<KeyWordField> fields = new ArrayList<KeyWordField>();
		for(Object obj : getResources()){
			ResourceAll resource = (ResourceAll)obj;
			List chapters = getResourceChapter(resource.getId());
			for(Object chapter : chapters){
				List<String> words = getKeyWordFilter().getContentKeyWord(getChapterContent(chapter),new ArrayList<KeyWord>());
				if(words.size() > 0){
					String word = "";
					for(String s : words){
						word += s +",";
					}
					KeyWordField field = new KeyWordField();
					field.setChapterName("");
					field.setId("");
					field.setKeyWord(word);
					field.setResourceName(resource.getName());
					fields.add(field);
				}
				
			}
		}
		
		return fields;
	}
	
	public List getResourceChapter(String resourceId){
		return getResourceService().getResourceChapter(getNewChapter(Integer.parseInt(resourceId.substring(0,1))), resourceId);
	}
	

	private String getChapterContent(Object chapter){
		try{
		if (chapter instanceof EbookChapter) {
			return (String) BeanUtils.forceGetProperty(chapter,
					"bContent");
		} else {
			return (String) BeanUtils.forceGetProperty(chapter,
					"content");
		}
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	
	private Class getNewChapter(Integer resourceType) {
		if (ResourceAll.RESOURCE_TYPE_BOOK.equals(resourceType)) {
			return EbookChapter.class;
		} else if (ResourceAll.RESOURCE_TYPE_COMICS.equals(resourceType)) {
			return ComicsChapter.class;
		} else if (ResourceAll.RESOURCE_TYPE_MAGAZINE.equals(resourceType)) {
			return MagazineChapter.class;
		} else if (ResourceAll.RESOURCE_TYPE_NEWSPAPER.equals(resourceType)) {
			return NewsPapersChapter.class;
		}
		return null;
	}
	
	
}
