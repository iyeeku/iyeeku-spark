package com.iyeeku.gut.task;

import com.iyeeku.gut.exception.*;
import com.iyeeku.gut.main.GUT;
import com.iyeeku.gut.unloader.IUnloader;
import com.iyeeku.gut.util.FileUtils;
import com.iyeeku.gut.util.GUTConstants;


import java.io.*;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * @ClassName AbstractTask
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 11:05
 * @Version 1.0
 **/
public abstract class AbstractTask extends Thread implements Comparable{

    private int taskID;
    private String fileName;
    private String sql;
    private boolean gz = false;
    private int fetchSize;
    private int bufferSize;
    private int taskOrder;
    private IUnloader unloader;
    private String supportedDataType;
    private long rowCount;
    private int columnCount;
    private int times = 1;
    private int status = 0;
    private Timestamp startTimeStamp;
    private Timestamp endTimeStamp;
    private String extStringOption1;
    private String extStringOption2;
    private String extStringOption3;
    private String extStringOption4;
    private String extStringOption5;
    private int extIntegerOption1;
    private int extIntegerOption2;
    private int extIntegerOption3;
    private int extIntegerOption4;
    private int extIntegerOption5;
    private int useTime;
    private static byte[] lock = new byte[0];
    private String oldFileName;

    public String getExtStringOption1() {
        return extStringOption1;
    }

    public void setExtStringOption1(String extStringOption1) {
        this.extStringOption1 = extStringOption1;
    }

    public String getExtStringOption2() {
        return extStringOption2;
    }

    public void setExtStringOption2(String extStringOption2) {
        this.extStringOption2 = extStringOption2;
    }

    public String getExtStringOption3() {
        return extStringOption3;
    }

    public void setExtStringOption3(String extStringOption3) {
        this.extStringOption3 = extStringOption3;
    }

    public String getExtStringOption4() {
        return extStringOption4;
    }

    public void setExtStringOption4(String extStringOption4) {
        this.extStringOption4 = extStringOption4;
    }

    public String getExtStringOption5() {
        return extStringOption5;
    }

    public void setExtStringOption5(String extStringOption5) {
        this.extStringOption5 = extStringOption5;
    }

    public int getExtIntegerOption1() {
        return extIntegerOption1;
    }

    public void setExtIntegerOption1(int extIntegerOption1) {
        this.extIntegerOption1 = extIntegerOption1;
    }

    public int getExtIntegerOption2() {
        return extIntegerOption2;
    }

    public void setExtIntegerOption2(int extIntegerOption2) {
        this.extIntegerOption2 = extIntegerOption2;
    }

    public int getExtIntegerOption3() {
        return extIntegerOption3;
    }

    public void setExtIntegerOption3(int extIntegerOption3) {
        this.extIntegerOption3 = extIntegerOption3;
    }

    public int getExtIntegerOption4() {
        return extIntegerOption4;
    }

    public void setExtIntegerOption4(int extIntegerOption4) {
        this.extIntegerOption4 = extIntegerOption4;
    }

    public int getExtIntegerOption5() {
        return extIntegerOption5;
    }

