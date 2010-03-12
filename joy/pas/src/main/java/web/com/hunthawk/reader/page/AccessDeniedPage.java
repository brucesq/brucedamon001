package com.hunthawk.reader.page;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.html.BasePage;

public abstract class AccessDeniedPage extends BasePage {

	private String errors = "您没有权限查看相关内容!";
	
	@Asset("img/iconWarning.gif")
	public abstract IAsset getWarnIcon();
	
	public abstract boolean isPopWindow();
	public abstract void setPopWindow(boolean pop);
	public String getErrors()
	{
		return errors;
	}

	public void setErrors(String error)
	{
		this.errors = error;
	}
	
	public abstract String getVindicateErrors();
	public abstract void setVindicateErrors(String message);

}

