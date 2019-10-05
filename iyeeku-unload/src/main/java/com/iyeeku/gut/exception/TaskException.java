package com.iyeeku.gut.exception;

import com.iyeeku.gut.task.AbstractTask;


/**
 * @ClassName TaskException
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 10:50
 * @Version 1.0
 **/
public class TaskException extends GUTException {

    private AbstractTask task;

    public AbstractTask getTask() {
        return task;
    }

    public void setTask(AbstractTask task) {
        this.task = task;
    }

    public TaskException(){

    }

    public TaskException(String paramString){
        super(paramString);
    }

    public TaskException(Throwable paramThrowable){
        super(paramThrowable);
    }

    public TaskException(GUTException paramGUTException){
        super(paramGUTException);
    }

    public TaskException(GUTExceptionInfo paramGUTExceptionInfo, String paramString, Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramString,paramThrowable);
    }

    public TaskException(GUTExceptionInfo paramGUTExceptionInfo, String paramString){
        super(paramGUTExceptionInfo,paramString);
    }

    public TaskException(GUTExceptionInfo paramGUTExceptionInfo, String[] paramArrayOfString, Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramArrayOfString,paramThrowable);
    }

    public TaskException(GUTExceptionInfo paramGUTExceptionInfo, String[] paramArrayOfString, Throwable paramThrowable, AbstractTask paramAbstractTask){
        super(paramGUTExceptionInfo,paramArrayOfString,paramThrowable);
        this.task = paramAbstractTask;
    }

    public TaskException(GUTExceptionInfo paramGUTExceptionInfo, String[] paramArrayOfString){
        super(paramGUTExceptionInfo,paramArrayOfString);
    }

    public TaskException(GUTExceptionInfo paramGUTExceptionInfo, String[] paramArrayOfString, AbstractTask paramAbstractTask){
        super(paramGUTExceptionInfo,paramArrayOfString);
        this.task = paramAbstractTask;
    }

    public TaskException(GUTExceptionInfo paramGUTExceptionInfo, Throwable paramThrowable){
        super(paramGUTExceptionInfo,paramThrowable);
    }

    public TaskException(GUTExceptionInfo paramGUTExceptionInfo, Throwable paramThrowable, AbstractTask paramAbstractTask){
        super(paramGUTExceptionInfo,paramThrowable);
        this.task = paramAbstractTask;
    }

    public TaskException(GUTExceptionInfo paramGUTExceptionInfo){
        super(paramGUTExceptionInfo);
    }

    public TaskException(String paramString, Throwable paramThrowable){
        super(paramString,paramThrowable);
    }

    public String getMessage(){
        if (this.task != null){
            StringBuffer localStringBuffer = new StringBuffer();
            //TODO his.task.getTaskID()
            localStringBuffer.append("NO." + this.task.toString() + " has exception!");
            localStringBuffer.append(super.getMessage());
            return localStringBuffer.toString();
        }
        return super.getMessage();
    }

}
