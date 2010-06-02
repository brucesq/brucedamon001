package com.hunthawk.reader.enhance.util;


import java.util.*;
import java.io.*;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import org.apache.commons.io.FileUtils;

public class UnzipFile {

    /** @link dependency */
    /*# ParseXlsFile parseXlsFile; */

    private final static int BUFFER_SIZE = 4916;

    /**
     * ��ѹ���ļ�
     * @param file ��ѹ�����ļ�
     * @param dir  ��ѹ������ļ����ŵ�Ŀ¼
     * */
    @SuppressWarnings("unchecked")
	public static void unzip(File file, File dir) throws Exception {
        BufferedOutputStream dest = null;
        BufferedInputStream is = null;
        ZipEntry entry;
        ZipFile zipfile = new ZipFile(file);

        try {
            //Enumeration e = zipfile.entries();
            Enumeration e = zipfile.getEntries();
            while (e.hasMoreElements()) {
                entry = (ZipEntry) e.nextElement();
                File entryFile = new File(dir, entry.getName());

                File parentDir=new File(entryFile.getParent());
                if(!parentDir.exists())
                    parentDir.mkdirs();

                is = new BufferedInputStream(zipfile.getInputStream((entry)));
                int count;
                byte data[] = new byte[BUFFER_SIZE];
                if (!entry.isDirectory()) {
                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                    while ((count = is.read(data, 0, BUFFER_SIZE)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } else {
                    if (!entryFile.exists()) {
                        entryFile.mkdirs();
                    }
                }

                is.close();
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            zipfile.close();
        }

    }


    /**
     *
     * ɾ����ѹ������ļ�����Ŀ¼
     * @param file ��ѹ�����ļ�
     * @param dir  ��ѹ������ļ����ŵ�Ŀ¼
     * */
    public static void cleanup(File file, File dir) throws IOException {

        //�����ѹ����������ļ���Ŀ¼
        FileUtils.forceDelete(dir);

        //����ϴ���zip�ļ�
        FileUtils.forceDelete(file);

    }


    /**
     *
     * ɾ����ѹ������ļ�����Ŀ¼
     * @param dir  ��ѹ������ļ����ŵ�Ŀ¼
     * */
    public static void dircleanup( File dirc) throws IOException {

        //�����ѹ����������ļ���Ŀ¼
        FileUtils.forceDelete(dirc);

        //����ϴ���zip�ļ�
        //FileUtils.forceDelete(file);

    }
    
	public static void main(String[] args) throws Exception {
        //File file2 = new File("D:\\upload\\ebook\\book.zip");
        //File dir2 = new File("D:\\upload\\ebook\\test");
       //unzip(file2, dir2);
       File dirFile = new File("D:\\upload\\resource\\5E1C5C3B-6793-69CE-B98E-41DBD669DAF1");
       dircleanup(dirFile);
    }

}