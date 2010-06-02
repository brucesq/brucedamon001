/**
 * 
 */
package com.hunthawk.reader.service.resource;

import java.util.List;

import com.hunthawk.reader.domain.resource.Video;

/**
 * @author sunquanzhi
 *
 */
public interface VideoSearchService {

	public List<Video> search(String partnerId,String q,int pageNo,int pageSize,String sort,boolean isAsc);
	
	public long searchCount(String partnerId,String q);
}
