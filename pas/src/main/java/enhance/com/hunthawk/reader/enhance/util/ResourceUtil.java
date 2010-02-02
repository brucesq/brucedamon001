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
	// �ļ�ͬ������:����
	public static final String RSYNC_TYPE_ADD = "ADD";

	// �ļ�ͬ������:ɾ��
	public static final String RSYNC_TYPE_DEL = "DEL";

	// �����С����λ���ֽ�
	private static final int BUFFER_SIZE = 4096;

	// ��ʱʱ�䣬��λ����
	private static final int TIMEOUT_DEFAULT = 300;

	// Сͼ���ļ�������
	public static final String SMALL_IMAGE = "small_image";

	// ��ͼ���ļ�������
	public static final String LARGE_IMAGE = "large_image";

	// ��ͼ�ļ�������
	public static final String CAPTURE_IMAGE = "capture_image";

	// Ӧ�ó����ļ�������
	public static final String PROGRAME = "program";

	private static final Log log = LogFactory.getLog(ResourceUtil.class);

	/**
	 * ͨ��java�ķ�����ƣ������ֶε�ֵ
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
	 * ��������Ĳ����Ѱ��Field��������û�У������β������ĸ������Ƿ��� ����û�У����׳��쳣
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

	// //////ͬ���ļ���غ���begin

	/**
	 * ִ���ⲿ�����г���
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
	 * ����rsync����ͬ���Ľű��ļ�
	 * 
	 * @param outputFile
	 *            ���ɵĽű��ļ���
	 * @param serverName
	 *            Զ���ļ�����������
	 * @param localRoot
	 *            �����ļ���Ÿ�·��
	 * @param rsyncIp
	 *            Զ���ļ���������ӦIP�б�,��SS���ŷ���
	 * @param rsyncModule
	 *            rsyncģ����,��SS���ŷ���
	 * @param rsyncType
	 *            "ADD"�������ļ���"DEL"��ɾ���ļ�
	 * @param fileNames
	 *            Ҫͬ�����ļ����б����·����
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
	 * ���ݲ�����rsync�ű��ļ���ͬ����Ӧ���ļ�
	 */
	public static void rsyncFile(String command, File outputFile,
			String serverName, String localRoot, String rsyncIp,
			String rsyncModule, String rsyncType, String[] fileNames) {

		// try
		// {
		// //�����ű��ļ�
		// generateRsyncFile(outputFile, serverName, localRoot, rsyncIp,
		// rsyncModule, rsyncType, fileNames);
		//
		// //ִ���ⲿ����
		// command = command + " " + outputFile.getCanonicalPath();
		// executeCommand(command);
		//
		// //�����ʱ�ű��ļ�
		// //�˴��Ȳ�ɾ��,���ⲿ����ɾ��
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
	 * ����Ŀ¼�������������ļ�
	 * 
	 * @author xiajianci Dec 25, 2007 10:52:54 AM
	 * @version 1.0
	 * @param filePath
	 *            �ļ�Ŀ¼
	 * @param fileName
	 *            �������ļ�����
	 * @return �������ļ����ƿ�ͷ���ļ�����
	 */
	public static File[] searchFile(File filePath, String fileName) {
		File[] dirs = filePath.listFiles((FileFilter) FileFilterUtils
				.prefixFileFilter(fileName));
		return dirs;
	}

	// //////ͬ���ļ���غ���end

	public static void main(String[] args) {

		// Resource resource = new Music();
		// setFieldName(resource, "name", "testname", new ArrayList());
		// setFieldName(resource, "word", "testword", new ArrayList());
		// System.out.println("name: " + ((Resource) resource).getName());
		// System.out.println("word: " + ((Music) resource).getWord());
//		File srcFile = null;
//		File unzipDir = new File(
//				"C:/Documents and Settings/x_xiajianci_a/����/application/��С�ܴ��ֲʺ絺/program/A668ϵ��");
//		File[] files = unzipDir.listFiles((FileFilter) FileFilterUtils
//				.prefixFileFilter("Fox3"));
//
//		if (files != null && files.length > 0) {
//			srcFile = files[0];
//			System.out.println("srcFile.getName()=" + srcFile.getName());
//			System.out.println("files.length=" + files.length);
//			System.out.println("files[0]=" + srcFile.getAbsolutePath());
//			// �����jar��jad����ȡjar�ļ�
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
			// �����ű��ļ�
			// ResourceUtil.generateRsyncFile(outputFile, serverName, localRoot,
			// rsyncIp,
			// rsyncModule, rsyncType, fileNames);
			//
			// //ִ���ⲿ����
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

			// �����ʱ�ű��ļ�
			// �˴��Ȳ�ɾ��,���ⲿ����ɾ��
			// FileUtils.forceDelete(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}