package com.iyeeku.spark.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @ClassName LogUtil
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/4 16:12
 * @Version 1.0
 **/
public class LogUtil {

    public static String LOG_DIR = "/home/shell/log";

    public static String LOCAL_LOG_DIR = "/home/shell/log";

    /**
     * 新增log4j日志导出到文件
     * @param logFile
     */
    public static void addRootLoggerFileAppender(String logFile){
        Logger rootLogger = Logger.getRootLogger();
        RollingFileAppender appender = new RollingFileAppender();
        PatternLayout layout = new PatternLayout();
        layout.setConversionPattern("%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n");
        appender.setLayout(layout);
        appender.setFile(logFile);
        appender.setMaxFileSize("100MB");
        appender.activateOptions();
        rootLogger.addAppender(appender);
    }

    /**
     * 处理日志目录
     * @param logPath
     */
    public static void initRootLogDir(File logPath){
        if (logPath.exists()){
            if (!logPath.isDirectory()){
                System.err.println(logPath.getAbsolutePath()+"不是目录，请处理!");
            }
        }else{
            if (!logPath.mkdir()){
                System.err.println(logPath.getAbsolutePath()+"创建不了，请处理!");
            }
        }
    }

    /**
     * 初始化日志目录
     * @param rq
     * @return
     */
    public static String initLogDir(String rq){
        String localrq = null;
        if (rq == null){
            Calendar now = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            localrq = sdf.format(now.getTime());
        }else{
            localrq = rq;
        }
        File file = new File(LOG_DIR,localrq);
        initRootLogDir(file);
        return file.getAbsolutePath();
    }

    public static String initLogFile(String logPath,String className){
        long time = System.currentTimeMillis();
        String fileName = className + "." + time + ".log";
        return new File(logPath,fileName).getAbsolutePath();
    }

}
