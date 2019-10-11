package com.iyeeku.gut.util;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

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

    public static ArrayList<String> getFileNameFromFolder(String paramString){
        ArrayList<String> localArrayList = new ArrayList();
        File localFile = new File(paramString);
        File[] arrayOfFile = localFile.listFiles();
        for (int i =0; i < arrayOfFile.length; i++){
            if (arrayOfFile[i].isFile()){
                localArrayList.add(arrayOfFile[i].getName());
            }
        }
        return localArrayList;
    }

    public static ArrayList<String> getFolderNameFromFolder(String paramString){
        ArrayList<String> localArrayList = new ArrayList();
        File localFile = new File(paramString);
        File[] arrayOfFile = localFile.listFiles();
        for (int i =0; i < arrayOfFile.length; i++){
            if (arrayOfFile[i].isDirectory()){
                localArrayList.add(arrayOfFile[i].getName());
            }
        }
        return localArrayList;
    }

    public static int getFileCount(String paramString){
        int i = 0;
        try {
            File localFile = new File(paramString);
            if (!isFolderExist(paramString)){
                return i;
            }
            File[] arrayOfFile = localFile.listFiles();
            for (int j = 0;j < arrayOfFile.length; j++){
                if (arrayOfFile[j].isFile()){
                    i++;
                }
            }
        }catch (Exception localException){
            i = 0;
        }
        return i;
    }

    public static int getFileCount(String paramString1, String paramString2){
        int i = 0;
        if (!isFolderExist(paramString1)){
            return i;
        }
        if ((paramString2.equals("")) || (paramString2 == null)){
            return getFileCount(paramString1);
        }
        File localFile = new File(paramString1);
        File[] arrayOfFile = localFile.listFiles();
        for (int j = 0;j<arrayOfFile.length;j++){
            if ((arrayOfFile[j].isFile()) && Pattern.matches(paramString2,arrayOfFile[j].getName())){
                i++;
            }
        }
        return i;
    }

    public static int getStrCountFromFile(String paramString1, String paramString2){
        if (!isFileExist(paramString1)){
            return 0;
        }
        FileReader localFileReader = null;
        BufferedReader localBufferedReader = null;
        int i = 0;
        try {
            localFileReader = new FileReader(paramString1);
            localBufferedReader = new BufferedReader(localFileReader);
            String str = null;
            while ((str = localBufferedReader.readLine()) != null){
                if (str.indexOf(paramString2) != -1){
                    i++;
                }
            }
        }catch (FileNotFoundException localFileNotFoundException){
            localFileNotFoundException.printStackTrace();
        }catch (IOException localIOException){
            localIOException.printStackTrace();
        }finally {
            try {
                if (localBufferedReader != null){
                    localBufferedReader.close();
                }
                if (localFileReader != null){
                    localFileReader.close();
                }
            }catch (Exception localException){
                localException.printStackTrace();
            }
        }
        return i;
    }

    public static int getFileLineCount(String paramString){
        if (!isFileExist(paramString)){
            return 0;
        }
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        int i = 0;
        try {
            fileReader = new FileReader(paramString);
            bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.readLine() != null){
                    i++;
            }
        }catch (FileNotFoundException localFileNotFoundException1){
            localFileNotFoundException1.printStackTrace();
        }catch (IOException localIOException){
            localIOException.printStackTrace();
        }finally {
            try {
                if (fileReader != null){
                    fileReader.close();
                }
                if (bufferedReader != null){
                    bufferedReader.close();
                }
            }catch (Exception l){
                l.printStackTrace();
            }
        }
        return i;
    }

    public static boolean ifFileIsNull(String paramString) throws  IOException{
        boolean bool = false;
        FileReader localFileReader = new FileReader(paramString);
        if (localFileReader.read() == -1){
            bool = true;
        }
        localFileReader.close();
        return bool;
    }

    public static boolean isFileExist(String paramString){
        if ((paramString == null) || (paramString.length() == 0)){
            return false;
        }
        File localFile = new File(paramString);
        return (localFile.exists()) && (!localFile.isDirectory());
    }

    public static boolean isFolderExist(String paramString){
        if ((paramString == null) || (paramString.length() == 0)){
            return false;
        }
        File localFile = new File(paramString);
        return localFile.isDirectory();
    }

    public static Double getFileSize(String paramString){
        if (!isFileExist(paramString)){
            return null;
        }
        File localFile = new File(paramString);
        double d = Math.ceil(localFile.length() / 1024.0D);
        return new Double(d);
    }

    public static Double getFileByteSize(String paramString){
        if (!isFileExist(paramString)){
            return null;
        }
        File localFile = new File(paramString);
        double d = Math.ceil(localFile.length());
        return new Double(d);
    }





}
