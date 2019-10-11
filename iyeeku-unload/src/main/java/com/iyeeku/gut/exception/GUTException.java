package com.iyeeku.gut.exception;

/**
 * @ClassName GUTException
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 10:16
 * @Version 1.0
 **/
public class GUTException extends Exception {

    private GUTExceptionInfo gexInfo;
    private String[] innerInfo;

    public GUTException(){
    }

    public GUTException(GUTException paramGUTException){
        super(paramGUTException);
        this.gexInfo = paramGUTException.getGexInfo();
        this.innerInfo = paramGUTException.getInnerInfo();
    }

    public GUTException(GUTExceptionInfo paramGUTExceptionInfo){
        this.gexInfo = paramGUTExceptionInfo;
    }

    public GUTException(String paramString){
        super(paramString);
    }

    public GUTException(GUTExceptionInfo paramGUTExceptionInfo, String paramString){
        super(paramString);
        this.gexInfo = paramGUTExceptionInfo;
    }

    public GUTException(String paramString,Throwable paramThrowable){
        super(paramString,paramThrowable);
    }

    public GUTException(Throwable paramThrowable){
        super(paramThrowable);
    }

    public GUTException(GUTExceptionInfo paramGUTExceptionInfo, Throwable paramThrowable){
        super(paramThrowable);
        this.gexInfo = paramGUTExceptionInfo;
    }

    public GUTException(GUTExceptionInfo paramGUTExceptionInfo, String[] paramArrayOfString){
        this(paramGUTExceptionInfo);
        this.innerInfo = paramArrayOfString;
    }

    public GUTException(GUTExceptionInfo paramGUTExceptionInfo,String[] paramArrayOfString,Throwable paramThrowable){
        this(paramGUTExceptionInfo,paramThrowable);
        this.innerInfo = paramArrayOfString;
    }

    public GUTException(GUTExceptionInfo paramGUTExceptionInfo,String paramString,Throwable paramThrowable){
        super(paramString,paramThrowable);
        this.gexInfo = paramGUTExceptionInfo;
    }

    public GUTExceptionInfo getGexInfo() {
        return gexInfo;
    }

    public void setGexInfo(GUTExceptionInfo gexInfo) {
        this.gexInfo = gexInfo;
    }

    public String[] getInnerInfo() {
        return innerInfo;
    }

    public void setInnerInfo(String[] innerInfo) {
        this.innerInfo = innerInfo;
    }

}
