package com.cloud.disk.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

public class FileReadAndWriter {
	
	public final static String SEPARATOR = "/";
	public final static String BASE_PATH = "C://temp/file";
	
	/**
	 * 命令打开文件
	 * @param fileDir
	 */
    public static void openFile(String fileDir) {
           try {
           Runtime   rt   =   Runtime.getRuntime();   
                  rt.exec("cmd   /c   start   " +fileDir);
           } catch (IOException e) {
                  e.printStackTrace();
           }
    }
    
    /**
     * 获取文件哈希值
     * @param fileDir
     * @return
     */
    public static String getFileSha1(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[8192];
        int len;
        try {
            digest =MessageDigest.getInstance("SHA-1");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 获取文件MD5值
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[8192];
        int len;
        try {
            digest =MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
      
    }
    
    
    /**
    * 获得当前的文件路径
    * @param basePath
    * @return
    */
    public static String getNowFilePath(String basePath,File file){
           String pathName = getFileSha1(file);
           File dir = new File(basePath + SEPARATOR + pathName);
           if(!dir.exists()){
        	 dir.mkdirs();
           }
           return dir.getPath();
    }
    
    /**
     * 保存文件
     * @param file  目标文件
     * @param fileName  上传文件名
     * @param filePath  上传文件路径
     * @throws Exception
     */
    public static void dumpAsset(File file, String fileName, String filePath) throws Exception{
           File outputFile = new File(filePath + SEPARATOR + fileName);
           if(outputFile.exists()){
                  return;
           }
           FileOutputStream fis = new FileOutputStream(outputFile);
           BufferedOutputStream bos = new BufferedOutputStream(fis);
           BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
           int character;
           while ((character = bis.read()) != -1){
        	   bos.write(character);
           }
           bos.flush();
           bis.close();
           fis.close();
           bos.close();
    }
    
    public static void main(String[] args) throws Exception {
    	File file = new File("C://temp/README.txt");
    	String basePath = "C://temp/file";
    	dumpAsset(file, file.getName(), getNowFilePath(basePath, file));
	}
    
}
