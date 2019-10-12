package com.iyeeku.gut.task.impl;

import com.iyeeku.gut.exception.GUTExceptionConstants;
import com.iyeeku.gut.exception.TaskException;
import com.iyeeku.gut.task.AbstractTask;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName MySqlFLTask
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/12 19:07
 * @Version 1.0
 **/
public class MySqlFLTask extends FixedLengthTask {

    @Override
    public ColumnMetaData getColumnMetaDataByRSMD(ColumnMetaData paramColumnMetaData) throws TaskException {
        try {
            int i = paramColumnMetaData.getColumnType();
            int j = paramColumnMetaData.getPrecision();
            int k = paramColumnMetaData.getColumnDisplaySize();
            paramColumnMetaData.setNeedReplace(false);
            switch (i){
                case 1:
                    paramColumnMetaData.setNeedReplace(true);
                    paramColumnMetaData.setFixedLength(paramColumnMetaData.getColumnDisplaySize());
                    break;
                case 12:
                    paramColumnMetaData.setNeedReplace(true);
                    paramColumnMetaData.setFixedLength(paramColumnMetaData.getColumnDisplaySize());
                    break;
                case 5:
                    paramColumnMetaData.setFixedLength(12);
                    break;
                case 3:
                    if (paramColumnMetaData.getColumnDisplaySize() == 7){
                        paramColumnMetaData.setFixedLength(7);
                    }else{
                        paramColumnMetaData.setFixedLength(j + 2);
                    }
                    break;
                case -5:
                    paramColumnMetaData.setFixedLength(20);
                    break;
                case -6:
                    paramColumnMetaData.setFixedLength(4);
                    break;
                case 4:
                    paramColumnMetaData.setFixedLength(12);
                    break;
                case 6:
                    paramColumnMetaData.setFixedLength(30);
                    break;
                case 92:
                    paramColumnMetaData.setFixedLength(6);
                    break;
                case 93:
                    paramColumnMetaData.setFixedLength(14);
                    break;
                case 91:
                    paramColumnMetaData.setFixedLength(8);
                    break;
                case 8:
                    paramColumnMetaData.setFixedLength(30);
                    break;
                case 7:
                    paramColumnMetaData.setFixedLength(30);
                    break;
                case -7:
                    paramColumnMetaData.setFixedLength(1);
                    break;
                case 2:
                    paramColumnMetaData.setFixedLength(k <= 20 ? k : 30);
                    break;
                default:
                    paramColumnMetaData.setNeedReplace(true);
                    paramColumnMetaData.setFixedLength(30);
            }
            return paramColumnMetaData;
        }catch (Exception localException){
            throw new TaskException(GUTExceptionConstants.GUT400235, localException);
        }
    }

    @Override
    public AbstractTask cloneTask(AbstractTask paramAbstractTask) throws TaskException {
        try {
            return (MySqlFLTask) BeanUtils.cloneBean(this);
        }catch (InstantiationException localInstantiationException){
            throw new TaskException(GUTExceptionConstants.GUT400505, localInstantiationException);
        }catch (NoSuchMethodException localNoSuchMethodException){
            throw new TaskException(GUTExceptionConstants.GUT400505, localNoSuchMethodException);
        }catch (IllegalAccessException localIllegalAccessException){
            throw new TaskException(GUTExceptionConstants.GUT400505, localIllegalAccessException);
        }catch (InvocationTargetException localInvocationTargetException){
            throw new TaskException(GUTExceptionConstants.GUT400505, localInvocationTargetException);
        }
    }

}
