package com.iyeeku.gut.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName UnloaderException
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 10:51
 * @Version 1.0
 **/
public class UnloaderException extends GUTException {

    public UnloaderException(){

    }

    public UnloaderException(String paramString){
        super(paramString);
    }

    public UnloaderException(GUTException paramGUTException){
        super(paramGUTException);
    }

    public UnloaderException(GUTExceptionInfo paramGUTExceptionInfo, String paramString, Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramString,paramThrowable);
    }

    public UnloaderException(GUTExceptionInfo paramGUTExceptionInfo, String paramString){
        super(paramGUTExceptionInfo,paramString);
    }

    public UnloaderException(GUTExceptionInfo paramGUTExceptionInfo, String[] paramArrayOfString, Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramArrayOfString,paramThrowable);
    }

    public UnloaderException(GUTExceptionInfo paramGUTExceptionInfo, String[] paramArrayOfString){
        super(paramGUTExceptionInfo,paramArrayOfString);
    }

    public UnloaderException(GUTExceptionInfo paramGUTExceptionInfo, Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramThrowable);
    }

    public UnloaderException(GUTExceptionInfo paramGUTExceptionInfo){
        super(paramGUTExceptionInfo);
    }

    public UnloaderException(String paramString, Throwable paramThrowable){
        super(paramString,paramThrowable);
    }

    public UnloaderException(Throwable paramThrowable){
        super(paramThrowable);
    }

}
