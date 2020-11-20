package com.iyeeku.spark.sql.hive;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.analysis.FunctionRegistry;
import org.apache.spark.sql.catalyst.catalog.ExternalCatalog;
import org.apache.spark.sql.catalyst.catalog.FunctionResourceLoader;
import org.apache.spark.sql.catalyst.catalog.GlobalTempViewManager;
import org.apache.spark.sql.catalyst.parser.ParserInterface;
import org.apache.spark.sql.hive.HiveExternalCatalog;
import org.apache.spark.sql.hive.HiveMetastoreCatalog;
import org.apache.spark.sql.hive.HiveSessionCatalog;
import org.apache.spark.sql.internal.SQLConf;
import scala.Function0;

/**
 * @ClassName HiveSessionCatalogExtend
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 16:52
 * @Version 1.0
 **/
public class HiveSessionCatalogExtend extends HiveSessionCatalog {

/*    public HiveSessionCatalogExtend(HiveExternalCatalog externalCatalog, GlobalTempViewManager globalTempViewManager,
                                    SparkSession sparkSession, FunctionResourceLoader functionResourceLoader,
                                    FunctionRegistry functionRegistry, SQLConf conf, Configuration hadoopConf){
        super(externalCatalog,globalTempViewManager,sparkSession,functionResourceLoader,functionRegistry,conf,hadoopConf);
    }*/


    public HiveSessionCatalogExtend(Function0<ExternalCatalog> externalCatalogBuilder, Function0<GlobalTempViewManager> globalTempViewManagerBuilder, HiveMetastoreCatalog metastoreCatalog, FunctionRegistry functionRegistry, SQLConf conf, Configuration hadoopConf, ParserInterface parser, FunctionResourceLoader functionResourceLoader) {
        super(externalCatalogBuilder, globalTempViewManagerBuilder, metastoreCatalog, functionRegistry, conf, hadoopConf, parser, functionResourceLoader);
    }
}
