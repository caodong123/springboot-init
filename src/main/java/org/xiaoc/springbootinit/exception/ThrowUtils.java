package org.xiaoc.springbootinit.exception;

import org.xiaoc.springbootinit.common.ErrorCode;

public class ThrowUtils {

    public static void throwIf(boolean condition,RuntimeException exception){
        if(condition){
            throw exception;
        }
    }

    public static void throwIf(boolean condition, ErrorCode errorCode){
        if(condition){
            throw new BusinessException(errorCode);
        }
    }

    public static void throwIf(boolean condition, ErrorCode errorCode,String message){
        if(condition){
            throw new BusinessException(errorCode,message);
        }
    }

}
