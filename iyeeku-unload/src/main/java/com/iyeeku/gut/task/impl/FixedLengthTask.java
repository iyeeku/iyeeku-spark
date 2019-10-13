package com.iyeeku.gut.task.impl;


import com.iyeeku.gut.exception.*;
import com.iyeeku.gut.main.GUT;
import com.iyeeku.gut.task.AbstractTask;
import com.iyeeku.gut.util.DataQueue;
import com.iyeeku.gut.util.GUTConstants;
import oracle.jdbc.driver.OracleConnection;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

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
    private int queueSize = GUTConstants.Default_QueueSize;
    private int threadSleepTime = GUTConstants.Default_ThreadSleepTime;
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
        return this.conn;
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

    public Thread getWriteThread() {
        return this.writeThread;
    }

    public void setWriteThread(Thread writeThread) {
        this.writeThread = writeThread;
    }

    public void setColumnMetaDatas(ColumnMetaData[] columnMetaDatas) {
        this.columnMetaDatas = columnMetaDatas;
    }

    public void resetBlankBytes(byte[] paramArrayOfByte){
        int size = paramArrayOfByte.length - 1;
        for (int i = 0; i < size; i++){
            paramArrayOfByte[i] = 32;
        }
    }

    public byte[] getRowTemplateByRowLength(int paramInt){
        byte[] arrayOfByte = new byte[paramInt + 1];
        for (int i = 0 ; i < paramInt ; i++){
            arrayOfByte[i] = 32;
        }
        arrayOfByte[paramInt] = 10;
        return arrayOfByte;
    }

    public void replaceBytes(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2){
        int i = paramArrayOfByte2.length;
        int j = paramInt2;
        if (i > 0){
            if (i < paramInt2){
                j = i;
            }
            for (int k = 0; k < j; k++){
                if (paramArrayOfByte2[k] != 10){
                    paramArrayOfByte1[paramInt1] = paramArrayOfByte2[k];
                }
                paramInt1++;
            }
        }
    }

    private void doRun4Type1(){
        int i = 0;
        OutputStream localOutputStream = null;

        try {
            ColumnMetaData[] arrayOfColumnMetaData = getColumnMetaDatas();
            int j = getColumnCount();
            int k = 0;
            int m = 0;
            if (isGz()) {
                localOutputStream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(getDataFileFullName()), getBufferSize4OS()));
            }else{
                localOutputStream = new BufferedOutputStream(new FileOutputStream(getDataFileFullName()), getBufferSize4OS());
            }
            while (this.rs.next()){
                i++;
                for (int n = 1; n < j + 1; n++){
                    ColumnMetaData localColumnMetaData = arrayOfColumnMetaData[(n -1)];
                    String str = getColContentByColumnMetaData(localColumnMetaData, this.rs, i);
                    if ((str == null) || ("".equals(str))){
                        localOutputStream.write(this.blankBytes, 0, localColumnMetaData.getFixedLength());
                    }else{
                        byte[] arrayOfByte = str.getBytes("GBK");
                        k = arrayOfByte.length;
                        m = localColumnMetaData.getFixedLength() - k;
                        if (m > 0){
                            localOutputStream.write(arrayOfByte);
                            localOutputStream.write(this.blankBytes, 0, m);
                        }else{
                            localOutputStream.write(arrayOfByte, 0, localColumnMetaData.getFixedLength());
                        }
                    }
                }
                localOutputStream.write(10);
                if ((GUT.isDebugMode()) && (i % 200000 == 1)){
                    GUT.getLogger().info("NO." + getTaskID() + " task or " + getOldFileName() + " unload " + i + " rows! ");
                }
            }
            GUTConstants.succ_num += 1;
            setStatus(GUTConstants.Task_Status_Success);
            setRowCount(i);
        }catch (TaskException localTaskException){
            GUT.getLogger().info("Export Row Count: " + i);
            GUTExceptionHandler.handlerException(localTaskException);
            setStatus(GUTConstants.Task_Status_Failed);
        }catch (Exception localException){
            GUTExceptionHandler.handlerException(new TaskException(GUTExceptionConstants.GUT400900, localException));
            setStatus(GUTConstants.Task_Status_Failed);
        }finally {
            completeRead();
            if (localOutputStream != null){
                try {
                    localOutputStream.flush();
                    localOutputStream.close();
                }catch (IOException localException){
                    GUT.getLogger().warn("WARNING:close NO." + getTaskID() + " task's or " + getOldFileName() + " data file outputstream failed!");
                }
            }
        }
    }

    @Override
    public void doRun() throws TaskException {
        BufferedWriter localBufferedWriter = null;
        Thread localThread = null;
        try {

            if (GUT.isDebugMode()) {
                GUT.getLogger().info("NO." + getTaskID() + " task or " + getOldFileName() + " Run Type: " + getDoRunType());
                GUT.getLogger().info("NO." + getTaskID() + " task or " + getOldFileName() + " Queue Size: " + getQueueSize());
                GUT.getLogger().info("NO." + getTaskID() + " task or " + getOldFileName() + " Sleep Time: " + getThreadSleepTime());
            }
            setStatus(GUTConstants.Task_Status_Running);
            this.conn = GUT.getConnFactory().getConnection();
            try {
                this.rs = this.conn.createStatement().executeQuery(getSql());
            }catch (SQLException localSQLException){
                throw new SQLConfigException(localSQLException);
            }
            this.rs.setFetchSize(getFetchSize() * 100);
            ResultSetMetaData localResultSetMetaData = this.rs.getMetaData();
            initColumnMetaDatasByRSMD(localResultSetMetaData);
            int i = getMaxColumnLength();
            if (i <= 0){
                i = 10;
            }
            this.blankBytes = new byte[i];
            for (int j = 0; j < i; j++){
                this.blankBytes[j] = 32;
            }
            int j = getBufferSize() * getRowLength();
            if ((j <= 0) || (j > GUT.getContext().getBufferMaxSize())){
                GUT.getLogger().warn("NO." + getTaskID() + " task's or " + getOldFileName() + " BufferSize configuration is unsuitable , the BufferSize will be set to " + GUT.getContext().getBufferMaxSize() + "(bytes)!");
                j = (int) GUT.getContext().getBufferMaxSize();
            }
            setBufferSize4OS(j);
            switch (getDoRunType()){
                case 2:
                    DataQueue localDataQueue = new DataQueue(getQueueSize());
                    localThread = new Thread(new ReadData(localDataQueue, this));
                    this.writeThread = new Thread(new WriteData(localDataQueue, this));
                    localThread.start();
                    break;
                default:
                    doRun4Type1();
            }
            if ((localThread != null) && (this.writeThread != null)){
                localThread.join();
                this.writeThread.join();
            }
            if (this.rs != null){
                try {
                    this.rs.close();
                }catch (SQLException localSQLException){
                    GUT.getLogger().warn("WARNING:close NO." + getTaskID() + " task's or " + getOldFileName() + " ResultSet failed!");
                }
            }
            if (this.conn != null){
                GUT.getConnFactory().freeConnection(this.conn);
            }
            if (getStatus() == GUTConstants.Task_Status_Success) {
                try{
                    localBufferedWriter = new BufferedWriter(new FileWriter(getFlagFileFullName()));
                    localBufferedWriter.write(toString());
                    localBufferedWriter.flush();
                    localBufferedWriter.close();
                }catch (IOException localIOException){
                    throw new TaskException(GUTExceptionConstants.GUT400310,localIOException);
                }
            }
        }catch (SQLConfigException localSQLConfigException){
            GUTExceptionHandler.handlerException(new TaskException(GUTExceptionConstants.GUT400100, new String[]{ getSql().replaceAll("\n","") }, localSQLConfigException ));
            setStatus(GUTConstants.Task_Status_Failed);
            if (this.rs != null){
                try {
                    this.rs.close();
                }catch (SQLException localSQLException){
                    GUT.getLogger().warn("WARNING:close NO." + getTaskID() + " task's or " + getOldFileName() + " ResultSet failed!");
                }
            }
            if (this.conn != null){
                GUT.getConnFactory().freeConnection(this.conn);
            }
            if (GUTConstants.Task_Status_Success == getStatus()) {
                try{
                    localBufferedWriter = new BufferedWriter(new FileWriter(getFlagFileFullName()));
                    localBufferedWriter.write(toString());
                    localBufferedWriter.flush();
                    localBufferedWriter.close();
                }catch (Exception localIOException2){
                    throw new TaskException(GUTExceptionConstants.GUT400310,localIOException2);
                }
            }
        } catch (InterruptedException localInterruptedException){
            throw new TaskException(GUTExceptionConstants.GUT400430, localInterruptedException);
        } catch (Exception localException){
            GUTExceptionHandler.handlerException(new TaskException(GUTExceptionConstants.GUT400900, localException));
            setStatus(GUTConstants.Task_Status_Failed);
            closeRsAndConnAndWriteFlag();
        }finally {
            closeRsAndConnAndWriteFlag();
        }
    }

    private void closeRsAndConnAndWriteFlag() throws TaskException{
        if (this.rs != null){
            try {
                this.rs.close();
            }catch (SQLException localSQLException){
                GUT.getLogger().warn("WARNING:close NO." + getTaskID() + " task's or " + getOldFileName() + " ResultSet failed!");
            }
        }
        if (this.conn != null){
            GUT.getConnFactory().freeConnection(this.conn);
        }
        if (GUTConstants.Task_Status_Success == getStatus()) {
            try{
                BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(getFlagFileFullName()));
                localBufferedWriter.write(toString());
                localBufferedWriter.flush();
                localBufferedWriter.close();
            }catch (Exception localIOException2){
                throw new TaskException(GUTExceptionConstants.GUT400310,localIOException2);
            }
        }
    }

    @Override
    public abstract AbstractTask cloneTask(AbstractTask paramAbstractTask) throws TaskException;

    public void initColumnMetaDatasByRSMD(ResultSetMetaData paramResultSetMetaData)
        throws UnsupportedDataTypeException, TaskException
    {
        try {
            int i = paramResultSetMetaData.getColumnCount();
            int j = 0;
            ArrayList<ColumnMetaData> localArrayList = new ArrayList();
            for (int k = 1; k < i + 1; k++){
                String str = paramResultSetMetaData.getColumnTypeName(k);
                if (!isUnspportedDataType(str)){
                    throw new UnsupportedDataTypeException(str);
                }
                ColumnMetaData localColumnMetaData = new ColumnMetaData(paramResultSetMetaData, k);
                if ((localColumnMetaData.getColumnDisplaySize() != 0) || (localColumnMetaData.getPrecision() != 0)){
                    localColumnMetaData.setOffsetA(j);
                    localArrayList.add(getColumnMetaDataByRSMD(localColumnMetaData));
                    j += localColumnMetaData.getFixedLength();
                    if (localColumnMetaData.getFixedLength() > getMaxColumnLength()){
                        setMaxColumnLength(localColumnMetaData.getFixedLength());
                    }
                }
            }
            ColumnMetaData[] arrayOfColumnMetaData = localArrayList.toArray(new ColumnMetaData[localArrayList.size()]);
            if (GUT.isDebugMode()){
                GUT.getLogger().info("NO." + getTaskID() + " task's or " + getOldFileName() + " column metadatas:");
                for (int m = 0 ; m < arrayOfColumnMetaData.length; m++){
                    GUT.getLogger().info(arrayOfColumnMetaData[m].toString());
                }
            }
            setColumnMetaDatas(arrayOfColumnMetaData);
            setColumnCount(arrayOfColumnMetaData.length);
            setRowLength(j);
        }catch (UnsupportedDataTypeException localUnsupportedDataTypeException){
            throw localUnsupportedDataTypeException;
        } catch (Exception localException){
            throw new TaskException(GUTExceptionConstants.GUT400230, localException);
        }
    }

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

    public String toString(){
        if (this == null){
            return "";
        }
        File localFile = getDataFile();
        Timestamp localTimestamp = new Timestamp(localFile.lastModified());
        String str = formatTimestamp(localTimestamp);
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(getFileName() + ".dat" + " " + (getRowLength() + 1) * getRowCount() + " " + getRowCount() + " " + str);
        localStringBuffer.append("\n\n");
        localStringBuffer.append(GUTConstants.FF_FILENAME + getDataFileFullNameNoPath());
        localStringBuffer.append("\n\n");
        //TODO
        localStringBuffer.append(GUTConstants.FF_FILESIZE + getFileName());
        localStringBuffer.append("\n\n");
        localStringBuffer.append(GUTConstants.FF_ROWCOUNT + getRowCount());
        localStringBuffer.append("\n\n");
        localStringBuffer.append(GUTConstants.FF_CREATEDATETIME + str);
        localStringBuffer.append("\n\n");
        localStringBuffer.append(GUTConstants.FF_SQL + getSql().trim());
        localStringBuffer.append("\n\n");
        localStringBuffer.append(GUTConstants.FF_ROWLENGTH + getRowLength());
        localStringBuffer.append("\n\n");
        localStringBuffer.append(GUTConstants.FF_COLUMNCOUNT + getColumnCount());
        localStringBuffer.append("\n\n");
        localStringBuffer.append(GUTConstants.FF_COLUMNDESCRIPTION + "\n");
        if (getColumnCount() > 0){
            int i = getColumnCount();
            for (int j = 0; j < i; j++){
                ColumnMetaData localColumnMetaData = getColumnMetaDatas()[j];
                localStringBuffer.append(localColumnMetaData.toString());
            }
        }
        localStringBuffer.append("\n\n");
        return localStringBuffer.toString();
    }

    public int getRowLength() {
        return this.rowLength;
    }

    public void setRowLength(int rowLength) {
        this.rowLength = rowLength;
    }

    public void completeRead(){
        this.isCompleteRead = true;
    }

    public boolean isCompleteRead() {
        return this.isCompleteRead;
    }

    public int getDoRunType() {
        if (getExtIntegerOption1() != 0) {
            return getExtIntegerOption1();
        }
        return this.doRunType;
    }

    public void setDoRunType(int doRunType) {
        this.doRunType = doRunType;
    }

    public int getQueueSize() {
        if (getExtIntegerOption2() > 0){
            return getExtIntegerOption2();
        }
        return this.queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getThreadSleepTime() {
        if (getExtIntegerOption3() != 0){
            return getExtIntegerOption3();
        }
        return this.threadSleepTime;
    }

    public void setThreadSleepTime(int threadSleepTime) {
        this.threadSleepTime = threadSleepTime;
    }

    public List getRecordExceptionInfos() {
        return this.recordExceptionInfos;
    }

    public void setRecordExceptionInfos(List recordExceptionInfos) {
        this.recordExceptionInfos = recordExceptionInfos;
    }

    public byte[] getBlankBytes() {
        return this.blankBytes;
    }

    public void setBlankBytes(byte[] blankBytes) {
        this.blankBytes = blankBytes;
    }


    public void setCompleteRead(boolean completeRead) {
        isCompleteRead = completeRead;
    }

}
