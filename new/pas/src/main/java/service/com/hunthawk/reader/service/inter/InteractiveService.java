/**
 * 
 */
package com.hunthawk.reader.service.inter;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.OffLineLog;
import com.hunthawk.reader.domain.inter.MsgBoard;
import com.hunthawk.reader.domain.inter.MsgOperateLog;
import com.hunthawk.reader.domain.inter.MsgRecord;
import com.hunthawk.reader.domain.inter.VoteAct;
import com.hunthawk.reader.domain.inter.VoteItem;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.enhance.annotation.Memcached;

/**
 * ���������
 * @author BruceSun
 *
 */
public interface InteractiveService {

	@Logable(name = "MsgBoard", action = "add", property = { "id=ID,name=����,status=״̬,auditing=��˷�ʽ,createTime=����ʱ��,updateTime=�޸�ʱ��,creator=������,modifier=�޸���" })
	public void addMsgBoard(MsgBoard board)throws Exception;
	
	@Logable(name = "MsgBoard", action = "add", property = { "id=ID,name=����,status=״̬,auditing=��˷�ʽ,createTime=����ʱ��,updateTime=�޸�ʱ��,creator=������,modifier=�޸���" })
	public void updateMsgBoard(MsgBoard board)throws Exception;
	
	public List<MsgBoard> getMsgBoards();
	
	public MsgBoard getMsgBoard(Integer boardId);
	
	public Long getMsgBoardCount(Collection<HibernateExpression> expressions);
	
	public List<MsgBoard> findMsgBoards(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);
	
	public List<MsgRecord> findMsgRecords(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);
	
	public Long getMsgRecordCount(Collection<HibernateExpression> expressions);
	
	public MsgRecord getMsgRecord(Integer recordId);
	
	public void auditMsgRecord(MsgRecord record,Integer status);
	
	public void addMsgOperateLog(MsgOperateLog log);
	
	/**
	 * ͶƱ����
	 */
	@Logable(name = "VoteAct", action = "add", property = { "id=ID,name=ͶƱ����,createTime=����ʱ��,creator=������" })
	public void addVoteAct(VoteAct voteAct) throws Exception;
	
	@Logable(name = "VoteAct", action = "update", property = { "id=ID,name=ͶƱ����,createTime=����ʱ��,creator=������" })	
	public void updateVoteAct(VoteAct voteAct) throws Exception;
	
	@Logable(name = "VoteAct", action = "delete", property = { "id=ID,name=ͶƱ����,createTime=����ʱ��,creator=������" })	
	public void deleteVoteAct(VoteAct voteAct) throws Exception;
	
	public Long getVoteActList(Collection<HibernateExpression> expressions);
	
	public VoteAct getVoteAct(Integer id);
	
	public List<VoteAct> findVoteActs(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);
	
	public List<VoteAct> getVoteActList();
	/**
	 * ͶƱ�����
	 * ��ʱû�м�¼��־
	 */
	@Logable(name = "VoteItem", action = "add", property = { "id=ID,name=ͶƱ��Ŀ����,voteNumber=ͶƱ���,voteId=����ͶƱID,createTime=����ʱ��,creator=������" })
	@Memcached(targetClass = VoteItem.class, properties = { "voteId" })
	public void addVoteItem(VoteItem voteItem) throws Exception;
	
	@Logable(name = "VoteItem", action = "update", property = { "id=ID,name=ͶƱ��Ŀ����,voteNumber=ͶƱ���,voteId=����ͶƱID,createTime=����ʱ��,creator=������" })
	@Memcached(targetClass = VoteItem.class, properties = { "voteId" })
	public void updateVoteItem(VoteItem voteItem) throws Exception;
	
	@Logable(name = "VoteItem", action = "delete", property = { "id=ID,name=ͶƱ��Ŀ����,voteNumber=ͶƱ���,voteId=����ͶƱID,createTime=����ʱ��,creator=������" })
	@Memcached(targetClass = VoteItem.class, properties = { "voteId" })
	public void deleteVoteItem(VoteItem voteItem) throws Exception;
	
	public Long getVoteItemList(Collection<HibernateExpression> expressions);
	
	public VoteItem getVoteItem(Integer id);
	
	public List<VoteItem> findVoteItems(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);
	
	public List<VoteItem> findVoteItemsByVote(Integer voteId);
	
	/***
	 * ���������ϴ� ��¼��־��Ϣ
	 */
	
	public void addLog(OffLineLog log);
	
	public void deleteLog(Integer mark);
	
	public List<OffLineLog> findOffLineLogList(Integer markId);
	
}
