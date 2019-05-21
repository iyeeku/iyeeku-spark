package com.iyeeku.spark.example;

import org.apache.commons.cli.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;

public class WordCount {

    public static String _datapath = "";
    public static String _flgpath = "";
    public static String _tablename = "";
    public static Options opts = new Options();

    public static void printUsage(){
        new HelpFormatter().printHelp("WordCount" , opts);
        System.exit(1);
    }

    public static void validate(String[] args) throws ParseException{
        opts.addOption("dp" , true , "Standerd Gut Data File");
        opts.addOption("fp" , true , "Standerd Gut Flag File");
        opts.addOption("t" , true , "Load Hive TableName");
        opts.addOption("u" , false , "Load TableName Add NewColumn UniqueID");

        CommandLine cliParser = new GnuParser().parse(opts , args);
        if (cliParser.hasOption("dp")){
            _datapath = cliParser.getOptionValue("dp");
        } else {
            printUsage();
        }

        if (cliParser.hasOption("fp")){
            _flgpath = cliParser.getOptionValue("fp");
        } else {
            printUsage();
        }

        if (cliParser.hasOption("t")){
            _tablename = cliParser.getOptionValue("t");
        } else {
            printUsage();
        }

    }

    public static void main(String[] args) throws ParseException {

        //validate(args);

        SparkConf conf = new SparkConf().setAppName("WordCount");
        JavaSparkContext sc = new JavaSparkContext(conf);
        //读取输入文件
        JavaRDD<String> input = sc.textFile("/home/iyeeku/READMD.md");
        // 切分为单词
        JavaRDD<String> words = input.flatMap(new FlatMapFunction<String, String>() {
            public Iterator<String> call(String s) throws Exception {
                return Arrays.asList(s.split(" ")).iterator();
            }
        });
        JavaPairRDD<String,Integer> counts = words.mapToPair(new PairFunction<String, String, Integer>() {
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<String, Integer>(s , 1);
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });

        //将统计出来的单词总数存入一个文本文件，引发求值
        counts.saveAsTextFile("outFile");

    }

}
