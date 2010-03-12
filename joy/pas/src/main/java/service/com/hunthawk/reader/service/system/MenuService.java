/**
 * 
 */
package com.hunthawk.reader.service.system;

import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.security.User;
import com.hunthawk.reader.domain.system.Menu;

/**
 * @author BruceSun
 *
 */
public interface MenuService {

	public List<Menu> getMenu(User user);  
	
	@Logable(name = "Menu", action = "add", property = { "id=ID,title=�˵�����,parent=���˵�,url=���ӵ�ַ,parameter=����,powers=Ȩ�޼���" })	
	public void add(Menu menu);
	
	@Logable(name = "Menu", action = "update", property = { "id=ID,title=�˵�����,parent=���˵�,url=���ӵ�ַ,parameter=����,powers=Ȩ�޼���" })	
	public void update(Menu menu);
	
	public List<Menu> getAllMenu();
	
	@Logable(name = "Menu", action = "delete", property = { "id=ID,title=�˵�����,parent=���˵�,url=���ӵ�ַ,parameter=����,powers=Ȩ�޼���" })	
	public void delete(Menu menu);
	
	public Menu getMenu(int menuId);
}
