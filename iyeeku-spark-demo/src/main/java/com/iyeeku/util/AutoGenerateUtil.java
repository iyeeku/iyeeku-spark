package com.iyeeku.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName AutoGenerateUtil
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/9/25 20:00
 * @Version 1.0
 **/
public class AutoGenerateUtil {

    //private final static Logger LOGGER = LoggerFactory.getLogger(AutoGenerateUtil.class);

    private final static int dqdhLength = 50;
    private final static String[] dqdhList = new String[]{"04","06","07","08","11","12","13","14","15","16","17","18",
                "19","20","21","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47",
                "48","49","50","51","52","53","55","56","57","58","59","60","61","62","63","66","69","70","80"};

    public static String getRandomKhbh(){
        int randDqdhIndex = (int)(Math.random()*dqdhLength);
        String dqdh = dqdhList[randDqdhIndex];
        String khlb = "0";
        String khxh = String.format("%0" + 6 + "d",(int)(Math.random()*2000));
        String khjcw = String.valueOf((int)(Math.random()*10));
        return dqdh + khlb + khxh + khjcw;
    }

    public static String formatCompWithZore(int sourceData,int formatLength){
        String newData = String.format("%0" + formatLength + "d",sourceData);
        return newData;
    }

    public static String formatString(String value,int strLength){
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";

        for (int i = 0 ; i < value.length();i++){
            String temp = value.substring(i,i+1);
            if (temp.matches(chinese)){
                valueLength += 2;
            }else{
                valueLength += 1;
            }
        }
        if (valueLength < strLength){
            int temp = strLength - valueLength;
            for (int i=0 ; i < temp ; i++){
                value+=" ";
            }
        }
        return value;
    }

}
