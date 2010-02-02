/**
 * 
 */
package com.hunthawk.reader.service.inter;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.inter.Remind;

/**
 * �������ѷ���
 * @author BruceSun
 *
 */
public interface RemindService {

	/**
	 * ������ѷ���
	 * @param remind
	 * @param mobiles �������ֻ�����
	 * @throws Exception
	 */
	@Logable(name = "Remind", action = "add", property = { "id=ID,name=����,status=״̬,sendTime=����ʱ��" })
	public void addRemind(Remind remind,List<String> mobiles)throws Exception;
	/**
	 * �������ѷ���
	 * @param remind
	 * @param type 0 ��ԭ�к������������ 1 ɾ��ԭ�е������º���
	 * @param mobiles �������ֻ�����
	 * @throws Exception
	 */
	@Logable(name = "Remind", action = "update", property = { "id=ID,name=����,status=״̬,sendTime=����ʱ��" })
	public void updateRemind(Remind remind,int type,List<String> mobiles) throws Exception;
	/**
	 * ɾ�����ѷ���
	 * @param remind
	 * @throws Exception
	 */
	@Logable(name = "Remind", action = "delete", property = { "id=ID,name=����,status=״̬,sendTime=����ʱ��" })
	public void deleteRemind(Remind remind) throws Exception;
	
	public int getReservationCount(String contentId);
	
	public int getAllFeeUserCount();
	
	public int getAllReservationCount();
	/**
	 * ��������
	 * @param remind
	 */
	public void sendRemind(Remind remind);
	
	public List<Remind> findReminds(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);
	
	public Long getRemindCount(Collection<HibernateExpression> expressions);
}
