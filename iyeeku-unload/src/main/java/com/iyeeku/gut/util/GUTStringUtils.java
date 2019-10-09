package com.iyeeku.gut.util;


import java.util.Enumeration;
import java.util.Properties;

/**
 * @ClassName GUTStringUtils
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/9 21:01
 * @Version 1.0
 **/
public class GUTStringUtils {

    public static String formatString(String paramString, String[] paramArrayOfString){
        if (paramString == null){
            return null;
        }
        if ((paramArrayOfString == null) || (paramArrayOfString.length <= 0)){
            return paramString;
        }
        for (int i = 0; i < paramArrayOfString.length; i++){
            paramString = paramString.replaceAll("##" + i + "##", paramArrayOfString[i] == null ? "" : paramArrayOfString[i]);
        }
        return paramString;
    }

    public static String formatString(String paramString, Properties paramProperties){
        if (paramProperties == null){
            return paramString;
        }
        Enumeration localEnumeration = paramProperties.propertyNames();
        while (localEnumeration.hasMoreElements()){
            String str = (String) localEnumeration.nextElement();
            paramString = paramString.replaceAll("##" + str + "##", paramProperties.getProperty(str) == null ? "" : paramProperties.getProperty(str));
        }
        return paramString;
    }

    public static String formatStringEx(String paramString,String[] paramArrayOfString){
        StringBuffer localStringBuffer = new StringBuffer();
        String str1 = null;
        if (paramString == null){
            return null;
        }
        if ((paramArrayOfString == null) || (paramArrayOfString.length <= 0)){
            return paramString;
        }
        for (int i = 0; i < paramArrayOfString.length; i++){
            String str2 = "##" + i + "##";
            str1 = paramString.substring(paramString.indexOf("##" + (paramArrayOfString.length -1) + "##") + str2.length());
            while (paramString.indexOf(str2) != -1){
                String str3 = paramString.substring(0,paramString.indexOf(str2));
                localStringBuffer.append(str3);
                localStringBuffer.append(paramArrayOfString[i] == null ? "" : paramArrayOfString[i]);
                paramString = paramString.substring(paramString.indexOf(str2) + str2.length());
            }
        }
        localStringBuffer.append(str1);
        return localStringBuffer.toString();
    }

}
