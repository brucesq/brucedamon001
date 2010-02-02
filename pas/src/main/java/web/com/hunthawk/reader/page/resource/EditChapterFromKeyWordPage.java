package com.hunthawk.reader.page.resource;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

@Restrict(roles = { "resourcechange" }, mode = Restrict.Mode.ROLE)
public abstract class EditChapterFromKeyWordPage extends EditPage implements PageBeginRenderListener{


	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	public abstract ResourceAll getResourceAll();
	public abstract void setResourceAll(ResourceAll resourceAll);


	public abstract String getChapterId();
	public abstract void setChapterId(String chapterId);
	
	public abstract String getContent();
	public abstract void setContent(String content);
	
	public abstract String getChoose();
	public abstract void setChoose(String choose);

	public abstract String getChapterName();
	public abstract void setChapterName(String chapterName);
	
	public abstract String getChapterOrder();
	public abstract void setChapterOrder(String chapterOrder);
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return Object.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */

	@Override
	protected boolean persist(Object obj) {
		try {
			
			EbookChapter eChapter = null;
			MagazineChapter mChapter = null;
			NewsPapersChapter nChapter = null;
			ComicsChapter cChapter = null;
			String content = getContent();
			System.out.println("--内容长度--"+content.length());
			if (obj instanceof EbookChapter) {
				eChapter = (EbookChapter) obj;
				eChapter.setBContent(content);
			} else if (obj instanceof MagazineChapter) {
				mChapter = (MagazineChapter) obj;
				mChapter.setContent(content);
			} else if (obj instanceof NewsPapersChapter) {
				nChapter = (NewsPapersChapter) obj;
				nChapter.setContent(content);
			} else if (obj instanceof ComicsChapter) {
				cChapter = (ComicsChapter) obj;
				nChapter.setContent(content);
			}
			System.out.println("------资源-------"+getResourceAll());
			getResourceService().updateResourceChapter(obj);
			if(getResourceAll()!=null)
				getResourceService().updateResource(getResourceAll(), 0);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new Object());
		}
	}
	
	public void savePage(){
		setChoose("true");
		save();
	}
}
