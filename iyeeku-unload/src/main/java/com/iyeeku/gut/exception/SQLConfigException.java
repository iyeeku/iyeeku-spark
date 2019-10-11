package com.iyeeku.gut.exception;

/**
 * @ClassName SQLConfigException
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 10:50
 * @Version 1.0
 **/
public class SQLConfigException extends TaskException {

    public SQLConfigException(){
    }

    public SQLConfigException(String paramString){
        super(paramString);
    }

    public SQLConfigException(GUTException paramGUTException){
        super(paramGUTException);
    }

    public SQLConfigException(GUTExceptionInfo paramGUTExceptionInfo, String paramString, Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramString,paramThrowable);
    }

    public SQLConfigException(GUTExceptionInfo paramGUTExceptionInfo, String paramString){
        super(paramGUTExceptionInfo,paramString);
    }

    public SQLConfigException(GUTExceptionInfo paramGUTExceptionInfo, String[] paramArrayOfString, Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramArrayOfString,paramThrowable);
    }

    public SQLConfigException(GUTExceptionInfo paramGUTExceptionInfo, String[] paramArrayOfString){
        super(paramGUTExceptionInfo,paramArrayOfString);
    }

    public SQLConfigException(GUTExceptionInfo paramGUTExceptionInfo, Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramThrowable);
    }

    public SQLConfigException(GUTExceptionInfo paramGUTExceptionInfo){
        super(paramGUTExceptionInfo);
    }

    public SQLConfigException(String paramString, Throwable paramThrowable){
        super(paramString,paramThrowable);
    }

    public SQLConfigException(Throwable paramThrowable){
        super(paramThrowable);
    }

}
