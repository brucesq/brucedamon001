/**
 * 
 */
package com.hunthawk.reader.page;

import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.engine.IEngineService;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.reader.domain.system.Menu;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.system.MenuService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles={"basic"},mode=Restrict.Mode.ROLE)
public abstract class Home extends SecurityPage{
	
	@InjectObject("spring:menuService")
	public abstract MenuService getMenuService();

	public String getUserName()
	{
		return getVisit().getUser().getName();
	}
	
	@SuppressWarnings("unchecked")
	public List getMenuList()
	{

		List<Menu> menuList = getMenuService().getMenu(getUser());

		for (int i = 0; i < menuList.size(); i++)
		{
			List<Menu> childList = menuList.get(i).getChilds();

			for (int j = 0; j < childList.size(); j++)
			{
				String aa = childList.get(j).getUrl();
				String parameter = childList.get(j).getParameter();
				if(parameter != null && parameter.length() > 6)
				{
					aa = getMenuURLPara(aa, childList.get(j).getId());
				}else{
					aa = getMenuURL(aa);
				}
				

				childList.get(j).setUrl(aa);
			}

		}

		return menuList;
	}
	@InjectObject("engine-service:page")
	public abstract IEngineService getPageService();

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();
	
	public String getMenuURL(String pageName)
	{
		IEngineService service = getPageService();
		String templateURL = PageHelper.getPageURL(service, pageName);

		return templateURL;
	}
	
	public String getMenuURLPara(String pageName,int menuid)
	{
		IEngineService service = getExternalService();
		Object[] params = new Object[1];
		params[0] = menuid;
		String templateURL = PageHelper.getExternalURL(service, pageName, params);

		return templateURL;
	}
	  public String getOperaLink()
      {
          IEngineService service = getExternalService();
          String resourceBagURL = PageHelper.getExternalURL(service,
                  "ProductPage", null);

          String musciBagLink = resourceBagURL.replaceAll(
                  "ProductPage.external", "opera_pams.zip");

          return musciBagLink;

      }
	  
	public String getPersonURL(){
			IEngineService service = getPageService();
			String templateURL = PageHelper
					.getPersonFunction(service, "PersonPage");

			return templateURL;
	}
	
	@InjectPage("PersonPage")
	public abstract PersonPage getPersonPage();
	
	public IPage modifyPerson(){
		UserImpl user = (UserImpl)getUser();
		getPersonPage().setModel(user);
		return getPersonPage();
	}
}
