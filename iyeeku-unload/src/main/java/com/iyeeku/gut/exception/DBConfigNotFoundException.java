package com.iyeeku.gut.exception;

/**
 * @ClassName DBConfigNotFoundException
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 10:39
 * @Version 1.0
 **/
public class DBConfigNotFoundException extends InitContextException {

    private String adapterType;

    public DBConfigNotFoundException(){

    }

    public DBConfigNotFoundException(String paramString){
        this.adapterType = paramString;
    }

    public String getMessage(){
        return "The adapter for '" + this.adapterType + "' not found in APP-INF.xml!";
    }

}
