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
	 * ��������
	 * 
	 * @param msgType
	 *            �������� �ο�MsgRecord�����
	 * @param boardId
	 *            ���ID
	 * @param mobile
	 * @param content
	 *            ��������
	 * @param parentId
	 *            ������ID
	 * @param contentId
	 *            ��Դ����ID
	 * @param columnId
	 *            ��ĿID
	 * @param productId
	 *            ��ƷID
	 * @param customId
	 *            �Զ���ID
	 * @param customName
	 *            �Զ�������
	 * @return 1�ɹ���2�ȴ���ˡ�3���������֡�4���԰�첻���ڻ��������ߡ�5��������Ϊ��
	 */
	public Integer addMsgRecord(int msgType, int boardId, String mobile,
			String content, Integer parentId, String contentId,
			Integer columnId, String productId, String customId,
			String customName);

	/**
	 * ��ȡ��������
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
	 * ��ȡ�����б�
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
	 * ���ͶƱ��
	 * @param voteId
	 * @return
	 */
	public List<VoteItem> getVoteItems(int voteId);
	/**
	 * ���ѡ��ͶƱ��
	 * @param voteType ͶƱ���Ͳμ�VoteSubItem
	 * @param itemId ѡ��ID
	 * @param contentId ����ID
	 * @param columnId ��ĿID
	 * @param productId ��ƷID
	 * @param customId �Զ���ID
	 * @return
	 */
	public Long getVoteItemCounts(int voteType,int itemId, 
			String contentId, Integer columnId, String productId,
			String customId); 
	/**
	 * ����ͶƱ��������ͶƱ�����׳��쳣
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
	 * ��ȡ����ͶƱKEY
	 * @return
	 */
	public Map<String, Object[]> getAllVoteKey() ;
	
	/**
	 * �������� �� ʱ����� ��ѯ�ȴ��б�
	 * @param showType
	 * @param timeType
	 * @param pageSize
	 * @return
	 */
    public List<StatData> getStatDataList(int showType,int timeType,int totalCount);
    
	public int getStatDataListCount(int showType,int timeType);
}
