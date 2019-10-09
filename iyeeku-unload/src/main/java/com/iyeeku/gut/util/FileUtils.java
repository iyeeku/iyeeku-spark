package com.iyeeku.gut.util;


import java.io.*;
import java.util.ArrayList;

/**
 * @ClassName FileUtils
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/9 18:54
 * @Version 1.0
 **/
public class FileUtils {

    public static InputStream getResourceStream(String paramString){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(paramString);
    }

    public static void closeInputStream(InputStream paramInputStream){
        if (paramInputStream != null){
            try {
                paramInputStream.close();
            }catch (IOException localIOException){

            }
        }
    }

    public static void closeFileOutputStream(FileOutputStream paramFileOutputStream){
        if (paramFileOutputStream != null){
            try {
                paramFileOutputStream.close();
            }catch (IOException localIOException){

            }
        }
    }

    public static String getSystemPath(){
        try {
            return System.getProperty("GUT_HOME");
        }catch (Exception localException){

        }
        return null;
    }

    public static String getProcessPath(){
        try {
            String str = System.getProperty("PROCESS_PATH");
            if ((null == str) || (str.isEmpty())){
                str = System.getProperty("GUT_HOME");
            }
            return str;
        }catch (Exception localException){

        }
        return null;
    }

    public static String extractDirPath(String paramString){
        int i = Math.max(paramString.lastIndexOf(47),paramString.lastIndexOf(92));
        return i == -1 ? null : paramString.substring(0,i);
    }

    public static String extractFileName(String paramString){
        int i = Math.max(paramString.lastIndexOf(47),paramString.lastIndexOf(92));
        return i == -1 ? null : paramString.substring(i + 1 , paramString.length());
    }

    public static File makeFile(String paramString) throws IOException{
        File localFile = new File(paramString);
        if (localFile.isFile()){
            return localFile;
        }
        if ((paramString.endsWith("/")) || (paramString.endsWith("\\"))){
            throw new IOException(paramString + " is a directory");
        }
        String str = extractDirPath(paramString);
        if (str != null){
            makeFolder(str);
        }
        localFile.createNewFile();
        return localFile;
    }

    public static boolean makeFolder(String paramString){
        try {
            File localFile = new File(paramString);
            if (!localFile.exists()){
                localFile.mkdir();
            }
        }catch (Exception localException){
            localException.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean deleteFile(String paramString){
        try {
            File localFile = new File(paramString);
            if (localFile.exists()){
                localFile.delete();
            }
        }catch (Exception localException){
            localException.printStackTrace();
            return false;
        }
        return true;
    }

    public static void deleteAllFile(String paramString, boolean paramBoolean){
        File localFile = new File(paramString);
        if (!localFile.exists()){
            return;
        }
        if (!localFile.isDirectory()){
            return;
        }
        String[] arrayOfString = localFile.list();
        String str = null;
        for (int i = 0 ; i < arrayOfString.length ; i++){
            if ((paramString.endsWith("\\")) || (paramString.endsWith("/"))){
                str = paramString + arrayOfString[i];
            }else{
                str = paramString + File.separator + arrayOfString[i];
            }
            if (new File(str).isFile()){
                deleteFile(str);
            }
            else if ((new File(str).isDirectory()) && (paramBoolean)){
                deleteAllFile(paramString + File.separator + arrayOfString[i] , paramBoolean);
                deleteFolder(paramString + File.separator + arrayOfString[i]);
            }
        }

    }

    public static void deleteFolder(String paramString){
        try {
            File localFile = new File(paramString);
            if (localFile.exists()){
                deleteAllFile(paramString,true);
                localFile.delete();
            }
        }catch (Exception localException){
            localException.printStackTrace();
        }
    }

    public static void copyFile(String paramString1, String paramString2){
        FileInputStream localFileInputStream = null;
        FileOutputStream localFileOutputStream = null;
        try {
            int i = 0;
            int j = 0;
            File localFile = new File(paramString1);
            if (localFile.exists()){
                localFileInputStream = new FileInputStream(paramString1);
                String str = extractDirPath(paramString2);
                if (str != null){
                    makeFolder(str);
                }
                localFileOutputStream = new FileOutputStream(paramString2);
                byte[] arrayOfByte = new byte[1444];
                while ((j = localFileInputStream.read(arrayOfByte)) != -1){
                    i += j;
                    localFileOutputStream.write(arrayOfByte,0,j);
                }
            }
        }catch (Exception localException){
            localException.printStackTrace();
        }finally {
            closeInputStream(localFileInputStream);
            closeFileOutputStream(localFileOutputStream);
        }
    }

    public static String makeFilePath(String paramString1, String paramString2){
        return paramString1 + File.separatorChar + paramString2;
    }

    public static void copyFolder(String paramString1, String paramString2){
        FileInputStream localFileInputStream = null;
        FileOutputStream localFileOutputStream = null;
        try {
            makeFolder(paramString2);
            String[] arrayOfString = new File(paramString1).list();
            File localFile = null;
            for (int i=0;i<arrayOfString.length;i++){
                String str = makeFilePath(paramString1,arrayOfString[i]);
                localFile = new File(str);
                if (localFile.isFile()){
                    localFileInputStream = new FileInputStream(localFile);
                    localFileOutputStream = new FileOutputStream(makeFilePath(paramString2,arrayOfString[i]));
                    byte[] arrayOfByte = new byte[5120];
                    int j = 0;
                    int k = 0;
                    while ((j = localFileInputStream.read(arrayOfByte)) != -1){
                        localFileOutputStream.write(arrayOfByte,0,j);
                        k += j;
                    }
                    localFileOutputStream.flush();
                    closeInputStream(localFileInputStream);
                    closeFileOutputStream(localFileOutputStream);
                }
                else if (localFile.isDirectory()){
                    copyFolder(paramString1 + "/" + arrayOfString[i], paramString2 + "/" + arrayOfString[i]);
                }
            }
        }catch (Exception localException){
            localException.printStackTrace();
        }finally {
            closeInputStream(localFileInputStream);
            closeFileOutputStream(localFileOutputStream);
        }
    }

    public static void moveFile(String paramString1, String paramString2){
        copyFile(paramString1,paramString2);
        deleteFile(paramString1);
    }

    public static void moveFolder(String paramString1, String paramString2){
        copyFolder(paramString1,paramString2);
        deleteFile(paramString1);
    }

    public static ArrayList<String> getFilePathFromFolder(String paramString){
        ArrayList<String> localArrayList = new ArrayList();
        File localFile = new File(paramString);
        try {
            File[] arrayOfFile = localFile.listFiles();
            for (int i = 0; i < arrayOfFile.length; i++){
                if (arrayOfFile[i].isFile()){
                    String str = arrayOfFile[i].getName();
                    localArrayList.add(makeFilePath(paramString,str));
                }
            }
        }catch (Exception localException){
            localArrayList.add("No File!");
        }
        return localArrayList;
    }






}
