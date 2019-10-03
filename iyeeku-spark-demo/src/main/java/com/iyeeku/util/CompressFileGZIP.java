package com.iyeeku.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * @ClassName CompressFileGZIP
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/9/24 22:38
 * @Version 1.0
 **/
public class CompressFileGZIP {

   // private final static Logger LOGGER = LoggerFactory.getLogger(CompressFileGZIP.class);

    public static void doCompressFile(String inFileName){
       // LOGGER.info("Creating the GZIP output stream");
        String outFileName = inFileName + ".gz";
        GZIPOutputStream out = null;

        try {
          out = new GZIPOutputStream(new FileOutputStream(outFileName));
        } catch (Exception e){
          //  LOGGER.error("Could not create file: " + outFileName, e);
            System.exit(1);
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(inFileName);
        } catch (FileNotFoundException e){
          //  LOGGER.error("File not found: " + inFileName,e);
            System.exit(1);
        }

        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0){
                out.write(buf, 0,len);
            }
            in.close();
          //  LOGGER.info("Completing the GZIP file");
            out.finish();
            out.close();
        }catch (IOException e){
          //  LOGGER.error("create GZIP file failure", e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
       // LOGGER.info("CompressFileGZIP doCompressFile");
        String strPath = "";
        doCompressFile(strPath);
    }


}
