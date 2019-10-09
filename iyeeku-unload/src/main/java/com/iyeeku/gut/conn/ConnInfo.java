package com.iyeeku.gut.conn;



/**
 * @ClassName ConnInfo
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/9 20:06
 * @Version 1.0
 **/
public class ConnInfo {

    private String ipAddress;
    private String port;
    private String serviceName;
    private String dbName;
    private String userID;
    private String password;
    private String driverClassName;
    private String url;
    private boolean autoCommit;
    private int reConnTimes;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public int getReConnTimes() {
        return reConnTimes;
    }

    public void setReConnTimes(int reConnTimes) {
        this.reConnTimes = reConnTimes;
    }
}
