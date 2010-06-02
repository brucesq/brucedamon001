/**
 * 
 */
package com.hunthawk.tag.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sunquanzhi
 *
 */
public class ResourceConfigUtils {

	public static File[] getResource(String resourceLocation,String pattern)
	{
		File root = new File(resourceLocation);
		pattern = pattern.replaceAll("\\*",".*");
		
		List<File> results = new ArrayList<File>();
		for(File file : root.listFiles())
		{
			if(file.getName().matches(pattern))
				results.add(file);
		}
		
		return results.toArray(new File[results.size()]);
	}
	
}
