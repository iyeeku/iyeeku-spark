package com.iyeeku.spark.example;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GenerateDataFile {


    public static void main(String[] args) {


        String rq = "20190510";

        int MaxFlag = 100000;
        Random random = new Random();
        int dayJyTotal = random.nextInt(MaxFlag) + MaxFlag;

        //System.out.println(dayJyTotal);

        String filePath = "F:\\B_大数据\\tmp\\IYEEKU_U_JYWLXXWJ.20190420.000000.dat";

        File fileout = new File(filePath);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");

        String startTime = df.format(new Date());
        long startTimeMillis = System.currentTimeMillis();

        try {
            FileOutputStream fos = new FileOutputStream(fileout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (int i = 0 ; i < dayJyTotal ; i++){
                StringBuffer sb = new StringBuffer();
                //khjylsbh
                sb.append("H00100").append(String.valueOf(System.currentTimeMillis()).substring(7)).append(DataUtils.formatCompWithZore(i,10))
                   //jydqdh  CHAR(2)
                  .append("11")
                  .append("701")   //jyjgbh   CHAR(3)
                  .append(DataUtils.formatCompWithZore(random.nextInt(99999999),8)) //gylsh CHAR(8)
                  .append("117010100100446448")  //zhdh  CHAR(18)
                  .append("XDF01010010044644800000000000000")  //dfzh   CHAR(32)
                  .append(String.format("%1$-120s" , "test_wlzhmc_name_" + String.format("%0" + 12 + "d" , random.nextInt(999999999))))  //wlzhmc VARCHAR(120)
                  .append("G10" + String.format("%0" + 11 + "d" , random.nextInt(999999999)))  //xzfdfhh CHAR(14)
                  .append("test_wzfdfhm_name_" + String.format("%0" + 10 + "d" , random.nextInt(999999999)))   //wzfdfhm
                  .append(String.format("% " + 14 + "d" , random.nextInt(999999999)) + "." + String.format("%0" + 2 + "d" , random.nextInt(99))) //jyje 17
                  .append(String.format("% " + 14 + "d" , random.nextInt(999999999)) + "." + String.format("%0" + 2 + "d" , random.nextInt(99)))  // bbjyje 17
                  .append("1")  //xzbz CHAR(1)
                  .append("1")  //szbj CHAR(1)
                  .append("120") //zydh CHAR(3)
                  .append("1")  //jlzt CHAR(1)
                  .append(String.format("%0" + 12 + "d",random.nextInt(999999999))) //cpzxh INTEGER(12)
                  .append(String.format("%0" + 12 + "d",random.nextInt(999999999))) //cpznxh  INTEGER(12)
                  .append("0000000000")    //khdh CHAR(10)
                  .append(String.format("%1$-9s" ,"a"))   //cpdh CHAR(9)
                  .append("01")     // hbzl CHAR(2)
                  .append("20190419");      //jyrq  CHAR(8)

                bw.write(sb.toString());
                bw.newLine();
            }
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        long endTimeMillis = System.currentTimeMillis();
        String endTime = df.format(new Date());

/*      String cmd1 = "ls -l /home/iyeeku/CCIF_U_JYWLXXWJ.20190420.000000.upd.dat | awk '{print $5}'";
        String cmd2 = "du -sh /home/iyeeku/CCIF_U_JYWLXXWJ.20190420.000000.upd.dat | awk '{print $1}'";

        String cmd1Result = execCmd(cmd1 , null);
        String cmd2Result = execCmd(cmd2 , null);*/

        StringBuffer log_sb = new StringBuffer();
        log_sb.append(DataUtils.formatString(rq,12))
                .append(DataUtils.formatString(String.valueOf(dayJyTotal),12))
                .append(DataUtils.formatString(startTime,28))
                .append(DataUtils.formatString(endTime,28))
                .append(DataUtils.formatString(String.valueOf((endTimeMillis - startTimeMillis)/1000) , 8))
                .append(DataUtils.formatString(String.valueOf((endTimeMillis - startTimeMillis)/60000) , 8));

        System.out.println(log_sb.toString());

    }

    public static String execCmd(String cmd, File dir){
        return execCmd(cmd,dir,true);
    }

    public static String execCmd(String cmd, File dir, boolean isNeedResult){
        StringBuffer result = new StringBuffer();

        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;
        try {
            String[] command = {"/bin/sh","-c",cmd};
            process = Runtime.getRuntime().exec(command , null ,dir);
            //方法阻塞，等待命令执行完成（成功会返回0）
            process.waitFor();

            if (isNeedResult){
                bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(),"UTF-8"));
                bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(),"UTF-8"));

                String line = null;
                while ((line = bufrIn.readLine()) != null){
                    result.append(line);
                }
                while ((line = bufrError.readLine()) !=null){
                    result.append(line);
                }
            }
        }catch (Exception e){

        }finally {
            try {
                if (bufrIn != null) {
                    bufrIn.close();
                }
                if (bufrError != null) {
                    bufrError.close();
                }
                if (process != null) {
                    process.destroy();
                }
            }catch (IOException e){

            }
        }
        return result.toString();
    }


    public static class DataUtils{

        public static String formatCompWithZore(int sourceData,int formatLength){
            return String.format("%0" + formatLength + "d",sourceData);
        }

        public static String formatString(String value,int strLength){
            int valueLength = 0;
            String chinese = "[\u4e00-\u9fa5]";
            for (int i = 0 ; i < value.length() ; i++){
                String temp = value.substring(i,i+1);
                if (temp.matches(chinese)){
                    valueLength += 2;
                }else {
                    valueLength += 1;
                }
            }
            if (valueLength < strLength){
                int temp = strLength - valueLength;
                for (int i = 0 ; i < temp ; i++){
                    value += " ";
                }
            }
            return value;
        }

    }

}
