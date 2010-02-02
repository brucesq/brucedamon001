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
	// ����û�������
	private String name;

	// ����û���id
	private int id;

	private String seeRight = "��";

	private String editRight = "��";

	private String deleteRight = "��";

	/**
	 * ����������������
	 * 
	 * @param groupPower
	 */
	public RightIterator(GroupPower groupPower)
	{
		setName(groupPower.getGroup().getName());
		setId(groupPower.getGroup().getId());

		// Ȩ�޵ĵ�����
		Iterator it = groupPower.getPowers().iterator();

		while (it.hasNext())
		{
			String rightName = (String) it.next();
			if (rightName.equals(PowerUtil.SEE))
			{
				this.setSeeRight("��");
			}
			else if (rightName.equals(PowerUtil.EDIT))
			{
				this.setEditRight("��");
			}
			else if (rightName.equals(PowerUtil.DELETE))
			{
				this.setDeleteRight("��");
			}

		}
	}

	/**
	 * ���û��������������
	 * 
	 * @param userPower
	 */
	public RightIterator(UserPower userPower)
	{
		setName(userPower.getUser().getName());
		setId(userPower.getUser().getId());
		// Ȩ�޵ĵ�����
		Iterator it = userPower.getPowers().iterator();

		while (it.hasNext())
		{
			String rightName = (String) it.next();

			if (rightName.equals(PowerUtil.SEE))
			{
				this.setSeeRight("��");
			}
			else if (rightName.equals(PowerUtil.EDIT))
			{
				this.setEditRight("��");
			}
			else if (rightName.equals(PowerUtil.DELETE))
			{
				this.setDeleteRight("��");
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
		group.setName("������");
		Set aa = new HashSet();

		aa.add(PowerUtil.SEE);

		GroupPower gp = new GroupPower();
		gp.setGroup(group);
		gp.addPower(PowerUtil.SEE);
		RightIterator aa1 = new RightIterator(gp);
//		System.out.println("����:" + aa1.getName());
//		System.out.println("�鿴Ȩ��:" + aa1.getSeeRight());
//		System.out.println("�༭Ȩ��:" + aa1.getEditRight());
//		System.out.println("ɾ��Ȩ��:" + aa1.getDeleteRight());

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
