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
 * 订制提醒服务
 * @author BruceSun
 *
 */
public interface RemindService {

	/**
	 * 添加提醒服务
	 * @param remind
	 * @param mobiles 被提醒手机号码
	 * @throws Exception
	 */
	@Logable(name = "Remind", action = "add", property = { "id=ID,name=名称,status=状态,sendTime=发送时间" })
	public void addRemind(Remind remind,List<String> mobiles)throws Exception;
	/**
	 * 更新提醒服务
	 * @param remind
	 * @param type 0 在原有号码基础上续增 1 删除原有的增加新号码
	 * @param mobiles 被提醒手机号码
	 * @throws Exception
	 */
	@Logable(name = "Remind", action = "update", property = { "id=ID,name=名称,status=状态,sendTime=发送时间" })
	public void updateRemind(Remind remind,int type,List<String> mobiles) throws Exception;
	/**
	 * 删除提醒服务
	 * @param remind
	 * @throws Exception
	 */
	@Logable(name = "Remind", action = "delete", property = { "id=ID,name=名称,status=状态,sendTime=发送时间" })
	public void deleteRemind(Remind remind) throws Exception;
	
	public int getReservationCount(String contentId);
	
	public int getAllFeeUserCount();
	
	public int getAllReservationCount();
	/**
	 * 发送提醒
	 * @param remind
	 */
	public void sendRemind(Remind remind);
	
	public List<Remind> findReminds(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);
	
	public Long getRemindCount(Collection<HibernateExpression> expressions);
}
