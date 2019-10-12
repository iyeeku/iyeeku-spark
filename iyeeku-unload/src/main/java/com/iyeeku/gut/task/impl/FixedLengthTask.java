package com.iyeeku.gut.task.impl;


import com.iyeeku.gut.exception.TaskException;
import com.iyeeku.gut.main.GUT;
import com.iyeeku.gut.task.AbstractTask;
import oracle.jdbc.driver.OracleConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;
import java.util.TimeZone;

/**
 * @ClassName FixedLengthTask
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/12 18:41
 * @Version 1.0
 **/
public abstract class FixedLengthTask extends AbstractTask {

    private boolean isCompleteRead = false;
    private int rowLength;
    private Thread writeThread = null;
    private ColumnMetaData[] columnMetaDatas;
    private int doRunType = 1;
    private int queueSize = 10000;
    private int threadSleepTime = 100;
    private List recordExceptionInfos = new ArrayList();
    private int maxColumnLength = 0;
    private Connection conn = null;
    private ResultSet rs = null;
    private int bufferSize4OS;
    private byte[] blankBytes = null;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat formatter_oracle = new SimpleDateFormat("yyyyMMddHHmmss");

    public int getBufferSize4OS() {
        return this.bufferSize4OS;
    }

    public void setBufferSize4OS(int bufferSize4OS) {
        this.bufferSize4OS = bufferSize4OS;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public ResultSet getRs() {
        return this.rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public int getMaxColumnLength() {
        return this.maxColumnLength;
    }

    public void setMaxColumnLength(int maxColumnLength) {
        this.maxColumnLength = maxColumnLength;
    }

    public ColumnMetaData[] getColumnMetaDatas() {
        return this.columnMetaDatas;
    }

    public void setColumnMetaDatas(ColumnMetaData[] columnMetaDatas) {
        this.columnMetaDatas = columnMetaDatas;
    }

    public Thread getWriteThread() {
        return this.writeThread;
    }

    public void setWriteThread(Thread writeThread) {
        this.writeThread = writeThread;
    }

    public void resetBlankBytes(byte[] paramArrayOfByte){
        int size = paramArrayOfByte.length - 1;
        for (int i = 0; i < size; i++){
            paramArrayOfByte[i] = 32;
        }
    }


    @Override
    public void doRun() throws TaskException {

    }



    @Override
    public abstract AbstractTask cloneTask(AbstractTask paramAbstractTask) throws TaskException;

    private boolean isUnspportedDataType(String paramString){
        return getSupportedDataType().toUpperCase().indexOf(paramString.toUpperCase()) > -1;
    }

    public abstract ColumnMetaData getColumnMetaDataByRSMD(ColumnMetaData paramColumnMetaData) throws TaskException;

    public String getColContentByColumnMetaData(ColumnMetaData paramColumnMetaData, ResultSet paramResultSet, int paramInt) throws TaskException{
        String str = "";
        try {
            int i = paramColumnMetaData.getColumnType();
            String localObject1 = GUT.getContext().getDatabaseType().trim();
            //Timestamp localObject2;
            switch (i){
                case 93:
                    str = formatTimestamp(paramResultSet.getTimestamp(paramColumnMetaData.getOrder()),paramColumnMetaData.getFixedLength());
                    break;
                case -101:
                    str = formatTimestamp(paramResultSet.getTimestamp(paramColumnMetaData.getOrder()),paramColumnMetaData.getFixedLength());
                    break;
                case -102:
                    ((OracleConnection)this.conn).setSessionTimeZone(TimeZone.getDefault().getID());
                    str = formatTimestamp(paramResultSet.getTimestamp(paramColumnMetaData.getOrder()),paramColumnMetaData.getFixedLength());
                    break;
                case 92:
                    str = formatTimes(paramResultSet.getTime(paramColumnMetaData.getOrder()),paramColumnMetaData.getFixedLength());
                    break;
                case 91:
                    if ( (localObject1.toLowerCase() == "oracle") || (localObject1.equalsIgnoreCase("oracle")) ){
                        Timestamp localObject2 = paramResultSet.getTimestamp(paramColumnMetaData.getOrder());
                        str = formatDate(localObject2);
                    }else{
                        str = formatDate(paramResultSet.getDate(paramColumnMetaData.getOrder()));
                    }
                    break;
                case 2:
                    BigDecimal localObject2 = paramResultSet.getBigDecimal(paramColumnMetaData.getOrder());
                    if (null == localObject2){
                        str = "";
                    }else{
                        str = localObject2.toString();
                    }
                    break;
                default:
                    str = paramResultSet.getString(paramColumnMetaData.getOrder());
                    if (str == null){
                        str = "";
                    }else if ((GUT.getContext().isReplaceSpecialChar()) && (paramColumnMetaData.isNeedReplace()) && ((str.indexOf("\n") != -1) || (str.indexOf("\r") != -1))){
                        str = str.replaceAll("[\n|\r]","");
                    }
                    break;
            }
            return str;
        }catch (SQLException localSQLException){
            StringBuffer localStringBuffer = new StringBuffer();
            localStringBuffer.append("The Task" + getTaskID() + " or " + getOldFileName() + "Get column content failed, the content replaced by blank! NO." + paramColumnMetaData.getOrder() + " column for " + paramInt + " row!");
            GUT.getLogger().warn(localStringBuffer.toString());
            str = "";
            return str;
        }catch (Exception localException){
            GUT.getLogger().error(localException.getStackTrace());
            throw new TaskException(localException);
        }finally {

        }
        //return str;
    }

    public String formatTimestamp(Timestamp paramTimestamp, int paramInt){
        String str;
        if (paramTimestamp == null){
            str = "";
        }else{
            str = paramTimestamp.toString();
            if (str.indexOf(".") != -1){
                str = str.substring(0,str.indexOf("."));
            }
            str = str.replaceAll("[-\\:\\ ]","");
        }
        return str;
    }

    public String formatTimes(Time paramTime, int paramInt){
        String str;
        if (paramTime == null){
            str = "";
        }else{
            str = paramTime.toString();
            str = str.replaceAll(":","");
        }
        return str;
    }

    public String formatDate(Date paramDate){
        if (paramDate == null){
            return "";
        }
        String str = this.formatter.format(paramDate);
        int i = str.length() - 8;
        if (i < 0){
            StringBuffer localStringBuffer = new StringBuffer();
            while (i++ != 0){
                localStringBuffer.append("0");
            }
            return localStringBuffer.append(str).toString();
        }
        return str;
    }

    public String formatDate(Timestamp paramTimestamp){
        if (paramTimestamp == null){
            return "";
        }
        String str = this.formatter_oracle.format(paramTimestamp);
        int i = str.length() - 14;
        if (i < 0){
            StringBuffer localStringBuffer = new StringBuffer();
            while (i != 0){
                i++;
                localStringBuffer.append("0");
            }
            return localStringBuffer.append(str).toString();
        }
        return str;
    }


    public String formatTimestamp(Timestamp paramTimestamp){
        String str;
        if (paramTimestamp == null){
            str = "";
        }else{
            str = paramTimestamp.toString();
            if (str.indexOf(".") != -1){
                str = str.substring(0,str.indexOf("."));
            }
        }
        return str;
    }


    public void completeRead(){
        this.isCompleteRead = true;
    }


}
