/**
 * 
 */
package com.hunthawk.reader.service.inter.impl;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.OffLineLog;
import com.hunthawk.reader.domain.inter.MsgBoard;
import com.hunthawk.reader.domain.inter.MsgOperateLog;
import com.hunthawk.reader.domain.inter.MsgRecord;
import com.hunthawk.reader.domain.inter.VoteAct;
import com.hunthawk.reader.domain.inter.VoteItem;
import com.hunthawk.reader.service.inter.InteractiveService;

/**
 * @author BruceSun
 * 
 */
public class InteractiveServiceImpl implements InteractiveService {

	private HibernateGenericController controller;

	private MemCachedClientWrapper memcached;

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void addMsgBoard(MsgBoard board) throws Exception {
		controller.save(board);

		if (board.getStatus().equals(1)) {
			// 清空留言板对象缓存
			String key = Utility.getMemcachedKey(MsgBoard.class, String
					.valueOf(board.getId()));
			memcached.delete(key);
		}
	}

	public MsgBoard getMsgBoard(Integer boardId) {
		return controller.get(MsgBoard.class, boardId);
	}

	public MsgRecord getMsgRecord(Integer recordId) {
		return controller.get(MsgRecord.class, recordId);
	}

	public List<MsgBoard> getMsgBoards() {
		return controller.getAll(MsgBoard.class);
	}

	public void updateMsgBoard(MsgBoard board) throws Exception {
		controller.update(board);
		// 清空留言板对象缓存
		String key = Utility.getMemcachedKey(MsgBoard.class, String
				.valueOf(board.getId()));
		memcached.delete(key);
	}

	public List<MsgBoard> findMsgBoards(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(MsgBoard.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public Long getMsgBoardCount(Collection<HibernateExpression> expressions) {

		long count = controller.getResultCount(MsgBoard.class, expressions);

		return count;
	}

	public List<MsgRecord> findMsgRecords(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		controller.setCacheQueries(false);// 关闭缓存
		List list = controller.findBy(MsgRecord.class, pageNo, pageSize,
				orderBy, isAsc, expressions);
		controller.setCacheQueries(true); // 打开缓存
		return list;
	}

	public Long getMsgRecordCount(Collection<HibernateExpression> expressions) {
		controller.setCacheQueries(false); // 关闭缓存
		long count = controller.getResultCount(MsgRecord.class, expressions);
		controller.setCacheQueries(true); // 打开缓存
		return count;
	}

	public void auditMsgRecord(MsgRecord record, Integer status) {
		record.setStatus(status);
		controller.update(record);
		// 清空留言缓存
		clearMsgCache(record);

	}

	public void addMsgOperateLog(MsgOperateLog log) {
		controller.save(log);
	}

	/**
	 * 清空留言缓存
	 * 
	 * @param record
	 */
	private void clearMsgCache(MsgRecord record) {
		String key = Utility.getMemcachedKey(MsgRecord.class, String
				.valueOf(record.getBoardId()));
		key += Constants.MEMCACHED_SLASH;

		switch (record.getMsgType()) {
		case MsgRecord.TYPE_CUSTOM:
			key += "custom" + record.getCustomId();
			break;
		case MsgRecord.TYPE_PRODUCT:
			key += "product" + record.getProductId();
			break;
		case MsgRecord.TYPE_COLUMN:
			key += "column" + record.getColumnId();
			break;
		case MsgRecord.TYPE_CONTENT:
			key += "content" + record.getContentId();
			break;
		}

		String countKey = key + "count";
		memcached.deleteForLocal(countKey);
		Integer[] sizes = { 3, 10, 20 };
		Integer[] pageNos = { 1, 2 };
		for (Integer size : sizes) {
			for (Integer pageNo : pageNos) {
				String tmpKey = key + "no" + pageNo + "size" + size;
				memcached.deleteForLocal(tmpKey);
			}
		}
	}

	public void addVoteAct(VoteAct voteAct) throws Exception {
		if (controller.isUnique(VoteAct.class, voteAct, "name"))
			controller.save(voteAct);
		else
			throw new Exception("此投票已经存在！");
	}

	public void addVoteItem(VoteItem voteItem) throws Exception {
		if (controller.isUnique(VoteItem.class, voteItem, "name"))
			controller.save(voteItem);
		else
			throw new Exception("此投票选项已经存在！");
	}

	public void deleteVoteAct(VoteAct voteAct) throws Exception {
		controller.delete(voteAct);
	}

	public void deleteVoteItem(VoteItem voteItem) throws Exception {
		controller.delete(voteItem);

	}

	public List<VoteAct> findVoteActs(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		return controller.findBy(VoteAct.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public List<VoteItem> findVoteItems(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(VoteItem.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public VoteAct getVoteAct(Integer id) {
		return controller.get(VoteAct.class, id);
	}

	public Long getVoteActList(Collection<HibernateExpression> expressions) {

		return controller.getResultCount(VoteAct.class, expressions);
	}

	public VoteItem getVoteItem(Integer id) {
		return controller.get(VoteItem.class, id);
	}

	public Long getVoteItemList(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(VoteItem.class, expressions);
	}

	public void updateVoteAct(VoteAct voteAct) throws Exception {
		controller.update(voteAct);
	}

	public void updateVoteItem(VoteItem voteItem) throws Exception {
		controller.update(voteItem);
	}

	public List<VoteAct> getVoteActList() {
		return controller.getAll(VoteAct.class);
	}

	public List<VoteItem> findVoteItemsByVote(Integer voteId) {
		return controller.findBy(VoteItem.class, "voteId", voteId, "id", true);
	}

	public void addLog(OffLineLog log) {
		controller.save(log);
	}

	public List<OffLineLog> findOffLineLogList(Integer markId) {

		return controller.findBy(OffLineLog.class, "mark", markId, "id", true);
	}
}
