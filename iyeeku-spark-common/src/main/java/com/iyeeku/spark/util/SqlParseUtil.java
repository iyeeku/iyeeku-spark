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

public class SqlParseUtil {

    public static final String[] note = {"#","--"};
    public static final String multnote = "/*";

    public static int getLeftNoteLine(String line){
        int[] _line_position_arr = new int[note.length];
        int index = 0;
        for (String eachnote : note){
            int _p = line.indexOf(eachnote);
            _line_position_arr[index] = _p;
            index ++;
        }

        int line_position = -1;
        for (int _line_position : _line_position_arr){
            if (_line_position >= 0 && line_position < 0)
                line_position = _line_position;
            if (_line_position >=0 && _line_position < line_position)
                line_position = _line_position;
        }
        return line_position;
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

        String multnote_flg = "0";
        String return_str = "";
        String line;
        for (String _line : lines){
            line = _line.trim();
            if (line.length() == 0){
                continue;
            }

            outString.append(line).append("\n");

        }


        return normalSql(outString.toString());
    }

}
