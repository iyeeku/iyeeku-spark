package com.iyeeku.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * @ClassName FileUtil
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/9/24 22:27
 * @Version 1.0
 **/
public class FileUtil {

   // private final static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public static boolean deleteFile(String filePath){
        boolean result = false;
        File file = new File(filePath);
        if (file.exists()){
            file.delete();
            result = true;
        }
        return result;
    }

    public static long getFileSize(String filePath){
        File file = new File(filePath);
        if (file.exists()){
            return file.length();
        }else{
            return 0L;
        }
    }

    public static boolean mkdir(String dir){
        boolean result = false;
        File file = new File(dir);
        if (!file.exists()){
            try {
                System.out.println("do mkdir.....");
                result = file.mkdir();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return result;
    }

    public static void write(String filePath,StringBuffer content){
        write(filePath,content.toString());
    }

    public static void write(String filePath,String content){
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(new File(filePath));
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(content);
        }catch(Exception e){
          //  LOGGER.error("FileUtil write error",e);
        }finally {
            try {
                if (bw != null){
                    bw.close();
                }
                if (fos != null){
                    fos.close();
                }
            }catch (Exception e){
            //    LOGGER.error("Close Stream error",e);
            }
        }
    }


}
