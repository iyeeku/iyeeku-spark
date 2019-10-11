package com.iyeeku.gut.exception;

import com.iyeeku.gut.main.GUT;
import com.iyeeku.gut.util.GUTStringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

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
        if (GUT.getLogger() == null){
            BasicConfigurator.configure();
            GUT.setLogger(Logger.getRootLogger());
        }
        GUTExceptionInfo localGUTExceptionInfo = paramGUTException.getGexInfo();
        String str = getMsgByCode(localGUTExceptionInfo.getErrorCode());
        if (paramGUTException.getInnerInfo() != null){
            str = GUTStringUtils.formatStringEx(str, paramGUTException.getInnerInfo()) + "\n";
        }
        localStringBuffer.append(str);
        localStringBuffer.append(paramGUTException.getMessage() == null ? "" : paramGUTException.getMessage());
        switch (localGUTExceptionInfo.getErrorLevel()){
            case 10:
                GUT.getLogger().debug(localStringBuffer);
                break;
            case 40:
                GUT.getLogger().error(localStringBuffer,paramGUTException);
                break;
            case 99:
                GUT.getLogger().fatal(localStringBuffer);
                break;
            case 20:
                GUT.getLogger().info(localStringBuffer);
                break;
            case 30:
                GUT.getLogger().warn(localStringBuffer);
                break;
            default:
                GUT.getLogger().warn(localStringBuffer);
        }
    }

    public static String getMsgByCode(String paramString){
        if (msg != null){
            return msg.getProperty(paramString);
        }
        return "";
    }

    public static void setExceptionMsg(Properties paramProperties){
        msg = paramProperties;
    }

}
