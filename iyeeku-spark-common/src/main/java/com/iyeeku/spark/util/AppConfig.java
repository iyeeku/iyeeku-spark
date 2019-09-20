package com.iyeeku.spark.util;

import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.SparkConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @ClassName AppConfig
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/9/20 20:00
 * @Version 1.0
 **/
public class AppConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);


    public static Properties getDefaultAppConfig(FileSystem fs, SparkConf sparkConf){
        return new Properties();
    }

}
