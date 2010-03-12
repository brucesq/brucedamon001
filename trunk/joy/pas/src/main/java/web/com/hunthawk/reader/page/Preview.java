/**
 * 
 */
package com.hunthawk.reader.page;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;

import com.hunthawk.framework.tapestry.SecurityPage;

/**
 * @author BruceSun
 *
 */
public abstract class Preview extends SecurityPage {

	@Asset("css.css")
	public abstract IAsset getStylesheet();
 
	public abstract void setContent(String content);
	public abstract String getContent();
	
	public abstract void setTitle(String title);
	public abstract String getTitle();

}