    public void setExtIntegerOption5(int extIntegerOption5) {
        this.extIntegerOption5 = extIntegerOption5;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public Timestamp getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(Timestamp startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public Timestamp getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(Timestamp endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }


    public int getStatus() {
        return status;
    }

    public String getStatusString(){
        switch (getStatus()){
            case -1:
                return "Failed";
            case 2:
                return "Success";
            case 1:
                return "Running";
            case 0:
                return "Waiting";
        }
        return "Unknown";
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public long getRowCount() {
        return rowCount;
    }

    public void setRowCount(long rowCount) {
        this.rowCount = rowCount;
    }

    public String getSupportedDataType() {
        return supportedDataType;
    }

    public void setSupportedDataType(String supportedDataType) {
        this.supportedDataType = supportedDataType;
    }



    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public long getUseMillionSeconds(){
        return getEndTimeStamp().getTime() - getStartTimeStamp().getTime();
    }

    public String runInfo(){
        StringBuffer localStringBuffer = new StringBuffer();
        String str1 = "Not Start";
        String str2 = "Not End";
        String str3 = "Not used time";
        if (getStartTimeStamp() != null){
            str1 = "Started at " + getStartTimeStamp().toString();
        }
        if (getEndTimeStamp() != null){
            str2 = "End at " + getEndTimeStamp().toString();
        }
        if ((getEndTimeStamp() != null) && (getStartTimeStamp() != null)){
            str3 = "Used " + new Long(getUseMillionSeconds()).toString() + " ms!";
        }
        String str4 = "Successed!";
        if (getStatus() != 2){
            str4 = "Failed!";
        }
        localStringBuffer.append("NO." + getTaskID() + " task or " + getOldFileName() + " has runned " + getTimes() + " times!" + str4 + "!" + str1 + "," + str2 + "," + str3);
        return localStringBuffer.toString();
    }

    public String getDataFileFullName(){
        String str;
        if (isGz()){
            str = ".dat.gz";
        }else{
            str = ".dat";
        }
        return GUT.getContext().getOutputFilePath() + File.separator + getFileName() + str;
    }

    public File getDataFile(){
        return new File(getDataFileFullName());
    }

    public String getDataFileFullNameNoPath(){
        String str;
        if (isGz()){
            str = ".dat.gz";
        }else{
            str = ".dat";
        }
        return getFileName() + str;
    }

    public String getFlagFileFullName(){
        return GUT.getContext().getOutputFilePath() + File.separator + getFileName() + ".flg";
    }

    @Override
    public void run() {
        try {
            if (getTimes() > 1) {
                GUT.getLogger().info("The NO." + getTaskID() + " task or " + getOldFileName() + " will restart after " + GUT.getContext().getSleepTime() + " ms !");
                sleep(GUT.getContext().getSleepTime());
            }
            beforeTask();
            doRun();
            if (getStatus() == -1){
                GUT.getLogger().info("The NO." + getTaskID() + " task or " + getOldFileName() + " end for " + getTimes() + " time(s)!Failed!");
                if (getTimes() < GUT.getContext().getErrorLimits()){
                    AbstractTask localAbstractTask = cloneTask(this);
                    localAbstractTask.setTimes(getTimes() + 1);
                    localAbstractTask.setStatus(0);
                    localAbstractTask.start();
                    localAbstractTask.join();
                    setStatus(localAbstractTask.getStatus());
                    setEndTimeStamp(localAbstractTask.getEndTimeStamp());
                    setTimes(localAbstractTask.getTimes());
                } else {
                    afterTask();
                }
            } else {
              setStatus(2);
              afterTask();
            }
        }catch (InterruptedException localInterruptedException){
            GUT.getLogger().error(localInterruptedException.toString());
            TaskException localTaskException2 = new TaskException(GUTExceptionConstants.GUT400400, localInterruptedException);
            GUTExceptionHandler.handlerException(localTaskException2);
            setStatus(-1);
            afterTask();
        }catch (TaskException localTaskException){
            GUT.getLogger().error(localTaskException.toString());
            GUTExceptionHandler.handlerException(localTaskException);
            setStatus(-1);
            afterTask();
        }
    }

    public abstract void doRun() throws TaskException;

    public abstract AbstractTask cloneTask(AbstractTask paramAbstractTask) throws TaskException;

    private void closeFileOutputStream(FileOutputStream paramFileOutputStream){
        if (null != paramFileOutputStream){
            try {
                paramFileOutputStream.flush();
                paramFileOutputStream.close();
            }catch (IOException localIOException){
                GUT.getLogger().error(localIOException.getMessage(),localIOException);
            }
        }
    }

    public void writeMarkFile(String paramString){
        FileOutputStream localFileOutputStream = null;
        try {
            File localFile = new File(GUTConstants.MARKFILE_PATH);
            localFileOutputStream = new FileOutputStream(localFile, true);
            localFileOutputStream.write(paramString.getBytes());
            localFileOutputStream.write(new byte[]{10});
            closeFileOutputStream(localFileOutputStream);
        }catch (FileNotFoundException localFileNotFoundException){
            GUT.getLogger().error(localFileNotFoundException.getMessage(),localFileNotFoundException);
            closeFileOutputStream(localFileOutputStream);
        }catch (IOException localIOException){
            GUT.getLogger().error(localIOException.getMessage(),localIOException);
            closeFileOutputStream(localFileOutputStream);
        }finally {
            closeFileOutputStream(localFileOutputStream);
        }
    }

    public void afterTask(){
        try {
            GUT.getLogger().info("The NO." + getTaskID() + " task or " + getOldFileName() + " end for " + getTimes() + " time(s) !" + getStatusString() + "!");
            setEndTimeStamp(new Timestamp(System.currentTimeMillis()));
            GUT.getLogger().info("The NO." + getTaskID() + " task or " + getOldFileName() + " run for " + getTimes() + " time(s) used " + getUseMillionSeconds() + " ms!");
            if (getStatus() == 2) {
                synchronized (lock) {
                    writeGUTLogFile(this);
                    String localObject1 = getFileName().substring(0,getFileName().indexOf(".")) + "=" + getUseMillionSeconds();
                    writeMarkFile(localObject1);
                }
            }else{
                File localFile = new File(getDataFileFullName());
                if (localFile != null){
                    localFile.delete();
                }
            }
        }catch (UnloaderException localUnloaderException){
            Object localObject1 = new TaskException(GUTExceptionConstants.GUT400520, localUnloaderException);
            GUTExceptionHandler.handlerException((GUTException) localObject1);
        }catch (Exception localException){
            Object localObject1 = new TaskException(GUTExceptionConstants.GUT400510, localException);
            GUTExceptionHandler.handlerException((GUTException) localObject1);
        }
    }


    private void writeGUTLogFile(AbstractTask paramAbstractTask) throws Exception{
        String str1 = GUT.getAppPath() + File.separator + "V" + GUT.getDataDate() + ".gut";
        FileOutputStream localFileOutputStream = null;
        FileInputStream localFileInputStream = null;
        try {
            File localFile1 = FileUtils.makeFile(str1);
            localFileInputStream = new FileInputStream(localFile1);
            Properties localProperties = new Properties();
            localProperties.load(localFileInputStream);
            String str2 = GUT.getConfigFileName();
            String str3 = localProperties.getProperty(str2);
            StringBuffer localStringBuffer = new StringBuffer();
            if ((str3 == null) || (str3.length() <= 0)) {
                File localFile2 = new File(GUT.getConfigPath() + File.separator + GUT.getConfigFileName());
                localStringBuffer.append(localFile2.lastModified());
                localStringBuffer.append(packageTaskID(paramAbstractTask.getTaskID()));
            } else {
                if (str3.indexOf(packageTaskID(paramAbstractTask.getTaskID())) > -1) {
                    return;
                }
                localStringBuffer.append(str3);
                localStringBuffer.append(packageTaskID(paramAbstractTask.getTaskID()));
            }
            localFileOutputStream = new FileOutputStream(localFile1);
            localProperties.setProperty(str2, localStringBuffer.toString());
            localProperties.store(localFileOutputStream, null);
            if (null != localFileOutputStream) {
                localFileOutputStream.flush();
                localFileOutputStream.close();
            }
            if (null != localFileInputStream) {
                localFileInputStream.close();
            }
        }catch (Exception localException){
            if (null != localFileOutputStream){
                localFileOutputStream.flush();
                localFileOutputStream.close();
            }
            if (null != localFileInputStream) {
                localFileInputStream.close();
            }
        }finally {
            if (null != localFileOutputStream){
                localFileOutputStream.flush();
                localFileOutputStream.close();
            }
            if (null != localFileInputStream) {
                localFileInputStream.close();
            }
        }
    }

    private String packageTaskID(int paramInt){
        return "|" + new Integer(paramInt).toString() + "|";
    }

    public void beforeTask() throws TaskException{
        try {
            setStartTimeStamp(new Timestamp(System.currentTimeMillis()));
            GUT.getLogger().info("The NO." + getTaskID() + " task or " + getOldFileName() + " start for " + getTimes() + " time(s) !");
            this.unloader.beforeTask(this);
        }catch (Exception localException){
            TaskException localTaskException = new TaskException(GUTExceptionConstants.GUT400500, localException);
            throw localTaskException;
        }
    }

    public int compareTo(Object paramObject){
        AbstractTask localAbstractTask = (AbstractTask) paramObject;
        return getUseTime() - localAbstractTask.getUseTime();
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isGz() {
        return this.gz;
    }

    public void setGz(String paramString) {
        if ((paramString != null) && ("true".equals(paramString))){
            this.gz = true;
        }
    }

    public IUnloader getUnloader() {
        return this.unloader;
    }

    public void setUnloader(IUnloader paramIUnloader) {
        this.unloader = paramIUnloader;
    }

    public int getTaskOrder() {
        return taskOrder;
    }

    public void setTaskOrder(int taskOrder) {
        this.taskOrder = taskOrder;
    }

    public int getUseTime() {
        return useTime;
    }

    public void setUseTime(int useTime) {
        this.useTime = useTime;
    }

    public static byte[] getLock() {
        return lock;
    }

    public static void setLock(byte[] lock) {
        AbstractTask.lock = lock;
    }

    public String getOldFileName() {
        return oldFileName;
    }

    public void setOldFileName(String oldFileName) {
        this.oldFileName = oldFileName;
    }
}
