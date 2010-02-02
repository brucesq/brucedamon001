/**
 * 
 */
package com.hunthawk.reader.pps;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/**
 * @author BruceSun
 * 
 */
public class StringResourceLoader extends ResourceLoader {

	public void init(ExtendedProperties configuration) {

	}

	/**
	 * @param templateString
	 *            模版字符串
	 * @return 返回模版数据流
	 */
	public synchronized InputStream getResourceStream(String templateString)
			throws ResourceNotFoundException {

		InputStream result = null;

		if (templateString == null || templateString.length() == 0) {
			throw new ResourceNotFoundException("No template string provided");
		}
		result = new ByteArrayInputStream(templateString.getBytes());
		return result;
	}

	/**
	 * <p>
	 * 模版信息是否已经更改
	 * </p>
	 * 
	 * @param resource
	 *            模版资源
	 */
	public boolean isSourceModified(Resource resource) {

		return false;

	}

	/**
	 * <p>
	 * 模版最后更改时间
	 * </p>
	 * 
	 * @param resource
	 *            模版资源
	 */
	public long getLastModified(Resource resource) {

		return 0;

	}
}
