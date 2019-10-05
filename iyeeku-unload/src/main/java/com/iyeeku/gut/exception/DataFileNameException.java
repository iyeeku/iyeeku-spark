package com.iyeeku.gut.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName DataFileNameException
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 10:41
 * @Version 1.0
 **/
public class DataFileNameException extends InitContextException {

    public String dataFileName;

    public DataFileNameException(){

    }

    public DataFileNameException(GUTException paramGUTException){
        super(paramGUTException);
    }

    public DataFileNameException(GUTExceptionInfo paramGUTExceptionInfo,String paramString, Throwable paramThrowable){
        super(paramGUTExceptionInfo, paramString , paramThrowable);
    }

    public DataFileNameException(GUTExceptionInfo paramGUTExceptionInfo,String paramString){
        super(paramGUTExceptionInfo, paramString);
    }

    public DataFileNameException(GUTExceptionInfo paramGUTExceptionInfo,String[] paramArrayOfString, Throwable paramThrowable){
        super(paramGUTExceptionInfo, paramArrayOfString , paramThrowable);
    }

    public DataFileNameException(GUTExceptionInfo paramGUTExceptionInfo,String[] paramArrayOfString){
        super(paramGUTExceptionInfo, paramArrayOfString);
    }

    public DataFileNameException(GUTExceptionInfo paramGUTExceptionInfo, Throwable paramThrowable){
        super(paramGUTExceptionInfo, paramThrowable);
    }

    public DataFileNameException(GUTExceptionInfo paramGUTExceptionInfo){
        super(paramGUTExceptionInfo);
    }

    public DataFileNameException(String paramString, Throwable paramThrowable){
        super(paramString , paramThrowable);
    }

    public DataFileNameException(String paramString){
        super(paramString);
    }

    public DataFileNameException(Throwable paramThrowable){
        super(paramThrowable);
    }

    public String getDataFileName() {
        return dataFileName;
    }

    public void setDataFileName(String dataFileName) {
        this.dataFileName = dataFileName;
    }

}
