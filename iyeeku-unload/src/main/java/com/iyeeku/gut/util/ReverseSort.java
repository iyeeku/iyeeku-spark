package com.iyeeku.gut.util;


import com.iyeeku.gut.task.AbstractTask;

import java.util.Comparator;

/**
 * @ClassName ReverseSort
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/10 22:37
 * @Version 1.0
 **/
public class ReverseSort implements Comparator {

    @Override
    public int compare(Object paramObject1, Object paramObject2) {
        AbstractTask localAbstractTask1 = (AbstractTask)paramObject1;
        AbstractTask localAbstractTask2 = (AbstractTask)paramObject2;
        return -(localAbstractTask1.getUseTime() - localAbstractTask2.getUseTime());
    }
}
