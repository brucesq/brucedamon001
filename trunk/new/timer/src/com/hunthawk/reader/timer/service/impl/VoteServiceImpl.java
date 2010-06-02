package com.hunthawk.reader.timer.service.impl;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.inter.VoteSubItem;
import com.hunthawk.reader.timer.service.VoteService;

public class VoteServiceImpl implements VoteService {

	private HibernateGenericController controller;

	private MemCachedClientWrapper memcached;

	public void setHibernateGenericController(
			HibernateGenericController hibernateGenericController) {
		this.controller = hibernateGenericController;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	private String getVoteKey(int itemId, String resourceId) {
		String key = "r_" + resourceId + "_" + itemId;

		return key;
	}

	public VoteSubItem getVoteSubItemById(String resourceId, int voteTypeId) {
		VoteSubItem vote = null;

		String id = getVoteKey(voteTypeId, resourceId);
		String keyId = Utility.getMemcachedKey(VoteSubItem.class, id);
//		try {
//			vote = (VoteSubItem) memcached.getCounter(keyId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (vote == null) {
			vote = controller.get(VoteSubItem.class, id);
//			memcached.setAndSaveLong(keyId, vote,
//					24 * MemCachedClientWrapper.HOUR);
//		}
        System.out.print("VOTEID:"+id+":");
        if(vote != null ){
        	System.out.println(vote.getVoteValue());
        }else{
        	System.out.println("null");
        }
		return vote;
	}

}
