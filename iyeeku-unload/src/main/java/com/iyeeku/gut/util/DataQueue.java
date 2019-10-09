package com.iyeeku.gut.util;



/**
 * @ClassName DataQueue
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/9 18:36
 * @Version 1.0
 **/
public class DataQueue {

    int queueSize;
    String[][] buffer;
    String[] item;
    int count = 0;
    int rear = 0;
    int front = 0;

    public DataQueue(int paramInt){
        this.buffer = new String[paramInt][];
        this.queueSize = paramInt;
    }

    public synchronized String[] take(){
        if (this.count == 0){
            try {
                wait();
            }catch (InterruptedException localInterruptedException){
                localInterruptedException.printStackTrace();
            }
        }
        this.item = this.buffer[this.front];
        this.count -= 1;
        this.front = ((this.front + 1) % this.queueSize);
        notify();
        return this.item;
    }

    public synchronized void put(String[] paramArrayOfString){
        if (this.count == this.queueSize){
            try {
                wait();
            }catch (InterruptedException localInterruptedException){
                localInterruptedException.printStackTrace();
            }
        }
        this.buffer[this.rear] = paramArrayOfString;
        this.count += 1;
        this.rear = ((this.rear + 1) % this.queueSize);
        notify();
    }

    public synchronized boolean isEmpty(){
        return this.count == 0;
    }

}
