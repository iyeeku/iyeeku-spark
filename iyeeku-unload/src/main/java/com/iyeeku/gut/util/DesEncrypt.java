package com.iyeeku.gut.util;

import org.apache.commons.codec.binary.Base64;

/**
 * @ClassName DesEncrypt
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/9 18:49
 * @Version 1.0
 **/
public class DesEncrypt {

    public static String getEncString(String paramString){
        byte[] arrayOfByte = Base64.encodeBase64Chunked(paramString.getBytes());
        return new String(arrayOfByte);
    }

    public static String getDesString(String paramString){
        byte[] arrayOfByte = Base64.decodeBase64(paramString.getBytes());
        return new String(arrayOfByte);
    }

    public static void main(String[] args) {
/*        if ((args!=null) && (args.length >0)){
            System.out.println(getEncString(args[0]));
        }else{
            StringBuffer localStringBuffer = new StringBuffer();
            localStringBuffer.append("[Your password is unavailable!]\n");
            System.out.println(localStringBuffer.toString());
        }*/

        System.out.println(getEncString("iyeekudev"));
    }


}
