package com.hunthawk.reader.security;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.framework.security.PermissionCheck;
import com.hunthawk.framework.security.User;
import com.hunthawk.reader.domain.system.Group;
import com.hunthawk.reader.domain.system.UserImpl;

public class PowerUtil {

	/**
	 * 拥有者
	 */
   public static final String OWNER = "o";
   /**
    * 创建者
    */
   public static final String CREATOR = "c";
   /**
    * 用户类型
    */
   public static final String USER = "u";
   /**
    * 群类型
    */
   public static final String GROUP = "g";
 
   /**
    * 修改权限
    */
   public static final String EDIT = "e";
   /**
    * 删除权限
    */
   public static final String DELETE = "d";
   /**
    * 查看权限
    */
   public static final String SEE = "s";

   public static String makePowers(UserImpl creator){
		String users = PowerUtil.OWNER+":"+creator.getId()+";";
		users += PowerUtil.CREATOR+":"+creator.getId()+";";
		
		for(Group group : creator.getGroups())
		{
			users += PowerUtil.GROUP +group.getId() +":@s@e@d;";
		}
		return users;
	}
   	public static void getPowerUsers(Object obj,List<UserPower> userPowers,List<GroupPower> groupPowers,HibernateGenericController controller){
		try{
			String users = (String)BeanUtils.getProperty(obj,"users");
			getPowerUsers(users,userPowers,groupPowers,controller);
		}catch(Exception e){}
	 }
   	public static void getPowerUsers(String users,List<UserPower> userList,List<GroupPower> groupList,HibernateGenericController controller) {
		String[] us = users.split(";");
		if(us == null || us.length < 1)
		{
			return ;
		}
		for(int i=0;i<us.length;i++)
		{
			String u = us[i];
			String[] su = u.split(":");
			try{
				
				Power power = null;
				if(su[0].charAt(0) == 'g')
				{
					Group group = controller.get(Group.class,Integer.parseInt(su[0].substring(1)));
					if(group != null)
					{
						power = new GroupPower();
						((GroupPower)power).setGroup(group);
					}
					
				}else if(su[0].charAt(0) == 'u')
				{
					User user = controller.get(UserImpl.class,Integer.parseInt(su[0].substring(1)));
					if(user != null)
					{
						power = new UserPower();
						((UserPower)power).setUser(user);
					}
					
				}
				if(power != null)
				{
					
					String[] ps = su[1].split("@");
					for(int k=0;k<ps.length;k++)
					{
						String pw = ps[k];
						if(pw.length() == 1)
						{
							power.addPower(pw);	
						}
					}
					if(power instanceof UserPower)
					{
						userList.add((UserPower)power);
					}else
					{
						groupList.add((GroupPower)power);
					}
				}
				
			}catch(Exception e){}
		}
	}
   	
    public static void updatePower(Object obj, List<UserPower> userPowers,List<GroupPower>  groupPowers){
		 try{
			 String users = (String)BeanUtils.getProperty(obj,"users");
			 if(users != null)
			 {
				 int ownerId = getOwnerId(users);
				 int creatorId = getCreatorId(users);
				 String us = updatePower(ownerId,creatorId,userPowers,groupPowers);
				 BeanUtils.setProperty(obj,"users",us);
			 }
			
		 }catch(Exception e){}
		 
	 }
    
