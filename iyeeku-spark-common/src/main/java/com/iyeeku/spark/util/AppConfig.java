package com.iyeeku.spark.util;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;

import java.io.IOException;
import java.util.Properties;

/**
 * @ClassName AppConfig
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/9/20 20:00
 * @Version 1.0
 **/
public class AppConfig {

    public static String APP_CONF_URL_KEY = "spark.app.conf";
    public static String DEFAULT_APP_CONF_URL = "/user/iyeeku/base.cfg";

    public static String DEFAULT_PARTITION_RQ_NAME = "ywrq_";
    public static String HIS_TABLE_FIX = "__his";


    public static Properties getAppConfig(FileSystem fs,String path){
        Properties properties = new Properties();
        FSDataInputStream fsin = null;
        try {
            fsin = fs.open(new Path(path));
            properties.load(fsin);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if (fsin != null){
                    fsin.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return properties;
    }


    public static Properties getDefaultAppConfig(FileSystem fs, SparkConf sparkConf){
        return getAppConfig(fs,sparkConf.get(APP_CONF_URL_KEY,DEFAULT_APP_CONF_URL));
    }

}
