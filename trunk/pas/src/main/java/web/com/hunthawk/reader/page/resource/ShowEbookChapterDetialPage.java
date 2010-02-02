package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.util.DetailPageField;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author yuzs
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class ShowEbookChapterDetialPage extends SecurityPage {

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	@Asset("img/Toolbar_bg.png")
	public abstract IAsset getBackGroundIcon();
	
	

	public abstract void setCurrentObject(Object obj);
	public abstract  Object getCurrentObject();

	public abstract void setChapterId(String id);
	public abstract String getChapterId();
	
	public List<DetailPageField> getDetailList(){
		
		return getObjectFields(getCurrentObject());
	}


	public abstract DetailPageField getCurrentDetailField();

	public List<DetailPageField> getObjectFields(Object obj){
		List<DetailPageField> details = new ArrayList<DetailPageField>();
		ResourceAll resourceAll = (ResourceAll)obj;
		String chapterId = getChapterId();
		if(resourceAll instanceof Ebook){
			EbookChapter chapter = getResourceService().getEbookChapterById(chapterId);
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("ID");
			filed.setValue(chapter.getId());
			details.add(filed);}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("name");
			filed.setValue(chapter.getName());
			details.add(filed);}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("ÕÂ½ÚÐòºÅ");
			filed.setValue(chapter.getChapterIndex().toString());
			details.add(filed);}
			{
				DetailPageField filed = new DetailPageField();
			filed.setTitle("ÕÂ½ÚÍ¼Æ¬");
			String imgPath="";
			String[] chapters = chapter.getImages();
			if(chapter.getImageName()!=null && !"".equals(chapter.getImageName())){
			for(int i=0;i<chapters.length;i++){
				String url = getResourceService().getChapterImg(resourceAll.getId(), chapters[i]);
				imgPath += "<img src='" + url + "' align='absmiddle'width='50' height='50' id='srcImg' />";
				filed.setValue(imgPath);
				}
			}else{
				filed.setValue(imgPath+"ÎÞÍ¼Æ¬");
			}
			details.add(filed);
			}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("ÕÂ½ÚÄÚÈÝ");
			filed.setValue(chapter.getBContent());
			details.add(filed);}
		}
		if(resourceAll instanceof Magazine){

			MagazineChapter chapter = getResourceService().getMagazineChapterById(chapterId);
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("ID");
			filed.setValue(chapter.getId());
			details.add(filed);}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("name");
			filed.setValue(chapter.getName());
			details.add(filed);}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("ÕÂ½ÚÐòºÅ");
			filed.setValue(chapter.getChapterIndex().toString());
			details.add(filed);}
			{
				DetailPageField filed = new DetailPageField();
			filed.setTitle("ÕÂ½ÚÍ¼Æ¬");
			String imgPath="";
			String[] chapters = chapter.getImages();
			if(chapter.getImageName()!=null && !"".equals(chapter.getImageName())){
			for(int i=0;i<chapters.length;i++){
				String url = getResourceService().getChapterImg(resourceAll.getId(), chapters[i]);
				imgPath += "<img src='" + url + "' align='absmiddle'width='50' height='50'id='srcImg'/>";
				filed.setValue(imgPath);
				}
			}else{
				filed.setValue(imgPath+"ÎÞÍ¼Æ¬");
			}
			details.add(filed);
			}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("À¸Ä¿ÄÚÈÝ");
			filed.setValue(chapter.getContent());
			details.add(filed);}
		
		}
		
		if(resourceAll instanceof NewsPapers){
			
			NewsPapersChapter chapter = getResourceService().getNewsPapersChapterById(chapterId);
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("ID");
			filed.setValue(chapter.getId());
			details.add(filed);}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("name");
			filed.setValue(chapter.getName());
			details.add(filed);}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("ÕÂ½ÚÐòºÅ");
			filed.setValue(chapter.getChapterIndex().toString());
			details.add(filed);}
			{
				DetailPageField filed = new DetailPageField();
			filed.setTitle("±¨Ö½Í¼Æ¬");
			String imgPath="";
			String[] chapters = chapter.getImages();
			if(chapter.getImageName()!=null && !"".equals(chapter.getImageName())){
			for(int i=0;i<chapters.length;i++){
				String url = getResourceService().getChapterImg(resourceAll.getId(), chapters[i]);
				imgPath += "<img src='" + url + "' align='absmiddle'width='50' height='50'id='srcImg'/>";
				filed.setValue(imgPath);
				}
			}else{
				filed.setValue(imgPath+"ÎÞÍ¼Æ¬");
			}
			details.add(filed);
			}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("À¸Ä¿ÄÚÈÝ");
			filed.setValue(chapter.getContent());
			details.add(filed);}
	
		}
		if(resourceAll instanceof Comics){
			ComicsChapter chapter = getResourceService().getComicsChapterById(chapterId);
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("ID");
			filed.setValue(chapter.getId());
			details.add(filed);}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("name");
			filed.setValue(chapter.getName());
			details.add(filed);}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("ÕÂ½ÚÐòºÅ");
			filed.setValue(chapter.getChapterIndex().toString());
			details.add(filed);}
			{
				DetailPageField filed = new DetailPageField();
			filed.setTitle("Âþ»­Í¼Æ¬");
			String imgPath="";
			String[] chapters = chapter.getImages();
			if(chapter.getImageName()!=null && !"".equals(chapter.getImageName())){
			for(int i=0;i<chapters.length;i++){
				String url = getResourceService().getChapterImg(resourceAll.getId(), chapters[i]);
				imgPath += "<img src='" + url + "' align='absmiddle' width='50' height='50'id='srcImg'/>";
				filed.setValue(imgPath);
				}
			}else{
				filed.setValue(imgPath+"ÎÞÍ¼Æ¬");
			}
			details.add(filed);
			}
			{DetailPageField filed = new DetailPageField();
			filed.setTitle("À¸Ä¿ÄÚÈÝ");
			filed.setValue(chapter.getContent());
			details.add(filed);}
	
		}
		return details;
	}
}
