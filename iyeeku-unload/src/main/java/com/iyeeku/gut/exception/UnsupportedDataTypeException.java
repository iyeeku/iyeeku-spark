package com.iyeeku.gut.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName UnsupportedDataTypeException
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 10:57
 * @Version 1.0
 **/
public class UnsupportedDataTypeException extends TaskException {

    public UnsupportedDataTypeException(){

    }

    public UnsupportedDataTypeException(String paramString){
        super(paramString);
    }

}
