/**
 * 
 */
package com.hunthawk.reader.service.resource;

import com.hunthawk.reader.domain.resource.ResourcePackReleation;

/**
 * @author BruceSun
 *
 */
public interface UebService {

	public void createUeb(ResourcePackReleation rel);
	
	public void deleteUeb(ResourcePackReleation rel);
}
