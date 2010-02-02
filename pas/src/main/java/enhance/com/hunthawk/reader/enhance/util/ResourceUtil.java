package com.hunthawk.reader.enhance.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResourceUtil {
	// 文件同步类型:增加
	public static final String RSYNC_TYPE_ADD = "ADD";

	// 文件同步类型:删除
	public static final String RSYNC_TYPE_DEL = "DEL";

	// 缓冲大小，单位：字节
	private static final int BUFFER_SIZE = 4096;

	// 超时时间，单位：秒
	private static final int TIMEOUT_DEFAULT = 300;

	// 小图标文件夹名称
	public static final String SMALL_IMAGE = "small_image";

	// 大图标文件夹名称
	public static final String LARGE_IMAGE = "large_image";

	// 截图文件夹名称
	public static final String CAPTURE_IMAGE = "capture_image";

	// 应用程序文件夹名称
	public static final String PROGRAME = "program";

	private static final Log log = LogFactory.getLog(ResourceUtil.class);

	/**
	 * 通过java的反射机制，设置字段的值
	 */
	public static void setFieldName(Object object, String fieldName,
			Object fieldValue, Collection resourceMessageCol) {

		try {
			if (PropertyUtils.getPropertyType(object, fieldName) == Integer.TYPE) {
				PropertyUtils.setProperty(object, fieldName, Integer
						.parseInt(fieldValue.toString()));

			} else {
				PropertyUtils.setProperty(object, fieldName, fieldValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 在整个类的层次中寻找Field，若此类没有，则依次查找它的父类中是否有 若都没有，则抛出异常
	 */
	private static Field getField(Class c, String fieldName) throws Exception {
		Field f = null;
		try {
			f = c.getDeclaredField(fieldName);
		} catch (Exception e) {
			if (c.getSuperclass() != null)
				f = getField(c.getSuperclass(), fieldName);
			else {
				throw e;
			}

		}

		return f;
	}

	// //////同步文件相关函数begin

	/**
	 * 执行外部命令行程序
	 */
	public static void executeCommand(String command) {

		ByteArrayOutputStream errorStream = new ByteArrayOutputStream(
				BUFFER_SIZE / 4);

		CommandRunner cr = new CommandRunner();

		try {

			cr.setCommand(command);
			cr.setInputStream(null);
			cr.setStdOutputStream(null);
			cr.setStdErrorStream(errorStream);

			cr.setTimeout(TIMEOUT_DEFAULT);

			cr.evaluate();

			if (cr.getExitValue() != 0) {
				log.error("executeCommand [" + command + "] have error: "
						+ errorStream.toString());
			}

		} catch (IOException e) {
			log.error(e);
		}

	}

	/**
	 * 产生rsync所需同步的脚本文件
	 * 
	 * @param outputFile
	 *            生成的脚本文件名
	 * @param serverName
	 *            远程文件服务器域名
	 * @param localRoot
	 *            本地文件存放根路径
	 * @param rsyncIp
	 *            远程文件服务器对应IP列表,由SS部门分配
	 * @param rsyncModule
	 *            rsync模块名,由SS部门分配
	 * @param rsyncType
	 *            "ADD"—增加文件；"DEL"—删除文件
	 * @param fileNames
	 *            要同步的文件名列表（相对路径）
	 */
	public static void generateRsyncFile(File outputFile, String serverName,
			String localRoot, String rsyncIp, String rsyncModule,
			String rsyncType, String[] fileNames) throws IOException {
		StringBuffer stringBuffer = new StringBuffer().append(
				"SERVERNAME=\"" + serverName + "\"" + "\n").append(
				"LOCALROOT=\"" + localRoot + "\"" + "\n").append(
				"RSYNC_IP=\"" + rsyncIp + "\"" + "\n").append(
				"RSYNC_MOD=\"" + rsyncModule + "\"" + "\n").append(
				"RSYNC_TYPE=\"" + rsyncType + "\"" + "\n");

		for (int i = 0; i < fileNames.length; i++) {
			if (fileNames[i] != null)
				stringBuffer.append(fileNames[i] + "\n");
		}

		FileUtils.writeStringToFile(outputFile, stringBuffer.toString(), "GBK");
	}

	/**
	 * 根据产生的rsync脚本文件，同步相应的文件
	 */
	public static void rsyncFile(String command, File outputFile,
			String serverName, String localRoot, String rsyncIp,
			String rsyncModule, String rsyncType, String[] fileNames) {

		// try
		// {
		// //产生脚本文件
		// generateRsyncFile(outputFile, serverName, localRoot, rsyncIp,
		// rsyncModule, rsyncType, fileNames);
		//
		// //执行外部命令
		// command = command + " " + outputFile.getCanonicalPath();
		// executeCommand(command);
		//
		// //清除临时脚本文件
		// //此处先不删除,由外部命令删除
		// //FileUtils.forceDelete(outputFile);
		// }
		// catch (IOException e)
		// {
		// log.error(e);
		// }
		SyncThread thread = new SyncThread(command, outputFile, serverName,
				localRoot, rsyncIp, rsyncModule, rsyncType, fileNames);

		thread.start();
	}

	/**
	 * 搜索目录下满足条件的文件
	 * 
	 * @author xiajianci Dec 25, 2007 10:52:54 AM
	 * @version 1.0
	 * @param filePath
	 *            文件目录
	 * @param fileName
	 *            搜索的文件名称
	 * @return 搜索的文件名称开头的文件数组
	 */
	public static File[] searchFile(File filePath, String fileName) {
		File[] dirs = filePath.listFiles((FileFilter) FileFilterUtils
				.prefixFileFilter(fileName));
		return dirs;
	}

	// //////同步文件相关函数end

	public static void main(String[] args) {

		// Resource resource = new Music();
		// setFieldName(resource, "name", "testname", new ArrayList());
		// setFieldName(resource, "word", "testword", new ArrayList());
		// System.out.println("name: " + ((Resource) resource).getName());
		// System.out.println("word: " + ((Music) resource).getWord());
//		File srcFile = null;
//		File unzipDir = new File(
//				"C:/Documents and Settings/x_xiajianci_a/桌面/application/狐小弟大闹彩虹岛/program/A668系列");
//		File[] files = unzipDir.listFiles((FileFilter) FileFilterUtils
//				.prefixFileFilter("Fox3"));
//
//		if (files != null && files.length > 0) {
//			srcFile = files[0];
//			System.out.println("srcFile.getName()=" + srcFile.getName());
//			System.out.println("files.length=" + files.length);
//			System.out.println("files[0]=" + srcFile.getAbsolutePath());
//			// 如果是jar或jad的则取jar文件
//			if (files.length > 1 && srcFile.getName().indexOf(".jad") > 0) {
//				srcFile = files[1];
//				System.out.println("files[1]=" + srcFile.getAbsolutePath());
//				String suiteFileName = srcFile.getName();
//				System.out.println("filename=="
//						+ suiteFileName.substring(suiteFileName
//								.lastIndexOf(".") + 1, suiteFileName.length()));
//			}
//		}
		String[] fileNames = {"ueb/21/21450_240.ueb","ueb/21/21451_240.ueb", "ueb/22/21450_240.ueb"};
		List<String> locals = new ArrayList<String>();
		for(String addr : fileNames){
			int index = addr.lastIndexOf("/");
			if(index > 0){
				String localDir = addr.substring(0,index);
				if(!locals.contains(localDir)){
					locals.add(localDir);
					System.out.println(localDir);
				}
			}
		}

	}
}

class SyncThread extends Thread {
	String command;
	File outputFile;
	String serverName;
	String localRoot;
	String rsyncIp;
	String rsyncModule;
	String rsyncType;
	String[] fileNames;
	static List<String> rsyncDirs = new ArrayList<String>();

	public SyncThread(String command, File outputFile, String serverName,
			String localRoot, String rsyncIp, String rsyncModule,
			String rsyncType, String[] fileNames) {
		this.command = command;
		this.outputFile = outputFile;
		this.serverName = serverName;
		this.localRoot = localRoot;
		this.rsyncIp = rsyncIp;
		this.rsyncModule = rsyncModule;
		this.rsyncType = rsyncType;
		this.fileNames = fileNames;

	}
	
	private boolean isParentDirNeedRsync(String path){
		int index = path.lastIndexOf("/");
		if(index > 0){
			String localDir = path.substring(0,index);
			if(rsyncDirs.contains(localDir)){
				return false;
			}
			try{
				File dir = new File(localDir);
				if(System.currentTimeMillis() - dir.lastModified()  < 3600000){
					System.out.println("OK:"+(System.currentTimeMillis() - dir.lastModified())+":"+localDir);
					rsyncDirs.add(localDir);
					return true;
				}
				
			}catch(Exception e){
				
			}
		}
		return false;
	}

	@Override
	public void run() {
		try {
			// 产生脚本文件
			// ResourceUtil.generateRsyncFile(outputFile, serverName, localRoot,
			// rsyncIp,
			// rsyncModule, rsyncType, fileNames);
			//
			// //执行外部命令
			// command = command + " " + outputFile.getCanonicalPath();
			// ResourceUtil.executeCommand(command);
			if(rsyncIp.equals("-1")){
				return;
			}
			
			List<String> locals = new ArrayList<String>();
			for(String addr : fileNames){
				int index = addr.lastIndexOf("/");
				if(index > 0){
					String localDir = addr.substring(0,index);
					if(!locals.contains(localDir)){
						if(isParentDirNeedRsync(localDir)){
							locals = new ArrayList<String>();
							break;
						}
						locals.add(localDir);
					}
				}
			}
			String[] ips = rsyncIp.split(" ");
			for (String ip : ips) {
				if(locals.size() > 0){
					for(String local : locals){
						String cmd = "rsync -razu --delete " +localRoot+"/"+ local + "/ " + ip
						+ "::" + rsyncModule+"/"+local+"/";
						System.out.println(cmd);
						Runtime.getRuntime().exec(cmd);
					}
				}else{
					String cmd = "rsync -razu --delete " + localRoot + "/ " + ip
							+ "::" + rsyncModule;
					System.out.println(cmd);
					Runtime.getRuntime().exec(cmd);
				}
			}

			// 清除临时脚本文件
			// 此处先不删除,由外部命令删除
			// FileUtils.forceDelete(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}