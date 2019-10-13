package com.iyeeku.gut.main;

/**
 * @ClassName GUTContext
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/9 20:09
 * @Version 1.0
 **/
public class GUTContext {

    private int threadNums;
    private String outputFilePath;
    private int errorLimits;
    private String databaseType;
    private int sleepTime;
    private String dataTimeFormat;
    private int reconnTimes;
    private String deployMode;
    private int fetchSize;
    private String logMode;
    private int logSize;
    private boolean isReplaceSpecialChar;
    private long bufferMaxSize;
    private int Skip;

    public int getThreadNums() {
        return threadNums;
    }

    public void setThreadNums(int threadNums) {
        this.threadNums = threadNums;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public int getErrorLimits() {
        return errorLimits;
    }

    public void setErrorLimits(int errorLimits) {
        this.errorLimits = errorLimits;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getDataTimeFormat() {
        return dataTimeFormat;
    }

    public void setDataTimeFormat(String dataTimeFormat) {
        this.dataTimeFormat = dataTimeFormat;
    }

    public int getReconnTimes() {
        return reconnTimes;
    }

    public void setReconnTimes(int reconnTimes) {
        this.reconnTimes = reconnTimes;
    }

    public String getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(String deployMode) {
        this.deployMode = deployMode;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getLogMode() {
        return logMode;
    }

    public void setLogMode(String logMode) {
        this.logMode = logMode;
    }

    public int getLogSize() {
        return logSize;
    }

    public void setLogSize(int logSize) {
        this.logSize = logSize;
    }

    public boolean isReplaceSpecialChar() {
        return isReplaceSpecialChar;
    }

    public void setReplaceSpecialChar(boolean replaceSpecialChar) {
        isReplaceSpecialChar = replaceSpecialChar;
    }

    public long getBufferMaxSize() {
        return bufferMaxSize;
    }

    public void setBufferMaxSize(long bufferMaxSize) {
        this.bufferMaxSize = bufferMaxSize;
    }

    public int getSkip() {
        return Skip;
    }

    public void setSkip(int skip) {
        Skip = skip;
    }
}
