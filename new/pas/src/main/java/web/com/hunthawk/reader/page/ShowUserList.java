package com.hunthawk.reader.page;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.security.Visit;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.UserImpl;

public abstract class ShowUserList extends SecurityPage{

    @InjectObject("service:tapestry.globals.HttpServletRequest")
    public abstract HttpServletRequest getServletRequest();
    
    public abstract UserImpl getUserImpl();
    public abstract void setUserImpl(UserImpl userImpl);
    
	public List<UserImpl> getShowList(){
		List<UserImpl> userList = new ArrayList<UserImpl>();
		HttpSession session = getServletRequest().getSession();
		ServletContext application = session.getServletContext(); 
		Vector activeSessions = (Vector) application.getAttribute("activeSessions"); 
		if (activeSessions == null) { activeSessions = new Vector(); 
	 	  application.setAttribute("activeSessions",activeSessions); 
	  	 } 
	  	 Iterator it = activeSessions.iterator();
	  	 while (it.hasNext()) 
	  	 { 
		   HttpSession sess = (HttpSession)it.next();
		   UserImpl user = (UserImpl)sess.getAttribute("state:reader:userImpl");
		   userList.add(user);
		   }
	  	 return userList;
	}
	
	@Asset("img/Toolbar_bg.png")
	public abstract IAsset getBackGroundIcon();
}
