/**
 * 
 */
package com.hunthawk.framework.util;

import java.awt.image.BufferedImage;
import java.io.File;

import com.aspire.wap.core.image.ImageUtils;
import com.aspire.wap.core.image.PicUtil;

/**
 * @author BruceSun
 * 
 */
public class ImageTool {

	private static PicUtil picUtil = new PicUtil(); 

	public static void resizeImage(String srcFile, String destFile, int width)
			throws Exception {
//		int height = getHeight(srcFile, destFile, width);
//		String cmd = "convert " + srcFile + " -resize " + width + "x" 
//				+ " " + destFile;
//		System.out.println("ImageMagic cmd:" + cmd);
//		Runtime.getRuntime().exec(cmd);
		
		picUtil.resizeImage(srcFile, destFile, width);
	}

	private static int getHeight(String srcFile, String destFile, int width)
			throws Exception {
		int index = destFile.lastIndexOf("/");
		String destDir = destFile.substring(0, index);
		String tempDir = destDir + "/temp";
		checkDir(tempDir);
		BufferedImage img = null;
		img = ImageUtils.read(srcFile, tempDir);
		return getHeight(img.getWidth(), img.getHeight(), width);
	}
	
	private static boolean checkDir(String strDir) {
		boolean flag = true;
		try {
			File destDir = new File(strDir);
			if (!destDir.exists()) {
				// System.out.println("文件目录不存在：" + strDir);
				destDir.mkdir();
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}

		return flag;
	}

	private static int getHeight(int width, int height, int currentWidth) {
		int currentHeight = height * currentWidth / width;
		return currentHeight;
	}
}
