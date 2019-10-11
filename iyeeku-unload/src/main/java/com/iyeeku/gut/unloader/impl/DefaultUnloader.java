package com.iyeeku.gut.unloader.impl;


import com.iyeeku.gut.exception.GUTExceptionConstants;
import com.iyeeku.gut.exception.UnloaderException;
import com.iyeeku.gut.main.GUT;
import com.iyeeku.gut.task.AbstractTask;
import com.iyeeku.gut.unloader.IUnloader;

import java.util.List;
import java.util.Observable;

/**
 * @ClassName DefaultUnloader
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/11 18:11
 * @Version 1.0
 **/
public class DefaultUnloader extends Observable implements IUnloader {

    private List<AbstractTask> taskList;
    private int nextTaskOrder;

    public DefaultUnloader(){
        addObserver(new TaskObserver());
        this.nextTaskOrder = (GUT.getContext().getThreadNums() + 1);
    }

    @Override
    public void initTaskList(List paramList) {
        setTaskList(paramList);
    }

    @Override
    public void afterUnload() throws UnloaderException {
    }

    @Override
    public void beforeUnload() throws UnloaderException {
    }

    @Override
    public synchronized void afterTask(AbstractTask paramAbstractTask) throws UnloaderException {
        try {
            int i = paramAbstractTask.getTaskOrder() + 1;
            if (i > this.nextTaskOrder){
                this.nextTaskOrder = i;
            }
            if (this.nextTaskOrder <= this.taskList.size()){
                super.setChanged();
                notifyObservers(this);
            }
        }catch (Exception localException){
            GUT.getLogger().error(localException.getStackTrace());
            throw new UnloaderException(GUTExceptionConstants.GUT200100, localException);
        }
    }

    @Override
    public void beforeTask(AbstractTask paramAbstractTask) throws UnloaderException {
    }

    public synchronized int nextTaskOrder(){
        return this.nextTaskOrder++;
    }

    @Override
    public void unload() throws UnloaderException {
        try {
            beforeUnload();
            doUnload();
            afterUnload();
        }catch (UnloaderException localUnloaderException){
            throw localUnloaderException;
        }
    }

    public void doUnload() throws UnloaderException{
        try {
            List localList = GUT.getTaskList();
            int i = GUT.getContext().getThreadNums();
            int j = localList.size() > i ? i : localList.size();
            AbstractTask localAbstractTask;
            for (int k = 0; k < j; k++){
                localAbstractTask = (AbstractTask) localList.get(k);
                if (k < i){
                    GUT.getLogger().info("The NO." + localAbstractTask.getTaskID() + " task or " + localAbstractTask.getOldFileName() + " Start...!");
                    localAbstractTask.start();
                }
            }
            for (int k = 0 ; k < localList.size(); k++){
                localAbstractTask = (AbstractTask)localList.get(k);
                localAbstractTask.join();
            }
        }catch (Exception localException){
            throw new UnloaderException(GUTExceptionConstants.GUT200100, localException);
        }
    }

    public List<AbstractTask> getTaskList() {
        return this.taskList;
    }

    public void setTaskList(List<AbstractTask> taskList) {
        this.taskList = taskList;
    }
}
