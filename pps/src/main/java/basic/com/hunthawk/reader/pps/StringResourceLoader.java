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
	 *            ģ���ַ���
	 * @return ����ģ��������
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
	 * ģ����Ϣ�Ƿ��Ѿ�����
	 * </p>
	 * 
	 * @param resource
	 *            ģ����Դ
	 */
	public boolean isSourceModified(Resource resource) {

		return false;

	}

	/**
	 * <p>
	 * ģ��������ʱ��
	 * </p>
	 * 
	 * @param resource
	 *            ģ����Դ
	 */
	public long getLastModified(Resource resource) {

		return 0;

	}
}
