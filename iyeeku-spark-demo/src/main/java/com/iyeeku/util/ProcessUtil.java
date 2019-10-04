package com.iyeeku.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @ClassName ProcessUtil
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/9/24 22:10
 * @Version 1.0
 **/
public class ProcessUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProcessUtil.class);

    public static String execCmd(String cmd, File dir,boolean isNeedResult){
        StringBuffer result = new StringBuffer();

        Process process = null;
        BufferedReader bufrln = null;
        BufferedReader bufrError = null;

        try {
          String[] command = {"/bin/sh","-c",cmd};
          process = Runtime.getRuntime().exec(command, null, dir);
          //方法阻塞，等待命令执行完成（成功返回0）
          process.waitFor();

          if (isNeedResult){
              bufrln = new BufferedReader(new InputStreamReader(process.getInputStream(),"UTF-8"));
              bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(),"UTF-8"));

              //读取输出
              String line = null;
              while ((line = bufrln.readLine()) != null){
                  result.append(line);
              }
              while ((line = bufrError.readLine()) != null){
                  result.append(line);
              }
          }
        } catch (Exception e){
            LOGGER.error("process error",e);
        }finally {
            try {
                if (bufrln != null){
                    bufrln.close();
                }
                if (bufrError != null){
                    bufrError.close();
                }
                //销毁子进程
                if (process != null){
                    process.destroy();
                }
            }catch (IOException e){
                LOGGER.error("close stream error",e);
            }
        }
        return result.toString();
    }

}
