package com.iyeeku.spark.util;

import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalog.Catalog;
import org.apache.spark.sql.catalog.Column;
import org.apache.spark.sql.catalyst.parser.ParseException;
import org.apache.spark.sql.types.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @ClassName Schema
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 15:22
 * @Version 1.0
 **/
public class Schema {

    /**
     * 根据字段列表信息生成对应的schema信息，字段类型全部为 StringType
     * @param field_map
     * @return
     */
    public static StructType getSchema(TreeMap<Integer,FileField> field_map){
        List<StructField> fields = new ArrayList<>();
        for (Map.Entry<Integer,FileField> entry : field_map.entrySet()){
            FileField fileField = entry.getValue();
            String fieldType = fileField.getFieldtype();
            String fieldName = fileField.getFieldname();
            StructField field = DataTypes.createStructField(fieldName,DataTypes.StringType,false);
            fields.add(field);
        }
        return DataTypes.createStructType(fields);
    }

    /**
     * 根据字段列表生成对应的schema信息，默认为 StringType
     * @param values
     * @return
     */
    public static StructType getSchema(String... values){
        List<StructField> fields = new ArrayList<>();
        for (String entry : values){
            StructField field = DataTypes.createStructField(entry,DataTypes.StringType,false);
            fields.add(field);
        }
        return DataTypes.createStructType(fields);
    }

    /**
     * 根据schema信息生成Map结构，不含长度
     * @param structType
     * @return
     */
    public static TreeMap<Integer,FileField> getFieldMap(StructType structType){
        TreeMap<Integer,FileField> field_map = new TreeMap<>();
        int size = structType.size();
        for (int i = 1 ; i < size + 1 ; i++){
            StructField structField = structType.apply(i-1);
            FileField fileField = new FileField(i,structField.name().toUpperCase(),structField.dataType().typeName().toUpperCase(),-1,-1,0);
            field_map.put(i,fileField);
        }
        return field_map;
    }

    /**
     * 若schema中存在DataType,TimestampType类型的话，转换成 StringType
     * @param structType
     * @return
     */
    public static StructType tranformDateType(StructType structType){
        List<StructField> fields = new ArrayList<>();
        for (StructField entry : structType.fields()){
            DataType fieldType = entry.dataType();
            String fieldName = entry.name();
            if (fieldType.equals(DataTypes.DateType) || fieldType.equals(DataTypes.TimestampType)){
                fieldType = DataTypes.StringType;
            }
            StructField field = DataTypes.createStructField(fieldName,fieldType,false);
            fields.add(field);
        }
        return DataTypes.createStructType(fields);
    }

    /**
     * 将schema中的字段名小写改为大写，此处在写jdbc时需要
     * @param structType
     * @return
     */
    public static StructType upper(StructType structType){
        List<StructField> fields = new ArrayList<StructField>();
        for (StructField entry : structType.fields()){
            StructField field = entry.copy(entry.name().toUpperCase(), entry.dataType(), entry.nullable(), entry.metadata());
            fields.add(field);
        }
        return DataTypes.createStructType(fields);
    }

    /**
     *将列表中的字段名小写改为大写
     */
    public static List<String> upper(List<String> arr_1){
        final List<String> arrList1 = Lists.transform(arr_1,
                new com.google.common.base.Function<String,String>(){
                    @Nullable
                    @Override
                    public String apply(@Nullable String input) {
                        return input.toLowerCase();
                    }
                });
        return arrList1;
    }

    /**
     *将列表中的字段名小写改为大写
     */
    public static List<String> upperColumn(List<org.apache.spark.sql.catalog.Column> tableNameColumnList){
        final List<String> arrList1 = Lists.transform(tableNameColumnList,
                new com.google.common.base.Function<org.apache.spark.sql.catalog.Column,String>(){
                    @Nullable
                    @Override
                    public String apply(@Nullable org.apache.spark.sql.catalog.Column input) {
                        return input.name().toLowerCase();
                    }
                });
        return arrList1;
    }

    /**
     *根据两个schema信息的差别生成第一个schema字段，若
     * @return
     */
    public static String getDiffFieldSql(StructType structType1,StructType structType2){
        List<String> arr1 = Lists.newArrayList(structType1.fieldNames());
        List<String> arr2 = Lists.newArrayList(structType2.fieldNames());

        final List<String> arrList1 = upper(arr1);
        final List<String> arrList2 = upper(arr2);

        List<String> fList = Lists.transform(arrList1,
                new com.google.common.base.Function<String,String>(){
                    @Nullable
                    @Override
                    public String apply(@Nullable String input) {
                        String str = "";
                        if (arrList2.contains(input)){
                            str = input;
                        }else{
                            str = "'' as " + input;
                        }
                        return str;
                    }
                });
        return scala.collection.JavaConversions.asScalaBuffer(fList).mkString(",");
    }

