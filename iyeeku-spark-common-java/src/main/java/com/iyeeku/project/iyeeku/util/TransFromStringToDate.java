package com.iyeeku.project.iyeeku.util;

import org.apache.spark.sql.api.java.UDF2;

import java.sql.Date;
import java.text.SimpleDateFormat;


/**
 * @ClassName TransFromStringToDate
 * @Description TODO
 * @Author YangQuan
 * @Date 2020/4/4 14:54
 * @Version 1.0
 **/
public class TransFromStringToDate implements UDF2<String,String,Date> {

    @Override
    public Date call(String in_str, String format) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date in_date = new java.sql.Date(sdf.parse(in_str).getTime());
        return in_date;
    }
}
