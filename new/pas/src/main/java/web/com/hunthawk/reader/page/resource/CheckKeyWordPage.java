package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectObject;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.page.util.DetailPageField;
import com.hunthawk.reader.service.system.KeyWordFilter;

/**
 * @author yuzs
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class CheckKeyWordPage extends SecurityPage {

	@Asset("img/Toolbar_bg.png")
	public abstract IAsset getBackGroundIcon();

	@InjectObject("spring:keywordFilter")
	public abstract KeyWordFilter getKeyWordFilter();

	public abstract void setCurrentObject(Object obj);

	public abstract Object getCurrentObject();
	
	/**
	 * 敏感词分类list集合
	 */
	public abstract List<KeyWord> getTypeList();
	public abstract void setTypeList(List<KeyWord> list);
	
	public List<DetailPageField> getDetailList() {

		return getObjectFields(getCurrentObject());
	}

	public abstract DetailPageField getCurrentDetailField();

	private List<DetailPageField> getObjectFields(Object obj) {

		EbookChapter eChapter = null;
		MagazineChapter mChapter = null;
		NewsPapersChapter nChapter = null;
		ComicsChapter cChapter = null;
		String content = "";
		if (obj instanceof EbookChapter) {
			eChapter = (EbookChapter) obj;
			content = eChapter.getBContent();// 书的内容
		} else if (obj instanceof MagazineChapter) {
			mChapter = (MagazineChapter) obj;
			content = mChapter.getContent();
		} else if (obj instanceof NewsPapersChapter) {
			nChapter = (NewsPapersChapter) obj;
			content = nChapter.getContent();
		} else if (obj instanceof ComicsChapter) {
			cChapter = (ComicsChapter) obj;
			content = cChapter.getContent();
		}

		List<DetailPageField> details = new ArrayList<DetailPageField>();
		DetailPageField field = new DetailPageField();
		field.setTitle("内容");
		field.setValue(getKeyWordFilter().highlight(content,getTypeList()));
		details.add(field);
		return details;
	}

	private Object getProperty(Object obj, String prop) {
		Object value = null;
		try {
			value = BeanUtils.forceGetProperty(obj, prop);
		} catch (Exception e) {
			logger.error("版权下载页获取对象属性信息时报错.", e);
			value = "";
		}
		return value;
	}
}
