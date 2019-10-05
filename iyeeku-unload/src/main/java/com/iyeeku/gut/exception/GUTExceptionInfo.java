package com.iyeeku.gut.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName GUTExceptionInfo
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 10:17
 * @Version 1.0
 **/
public class GUTExceptionInfo {

    private String errorCode;
    private int errorLevel;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(int errorLevel) {
        this.errorLevel = errorLevel;
    }

    public GUTExceptionInfo(String errorCode,int errorLevel){
        this.errorCode = errorCode;
        this.errorLevel = errorLevel;
    }

}
