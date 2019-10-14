package com.iyeeku.gut.main;


import com.iyeeku.gut.conn.ConnFactory;
import com.iyeeku.gut.conn.ConnInfo;
import com.iyeeku.gut.exception.*;
import com.iyeeku.gut.task.AbstractTask;
import com.iyeeku.gut.unloader.IUnloader;
import com.iyeeku.gut.util.*;

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
            beforeExit();
            endTS = new Timestamp(System.currentTimeMillis());
            checkStatus(GUTConstants.succ_num, getTaskList());
        }

    }

    private static void checkStatus(int paramInt, List paramList){
        long l = 0L;
        String str = "GUT Exiting 1!";
        int i = 0;
        try {
            l = endTS.getTime() - startTS.getTime();
            //TODO
            if (getTaskList() == null){
                str = "Init GUT Failed!";
            }
            if (getLogger() == null){
                str = "Init GUT Failed!";
            }
            if (GUTConstants.succ_num == getTaskList().size()){
                i = 1;
            }
        }catch (Exception localException){
            str = "Init GUT Failed!";
        }finally {
            if (null != getLogger()){
                getLogger().info("GUT Use " + l + " ms! or ");
            }  else {
                System.out.println("xxxxxxxxxxx");
            }
            if (i != 0){
                getLogger().info("GUT Exiting 0!");
                System.exit(0);
            }else{
                System.out.println(str);
                System.exit(1);
            }
        }
    }

    public void  ResetTaskList(boolean paramBoolean1, boolean paramBoolean2){
        ArrayList localArrayList = new ArrayList();
        Properties localProperties = new Properties();
        FileInputStream localFileInputStream = null;
        int i = 0;
        try {
            AbstractTask localAbstractTask = null;
            File localFile = new File(GUTConstants.MARKFILE_PATH);
            if ((!localFile.exists()) || (localFile.length() == 0L)){
                return;
            }
            localFileInputStream = new FileInputStream(localFile);
            localProperties.load(localFileInputStream);
            for (int j = 0; j < taskList.size(); j++){
                localAbstractTask = (AbstractTask) taskList.get(j);
                String str = localAbstractTask.getOldFileName();
                if (null == localProperties.get(str)){
                    localAbstractTask.setUseTime(0);
                    localArrayList.add(localAbstractTask);
                }else{
                    localAbstractTask.setUseTime(Integer.parseInt(localProperties.get(str).toString()));
                    localArrayList.add(localAbstractTask);
                }
            }
            Collections.sort(localArrayList, new ReverseSort());
            setTaskList(localArrayList);
            int j = ((AbstractTask)taskList.get(0)).getUseTime();
            if (context.getSkip() != 0){
                for (int k = 0; k < localArrayList.size(); k++){
                    localAbstractTask = (AbstractTask) localArrayList.get(k);
                    if (localAbstractTask.getTaskID() <= context.getSkip()){
                        localAbstractTask.setUseTime(j + context.getSkip() + 1 - localAbstractTask.getTaskID());
                    }
                }
                Collections.sort(localArrayList, new ReverseSort());
                setTaskList(localArrayList);
            }
            for (int k = 0; k < taskList.size(); k++){
                localAbstractTask = (AbstractTask) taskList.get(k);
                if ((paramBoolean1) || (paramBoolean2)){
                    localAbstractTask.setTaskOrder(k + 1);
                }else{
                    i++;
                    if (localAbstractTask.getTaskOrder() != 0){
                        localAbstractTask.setTaskOrder(i);
                    }
                }
            }

        }catch (FileNotFoundException localFileNotFoundException){
            getLogger().error(localFileNotFoundException.getMessage());
        }catch (IOException localIOException){
            getLogger().error(localIOException.getMessage());
        }finally {
            try {
                if (localFileInputStream != null){
                    localFileInputStream.close();
                }
            }catch (IOException localIOException1){
                getLogger().error(localIOException1.getMessage() + "Close Io Failed");
                localIOException1.printStackTrace();
            }
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
                System.exit(1);
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
            initContextParams();
            initTaskList();
            initConnFactory();
            afterInit();
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
            initUnloader();
        }catch (InitContextException localInitContextException){
            System.out.println("Init GUT Failed! Please check the configuration!");
            throw localInitContextException;
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

    private void afterInit() throws InitContextException{
        getLogger().info("Init Success!");
        File localFile = new File(GUTConstants.MARKFILE_PATH);
        if (localFile.exists()){
            localFile.delete();
        }
        if ((localFile.length() == 0L) || (!localFile.exists()));
    }

    private void initContextParams() throws InitContextException{
        try {
            Element localElement = (Element) getXmlConfigDocument().getRootElement().elementIterator("ContextParams").next();
            context.setOutputFilePath(localElement.elementText("OutputFilePath").trim());
            GUTXMLParser.FileisExist(localElement.elementText("OutputFilePath").trim());
            context.setErrorLimits(new Integer(localElement.elementText("ErrorLimits").trim()).intValue());
            context.setSleepTime(new Integer(localElement.elementText("SleepTime").trim()).intValue());
            context.setReconnTimes(new Integer(localElement.elementText("ReconnTimes").trim()).intValue());
            String str = localElement.elementText("FetchSize");
            if (str == null){
                context.setFetchSize(1);
            }else{
                context.setFetchSize(new Integer(str.trim()).intValue());
            }
            str = localElement.elementText("DataTimeFormat");
            if (str == null){
                context.setDataTimeFormat("yyyy-MM-dd");
            }else{
                context.setDataTimeFormat(str.trim());
            }
            str = localElement.elementText("ReplaceSpecialChar");
            if (str == null){
                context.setReplaceSpecialChar(true);
            }else if ("false".equals(str.trim())){
                context.setReplaceSpecialChar(false);
            }else{
                context.setReplaceSpecialChar(true);
            }
            str = localElement.elementText("Skip");
            if (null != str){
                context.setSkip(Integer.parseInt(str));
            }else{
                context.setSkip(0);
            }
        }catch (Exception localException){
            System.out.println("Init GUT Failed!");
            throw new InitContextException(GUTExceptionConstants.GUT100200, localException);
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

    private void initUnloader() throws InitContextException{
        String str = getDbInfo().getUnloaderClassName();
        try{
            setUnloader((IUnloader) Class.forName(str).newInstance());
        }catch (Exception localException){
            getLogger().error("Init Unloader Failed! " + localException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100130, new String[]{ str }, localException);
        }
    }

    private void initConnFactory() throws InitContextException{
        FileInputStream localFileInputStream = null;
        try {
            localFileInputStream = new FileInputStream(getConfigPath() + File.separator + getConnfileName());
            Properties localProperties = new Properties();
            localProperties.load(localFileInputStream);
            ConnInfo localConnInfo = new ConnInfo();
            localConnInfo.setIpAddress(localProperties.getProperty("IP"));
            localConnInfo.setPort(localProperties.getProperty("PORT"));
            localConnInfo.setServiceName(localProperties.getProperty("SERVICENAME"));
            localConnInfo.setDbName(localProperties.getProperty("DBNAME"));
            localConnInfo.setUserID(localProperties.getProperty("USERID"));
            localConnInfo.setAutoCommit(getDbInfo().isAutoCommit());
            localConnInfo.setPassword(DesEncrypt.getDesString(localProperties.getProperty("PASSWORD")));
            localConnInfo.setDriverClassName(getDbInfo().getDriverClassName());
            localConnInfo.setUrl(GUTStringUtils.formatString(getDbInfo().getUrlPattern(),localProperties));
            localConnInfo.setReConnTimes(getContext().getReconnTimes());
            setConnFactory(ConnFactory.getConnFactory(localConnInfo, getContext().getThreadNums()));
        }catch (FileNotFoundException localFileNotFoundException){
            getLogger().error("Connection File Not Found! " + localFileNotFoundException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100410, localFileNotFoundException);
        }catch (GUTException localGUTException){
            getLogger().error("Init GUT Failed! " + localGUTException.toString());
            throw new InitContextException(localGUTException);
        }catch (Exception localException){
            getLogger().error(localException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100400, localException);
        }finally {
            if (localFileInputStream != null){
                try {
                    localFileInputStream.close();
                }catch (IOException io){
                    getLogger().warn(io.toString());
                }
            }
        }
    }

    private void initLogger() throws InitContextException{
        InputStream localInputStream = null;
        try{
            //com/iyeeku/gut/config/log4j.properties  com/iyeeku/gut/config/log4j.properties
            //GUT.class.getResourceAsStream("/com/iyeeku/gut/config/log4j.properties");//
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
            System.out.println("initLogger ==> " + localException.toString());
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

    private void initTaskList() throws InitContextException{
        String str1 = getDbInfo().getTaskClassName();
        String str2 = getDbInfo().getFileNamePattern();
        setTaskList(new ArrayList());
        try {
            int i = 0;
            int j = 0;
            String str4 = null;
            Element localElement2 = getXmlConfigDocument().getRootElement();
            boolean bool = getMode().equals("run");
            if (bool){
                clearSuccessedTaskIDs();
            }else{
                str4 = getSuccessedTaskIDs();
            }
            Element localElement3 = localElement2.element("Tasks");
            Iterator<Element> localObject = localElement3.elementIterator("Task");
            while (localObject.hasNext()){
                i++;
                Element localElement1 = localObject.next();
                if ((bool) || (isNeedToRun(i, str4))){
                    j++;
                    AbstractTask localAbstractTask = (AbstractTask)Class.forName(str1).newInstance();
                    localAbstractTask.setFileName(formatFileName(localElement1.elementText("FileName").trim(), str2).trim());
                    localAbstractTask.setOldFileName(localElement1.elementText("FileName").trim());
                    localAbstractTask.setTaskOrder(j);
                    localAbstractTask.setTaskID(i);
                    localAbstractTask.setSql(translateSQL(localElement1.elementText("SQL")).replaceAll("\n"," ").trim());
                    localAbstractTask.setGz(localElement1.elementText("GZ").trim());
                    localAbstractTask.setSupportedDataType(getDbInfo().getSupportedDataType());
                    localAbstractTask.setUnloader(getUnloader());
                    String str3 = localElement1.elementText("FetchSize");
                    if (str3 == null){
                        localAbstractTask.setFetchSize(getContext().getFetchSize());
                    }else{
                        localAbstractTask.setFetchSize(new Integer(str3.trim()).intValue());
                    }
                    str3 = localElement1.elementText("BufferSize");
                    if (str3 == null){
                        localAbstractTask.setBufferSize(10000);
                    }else{
                        localAbstractTask.setBufferSize(new Integer(str3.trim()).intValue());
                    }
                    str3 = localElement1.elementText("ExtIntegerOption1");
                    if (str3 == null){
                        localAbstractTask.setExtIntegerOption1(0);
                    }else{
                        localAbstractTask.setExtIntegerOption1(new Integer(str3.trim()).intValue());
                    }
                    str3 = localElement1.elementText("ExtIntegerOption2");
                    if (str3 == null){
                        localAbstractTask.setExtIntegerOption2(0);
                    }else{
                        localAbstractTask.setExtIntegerOption2(new Integer(str3.trim()).intValue());
                    }
                    str3 = localElement1.elementText("ExtIntegerOption3");
                    if (str3 == null){
                        localAbstractTask.setExtIntegerOption3(0);
                    }else{
                        localAbstractTask.setExtIntegerOption3(new Integer(str3.trim()).intValue());
                    }
                    str3 = localElement1.elementText("ExtIntegerOption4");
                    if (str3 == null){
                        localAbstractTask.setExtIntegerOption4(0);
                    }else{
                        localAbstractTask.setExtIntegerOption4(new Integer(str3.trim()).intValue());
                    }
                    str3 = localElement1.elementText("ExtIntegerOption5");
                    if (str3 == null){
                        localAbstractTask.setExtIntegerOption5(0);
                    }else{
                        localAbstractTask.setExtIntegerOption5(new Integer(str3.trim()).intValue());
                    }
                    str3 = localElement1.elementText("ExtStringOption1");
                    if (str3 != null){
                        localAbstractTask.setExtStringOption1(str3.trim());
                    }
                    str3 = localElement1.elementText("ExtStringOption2");
                    if (str3 != null){
                        localAbstractTask.setExtStringOption2(str3.trim());
                    }
                    str3 = localElement1.elementText("ExtStringOption3");
                    if (str3 != null){
                        localAbstractTask.setExtStringOption3(str3.trim());
                    }
                    str3 = localElement1.elementText("ExtStringOption4");
                    if (str3 != null){
                        localAbstractTask.setExtStringOption4(str3.trim());
                    }
                    str3 = localElement1.elementText("ExtStringOption5");
                    if (str3 != null){
                        localAbstractTask.setExtStringOption5(str3.trim());
                    }
                    getTaskList().add(localAbstractTask);
                }
            }
            Map localMap = new HashMap();
            int k = 0;
            for (int m = 0; m < taskList.size(); m++){
                String str5 = ((AbstractTask)taskList.get(m)).getOldFileName();
                Integer localInteger1 = (Integer)localMap.get(str5);
                localMap.put(str5, localInteger1 == null ? new Integer(1) : new Integer(localInteger1.intValue() + 1));
                Integer localInteger2 = (Integer)localMap.get(str5);
                if ((localInteger2 != null) && (localInteger2.intValue() > 1)){
                    getLogger().error("Element '<FileName>' Of Config value Can't Repeat By " + str5);
                    k++;
                }
            }
            if (k > 0){
                throw new InitContextException(GUTExceptionConstants.GUT100310, new String[]{ str1 });
            }
            if (taskList.size() == 0){
                getLogger().info("Hasn't task need to run!");
            }
            ResetTaskList(isNeedToRun(i, str4), bool);
            getUnloader().initTaskList(taskList);
        }catch (Exception localException){
            //TODO
            getLogger().error(localException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100300, localException);
        }
    }

    private boolean isNeedToRun(int paramInt,String paramString){
        if ((paramString == null) || (paramString.length() <= 0)){
            return true;
        }
        return paramString.indexOf("|" + new Integer(paramInt).toString() + "|") <= -1;
    }

    private String getSuccessedTaskIDs() throws InitContextException{
        FileInputStream localFileInputStream = null;
        String str1 = "";
        try{
            str1 = getAppPath() + File.separator + "V" + getDataDate() + ".gut";
            File localFile1 = FileUtils.makeFile(str1);
            localFileInputStream = new FileInputStream(localFile1);
            Properties localProperties = new Properties();
            localProperties.load(localFileInputStream);
            String str2 = localProperties.getProperty(getConfigFileName());
            File localFile2 = new File(getConfigPath() + File.separator + getConfigFileName());
            if (str2 != null){
                if (str2.indexOf(new Long(localFile2.lastModified()).toString()) <= -1){
                    throw new InitContextException(GUTExceptionConstants.GUT100350, new String[]{ getConfigFileName() });
                }
                return str2;
            }
            return "";
        }catch (InitContextException localInitContextException){
            getLogger().error(localInitContextException.toString());
            throw localInitContextException;
        }catch (IOException localIOException){
            getLogger().error(localIOException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100330, localIOException);
        }finally {
            if (localFileInputStream != null){
                try {
                    localFileInputStream.close();
                }catch (IOException localIOException){
                    getLogger().warn("WARNING:Close " + str1 + " failed! " + localIOException);
                }
            }
        }
    }

    private void clearSuccessedTaskIDs() throws InitContextException{
        FileInputStream localFileInputStream = null;
        FileOutputStream localFileOutputStream = null;
        String str1 = "";
        try {
            str1 = getAppPath() + File.separator + "V" + getDataDate() + ".gut";
            File localFile1 = FileUtils.makeFile(str1);
            localFileInputStream = new FileInputStream(localFile1);
            Properties localProperties = new Properties();
            localProperties.load(localFileInputStream);
            String str2 = getConfigFileName();
            localFileOutputStream = new FileOutputStream(localFile1);
            File localFile2 = new File(getConfigPath() + File.separator + getConfigFileName());
            localProperties.setProperty(str2,new Long(localFile2.lastModified()).toString());
            localProperties.store(localFileOutputStream,null);
        }catch (Exception localException){
            getLogger().error(localException.toString());
            throw new InitContextException(GUTExceptionConstants.GUT100340, localException);
        }finally {
            if (localFileInputStream != null){
                try {
                    localFileInputStream.close();
                }catch (IOException localIOException1){
                    System.out.println("WARNING:Close " + str1 + " failed! " + localIOException1.toString());
                }
            }
            if (localFileOutputStream != null){
                try {
                    localFileOutputStream.close();
                }catch (IOException localIOException2){
                    System.out.println("WARNING:Close " + str1 + " failed! " + localIOException2.toString());
                }
            }
        }
    }

    private String formatFileName(String paramString1,String paramString2) throws DataFileNameException{
        isAvilableFileName(paramString1);
        Properties localProperties = new Properties();
        localProperties.setProperty("fileName", paramString1);
        localProperties.setProperty("dataDate", getDataDate());
        return GUTStringUtils.formatString(paramString2,localProperties);
    }

    public void isAvilableFileName(String paramString) throws DataFileNameException{
        String str = "(?!((^(con)$)|^(con)//..*|(^(prn)$)|^(prn)//..*|(^(aux)$)|^(aux)//..*|(^(nul)$)|^(nul)//..*|(^(com)[1-9]$)|^(com)[1-9]//..*|(^(lpt)[1-9]$)|^(lpt)[1-9]//..*)|^//s+|.*//s$)(^[^/////////://*//?///\"//<//>//|]{1,255}$)";
        Pattern localPattern = Pattern.compile(str);
        Matcher localMatcher = localPattern.matcher(paramString);
        boolean bool = localMatcher.matches();
        if (!bool){
            throw new DataFileNameException(paramString);
        }
    }

    public static void beforeExit(){
        if (getTaskList() != null){
            int i = getTaskList().size();
            for (int j = 0; j < i; j++){
                getLogger().info(((AbstractTask)getTaskList().get(j)).runInfo());
            }
        }
        try {
            if (getConnFactory() != null){
                getConnFactory().release();
            }
        }catch (GUTException localGUTException){
            getLogger().error(localGUTException.toString());
            GUTExceptionHandler.handlerException(localGUTException);
        }
    }

    public static IUnloader getUnloader() {
        return unloader;
    }

    public static void setUnloader(IUnloader unloader) {
        GUT.unloader = unloader;
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
