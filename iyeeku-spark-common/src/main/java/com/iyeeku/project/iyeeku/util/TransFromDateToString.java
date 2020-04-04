package com.iyeeku.project.iyeeku.util;

import org.apache.spark.sql.api.java.UDF2;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName TransFromDateToString
 * @Description TODO
 * @Author YangQuan
 * @Date 2020/4/4 14:52
 * @Version 1.0
 **/
public class TransFromDateToString implements UDF2<Date,String,String> {

    @Override
    public String call(Date in_date, String format) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String str = sdf.format(in_date);
        return str;
    }

}
