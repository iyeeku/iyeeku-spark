package com.iyeeku.gut.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName InitContextException
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 10:32
 * @Version 1.0
 **/
public class InitContextException extends GUTException {

    public InitContextException(){

    }

    public InitContextException(String paramString){
        super(paramString);
    }

    public InitContextException(String paramString, Throwable paramThrowable){
        super(paramString,paramThrowable);
    }

    public InitContextException(Throwable paramThrowable){
        super(paramThrowable);
    }

    public InitContextException(GUTExceptionInfo paramGUTExceptionInfo,String paramString,Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramString,paramThrowable);
    }

    public InitContextException(GUTExceptionInfo paramGUTExceptionInfo,String paramString){
        super(paramGUTExceptionInfo,paramString);
    }

    public InitContextException(GUTExceptionInfo paramGUTExceptionInfo,Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramThrowable);
    }

    public InitContextException(GUTExceptionInfo paramGUTExceptionInfo){
        super(paramGUTExceptionInfo);
    }

    public InitContextException(GUTExceptionInfo paramGUTExceptionInfo,String[] paramArrayOfString){
        super(paramGUTExceptionInfo,paramArrayOfString);
    }

    public InitContextException(GUTExceptionInfo paramGUTExceptionInfo,String[] paramArrayOfString,Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramArrayOfString,paramThrowable);
    }

    public InitContextException(GUTException paramGUTException){
        super(paramGUTException);
    }

}
