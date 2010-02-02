/**
 * 
 */
package com.hunthawk.reader.page.guide;

import java.io.Serializable;

import org.apache.tapestry.engine.IEngineService;

import com.hunthawk.reader.domain.bussiness.SysTag;
import com.hunthawk.reader.domain.bussiness.UserDefTag;
import com.hunthawk.reader.page.util.PageHelper;

/**
 * @author BruceSun
 *
 */
public class TagGuide implements Serializable{

	public static String Dialog = "Dialog";
	
	public static String Static = "Static";
	
	public static String UserDefTagName = "userdeftag";
	
	private String title;
    
    private String name;
	
	private String process;
    
    public TagGuide(SysTag sysTag,String contextPath)
    {
        title = sysTag.getTitle();
        
        name = sysTag.getName();
        
        if(sysTag.getType().equals(Dialog))
        {
            Object[] params = new Object[3];
            
            //params[0] = sysTag.getWizardParas();
            
            
            //自己拼参数
            params[0] = sysTag.getId();
            params[1] = sysTag.getName();
            params[2] = System.currentTimeMillis();
            process = contextPath + "guide/CommonGuide.external?sp=" + sysTag.getId() + "&sp=" + sysTag.getName() + "&sp=" + System.currentTimeMillis();
            //process = PageHelper.getExternalFunction(service,
                //  sysTag.getWizardPage()+"Rtf",params );
        }else if(sysTag.getType().equals(Static))
        {
            process = "AddText(document,'$#"+sysTag.getName()+"#')";
        }else{
            process = "AddText(document,'$#"+sysTag.getName()+"#')";
        }
    }
	
	
	public TagGuide(SysTag sysTag,IEngineService service)
	{
		title = sysTag.getTitle();
        name = sysTag.getName();
		
		if(sysTag.getType().equals(Dialog))
		{
			Object[] params = new Object[3];
			params[0] = sysTag.getWizardParas();
			params[1] = sysTag.getName();
			params[2] = System.currentTimeMillis();
			process = PageHelper.getExternalFunction(service,
					sysTag.getWizardPage(),params );
		}else if(sysTag.getType().equals(Static))
		{
			process = "AddText(document,'$#"+sysTag.getName()+"#')";
		}else{
			process = "AddText(document,'$#"+sysTag.getName()+"#')";
		}
	}

	public TagGuide(UserDefTag tag)
	{
		name = tag.getName();
        title = tag.getTitle();
		process = "AddText(document,'$#"+UserDefTagName+".id="+tag.getId()+"#')";
	}
	
	public String getProcess()
	{
		return process;
	}
	public String getTitle()
	{
		return title;
	}
    public String getName()
    {
        return name;
    }
}