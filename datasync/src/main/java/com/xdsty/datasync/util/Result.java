package com.xdsty.datasync.util;

/**
 * @author 张富华
 * @date 2020/4/2 11:22
 */
public class Result<T> {

    private T data;

    private Integer status;

    private String msg;

    private Result(){}

    private Result(T data, Integer status, String msg){
        this.data = data;
        this.status = status;
        this.msg = msg;
    }

    public static <T> Result createSuccess(T data, String msg){
        return new Result<>(data, 0, msg);
    }

    public static <T> Result createFailure(T data, String msg){
        return new Result<>(data, -1, msg);
    }

    public static <T> Result createFailure(String msg){
        return createFailure(null, msg);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
