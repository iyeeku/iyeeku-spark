package com.iyeeku.gut.main;


import com.iyeeku.gut.conn.ConnFactory;
import com.iyeeku.gut.exception.*;
import com.iyeeku.gut.unloader.IUnloader;
import com.iyeeku.gut.util.FileUtils;
import com.iyeeku.gut.util.GUTConstants;
import com.iyeeku.gut.util.GUTXMLParser;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static String configPath = getProcessPath() + File.separator + "config";
    private static String appPath = getProcessPath() + File.separator + "app";
    private static GUTContext context = new GUTContext();
    private static Logger logger;
    private static Document xmlConfigDocument;
    private static GUTDBInfo dbInfo = new GUTDBInfo();
    private static Timestamp startTS;
    private static Timestamp endTS;
    private static Map sqlParams = new HashMap();
    public static String MarkFileName = "";

    private GUT() throws InitContextException{
        System.out.println("GUT start ...!");
        init();
    }

    public static void main(String[] paramArrayOfString) {
        startTS = new Timestamp(System.currentTimeMillis());
        if (paramArrayOfString == null || paramArrayOfString.length == 0){
            printCommandOptionErrorInfo(paramArrayOfString);
            System.out.println("GUT Exiting 1!");
            System.exit(1);
        }
        try {
            parserParams(paramArrayOfString);
            GUT localGUT = new GUT();
            localGUT.run();
        }catch (InitContextException localInitContextException){
            System.out.println("Init GUT Failed! Please Check the configuration");
            GUTExceptionHandler.handlerException(localInitContextException);
        }catch (Throwable localThrowable){
            System.out.println("Init GUT Failed! Please Check the configuration");
            GUTExceptionHandler.handlerException(new GUTException(GUTExceptionConstants.GUT100500, localThrowable));
        }finally {
            //TODO

            endTS = new Timestamp(System.currentTimeMillis());

        }

    }

    private static boolean parserParams(String[] paramArrayOfString){
        int i = paramArrayOfString.length;
        if (i == 1){
            if ("-version".equals(paramArrayOfString[0])){
                printGUTVersionInfo();
                System.exit(0);
            }else if ("-help".equals(paramArrayOfString[0])){
                printCommandOptionHelpInfo();
                System.exit(0);
            }else {
                printCommandOptionErrorInfo(paramArrayOfString);
                System.out.println("GUT Exiting 1!");
                System.out.println(1);
            }
        }else if ((8 <= i) && (i % 2 == 0)){
            if (parserParams4Eight(paramArrayOfString)){
                return true;
            }
            System.out.println("SQL Parameters error!");
            printCommandOptionErrorInfo(paramArrayOfString);
            System.out.println("GUT Exiting 1!");
            System.out.println(1);
        }
        return false;
    }

    private static void printCommandOptionErrorInfo(String[] paramArrayOfString){
        System.out.println(getCommandOptionErrorInfo(paramArrayOfString));
    }

    private static void printGUTVersionInfo(){
        FileInputStream localFileInputStream = null;
        try{
            localFileInputStream = new FileInputStream(getConfigPath() + File.separator + "application.xml");
            Element localElement = GUTXMLParser.getDocumentByInputStream(localFileInputStream).getRootElement();
            String str = localElement.elementText("Version");
            System.out.println("GUT Version: " + str);
        }catch (FileNotFoundException localFileNotFoundException){
            localFileNotFoundException.printStackTrace();
            System.out.println("GUT Version: error!");
        }catch (DocumentException localDocumentException){
            localDocumentException.printStackTrace();
            System.out.println("GUT Version: error!");
        }finally {
            if (localFileInputStream != null){
                try {
                    localFileInputStream.close();
                }catch (IOException localIOException){
                    System.out.println("WARNING:Close application.xml failed!" + localIOException.getMessage());
                }
            }
        }
    }

    private static void printCommandOptionHelpInfo(){
        System.out.println(getCommandOptionHelpInfo());
    }

    private static String getCommandOptionHelpInfo(){
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append("Usage:GUT -config configFileName -conn connFileName -mode run/continue -date yyyyMMdd\n");
        localStringBuffer.append("      or GUT -version\n");
        localStringBuffer.append("      or GUT -help\n");
        localStringBuffer.append("the options:\n");
        localStringBuffer.append("  -config\t\t\tthe config file name.\n");
        localStringBuffer.append("  -conn\t\t\tthe connection database file name.\n");
        localStringBuffer.append("  -mode\t\t\tGUN run mode.available mode include 'run' and 'continue'.\n");
        localStringBuffer.append("  -date\t\t\tthe date of the data.\n");
        localStringBuffer.append("  -version\t\t\tprint product version and exit.\n");
        localStringBuffer.append("  -help\t\t\tprint this help message.\n");
        return localStringBuffer.toString();
    }

    private static String getCommandOptionErrorInfo(String[] paramArrayOfString){
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append("Usage:GUT -config configFileName.xml -conn connFileName.txt -mode run/continue -date yyyyMMdd \n");
        localStringBuffer.append("Please check the Parameter format \n");
        localStringBuffer.append("Use -help for Parameter message\n");
        localStringBuffer.append("!Error Message:\n");
        String str = "";
        for (int i = 0; i < paramArrayOfString.length; i++) {
            str = str + paramArrayOfString[i] + " ";
            if (paramArrayOfString[i].equals("-config")) {
                if (i + 1 == paramArrayOfString.length) {
                    localStringBuffer.append("      Parameter [-config] value not found \n");
                } else if (!paramArrayOfString[(i + 1)].endsWith(".xml")) {
                    localStringBuffer.append("      Parameter [-config] value can not be [" + paramArrayOfString[(i + 1)] + "] \n");
                }
            }
            if (paramArrayOfString[i].equals("-conn")) {
                if (i + 1 == paramArrayOfString.length) {
                    localStringBuffer.append("      Parameter [-conn] value not found \n");
                } else if (!paramArrayOfString[(i + 1)].endsWith(".txt")) {
                    localStringBuffer.append("      Parameter [-conn] value can not be [" + paramArrayOfString[(i + 1)] + "] \n");
                }
            }
            if (paramArrayOfString[i].equals("-date")) {
                if (i + 1 == paramArrayOfString.length) {
                    localStringBuffer.append("      Parameter [-date] value not found \n");
                } else if (!checkDataDate(paramArrayOfString[(i + 1)])) {
                    localStringBuffer.append("      Parameter [-date] value can not be [" + paramArrayOfString[(i + 1)] + "] \n");
                }
            }
            if (paramArrayOfString[i].equals("-mode")) {
                if (i + 1 == paramArrayOfString.length) {
                    localStringBuffer.append("      Parameter [-mode] value not found \n");
                } else if (!checkMode(paramArrayOfString[(i + 1)])) {
                    localStringBuffer.append("      Parameter [-mode] value can not be [" + paramArrayOfString[(i + 1)] + "] \n");
                }
            }
            if ((checkDataDate(paramArrayOfString[i])) && (i != 0) && (!paramArrayOfString[(i - 1)].equals("-date"))) {
                localStringBuffer.append("      Parameter " + paramArrayOfString[i] + " error Please check the parameter of the position or Check whether the repeated values\n");
            }
            if ((!paramArrayOfString[i].equals("-config")) && (!paramArrayOfString[i].equals("-conn")) && (!paramArrayOfString[i].equals("-mode"))
                    && (!paramArrayOfString[i].equals("-date")) && (!checkDataDate(paramArrayOfString[i]))) {
                if ((paramArrayOfString[i].endsWith(".xml")) || (paramArrayOfString[i].endsWith(".txt")) || (paramArrayOfString[i].endsWith("run"))
                        || (paramArrayOfString[i].endsWith("continue")) || (checkDataDate(paramArrayOfString[i]))) {
                    if ((i == 0) || ((!paramArrayOfString[(i - 1)].equals("-conn")) && (!paramArrayOfString[(i - 1)].equals("-config")) && (!paramArrayOfString[(i - 1)].equals("-mode"))
                            && (!paramArrayOfString[(i - 1)].equals("-date")))) {
                        localStringBuffer.append("      Parameter [" + paramArrayOfString[i] + "] error ! Please check the parameter of the position or Check whether the repeated values\n");
                    } else if ((!paramArrayOfString[i].endsWith(".xml")) && (!paramArrayOfString[i].endsWith(".txt"))) {
                        localStringBuffer.append("      Parameter [" + paramArrayOfString[i] + "] format error!\n");
                    }
                }
            }
        }
        if (str.indexOf("-conn") == -1){
            localStringBuffer.append("      Parameter [-conn] not found \n");
        }
        if (str.indexOf("-config") == -1){
            localStringBuffer.append("      Parameter [-config] not found \n");
        }
        if (str.indexOf("-mode") == -1){
            localStringBuffer.append("      Parameter [-mode] not found \n");
        }
        if (str.indexOf("-date") == -1){
            localStringBuffer.append("      Parameter [-date] not found \n");
        }
        if (str.indexOf(".xml") == -1){
            localStringBuffer.append("      Parameter [-config] value not found Please add configuration file name \n");
        }
        if (str.indexOf(".txt") == -1){
            localStringBuffer.append("      Parameter [-conn] value not found Please add configuration file name \n");
        }
        if ((str.indexOf("run") == -1) && (str.indexOf("continue") == -1)){
            localStringBuffer.append("      Parameter [-mode] value not found Please add operation mode\n");
        }
        if (str.trim().length() == 0){
            localStringBuffer.append("      Parameter [-date] value not found Please add operation date\n");
        }
        return localStringBuffer.toString();
    }

    private static boolean parserParams4Eight(String[] paramArrayOfString){
        int i = 0;
        int j = 0;
        while (j < paramArrayOfString.length){
            if ("-config".equals(paramArrayOfString[j])){
                if (paramArrayOfString[(j + 1)].endsWith(".xml")){
                    MarkFileName = paramArrayOfString[(j + 1)].substring(0, paramArrayOfString[(j + 1)].lastIndexOf("."));
                    setConfigFileName(paramArrayOfString[(j + 1)]);
                    i++;
                }
            }else if ("-conn".equals(paramArrayOfString[j])){
                if (paramArrayOfString[(j + 1)].endsWith(".txt")){
                    setConnfileName(paramArrayOfString[(j + 1)]);
                    i++;
                }
            }else if ("-mode".equals(paramArrayOfString[j])){
                if (checkMode(paramArrayOfString[(j + 1)])){
                    setMode(paramArrayOfString[(j + 1)]);
                    i++;
                }else{
                    return false;
                }
            }else if ("-date".equals(paramArrayOfString[j])){
                if (checkDataDate(paramArrayOfString[(j + 1)])){
                    setDataDate(paramArrayOfString[(j + 1)]);
                    i++;
                }else{
                    return false;
                }
            }else if (checkSQLParam(paramArrayOfString[j], paramArrayOfString[(j + 1)])){
                String str = paramArrayOfString[j].substring(1);
                sqlParams.put(str, paramArrayOfString[(j + 1)]);
                i++;
            }else{
                return false;
            }
            j += 2;
        }
        return i >= 4;
    }

    private static boolean checkMode(String paramString){
        return ("continue".equals(paramString)) || ("run".equals(paramString));
    }

    private static boolean checkDataDate(String paramString){
        if (paramString.length() != 8){
            return false;
        }
        try {
            //日期正则表达式 https://www.debugease.com/javaweb/1289586.html
            String str = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
            Pattern localPattern = Pattern.compile(str);
            Matcher localMatcher = localPattern.matcher(paramString);
            boolean bool = localMatcher.matches();
            return bool;
        }catch (RuntimeException localRuntimeException){

        }
        return false;
    }

    private static boolean checkSQLParam(String paramString1, String paramString2){
        if ((paramString1.length() >= 2) && ("-".equals(paramString1.substring(0,1)))){
            String str = paramString1.substring(1);
            if (null == sqlParams.get(str)){
                return true;
            }
        }
        System.out.println(paramString1 + ":" + paramString2);
        return false;
    }

    private String translateSQL(String paramString){
        String str1 = paramString.replaceAll("\n"," ");
        Iterator localIterator = sqlParams.entrySet().iterator();
        while (localIterator.hasNext()){
            Map.Entry<String,String> localEntry = (Map.Entry)localIterator.next();
            String str2 = localEntry.getKey();
            String str3 = localEntry.getValue();
            str1 = str1.replace("#"+str2+"#",str3);
        }
        return str1;
    }

    @Override
    public void run() {
        try {
            unloader.unload();
        }catch (UnloaderException localUnloaderException){
            getLogger().error("Unload Task Failed: " + localUnloaderException.getMessage());
            GUTExceptionHandler.handlerException(localUnloaderException);
        }
    }

    private void init() throws InitContextException{
        try {
            beforeInit();
            initApplication();

        }catch (InitContextException localInitContextException){
            System.out.println("Init GUT Failed! Please check the configuration!");
            throw localInitContextException;
        }
    }


    private void initApplication() throws InitContextException{
        try {
            initLogger();
            initExceptionInfo();
            initDBInfo();

        }catch (InitContextException localInitContextException){
            System.out.println("Init GUT Failed! Please check the configuration!");
            throw localInitContextException;
        }
    }


    private void initDBInfo() throws InitContextException{
        FileInputStream localFileInputStream = null;
        try {
            localFileInputStream = new FileInputStream(getConfigPath() + File.separator + "application.xml");
            Element localElement1 = GUTXMLParser.getDocumentByInputStream(localFileInputStream).getRootElement();
            Element localElement2 = (Element)localElement1.elementIterator(GUTConstants.XML_APPINF_DBInfos).next();
            Element localElement3 = (Element)localElement1.elementIterator(GUTConstants.XML_APPINF_DataFile).next();
            Element localElement4 = (Element)localElement1.elementIterator(GUTConstants.XML_APPINF_ColumnType).next();
            String str1 = localElement4.elementText(GUTConstants.XML_APPINF_NeedScale);
            String str2 = localElement4.elementText(GUTConstants.XML_APPINF_NotNeedPrecision);
            String fileNamePattern = localElement3.elementText(GUTConstants.XML_APPINF_NamePattern);
            Element localElement5 = (Element)localElement1.elementIterator(GUTConstants.XML_APPINF_TaskThread).next();
            int i = 3;
            try {
                i = new Integer(localElement5.elementText(GUTConstants.XML_APPINF_BufferSizeRatio)).intValue();
                if ((i < 3) || (i > 10)){
                    i = 3;
                }
            }catch (Exception localException3){
                getLogger().error(localException3.toString());
            }
            Runtime localRuntime = Runtime.getRuntime();
            context.setBufferMaxSize(localRuntime.maxMemory() * i / 100L);
            String str4 = getContext().getDatabaseType();
            int j = 0;
            Iterator<Element> localIterator = localElement2.elementIterator(GUTConstants.XML_APPINF_DBInfo);
            while (localIterator.hasNext()){
                Element element = localIterator.next();
                String dbType = element.attributeValue(GUTConstants.XML_APPINF_DBInfo_Type);
                if ((dbType != null) && (str4.equals(dbType))){
                    j = 1;
                    dbInfo.setUnloaderClassName(element.elementText(GUTConstants.XML_APPINF_Unloader));
                    dbInfo.setTaskClassName(element.elementText(GUTConstants.XML_APPINF_Task));
                    dbInfo.setDriverClassName(element.elementText(GUTConstants.XML_APPINF_DriverClassName));
                    dbInfo.setUrlPattern(element.elementText(GUTConstants.XML_APPINF_UrlPattern));
                    dbInfo.setSupportedDataType(element.elementText(GUTConstants.XML_APPINF_SupportedDataType));
                    dbInfo.setFileNamePattern(fileNamePattern);
                    dbInfo.setColumnTypeNames4NeedScale(str1);
                    dbInfo.setColumnTypeNames4NotNeedPrecision(str2);
                    String autoCommitFlg = element.elementText(GUTConstants.XML_APPINF_AutoCommit);
                    if (autoCommitFlg == null){
                        dbInfo.setAutoCommit(true);
                    }else{
                        dbInfo.setAutoCommit(false);
                    }
                }
            }
            if (j == 0){
                throw new DBConfigNotFoundException(str4);
            }
        }catch (FileNotFoundException localFileNotFoundException){
            getLogger().error("GUT Not Found Connection File or Not Found Config File " + localFileNotFoundException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100110, localFileNotFoundException);
        }catch (DocumentException localDocumentException){
            getLogger().error("check Config File Failed, Please check the configuration! " + localDocumentException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100115, localDocumentException);
        }catch (DBConfigNotFoundException localDBConfigNotFoundException){
            getLogger().error("Database Connection Failed, Please Check the configuration! " + localDBConfigNotFoundException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100120, localDBConfigNotFoundException);
        }catch (Exception localException){
            getLogger().error(localException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100100, localException);
        }
    }


    private void beforeInit() throws InitContextException{
        try {
            if (systemPath == null){
                System.out.println("The GUT_HOME system properties is not defined correctly");
                throw new InitContextException(GUTExceptionConstants.GUT100005, "The GUT_HOME system properties is not defined correctly!");
            }
            String str1 = getConfigPath() + File.separator + getConfigFileName();
            GUTXMLParser.checkXMLFormat(str1, GUTConstants.XML_XSD_PATH);
            Document localDocument = GUTXMLParser.getDocumentByPath(str1);
            setXmlConfigDocument(localDocument);
            Element localElement = (Element) getXmlConfigDocument().getRootElement().elementIterator("ContextParams").next();
            context.setThreadNums(new Integer(localElement.elementText("ThreadNum").trim()).intValue());
            String str2 = localElement.elementText("LogMode");
            if (str2 != null){
                context.setLogMode(str2.trim());
            }else{
                context.setLogMode("loop");
            }
            String str3 = localElement.elementText("LogSize");
            if (str3 != null){
                context.setLogSize(new Integer(str3.trim()).intValue());
            }else {
                context.setLogSize(GUTConstants.Default_LogSize);
            }
            context.setDatabaseType(localElement.elementText("DatabaseType").trim());
            String str4 = localElement.elementText("DeployMode");
            if (str4 != null){
                context.setDeployMode(str4.trim());
            }else {
                context.setDeployMode("Product");
            }
        } catch (FileNotFoundException localFileNotFoundException){
            System.out.println("The config file checked or parsed failed, please check the content format or url of the config file! " + localFileNotFoundException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100015, localFileNotFoundException);
        }
        catch (SAXException localSAXException){
            System.out.println("The config file checked or parsed failed, please check the content format or url of the config file! " + localSAXException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100015, localSAXException);
        }
        catch (IOException localIOException){
            System.out.println("The config file checked or parsed failed, please check the content format or url of the config file! " + localIOException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100015, localIOException);
        }
        catch (ParserConfigurationException localParserConfigurationException){
            System.out.println("The config file checked or parsed failed, please check the content format or url of the config file! " + localParserConfigurationException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100015, localParserConfigurationException);
        }
        catch (DocumentException localDocumentException){
            System.out.println("The config file checked or parsed failed, please check the content format or url of the config file! " + localDocumentException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100015, localDocumentException);
        }
        catch (Exception localException){
            System.out.println("Init GUT Failed! " + localException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100000, localException);
        }
    }


    private void initLogger() throws InitContextException{
        InputStream localInputStream = null;
        try{
            localInputStream = getClass().getResourceAsStream("/com/iyeeku/gut/config/log4j.properties");
            Properties localProperties = new Properties();
            localProperties.load(localInputStream);
            localProperties = modifyProperties(localProperties);
            if (!FileUtils.isFolderExist(getProcessPath() + File.separator + "log")){
                System.out.println("Init Log Failed, The Log File Path Not Found!");
                throw new InitContextException("The folder named 'log' not found!");
            }
            PropertyConfigurator.configure(localProperties);
            setLogger(Logger.getLogger(getClass()));
        }catch (InitContextException localInitContextException){
            System.out.println(localInitContextException.toString());
            throw localInitContextException;
        }catch (Exception localException){
            System.out.println(localException.toString());
            throw new InitContextException("Init GUT Logger Failed!", localException);
        }finally {
            if (localInputStream != null){
                try{
                    localInputStream.close();
                }catch (IOException localIOException){
                    System.out.println("WARNING:Close log4j.properties failed! " + localIOException.getMessage());
                }
            }
        }


    }


    private Properties modifyProperties(Properties paramProperties) throws InitContextException{
        String str1 = getContext().getLogMode();
        int i = getContext().getLogSize();
        if (GUTConstants.LogMode_Archive.equals(str1)){
            paramProperties.setProperty("log4j.appender.fileAppender.MaxBackupIndex","0");
            paramProperties.setProperty("log4j.appender.errFileAppender.MaxBackupIndex","0");
            paramProperties.setProperty("log4j.appender.warnFileAppender.MaxBackupIndex","0");
        }else if (GUTConstants.LogMode_Loop.equals(str1)){
            paramProperties.setProperty("log4j.appender.fileAppender.maximumFileSize",new Integer(i).toString());
            paramProperties.setProperty("log4j.appender.errFileAppender.maximumFileSize",new Integer(i).toString());
            paramProperties.setProperty("log4j.appender.warnFileAppender.maximumFileSize",new Integer(i).toString());
        }
        paramProperties.setProperty("log4j.appender.fileAppender.File", systemPath + File.separator + "log" + File.separator + GUTConstants.Default_LogFileName);
        paramProperties.setProperty("log4j.appender.errFileAppender.File", systemPath + File.separator + "log" + File.separator + GUTConstants.Default_ErrorLogFileName);
        paramProperties.setProperty("log4j.appender.warnFileAppender.File", systemPath + File.separator + "log" + File.separator + GUTConstants.Default_WarnLogFileName);
        return paramProperties;
    }

    private void initExceptionInfo() throws InitContextException{
        InputStream localInputStream = null;
        try {
            localInputStream = getClass().getResourceAsStream("/com/iyeeku/gut/config/errorMsg_en_US.properties");
            Properties localProperties = new Properties();
            localProperties.load(localInputStream);
            GUTExceptionHandler.setExceptionMsg(localProperties);
        }catch (Exception localException){
            getLogger().error(localException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100140, localException);
        }finally {
            if (localInputStream != null){
                try{
                    localInputStream.close();
                }catch (IOException localIOException){
                    System.out.println("WARNING:Close errorMsg_en_US.properties failed! " + localIOException.getMessage());
                }
            }
        }

    }


    public static Document getXmlConfigDocument() {
        return xmlConfigDocument;
    }

    public static void setXmlConfigDocument(Document xmlConfigDocument) {
        GUT.xmlConfigDocument = xmlConfigDocument;
    }

    public static String getMode() {
        return mode;
    }

    public static void setMode(String mode) {
        GUT.mode = mode;
    }

    public static String getConnfileName() {
        return connfileName;
    }

    public static void setConnfileName(String connfileName) {
        GUT.connfileName = connfileName;
    }

    public static String getProcessPath() {
        return processPath;
    }

    public static void setProcessPath(String paramString) {
        processPath = paramString;
    }

    public static ConnFactory getConnFactory() {
        return connFactory;
    }

    public static void setConnFactory(ConnFactory connFactory) {
        GUT.connFactory = connFactory;
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
