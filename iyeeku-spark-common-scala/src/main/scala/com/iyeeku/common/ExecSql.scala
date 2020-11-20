package com.iyeeku.common

import java.io.File

import com.iyeeku.util.SqlParseUtil
import org.apache.commons.cli.{CommandLine, GnuParser, HelpFormatter, Options}
import org.apache.commons.io.FileUtils
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

object ExecSql {

  var sqlFileName : String = ""
  var opts : Options = new Options()
  var logger : Logger = LoggerFactory.getLogger(ExecSql.getClass)

  def printHelp() : Unit = {
    new HelpFormatter().printHelp("ExecSql" , opts)
    System.exit(1)
  }

  def validateArgs(args: Array[String]) : Unit = {
    opts.addOption("f", true, "Run Sql File")
    val commandLine : CommandLine = new GnuParser().parse(opts,args)
    if (commandLine.hasOption("f")){
      sqlFileName = commandLine.getOptionValue("f")
    }else{
      printHelp()
    }

    if (!FileUtils.getFile(sqlFileName).exists()){
      System.err.println("Sql File:[" + sqlFileName + "] Not Found!")
      System.exit(1)
    }
  }

  def runSql(session : SparkSession, sqlFileName : String) : Unit = {
    val allSql : String = SqlParseUtil.readLinesExceptBlank(new File(sqlFileName))
    SqlParseUtil.runSqlSegment(session,allSql)
  }

  def main(args: Array[String]): Unit = {
    validateArgs(args)

    val session : SparkSession = SparkSession.builder()
                          .appName("ExecSql")
                          .master("local[*]")
                          .enableHiveSupport()
                          .getOrCreate()

    val context : SparkContext = session.sparkContext
    context.setLogLevel("WARN")
    runSql(session,sqlFileName)
    session.stop()

  }

}
