package com.iyeeku.gut.unloader.impl;

import com.iyeeku.gut.main.GUT;
import com.iyeeku.gut.task.AbstractTask;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @ClassName TaskObserver
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/11 18:09
 * @Version 1.0
 **/
public class TaskObserver implements Observer {

    @Override
    public void update(Observable paramObservable, Object paramObject) {
        AbstractTask localAbstractTask = null;
        try {
            DefaultUnloader localDefaultUnloader = (DefaultUnloader)paramObject;
            List<AbstractTask> localList = localDefaultUnloader.getTaskList();
            int i = localDefaultUnloader.nextTaskOrder();
            localAbstractTask = localList.get(i-1);
            localAbstractTask.start();
        }catch (Exception localException){
            GUT.getLogger().error(localException.getMessage() + "\nthe method update() start thread failed!");
        }

    }
}
