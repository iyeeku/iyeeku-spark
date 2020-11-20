package com.iyeeku.spark.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 关于日期的操作可以参考org.apache.spark.sql.catalyst.util.DateTimeUtils
 * @ClassName JavaDateUtils
 * @Description TODO
 * @Author YangQuan
 * @Date 2020/4/4 10:29
 * @Version 1.0
 **/
public class JavaDateUtils {

    public static String getCurrentTime(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(now.getTime());
    }

}
