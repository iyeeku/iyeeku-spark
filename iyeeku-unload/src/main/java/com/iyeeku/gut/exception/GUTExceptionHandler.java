package com.iyeeku.gut.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @ClassName GUTExceptionHandler
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 10:49
 * @Version 1.0
 **/
public class GUTExceptionHandler {

    private static Properties msg;

    public static synchronized void handlerException(GUTException paramGUTException){
        StringBuffer localStringBuffer = new StringBuffer(paramGUTException.getGexInfo().getErrorCode() + ":");

    }



}
