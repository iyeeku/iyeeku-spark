package com.iyeeku.spark.util;

import com.iyeeku.spark.common.ExecSql;
import org.apache.commons.io.FileUtils;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yq180
 */
public class SqlParseUtil {

    public static final String[] NOTE = {"#","--"};
    public static final String MULTNOTE = "/*";

    public static int getLeftNoteLine(String line){
        int[] linePositionArr = new int[NOTE.length];
        int index = 0;
        for (String eachnote : NOTE){
            int p = line.indexOf(eachnote);
            linePositionArr[index] = p;
            index ++;
        }

        int linePosition = -1;
        for (int varLinePosition : linePositionArr){
            if (varLinePosition >= 0 && linePosition < 0) {
                linePosition = varLinePosition;
            }
            if (varLinePosition >=0 && varLinePosition < linePosition) {
                linePosition = varLinePosition;
            }
        }
        return linePosition;
    }

    /**
     * 将sql中多余的空格转化为一个空格
     * @param sqlString
     * @return
     */
    public static String normalSql(String sqlString){
        String[] strArray = sqlString.split("\\s+");
        StringBuffer outString = new StringBuffer();
        for (String word : strArray){
            outString.append(word).append(" ");
        }
        return outString.toString().trim();
    }

    /**
     * 分析sql得到sql数组
     * @param sqlString
     * @return
     */
    public static String[] spliteSql(String sqlString){
        String[] sqlArray = sqlString.split(";");
        List<String> list = new ArrayList<String>();
        for (String sql : sqlArray){
            String str = sql.trim();
            if (str.length() > 0){
                list.add(normalSql(str));
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public static void runSqlSegment(SparkSession session , String allString){

        String[] sqlArray = spliteSql(allString);

        Logger logger = LoggerFactory.getLogger(ExecSql.class);

        for (String sql : sqlArray){
            logger.warn("runSql:[ " + sql + " ]");
            long start = System.currentTimeMillis();
            session.sql(sql).show();
            long end = System.currentTimeMillis();
            logger.warn("runSql:[ " + (end - start) + " ] ms");
        }

    }


    public static String readLinesExceptBlank(File filePath) throws IOException{

        List<String> lines = FileUtils.readLines(filePath);
        StringBuffer outString = new StringBuffer();

        String multnoteFlg = "0";
        String returnStr = "";
        String line;
        for (String v : lines){
            line = v.trim();
            if (line.length() == 0){
                continue;
            }

            outString.append(line).append("\n");

        }


        return normalSql(outString.toString());
    }

}
