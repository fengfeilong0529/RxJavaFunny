package com.axaet.rxhttp.okhttp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.axaet.rxhttp.okhttp.cache.CacheManager;
import com.axaet.rxhttp.utils.NetworkUtil;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 缓存拦截器
 * date: 2018/2/6
 *
 * @author yuShu
 */

public class CacheInterceptor implements Interceptor {

    private static final String TAG = "CacheInterceptor";
    /**
     * 缓存为空
     */
    private static final String CACHE_EMPTY = "{\"code\":-1,\"msg\":\"network error\"}";
    /**
     * post请求
     */
    private static final String REQUEST_METHOD = "POST";
    /**
     * 请求成功
     */
    private static final int RESPONSE_CODE = 200;
    /**
     * 需要缓存
     */
    private static final String CACHE_SAVE = "true";

    private Context mContext;

    CacheInterceptor(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        boolean networkIsAvailable = NetworkUtil.networkIsAvailable(mContext);
        Request request = chain.request();
        Response response;
        String url = request.url().toString();
        RequestBody requestBody = request.body();
        Charset charset = Charset.forName("UTF-8");
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        Headers headers = request.headers();
        //需要缓存的
        boolean isCache = CACHE_SAVE.equals(headers.get("Cache-Save"));
        Log.i(TAG, url);
        if (request.method().equals(REQUEST_METHOD) && isCache) {
            //需要缓存的url
            MediaType contentType = null;
            if (requestBody != null) {
                contentType = requestBody.contentType();
            }
            if (contentType != null) {
                charset = contentType.charset(Charset.forName("UTF-8"));
                Buffer buffer = new Buffer();
                try {
                    requestBody.writeTo(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (charset != null) {
                    sb.append(buffer.readString(charset));
                }
                buffer.close();
            }
        }
        String key = sb.toString();
        if (networkIsAvailable) {
            response = chain.proceed(request);
            if (response.code() == RESPONSE_CODE) {
                //需要缓存的url
                if (isCache) {
                    ResponseBody responseBody = response.body();
                    MediaType contentType = null;
                    if (responseBody != null) {
                        contentType = responseBody.contentType();
                    }
                    BufferedSource source = null;
                    if (responseBody != null) {
                        source = responseBody.source();
                    }
                    if (source != null) {
                        source.request(Long.MAX_VALUE);
                    }
                    Buffer buffer = null;
                    if (source != null) {
                        buffer = source.buffer();
                    }
                    if (contentType != null) {
                        charset = contentType.charset(Charset.forName("UTF-8"));
                    }
                    if (charset != null) {
                        //服务器返回的json原始数据
                        if (buffer != null) {
                            String json = buffer.clone().readString(charset);
                            CacheManager.getInstance(mContext).putCache(key, json);
                            Log.i(TAG, "save: " + json);
                        }
                    }
                }
            }else {
                String cache = CacheManager.getInstance(mContext).getCache(key);
                if (TextUtils.isEmpty(cache)) {
                    cache = CACHE_EMPTY;
                }
                Log.i(TAG, "cache: " + cache);
                response = new Response.Builder()
                        .body(ResponseBody.create(MediaType.parse("application/json"), cache.getBytes()))
                        .request(request)
                        .message("Request success:200")
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .build();
            }
        } else {
            String cache = CacheManager.getInstance(mContext).getCache(key);
            if (TextUtils.isEmpty(cache)) {
                cache = CACHE_EMPTY;
            }
            Log.i(TAG, "cache: " + cache);
            response = new Response.Builder()
                    .body(ResponseBody.create(MediaType.parse("application/json"), cache.getBytes()))
                    .request(request)
                    .message("Request success:200")
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .build();
        }
        return response;
    }
}
