package com.iyeeku.gut.unloader;

import com.iyeeku.gut.exception.UnloaderException;
import com.iyeeku.gut.task.AbstractTask;

import java.util.List;

/**
 * @ClassName IUnloader
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/11 18:05
 * @Version 1.0
 **/
public abstract interface IUnloader {

    public abstract void unload() throws UnloaderException;

    public abstract void beforeTask(AbstractTask paramAbstractTask) throws UnloaderException;

    public abstract void afterTask(AbstractTask paramAbstractTask) throws UnloaderException;

    public abstract void beforeUnload() throws UnloaderException;

    public abstract void afterUnload() throws UnloaderException;

    public abstract void initTaskList(List paramList);

}
