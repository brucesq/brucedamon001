/**
 * 
 */
package com.hunthawk.reader.pps.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.inter.MsgRecord;
import com.hunthawk.reader.domain.inter.VoteItem;
import com.hunthawk.reader.domain.statistics.StatData;

/**
 * @author BruceSun
 * 
 */
public interface InteractiveService {

	/**
	 * 发表留言
	 * 
	 * @param msgType
	 *            留言类型 参考MsgRecord定义的
	 * @param boardId
	 *            板块ID
	 * @param mobile
	 * @param content
	 *            留言内容
	 * @param parentId
	 *            父留言ID
	 * @param contentId
	 *            资源内容ID
	 * @param columnId
	 *            栏目ID
	 * @param productId
	 *            产品ID
	 * @param customId
	 *            自定义ID
	 * @param customName
	 *            自定义名称
	 * @return 1成功。2等待审核。3含有敏感字。4留言板快不存在或者已下线。5留言内容为空
	 */
	public Integer addMsgRecord(int msgType, int boardId, String mobile,
			String content, Integer parentId, String contentId,
			Integer columnId, String productId, String customId,
			String customName);

	/**
	 * 获取留言总数
	 * 
	 * @param msgType
	 * @param boardId
	 * @param parentId
	 * @param contentId
	 * @param columnId
	 * @param productId
	 * @param customId
	 * @return
	 */
	public Integer getMsgRecordCount(int msgType, int boardId,
			String contentId, Integer columnId, String productId,
			String customId);

	/**
	 * 获取留言列表
	 * 
	 * @param msgType
	 * @param boardId
	 * @param parentId
	 * @param contentId
	 * @param columnId
	 * @param productId
	 * @param customId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<MsgRecord> getMsgRecordList(int msgType, int boardId,
			String contentId, Integer columnId, String productId,
			String customId, int pageNo, int pageSize);
	/**
	 * 获得投票项
	 * @param voteId
	 * @return
	 */
	public List<VoteItem> getVoteItems(int voteId);
	/**
	 * 获得选项投票数
	 * @param voteType 投票类型参见VoteSubItem
	 * @param itemId 选项ID
	 * @param contentId 内容ID
	 * @param columnId 栏目ID
	 * @param productId 产品ID
	 * @param customId 自定义ID
	 * @return
	 */
	public Long getVoteItemCounts(int voteType,int itemId, 
			String contentId, Integer columnId, String productId,
			String customId); 
	/**
	 * 新增投票，不符合投票规则抛出异常
	 * @param voteType
	 * @param itemId
	 * @param contentId
	 * @param columnId
	 * @param productId
	 * @param customId
	 * @param isVote
	 * @param voteMaxnNumber
	 * @return
	 * @throws Exception
	 */
	public Long vote(int voteType,int itemId, 
			String contentId, Integer columnId, String productId,
			String customId,String mobile,Integer isVote,Integer voteMaxNumber)throws Exception;
	
	/**
	 * 获取所有投票KEY
	 * @return
	 */
	public Map<String, Object[]> getAllVoteKey() ;
	
	/**
	 * 根据类型 和 时间规则 查询热词列表
	 * @param showType
	 * @param timeType
	 * @param pageSize
	 * @return
	 */
    public List<StatData> getStatDataList(int showType,int timeType,int totalCount);
    
	public int getStatDataListCount(int showType,int timeType);
}
