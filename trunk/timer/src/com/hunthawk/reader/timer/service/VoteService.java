package com.hunthawk.reader.timer.service;

import com.hunthawk.reader.domain.inter.VoteSubItem;

public interface VoteService {
	public VoteSubItem getVoteSubItemById(String resourceId,int voteTypeId);
}
