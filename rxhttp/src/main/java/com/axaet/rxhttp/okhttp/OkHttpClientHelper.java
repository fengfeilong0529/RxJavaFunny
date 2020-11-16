package com.axaet.rxhttp.okhttp;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * date:2016/11/14
 * time:09:52
 *
 * @author YuShu
 */
public class OkHttpClientHelper {

    private static final String TAG = "yushu";

    /**
     * httpclient Operation timeout
     */
    private static final int HTTP_TIME_OUT = 10;

    private static OkHttpClient okHttpClient;


    public static OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient == null) {
            synchronized (OkHttpClientHelper.class) {
                if (okHttpClient == null) {
                    Interceptor cacheInterceptor = new CacheInterceptor(context);
                    okHttpClient = new OkHttpClient.Builder()
                            //连接超时
                            .connectTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                            //读取超时
                            .readTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                            //写超时
                            .writeTimeout(30, TimeUnit.SECONDS)
                            //缓存拦截
//                            .addInterceptor(cacheInterceptor)
                            .addInterceptor(new LogInterceptor())
                            //失败重连
                            .retryOnConnectionFailure(true)
                            .build();
                }
            }
        }
        return okHttpClient;
    }
}
