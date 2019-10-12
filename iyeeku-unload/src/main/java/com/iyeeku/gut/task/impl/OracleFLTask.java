package com.iyeeku.gut.task.impl;

import com.iyeeku.gut.exception.GUTExceptionConstants;
import com.iyeeku.gut.exception.TaskException;
import com.iyeeku.gut.task.AbstractTask;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName OracleFLTask
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/12 18:55
 * @Version 1.0
 **/
public class OracleFLTask extends FixedLengthTask{

    @Override
    public ColumnMetaData getColumnMetaDataByRSMD(ColumnMetaData paramColumnMetaData) throws TaskException {
        try {
            int i = paramColumnMetaData.getPrecision();
            int j = paramColumnMetaData.getPrecision_true();
            paramColumnMetaData.setFixedLength(i > 0 ? i : paramColumnMetaData.getColumnDisplaySize());
            paramColumnMetaData.setNeedReplace(false);
            switch (paramColumnMetaData.getColumnType()){
                case 4:
                    paramColumnMetaData.setFixedLength(12);
                    break;
                case 2:
                    paramColumnMetaData.setFixedLength(j == 0 ? 40 : i + 2);
                    break;
                case 91:
                    paramColumnMetaData.setFixedLength(14);
                    break;
                case -102:
                case -101:
                case 93:
                    paramColumnMetaData.setFixedLength(14);
                    break;
                default:
                    paramColumnMetaData.setNeedReplace(true);
            }
            return paramColumnMetaData;
        }catch (Exception localException){
            throw new TaskException(GUTExceptionConstants.GUT400235, localException);
        }
    }

    @Override
    public AbstractTask cloneTask(AbstractTask paramAbstractTask) throws TaskException {
        try {
            return (OracleFLTask)BeanUtils.cloneBean(this);
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
