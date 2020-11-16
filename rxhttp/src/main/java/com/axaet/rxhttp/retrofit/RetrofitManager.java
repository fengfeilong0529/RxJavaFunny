package com.axaet.rxhttp.retrofit;

import android.content.Context;
import android.content.SharedPreferences;

import com.axaet.rxhttp.okhttp.OkHttpClientHelper;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * date: 2017/12/2
 *
 * @author yuShu
 */

public class RetrofitManager {

    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "Message";
    /**
     * 存储APP请求服务器域名
     */
    private static final String SP_KEY_SERVER_URL = "server_url";

    /**
     * 会根据服务器的切换而设置不同的host(动态变化)
     */
    public static String HOST = "https://www.wanandroid.com";
    private Retrofit mRetrofit;
    private static RetrofitManager mInstance;


    private RetrofitManager(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
//        HOST = sp.getString(SP_KEY_SERVER_URL, "");
        mRetrofit = new Retrofit.Builder()
                .client(OkHttpClientHelper.getOkHttpClient(context))
                .baseUrl(HOST)
                .addConverterFactory(CustomJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


    public void updateBaseUrl(Context context, String url) {
        HOST = url;
        mRetrofit = new Retrofit.Builder()
                .client(OkHttpClientHelper.getOkHttpClient(context))
                .baseUrl(url)
                .addConverterFactory(CustomJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


    public static RetrofitManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (RetrofitManager.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitManager(context);
                }
            }
        }
        return mInstance;
    }


    public <T> T getService(final Class<T> service) {
        return mRetrofit.create(service);
    }
}