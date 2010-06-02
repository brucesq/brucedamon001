package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.Infomation;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.domain.resource.VideoSuite;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.util.DetailPageField;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author yuzs
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class ShowEbookDetialPage extends SecurityPage {

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	@Asset("img/Toolbar_bg.png")
	public abstract IAsset getBackGroundIcon();
	
	

	public abstract void setCurrentObject(Object obj);

	public abstract  Object getCurrentObject();

	public List<DetailPageField> getDetailList(){
		
		return getObjectFields(getCurrentObject());
	}

	public List<DetailPageField> getCatalogList(){
		return getEbookCatalogFields(getCurrentObject());
	}
	
	public List<DetailPageField> getPackList(){
		return getPackFields(getCurrentObject());
	}
	public abstract DetailPageField getCurrentDetailField();

	public abstract DetailPageField getCurrentCatalogField();
	
	public abstract DetailPageField getCurrentPackField();
	
	public List<DetailPageField> getEbookCatalogFields(Object obj){
		List<DetailPageField> details = new ArrayList<DetailPageField>();
		ResourceAll resourceAll = (ResourceAll)obj;
		if(resourceAll instanceof Ebook){
			List<EbookChapter> list = getResourceService().getResourceChapter(EbookChapter.class, resourceAll.getId());
			System.out.println(list.size());
			for(EbookChapter chapter : list){
				DetailPageField field = new DetailPageField();
				field.setTitle(chapter.getId());
				field.setValue("章节"+chapter.getChapterIndex()+":"+chapter.getName());
				details.add(field);
			}
		}
		if(resourceAll instanceof Magazine){
			List<MagazineChapter> list = getResourceService().getResourceChapter(MagazineChapter.class, resourceAll.getId());
			System.out.println(list.size());
			for(MagazineChapter chapter : list){
				DetailPageField field = new DetailPageField();
				field.setTitle(chapter.getId());
				field.setValue("栏目"+chapter.getChapterIndex()+":"+chapter.getName());
				details.add(field);
			}	
		}
		if(resourceAll instanceof NewsPapers){
			List<NewsPapersChapter> list = getResourceService().getResourceChapter(NewsPapersChapter.class, resourceAll.getId());
			System.out.println(list.size());
			for(NewsPapersChapter chapter : list){
				DetailPageField field = new DetailPageField();
				field.setTitle(chapter.getId());
				field.setValue("栏目"+chapter.getChapterIndex()+":"+chapter.getName());
				details.add(field);
			}	
		}
		if(resourceAll instanceof Comics){
			List<ComicsChapter> list = getResourceService().getResourceChapter(ComicsChapter.class, resourceAll.getId());
			System.out.println(list.size());
			for(ComicsChapter chapter : list){
				DetailPageField field = new DetailPageField();
				field.setTitle(chapter.getId());
				field.setValue("章节"+chapter.getChapterIndex()+":"+chapter.getName());
				details.add(field);
			}	
		}
		if(resourceAll instanceof Video){
			List<VideoSuite> list = getResourceService().getResourceChapter(VideoSuite.class, resourceAll.getId());
			System.out.println(list.size());
			for(VideoSuite chapter : list){
				DetailPageField field = new DetailPageField();
				field.setTitle(chapter.getId());
				field.setValue("文件"+chapter.getChapterIndex()+":"+chapter.getFilename());
				details.add(field);
			}	
		}
		return details;
	}
	
	private List<DetailPageField> getObjectFields(Object obj) {

		Variables variables = getSystemService().getVariables(
				"detail:" + getCurrentObject().getClass().getName());
		List<DetailPageField> details = new ArrayList<DetailPageField>();
		DetailPageField fieldCover = new DetailPageField();
		fieldCover.setTitle("封面图片");
		ResourceAll resource = (ResourceAll)obj;
		String imgPath = "";
		if (resource instanceof Ebook) { // 图书
			Ebook book = (Ebook) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					book.getBookPic());
		}
		if (resource instanceof Magazine) { // 杂志
			Magazine magazine = (Magazine) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					magazine.getImage());
		}
		if (resource instanceof NewsPapers) { // 报纸
			NewsPapers newsPapers = (NewsPapers) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					newsPapers.getImage());
		}
		if (resource instanceof Comics) { // 漫画
			Comics comics = (Comics) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					comics.getImage());
		}
		if (resource instanceof Video) { // 视频
			Video video = (Video) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					video.getImage());
		}
		if (resource instanceof Infomation) { // 新闻
			Infomation info = (Infomation) resource;
			imgPath = getResourceService().getPreviewCoverImg(resource.getId(),
					info.getImage());
		}
		fieldCover.setValue("<img src='"+imgPath+"' align='absmiddle'");
		details.add(fieldCover);
		String desc = variables.getValue();
		String[] props = desc.split(",");
		for (String prop : props) {
			DetailPageField field = new DetailPageField();
			String[] kv = prop.split("=");
			if(kv.length < 2){
				continue;
			}
			field.setTitle(kv[1]);
			field.setValue(getObjectField(obj, kv[0]));
			details.add(field);
		}
		DetailPageField fieldChapter = new DetailPageField();
		fieldChapter.setTitle("章节信息");
		fieldChapter.setValue("");
		details.add(fieldChapter);
		return details;
	}

	private String getObjectField(Object obj, String prop) {
		if (prop.startsWith("@")) {
			Object target = getProperty(obj,prop.substring(1));
			List<DetailPageField> fields = getObjectFields(target);
			StringBuilder builder = new StringBuilder();
			for(DetailPageField field : fields){
				builder.append(field.getTitle());
				builder.append(":");
				builder.append(field.getValue());
				builder.append("\r\n");
			}
			return builder.toString();
		}else{
			return getProperty(obj,prop).toString();
		}
		
	}
	private Object getProperty(Object obj,String prop){
		String[] propertys = prop.split("\\.");
		try{
			Object value = obj;
			for(String str : propertys){
				value = BeanUtils.forceGetProperty(value, str);
			}
			return value.toString();
		}catch(Exception e){
//			logger.error("Memcached Enhance error!", e);
			return "";
		}
	}
	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();

	
	public List<DetailPageField> getPackFields(Object obj){
		ResourceAll resourceAll = (ResourceAll)obj;
		Set<ResourcePack> set = getResourcePackService().
			findResourcePackByResource(resourceAll.getId());
		List<DetailPageField> details = new ArrayList<DetailPageField>();
		if(set!=null && set.size()>0){
			for(ResourcePack pack : set){
				DetailPageField fieldChapter = new DetailPageField();
				fieldChapter.setTitle(pack.getId().toString());
				String feeMsg = "";
				if(pack.getType()==Constants.FEE_TYPE_FREE)
					feeMsg = "免费";
				if(pack.getType()==Constants.FEE_TYPE_VIP)
					feeMsg = "包月(VIP)";
				if(pack.getType()==Constants.FEE_TYPE_CHOICE)
					feeMsg = "包月(内容控制)";
				if(pack.getType()==Constants.FEE_TYPE_NORMAL)
					feeMsg = "包月(常规)";
				if(pack.getType()==Constants.FEE_TYPE_VIEW)
					feeMsg = "按次";
				fieldChapter.setValue("批价包名称："+pack.getName()+"-----计费类型："+feeMsg);
				details.add(fieldChapter);
			}
				//message += "|"+pack.getId();
			}
		return details;
	}
	@InjectPage("resource/ShowEbookChapterDetialPage")
	public abstract ShowEbookChapterDetialPage getShowEbookChapterDetialPage();
	
	public IPage showChapter(Object obj,String chapterId){
		ResourceAll resourceAll = (ResourceAll)obj;
		getShowEbookChapterDetialPage().setCurrentObject(resourceAll);
		getShowEbookChapterDetialPage().setChapterId(chapterId);
		return getShowEbookChapterDetialPage();
	}
	
	@InjectPage("resource/ShowEpackPage")
	public abstract ShowEpackPage getShowEpackPage();
	
	public IPage showPack(String packId){		
		getShowEpackPage().setPackid(Integer.parseInt(packId));
		return getShowEpackPage();
	}
	
}
