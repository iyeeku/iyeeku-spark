package com.iyeeku.gut.main;



/**
 * @ClassName GUTDBInfo
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/9 20:12
 * @Version 1.0
 **/
public class GUTDBInfo {

    private String unloaderClassName;
    private String taskClassName;
    private String driverClassName;
    private String urlPattern;
    private String supportedDataType;
    private String fileNamePattern;
    private String columnTypeNames4NeedScale;
    private String columnTypeNames4NotNeedPrecision;
    private boolean isAutoCommit;

    public String getUnloaderClassName() {
        return unloaderClassName;
    }

    public void setUnloaderClassName(String unloaderClassName) {
        this.unloaderClassName = unloaderClassName;
    }

    public String getTaskClassName() {
        return taskClassName;
    }

    public void setTaskClassName(String taskClassName) {
        this.taskClassName = taskClassName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getSupportedDataType() {
        return supportedDataType;
    }

    public void setSupportedDataType(String supportedDataType) {
        this.supportedDataType = supportedDataType;
    }

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    public String getColumnTypeNames4NeedScale() {
        return columnTypeNames4NeedScale;
    }

    public void setColumnTypeNames4NeedScale(String columnTypeNames4NeedScale) {
        this.columnTypeNames4NeedScale = columnTypeNames4NeedScale;
    }

    public String getColumnTypeNames4NotNeedPrecision() {
        return columnTypeNames4NotNeedPrecision;
    }

    public void setColumnTypeNames4NotNeedPrecision(String columnTypeNames4NotNeedPrecision) {
        this.columnTypeNames4NotNeedPrecision = columnTypeNames4NotNeedPrecision;
    }

    public boolean isAutoCommit() {
        return isAutoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        isAutoCommit = autoCommit;
    }
}
