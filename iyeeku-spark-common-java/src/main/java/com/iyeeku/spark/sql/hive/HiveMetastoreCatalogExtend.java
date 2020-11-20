package com.iyeeku.spark.sql.hive;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.TableIdentifier;
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;
import org.apache.spark.sql.hive.HiveMetastoreCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName HiveMetastoreCatalogExtend
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/9/24 21:59
 * @Version 1.0
 **/
public class HiveMetastoreCatalogExtend extends HiveMetastoreCatalog {

    private final static Logger LOGGER = LoggerFactory.getLogger(HiveMetastoreCatalogExtend.class);

    public HiveMetastoreCatalogExtend(SparkSession sparkSession) {
        super(sparkSession);
    }

    @Override
    public LogicalPlan getCachedDataSourceTable(TableIdentifier table) {
        return super.getCachedDataSourceTable(table);
    }
}
