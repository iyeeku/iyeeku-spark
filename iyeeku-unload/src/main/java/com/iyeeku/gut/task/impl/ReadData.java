package com.iyeeku.gut.task.impl;

import com.iyeeku.gut.exception.GUTExceptionConstants;
import com.iyeeku.gut.exception.GUTExceptionHandler;
import com.iyeeku.gut.exception.TaskException;
import com.iyeeku.gut.main.GUT;
import com.iyeeku.gut.util.DataQueue;

import java.sql.ResultSet;

/**
 * @ClassName ReadData
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/12 19:16
 * @Version 1.0
 **/
public class ReadData implements Runnable {

    private DataQueue queue;
    private FixedLengthTask task;

    public ReadData(DataQueue paramDataQueue, FixedLengthTask paramFixedLengthTask){
        this.queue = paramDataQueue;
        this.task = paramFixedLengthTask;
    }

    @Override
    public void run() {
        try {
            this.task.setStatus(1);
            ColumnMetaData[] arrayOfColumnMetaData = this.task.getColumnMetaDatas();
            int i = this.task.getColumnCount();
            int j = 0;
            ResultSet localResult = this.task.getRs();
            this.task.getWriteThread().start();
            while (localResult.next()){
                String[] arrayOfString = new String[i];
                j++;
                for (int k =1 ; k < i + 1 ; k++){
                    ColumnMetaData localColumnMetaData = arrayOfColumnMetaData[(k -1)];
                    arrayOfString[(k -1)] = this.task.getColContentByColumnMetaData(localColumnMetaData, localResult, j);
                }
                this.queue.put(arrayOfString);
                if ((GUT.isDebugMode()) && (j % 50000 == 1)){
                    GUT.getLogger().info("NO." + this.task.getTaskID() + " task or " + this.task.getOldFileName() + " unload " + j + " rows! ");
                }
            }
            this.task.setRowCount(j);
        }catch (TaskException localTaskException){
            GUTExceptionHandler.handlerException(localTaskException);
            this.task.setStatus(-1);
        }catch (Exception localException){
            GUTExceptionHandler.handlerException(new TaskException(GUTExceptionConstants.GUT400900, localException));
            this.task.setStatus(-1);
        }finally {
            this.task.completeRead();
        }
    }

}
