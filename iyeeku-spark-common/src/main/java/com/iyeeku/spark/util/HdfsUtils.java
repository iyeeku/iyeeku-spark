package com.iyeeku.spark.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.GzipCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @ClassName HdfsUtils
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/9/20 20:09
 * @Version 1.0
 **/
public class HdfsUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(HdfsUtils.class);

    /**
     * 拷贝本地文件到hdfs
     * @param srcPath
     * @param tgtPath
     */
    public static void copyFileToHdfs(String srcPath,String tgtPath){
        //TODO
        Path _tgtPath = new Path(tgtPath);
    }

    /**
     * 解压缩本地文件到hdfs
     * @param srcPath
     * @param tgtPath
     */
    public static void gzipFileToHdfs(String srcPath,String tgtPath){
        Configuration conf = new Configuration();
        GzipCodec zip = new GzipCodec();
        if (!srcPath.endsWith(zip.getDefaultExtension())){
            return;
        }
        zip.setConf(conf);
        Path _srcPath = new Path(srcPath);
        Path _tgtPath = new Path(tgtPath);
        FileSystem srcDfs;
        FileSystem tgtDfs;
        try {
            srcDfs = _srcPath.getFileSystem(conf);
            tgtDfs = _tgtPath.getFileSystem(conf);
            CompressionInputStream inputStream = zip.createInputStream(srcDfs.open(_srcPath));
            FSDataOutputStream outputStream = tgtDfs.create(_tgtPath);
            org.apache.hadoop.io.IOUtils.copyBytes(inputStream,outputStream,conf);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 搜索hdfs某个目录下的文件前缀和日期文件
     * @param fs
     * @param hdfsDirPath
     * @param filePrefix
     * @param ywrq
     * @return
     */
    public static scala.Tuple2<String,String> searchHdfsGzAndFlg(FileSystem fs,String hdfsDirPath,String filePrefix,String ywrq){
        String hdfsDataGzFile = "";
        String hdfsDataFlgFile = "";
        String hdfsDataFile = "";

        String matchers = filePrefix + "." + ywrq;
        try {
            RemoteIterator<LocatedFileStatus> files = fs.listFiles(new Path(hdfsDirPath),true);
            while (files.hasNext()){
                LocatedFileStatus fileStatus = files.next();
                if (fileStatus.isFile()){
                    Path filePath = fileStatus.getPath();
                    String fileName = filePath.getName();
                    if (fileName.contains(matchers) && fileName.endsWith("gz")){
                        hdfsDataGzFile = filePath.toUri().toString();
                    }
                    if (fileName.contains(matchers) && fileName.endsWith("flg")){
                        hdfsDataFlgFile = filePath.toUri().toString();
                    }
                }
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        hdfsDataFile = hdfsDataGzFile.replace(".gz","");
        return scala.Tuple2.apply(hdfsDataGzFile,hdfsDataFlgFile);
    }

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        scala.Tuple2<String,String> tuple = searchHdfsGzAndFlg(fs , "/in/data","IYEEKU_JBXX","20190920");
    }





}
