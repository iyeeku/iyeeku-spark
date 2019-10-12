package com.iyeeku.gut.main;


import com.iyeeku.gut.conn.ConnFactory;
import com.iyeeku.gut.exception.InitContextException;
import com.iyeeku.gut.unloader.IUnloader;
import com.iyeeku.gut.util.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;


import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName GUT
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/9 18:54
 * @Version 1.0
 **/
public class GUT extends Thread{

    private static IUnloader unloader;
    private static List taskList;
    private static String configFileName;
    private static String connfileName;
    private static String mode;
    private static String dataDate;
    private static ConnFactory connFactory;
    private static String systemPath = FileUtils.getSystemPath();
    private static String processPath = FileUtils.getProcessPath();
    //TODO
    private static String configPath = File.separator + "config";
    private static String appPath = File.separator + "app";
    private static GUTContext context = new GUTContext();
    private static Logger logger;
    private static Document xmlConfigDocument;
    private static GUTDBInfo dbInfo = new GUTDBInfo();
    private static Timestamp startTS;
    private static Timestamp endTS;
    private static Map sqlParams = new HashMap();
    public static String MarkFileName = "";


    private void init() throws InitContextException{

    }




    public static GUTContext getContext() {
        return context;
    }

    public static void setContext(GUTContext context) {
        GUT.context = context;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        GUT.logger = logger;
    }

    public static String getConfigPath() {
        return configPath;
    }

    public static void setConfigPath(String configPath) {
        GUT.configPath = configPath;
    }

    public static String getAppPath() {
        return appPath;
    }

    public static void setAppPath(String appPath) {
        GUT.appPath = appPath;
    }

    public static String getDataDate() {
        return dataDate;
    }

    public static void setDataDate(String dataDate) {
        GUT.dataDate = dataDate;
    }

    public static String getConfigFileName() {
        return configFileName;
    }

    public static void setConfigFileName(String configFileName) {
        GUT.configFileName = configFileName;
    }

    public static List getTaskList() {
        return taskList;
    }

    public static void setTaskList(List taskList) {
        GUT.taskList = taskList;
    }

    public static GUTDBInfo getDbInfo() {
        return dbInfo;
    }

    public static void setDbInfo(GUTDBInfo dbInfo) {
        GUT.dbInfo = dbInfo;
    }

    public static boolean isDebugMode(){
        return "Debug".equals(getContext().getDeployMode());
    }

}