    /**
     *
     */
    public static String getDiffFieldSql(SparkSession spark,String tableName,String hisTableName){
        StructType schema1 = spark.table(tableName).schema();
        StructType schema2 = spark.table(hisTableName).schema();
        return getDiffFieldSql(schema2,schema1);
    }

    public static String getDiffFieldSqlNoPartition(SparkSession spark,String tableName,String hisTableName) throws ParseException{
        List<org.apache.spark.sql.catalog.Column> tableNameColumnList = TableOps.getTableColumns(spark,tableName,false);
        List<org.apache.spark.sql.catalog.Column> hisTableNameColumnList = TableOps.getTableColumns(spark,hisTableName,false);

        final List<String> arrList_1 = upperColumn(tableNameColumnList);
        final List<String> arrList_2 = upperColumn(hisTableNameColumnList);

        List<String> fList = Lists.transform(arrList_1,
                new com.google.common.base.Function<String,String>(){
                    @Nullable
                    @Override
                    public String apply(@Nullable String input) {
                        String str = "";
                        if (arrList_2.contains(input)){
                            str = input;
                        }else{
                            str = "'' as " + input;
                        }
                        return str;
                    }
                });
        return scala.collection.JavaConversions.asScalaBuffer(fList).mkString(",");
    }

    /**
     *比较2个表的差异
     */
    public static Dataset<Row> getDiffTable(SparkSession spark,String tableName,String hisTableName,boolean seq){
        StructType schema1 = spark.table(tableName).schema();
        StructType schema2 = spark.table(hisTableName).schema();

        List<String> arr1 = Lists.newArrayList(schema1.fieldNames());
        List<String> arr2 = Lists.newArrayList(schema2.fieldNames());

        final List<String> arrList_1 = upper(arr1);
        final List<String> arrList_2 = upper(arr2);

        int arr1_size = arr1.size();
        int arr2_size = arr2.size();
        int allFieldCount = (arr1_size >= arr2_size)?arr1_size:arr2_size;
        List<Row> differRow = new ArrayList<Row>();

        if (seq){
            for (int i = 0 ; i <= allFieldCount - 1 ; i++){
                String fieldName1 = (i<=arr1_size-1)?arrList_1.get(i):"";
                String fieldName2 = (i<=arr2_size-1)?arrList_2.get(i):"";
                differRow.add(RowFactory.create(""+(i+1),fieldName1,fieldName2,fieldName1.equals(fieldName2)+""));
            }
            StructType newSchema = getSchema("seq",tableName,hisTableName,"==");
            return spark.createDataFrame(differRow,newSchema);
        }else{
            //交集
            final List<String> intersectList = ListUtils.intersection(arrList_1,arrList_2);
            //差集
            final List<String> subtractList1 = ListUtils.subtract(arrList_1,arrList_2);
            //差集
            final List<String> subtractList2 = ListUtils.subtract(arrList_2,arrList_1);

            for (String element: intersectList){
                differRow.add(RowFactory.create(element,element));
            }

            for (String element: subtractList1){
                differRow.add(RowFactory.create(element,""));
            }

            for (String element: subtractList2){
                differRow.add(RowFactory.create("",element));
            }

            StructType newSchema = getSchema(tableName,hisTableName);
            return spark.createDataFrame(differRow,newSchema);
        }

    }

    public static void createTableHis(SparkSession spark,String tableName,String hisTableName,boolean overwrite) throws ParseException{
        scala.Tuple2<String,String> tableIdentifierTableName = TableOps.formatTable(spark,tableName);
        scala.Tuple2<String,String> tableIdentifierHisTableName = TableOps.formatTable(spark,hisTableName);

        Catalog catalog = spark.catalog();
        catalog.setCurrentDatabase(tableIdentifierHisTableName._1);
        if (catalog.tableExists(tableIdentifierHisTableName._2) && overwrite){
            SqlParseUtil.runSqlSegment(spark,"drop table " + hisTableName);
        }else if (catalog.tableExists(tableIdentifierHisTableName._2) && overwrite == false){
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("create table ").append(tableIdentifierHisTableName._1).append(".").append(tableIdentifierHisTableName._2).append("(");
        try {
            catalog.setCurrentDatabase(tableIdentifierTableName._1);
            List<org.apache.spark.sql.catalog.Column> columnList = catalog.listColumns(tableIdentifierTableName._2).collectAsList();
            for (int i = 0 ; i < columnList.size() ; i++){
                org.apache.spark.sql.catalog.Column column = columnList.get(i);
                if (i<columnList.size()-1){
                    sb.append(column.name()).append(" ").append(column.dataType()).append(",");
                }else{
                    sb.append(column.name()).append(" ").append(column.dataType());
                }
            }
            sb.append(") ").append("partitioned by(" + AppConfig.DEFAULT_PARTITION_RQ_NAME + " string) stored as parquet");
        }catch (AnalysisException e){
            e.printStackTrace();
        }
        SqlParseUtil.runSqlSegment(spark,sb.toString());
    }

}
