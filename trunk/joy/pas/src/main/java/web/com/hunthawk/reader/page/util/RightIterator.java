/**
 * 
 */
package com.hunthawk.reader.page.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hunthawk.reader.domain.system.Group;
import com.hunthawk.reader.security.GroupPower;
import com.hunthawk.reader.security.PowerUtil;
import com.hunthawk.reader.security.UserPower;

/**
 * @author BruceSun
 *
 */
public class RightIterator implements Serializable
{
	// 组或用户的名称
	private String name;

	// 组或用户的id
	private int id;

	private String seeRight = "否";

	private String editRight = "否";

	private String deleteRight = "否";

	/**
	 * 用组对象来构造对象
	 * 
	 * @param groupPower
	 */
	public RightIterator(GroupPower groupPower)
	{
		setName(groupPower.getGroup().getName());
		setId(groupPower.getGroup().getId());

		// 权限的迭代器
		Iterator it = groupPower.getPowers().iterator();

		while (it.hasNext())
		{
			String rightName = (String) it.next();
			if (rightName.equals(PowerUtil.SEE))
			{
				this.setSeeRight("是");
			}
			else if (rightName.equals(PowerUtil.EDIT))
			{
				this.setEditRight("是");
			}
			else if (rightName.equals(PowerUtil.DELETE))
			{
				this.setDeleteRight("是");
			}

		}
	}

	/**
	 * 用用户对象来构造对象
	 * 
	 * @param userPower
	 */
	public RightIterator(UserPower userPower)
	{
		setName(userPower.getUser().getName());
		setId(userPower.getUser().getId());
		// 权限的迭代器
		Iterator it = userPower.getPowers().iterator();

		while (it.hasNext())
		{
			String rightName = (String) it.next();

			if (rightName.equals(PowerUtil.SEE))
			{
				this.setSeeRight("是");
			}
			else if (rightName.equals(PowerUtil.EDIT))
			{
				this.setEditRight("是");
			}
			else if (rightName.equals(PowerUtil.DELETE))
			{
				this.setDeleteRight("是");
			}
		}
	}

	public String getDeleteRight()
	{
		return deleteRight;
	}

	public void setDeleteRight(String deleteRight)
	{
		this.deleteRight = deleteRight;
	}

	public String getEditRight()
	{
		return editRight;
	}

	public void setEditRight(String editRight)
	{
		this.editRight = editRight;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSeeRight()
	{
		return seeRight;
	}

	public void setSeeRight(String seeRight)
	{
		this.seeRight = seeRight;
	}

	public static void main(String[] args)
	{
		Group group = new Group();
		group.setName("测试组");
		Set aa = new HashSet();

		aa.add(PowerUtil.SEE);

		GroupPower gp = new GroupPower();
		gp.setGroup(group);
		gp.addPower(PowerUtil.SEE);
		RightIterator aa1 = new RightIterator(gp);
//		System.out.println("组名:" + aa1.getName());
//		System.out.println("查看权限:" + aa1.getSeeRight());
//		System.out.println("编辑权限:" + aa1.getEditRight());
//		System.out.println("删除权限:" + aa1.getDeleteRight());

	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

}
