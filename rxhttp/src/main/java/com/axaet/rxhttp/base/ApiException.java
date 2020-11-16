package com.axaet.rxhttp.base;

/**
 * $desc$
 *
 * @author yuShu
 * @date 2017/12/6
 */

public class ApiException extends Exception {

    private int code;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
