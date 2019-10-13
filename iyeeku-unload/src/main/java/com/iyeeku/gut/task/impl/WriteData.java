package com.iyeeku.gut.task.impl;

import com.iyeeku.gut.exception.GUTExceptionConstants;
import com.iyeeku.gut.exception.GUTExceptionHandler;
import com.iyeeku.gut.exception.TaskException;
import com.iyeeku.gut.main.GUT;
import com.iyeeku.gut.util.DataQueue;
import com.iyeeku.gut.util.GUTConstants;

import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 * @ClassName WriteData
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/13 8:28
 * @Version 1.0
 **/
public class WriteData implements Runnable {

    private DataQueue queue;
    private FixedLengthTask task;

    public WriteData(DataQueue paramDataQueue, FixedLengthTask paramFixedLengthTask){
        this.queue = paramDataQueue;
        this.task = paramFixedLengthTask;
    }

    @Override
    public void run() {
        OutputStream localOutputStream = null;
        try {
            ColumnMetaData[]  arrayOfColumnMetaData = this.task.getColumnMetaDatas();
            int i = this.task.getColumnCount();
            if (this.task.isGz()){
                localOutputStream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(this.task.getDataFileFullName()), this.task.getBufferSize4OS()));
            }else{
                localOutputStream = new BufferedOutputStream(new FileOutputStream(this.task.getDataFileFullName()), this.task.getBufferSize4OS());
            }
            int j = 0;
            int k = 0;
            int m = 0;
            byte[] arrayOfByte2 = this.task.getBlankBytes();
            while ((!this.task.isCompleteRead()) || (!this.queue.isEmpty())){
                if (this.queue.isEmpty()){
                    m++;
                    Thread.sleep(this.task.getThreadSleepTime());
                }else{
                    String[] arrayOfString = this.queue.take();
                    for (int n = 0; n < i; n++){
                        ColumnMetaData localColumnMetaData = arrayOfColumnMetaData[n];
                        String str = arrayOfString[n];
                        if ((str == null) || ("".equals(str))){
                            localOutputStream.write(arrayOfByte2, 0 , localColumnMetaData.getFixedLength());
                        }else{
                            byte[] arrayOfByte1 = str.getBytes("GBK");
                            k = arrayOfByte1.length;
                            j = localColumnMetaData.getFixedLength() - k;
                            if (j > 0){
                                localOutputStream.write(arrayOfByte1);
                                localOutputStream.write(arrayOfByte2, 0, j);
                            }else{
                                localOutputStream.write(arrayOfByte1, 0, localColumnMetaData.getFixedLength());
                            }
                        }
                    }
                    localOutputStream.write(10);
                }
            }
            if (GUT.isDebugMode()){
                GUT.getLogger().info("");
            }
            this.task.setStatus(GUTConstants.Task_Status_Success);
            GUTConstants.succ_num += 1;
        }catch (IOException localIOException) {
            GUTExceptionHandler.handlerException(new TaskException(GUTExceptionConstants.GUT400910, localIOException));
            this.task.setStatus(GUTConstants.Task_Status_Failed);
        }catch (InterruptedException localInterruptedException){
            GUTExceptionHandler.handlerException(new TaskException(GUTExceptionConstants.GUT400410, localInterruptedException));
            this.task.setStatus(GUTConstants.Task_Status_Failed);
        }catch (Exception localException){
            GUTExceptionHandler.handlerException(new TaskException(GUTExceptionConstants.GUT400910, localException));
            this.task.setStatus(GUTConstants.Task_Status_Failed);
        }finally {
            if (localOutputStream != null){
                try {
                    localOutputStream.flush();
                    localOutputStream.close();
                }catch (IOException localIOException){
                    GUT.getLogger().error("WARNING:close NO." + this.task.getTaskID() + " task's or " + this.task.getOldFileName() + " data file outputstream failed!");
                }
            }
        }
    }
}
