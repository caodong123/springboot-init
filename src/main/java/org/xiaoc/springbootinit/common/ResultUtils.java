package org.xiaoc.springbootinit.common;

/**
 * 用于处理返回的工具类
 */
public class ResultUtils {

    /**
     * 成功返回
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<T>(0,data,"ok");
    }

    /**
     * 失败返回
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse(errorCode);
    }

    /**
     * 失败返回
     * @param errorCode
     * @param message
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode,String message){
        return new BaseResponse(errorCode.getCode(),null,message);
    }




}