    public static int getCreatorId(String users) {
		
		int creatorId = -1;
		String[] us = users.split(";");
		if(us != null && us.length >= 1)
		{
			for(int i=0;i<us.length;i++)
			{
				String u = us[i];
				String[] su = u.split(":");
				try{
					if(PowerUtil.CREATOR.equals(su[0]))
					{
						creatorId = Integer.parseInt(su[1]);
						break;
					}
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return creatorId;
	}
    public static int getOwnerId(String users){
		int ownerId = -1;
		String[] us = users.split(";");
		if(us != null && us.length >= 1)
		{
			for(int i=0;i<us.length;i++)
			{
				String u = us[i];
				String[] su = u.split(":");
				try{
					if(PowerUtil.OWNER.equals(su[0]))
					{
						ownerId = Integer.parseInt(su[1]);
						break;
					}
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return ownerId;
	}
    
    public static String updatePower(int ownerId,int creatorId, List<UserPower> userPowers,List<GroupPower> groupPowers) {
		
		String users = PowerUtil.OWNER+":"+ownerId+";";
		users += PowerUtil.CREATOR+":"+creatorId+";";
		
		for(int i=0;i<userPowers.size();i++)
		{
			UserPower up = userPowers.get(i);
			users += PowerUtil.USER+up.getUser().getId()+":";
			Set set = up.getPowers();
			Iterator iter = set.iterator();
			while(iter.hasNext())
			{
				users += "@"+iter.next();
			}
			users += ";";
		}
		
		for(int i=0;i<groupPowers.size();i++)
		{
			GroupPower gp = groupPowers.get(i);
			users += PowerUtil.GROUP+gp.getGroup().getId()+":";
			Set set = gp.getPowers();
			Iterator iter = set.iterator();
			while(iter.hasNext())
			{
				users += "@"+iter.next();
			}
			users += ";";
		}
		
		return users;
	}
    
    /**
	 * <p>判断用户是否对对象有权限</p>
	 */
	public static boolean hasPower(Object obj, String powerName, UserImpl user){
		if(obj == null)
			return false;
		boolean result = false;
		if(obj instanceof List)
		{//判断是否对列表中的内容都有权限
			List list = (List)obj;
			for(int i=0;i<list.size();i++)
			{
				Object o = (Object)list.get(i);
				result = hasPower(o,powerName,user);
				if(!result)
				{
					break;
				}
			}
			return true;
		}else{
			//判断其中某一个是否有权限
			String users;
			try {
				users = (String)BeanUtils.getProperty(obj,"users");
				
				if(users == null)
				{//不需要权限验证
					result = true;
				}else{
					result = hasPower(users,powerName,user);
				}
			} catch (Exception e){
				
			}
			
		}
		return result;
	}
	
	private static boolean hasPower(String users,String powerName,UserImpl user){
		int ownerId = getOwnerId(users);
		if(user.getId() == ownerId || user.isAdmin())
		{
			return true;
		}
		List userList = new ArrayList();
		List groupList = new ArrayList();
		parserUsers(users,userList,groupList);
		
		for(int i=0;i<userList.size();i++)
		{//判断用户是否有权限
			String up = (String)userList.get(i);
			if(up.startsWith(PowerUtil.USER+user.getId()+":"))
			{
				if(up.indexOf("@"+powerName) > 0)
				{
					return true;
				}
				if(powerName.equals(PowerUtil.OWNER) && user.hasRole("right"))
				{
					return true;
				}
				break;
			}
		}
		String groups = PowerUtil.GROUP;
		for(Group group : user.getGroups())
		{
			groups +=  group.getId() +PowerUtil.GROUP;
		}
		
		for(int i=0;i<groupList.size();i++)
		{//判断用户所在组是否有权限
			String up = (String)groupList.get(i);
			String startUP = up.substring(0,up.indexOf(":"))+PowerUtil.GROUP;
			
			if(groups.indexOf(startUP) >= 0)
			{
				if(up.indexOf("@"+powerName) > 0)
				{
					return true;
				}
				if(powerName.equals(PowerUtil.OWNER) && user.hasRole("right"))
				{
					return true;
				}
			}
			
		}
		return false;
	}
	private static  void parserUsers(String users,List userList,List groupList){
		String[] strs = users.split(";");
		for(int i=0;i<strs.length;i++)
		{
			String s = strs[i];
			if(s.charAt(0) == 'u')
			{
				userList.add(s);
			}else if(s.charAt(0) == 'g')
			{
				groupList.add(s);
			}
		}
	}
	
	public void hasPowerForDrl(String action,PersistentObject obj,PermissionCheck check,UserImpl user){
		
		boolean isTrue = false;
		if(action.equals("edit")){
			isTrue = hasPower(obj,PowerUtil.EDIT,user);
		}else if(action.equals("delete")){
			isTrue =hasPower(obj,PowerUtil.DELETE,user);
		}
		if( isTrue)
			check.grant();
	}
}
