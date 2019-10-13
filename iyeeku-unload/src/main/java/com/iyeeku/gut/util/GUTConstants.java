package com.iyeeku.gut.util;

import java.io.File;

/**
 * @ClassName GUTConstants
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/9 19:44
 * @Version 1.0
 **/
public class GUTConstants {

    public static final String XML_RUN_ContextParams = "ContextParams";
    public static final String XML_RUN_Task = "Task";
    public static final String XML_RUN_Skip = "Skip";
    public static final String XML_RUN_Tasks = "Tasks";
    public static final String XML_RUN_ThreadNum = "ThreadNum";
    public static final String XML_RUN_OutputFilePath = "";
    public static final String XML_RUN_ErrorLimits = "";
    public static final String XML_RUN_DataBaseType = "";
    public static final String XML_RUN_SleepTime = "";
    public static final String XML_RUN_DataTimeFormat = "";
    public static final String XML_RUN_ReplaceSpecialChar = "";
    public static final String XML_RUN_ReconnTimes = "";
    public static final String XML_RUN_DeployMode = "";
    public static final String XML_RUN_FetchSize = "";
    public static final String XML_RUN_LogMode = "";
    public static final String XML_RUN_LogSize = "";
    public static final String XML_Task_Sql = "SQL";
    public static final String XML_Task_FileName = "FileName";
    public static final String XML_Task_GZ = "GZ";
    public static final String XML_Task_FetchSize = "";
    public static final String XML_Task_BufferSize = "";
    public static final String XML_Task_Ext_String_Option1 = "ExtStringOption1";
    public static final String XML_Task_Ext_String_Option2 = "ExtStringOption2";
    public static final String XML_Task_Ext_String_Option3 = "ExtStringOption3";
    public static final String XML_Task_Ext_String_Option4 = "ExtStringOption4";
    public static final String XML_Task_Ext_String_Option5 = "ExtStringOption5";
    public static final String XML_Task_Ext_Integer_Option1 = "ExtIntegerOption1";
    public static final String XML_Task_Ext_Integer_Option2 = "ExtIntegerOption2";
    public static final String XML_Task_Ext_Integer_Option3 = "ExtIntegerOption3";
    public static final String XML_Task_Ext_Integer_Option4 = "ExtIntegerOption4";
    public static final String XML_Task_Ext_Integer_Option5 = "ExtIntegerOption5";
    public static final String DataBaseType_DB2 = "DB2";
    public static final String DataBaseType_Oracle = "";
    public static final String DataBaseType_Informix = "";
    public static final String DataBaseType_MySql = "";
    public static final String DataBaseType_SqlServer = "";
    public static final String Mode_Run = "run";
    public static final String Mode_Continue = "";
    public static final String Command_Option_Config = "";
    public static final String Command_Option_Conn = "";
    public static final String Command_Option_Mode = "";
    public static final String Command_Option_Date = "";
    public static final String Command_Option_Version = "";
    public static final String Command_Option_Help = "";


    public static final String MARKFILE_PATH = "";

    public static final String XML_APPINF_Version = "Version";
    public static final String XML_APPINF_DataFile = "DataFile";
    public static final String XML_APPINF_TaskThread = "TaskThread";
    public static final String XML_APPINF_BufferSizeRatio = "BufferSizeRatio";
    public static final String XML_APPINF_ColumnType = "ColumnType";
    public static final String XML_APPINF_NamePattern = "NamePattern";
    public static final String XML_APPINF_NeedScale = "NeedScale";
    public static final String XML_APPINF_NotNeedPrecision = "NotNeedPrecision";
    public static final String XML_APPINF_DBInfos = "DBInfos";
    public static final String XML_APPINF_DBInfo = "DBInfo";
    public static final String XML_APPINF_DBInfo_Type = "type";
    public static final String XML_APPINF_Unloader = "Unloader";
    public static final String XML_APPINF_Task = "Task";
    public static final String XML_APPINF_AutoCommit = "AutoCommit";
    public static final String XML_APPINF_DriverClassName = "DriverClassName";
    public static final String XML_APPINF_UrlPattern = "UrlPattern";
    public static final String XML_APPINF_SupportedDataType = "SupportedDataType";
    public static final String Properties_IP = "IP";
    public static final String Properties_PORT = "PORT";
    public static final String Properties_SERVICENAME = "SERVICENAME";
    public static final String Properties_DBNAME = "DBNAME";
    public static final String Properties_USERID = "USERID";
    public static final String Properties_PASSWORD = "PASSWORD";
    public static final String GZ_Yes = "true";
    public static final String GZ_No = "false";
    public static final String LogMode_Loop = "loop";
    public static final String LogMode_Archive = "archive";
    public static final String DeployMode_Debug = "Debug";
    public static final String DeployMode_Product = "Product";
    public static final int Task_Status_Wait = 0;
    public static final int Task_Status_Running = 1;
    public static final int Task_Status_Success = 2;
    public static final int Task_Status_Failed = -1;
    public static volatile int succ_num = 0;
    public static final String FF_FILENAME = "FILENAME=";
    public static final String FF_FILESIZE = "FILESIZE=";
    public static final String FF_ROWCOUNT = "ROWCOUNT=";
    public static final String FF_CREATEDATETIME = "CREATEDATETIME=";
    public static final String FF_SQL = "SQL=";
    public static final String FF_ROWLENGTH = "ROWLENGTH=";
    public static final String FF_COLUMNCOUNT = "COLUMNCOUNT=";
    public static final String FF_COLUMNDESCRIPTION = "COLUMNDESCRIPTION=";
    public static final String FF_COLUMNEXCEPTION = "WARNING:\n";
    public static final int FixedLengthTask_TYPE1 = 1;
    public static final int FixedLengthTask_TYPE2 = 2;
    public static final int FixedLengthTask_TYPE3 = 3;
    public static final int Default_QueueSize = 10000;
    public static final String XML_XSD_PATH = FileUtils.getSystemPath() + File.separator + "schema" + File.separator + "GUTConfigSchema.xsd";
    public static final int Default_BufferSize = 10000;
    public static final int Default_FetchSize = 1;
    public static final int Default_LogSize = 10485760;
    public static final String Default_DataTimeFormat = "yyyy-MM-dd";
    public static final String Default_LogFileName = "GUTInfo.log";
    public static final String Default_ErrorLogFileName = "GUTError.log";
    public static final String Default_WarnLogFileName = "GUTWarn.log";
    public static final String Boolean_String_True = "true";
    public static final String Boolean_String_False = "false";
    public static final int Default_ThreadSleepTime = 100;


}
