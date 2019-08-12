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

    /**
     * 哪些字符属于注释#或者-- 遇到这种改行后续字符都忽略
     */
    public static final String[] NOTE = {"#","--"};
    public static final String MULTILINE_NOTE = "/*";

    public static boolean sql_error_if_exit = true;

    public static int getLeftNoteLine(String line){
        int[] linePositionArr = new int[NOTE.length];
        int index = 0;
        for (String eachNote : NOTE){
            int p = line.indexOf(eachNote);
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
     * 找到匹配 MULTILINE_NOTE 的位置最左位置，最右位置
     * @param line
     * @return
     */
    public static int getLeftMultilineNoteLine(String line){
        int multilinePositionLeft = line.indexOf(MULTILINE_NOTE);
        if (multilinePositionLeft >= 0){
            return multilinePositionLeft;
        } else {
            return -1;
        }
    }


    public static int getRightMultilineNoteLine(String line){
        int multilinePositionRight = line.indexOf(org.apache.commons.lang.StringUtils.reverse(MULTILINE_NOTE));
        if (multilinePositionRight >= 0){
            return multilinePositionRight;
        } else {
            return -1;
        }
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
    public static String[] splitSql(String sqlString){
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

        String[] sqlArray = splitSql(allString);

        Logger logger = LoggerFactory.getLogger(ExecSql.class);

        for (String sql : sqlArray){
            logger.warn("runSql:[ " + sql + " ]");
            long start = System.currentTimeMillis();
            session.sql(sql).show();
            long end = System.currentTimeMillis();
            logger.warn("runSql:[ " + (end - start) + " ] ms");
        }

    }


    /**
     * 读取文件并把内容中的空行和注释（当行注释或者多行注释）去掉
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String readLinesExceptBlank(File filePath) throws IOException{

        List<String> lines = FileUtils.readLines(filePath);
        StringBuffer outString = new StringBuffer();

        String multilineNoteFlag = "0";
        String returnStr,line;
        for (String v : lines){
            line = v.trim();
            if (line.length() == 0){
                continue;
            }

            int linePosition = getLeftNoteLine(line);
            int multilinePositionLeft = getLeftMultilineNoteLine(line);
            int multilinePositionRight = getRightMultilineNoteLine(line);
            if(multilinePositionLeft >= 0){
                multilineNoteFlag = "1";
            }
            if (linePosition >= 0 && multilineNoteFlag.equals("0")){
                returnStr = line.substring(0,linePosition);
            }
            //若查询到/* 则忽略改行
            else if (multilineNoteFlag.equals("1")){
                returnStr = "";
            } else {
                returnStr = line;
            }
            if (multilinePositionRight >= 0){
                multilineNoteFlag = "0";
            }
            outString.append(returnStr).append("\n");
        }
        return normalSql(outString.toString());
    }


    /**
     * 将sql空格去掉得到Array数组
     * @param sqlString
     * @return
     */
    public static String[] sqlToArray(String sqlString){
        return sqlString.split("\\s+");
    }

}
