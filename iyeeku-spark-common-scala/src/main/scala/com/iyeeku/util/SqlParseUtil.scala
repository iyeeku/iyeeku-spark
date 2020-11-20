package com.iyeeku.util

import java.io.File
import java.util

import com.iyeeku.common.ExecSql
import org.apache.commons.io.FileUtils
import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.{Row, SparkSession}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
import scala.util.control.Breaks._

object SqlParseUtil {

  val NOTE = Array("#","--")
  val MULTILINE_NOTE : String = "/*"
  val sql_error_if_exit : Boolean = true

  def getLeftNoteLine(line : String) : Int = {
    val linePositionArr : Array[Int] = new Array[Int](NOTE.length)
    var index : Int = 0
    for (eachNote : String <- NOTE){
      val p : Int = line.indexOf(eachNote)
      linePositionArr(index) = p
      index = index + 1
    }
    var linePosition : Int = -1
    for (varLinePosition : Int <- linePositionArr){
      if (varLinePosition >= 0 && linePosition < 0){
        linePosition = varLinePosition
      }
      if (varLinePosition >= 0 && varLinePosition < linePosition){
        linePosition = varLinePosition
      }
    }
    return linePosition
  }

  def getLeftMultilineNoteLine(line : String) : Int = {
    val multilinePositionLeft : Int = line.indexOf(MULTILINE_NOTE)
    if (multilinePositionLeft >= 0){
      return multilinePositionLeft
    }else{
      return -1
    }
  }

  def getRightMultilineNoteLine(line : String) : Int = {
    val multilinePositionRight = line.indexOf(org.apache.commons.lang.StringUtils.reverse(MULTILINE_NOTE))
    if (multilinePositionRight >= 0){
      return multilinePositionRight
    }else{
      return -1
    }
  }

  def normalSql(sqlString : String) : String = {
    val strArray : Array[String] = sqlString.split("\\s+")
    val outString : StringBuffer = new StringBuffer()
    for (word : String <- strArray){
      outString.append(word).append(" ")
    }
    return outString.toString.trim()
  }

  def splitSql(sqlString: String) : Array[String] = {
    val sqlArray : Array[String] = sqlString.split(";")
    val list : util.ArrayList[String] = new util.ArrayList[String]()
    for (sql : String <- sqlArray){
      val str = sql.trim()
      if (str.length > 0){
        list.add(normalSql(str))
      }
    }
    return list.toArray(new Array[String](list.size()))
  }

  def runSqlSegment(session : SparkSession, allString : String) : Unit = {
    val sqlArray : Array[String] = splitSql(allString)
    val logger : Logger = LoggerFactory.getLogger(ExecSql.getClass)
    for (sql : String <- sqlArray){
      logger.warn("runSql : [" + sql + "]")
      val start : Long = System.currentTimeMillis()
      session.sparkContext.setCallSite(sql)
      try {
        if (sql.toLowerCase().startsWith("select")) {
          session.sql(sql).show()
        } else {
          session.sql(sql).foreach(new ForeachFunction[Row] {
            override def call(t: Row): Unit = {

            }
          })
        }

        session.sparkContext.clearCallSite()
        val end: Long = System.currentTimeMillis()
        logger.warn("runSql:[" + (end - start) + "] ms")
      }catch {
        case ex: Exception => {
          ex.printStackTrace()
          logger.error("runSql:[" +ex.getMessage()+ "]")
          if (sql_error_if_exit) System.exit(2)
          throw ex
        }
      }
    }
  }

  def readLinesExceptBlank(filePath : File) : String = {
    val lines : util.List[String] = FileUtils.readLines(filePath)
    val outString : StringBuilder = new StringBuilder()
    var multilineNoteFlag : String = "0"
    var returnStr,line : String = null
    for (v : String <- lines){
      line = v.trim()
      breakable{
        if (v.trim().length() == 0){
          break()
        }
      }
      val linePosition : Int = getLeftNoteLine(line)
      val multilinePositionLeft : Int = getLeftMultilineNoteLine(line)
      val multilinePositionRight : Int = getRightMultilineNoteLine(line)
      if (multilinePositionLeft >= 0){
        multilineNoteFlag = "1"
      }
      if (linePosition >= 0 && multilineNoteFlag.equals("0")){
        returnStr = line.substring(0,linePosition)
      }
      else if (multilineNoteFlag.equals("1")){
        returnStr = ""
      }else{
        returnStr = line
      }
      if (multilinePositionRight >= 0){
        multilineNoteFlag = "0"
      }
      outString.append(returnStr).append("\n")
    }
    return normalSql(outString.toString())
  }

  def sqlToArray(sqlString : String) : Array[String] = {
    return sqlString.split("\\s+")
  }

}
