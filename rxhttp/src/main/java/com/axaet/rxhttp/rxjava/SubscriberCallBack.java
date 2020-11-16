package com.axaet.rxhttp.rxjava;


/**
 * Request CallBack
 * date: 2017/12/2
 *
 * @author YuShu
 */
public interface SubscriberCallBack<T> {
    /**
     * Request success
     *
     * @param t data model
     */
    void onSuccess(T t);

    /**
     * Request error
     *
     * @param code Error code
     * @param msg  Error message
     */
    void onError(int code, String msg);
}
