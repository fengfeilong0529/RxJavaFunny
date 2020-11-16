package com.axaet.rxhttp.base;

import java.io.Serializable;

/**
 * date: 2017/12/2
 * 获取json数据基类
 *
 * @author yuShu
 */

public class BaseResponse<T> implements Serializable {

    public BaseResponse() {
    }


    public BaseResponse(int code, String msg) {
        this.errorCode = code;
        this.errorMsg = msg;
    }

    public BaseResponse(int code, String msg, T data) {
        this.errorCode = code;
        this.errorMsg = msg;
        this.data = data;
    }

    private int errorCode;  //0成功
    private String errorMsg;
    private T data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", data=" + data +
                '}';
    }
}