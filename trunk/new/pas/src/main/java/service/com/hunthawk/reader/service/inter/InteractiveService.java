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
 * 交互类服务
 * @author BruceSun
 *
 */
public interface InteractiveService {

	@Logable(name = "MsgBoard", action = "add", property = { "id=ID,name=名称,status=状态,auditing=审核方式,createTime=创建时间,updateTime=修改时间,creator=创建人,modifier=修改人" })
	public void addMsgBoard(MsgBoard board)throws Exception;
	
	@Logable(name = "MsgBoard", action = "add", property = { "id=ID,name=名称,status=状态,auditing=审核方式,createTime=创建时间,updateTime=修改时间,creator=创建人,modifier=修改人" })
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
	 * 投票管理
	 */
	@Logable(name = "VoteAct", action = "add", property = { "id=ID,name=投票名称,createTime=创建时间,creator=创建人" })
	public void addVoteAct(VoteAct voteAct) throws Exception;
	
	@Logable(name = "VoteAct", action = "update", property = { "id=ID,name=投票名称,createTime=创建时间,creator=创建人" })	
	public void updateVoteAct(VoteAct voteAct) throws Exception;
	
	@Logable(name = "VoteAct", action = "delete", property = { "id=ID,name=投票名称,createTime=创建时间,creator=创建人" })	
	public void deleteVoteAct(VoteAct voteAct) throws Exception;
	
	public Long getVoteActList(Collection<HibernateExpression> expressions);
	
	public VoteAct getVoteAct(Integer id);
	
	public List<VoteAct> findVoteActs(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);
	
	public List<VoteAct> getVoteActList();
	/**
	 * 投票项管理
	 * 暂时没有记录日志
	 */
	@Logable(name = "VoteItem", action = "add", property = { "id=ID,name=投票栏目名称,voteNumber=投票序号,voteId=所属投票ID,createTime=创建时间,creator=创建人" })
	@Memcached(targetClass = VoteItem.class, properties = { "voteId" })
	public void addVoteItem(VoteItem voteItem) throws Exception;
	
	@Logable(name = "VoteItem", action = "update", property = { "id=ID,name=投票栏目名称,voteNumber=投票序号,voteId=所属投票ID,createTime=创建时间,creator=创建人" })
	@Memcached(targetClass = VoteItem.class, properties = { "voteId" })
	public void updateVoteItem(VoteItem voteItem) throws Exception;
	
	@Logable(name = "VoteItem", action = "delete", property = { "id=ID,name=投票栏目名称,voteNumber=投票序号,voteId=所属投票ID,createTime=创建时间,creator=创建人" })
	@Memcached(targetClass = VoteItem.class, properties = { "voteId" })
	public void deleteVoteItem(VoteItem voteItem) throws Exception;
	
	public Long getVoteItemList(Collection<HibernateExpression> expressions);
	
	public VoteItem getVoteItem(Integer id);
	
	public List<VoteItem> findVoteItems(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions);
	
	public List<VoteItem> findVoteItemsByVote(Integer voteId);
	
	/***
	 * 处理离线上传 记录日志信息
	 */
	
	public void addLog(OffLineLog log);
	
	public void deleteLog(Integer mark);
	
	public List<OffLineLog> findOffLineLogList(Integer markId);
	
}
